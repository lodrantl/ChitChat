/**
 * BSD 2-Clause License
 *
 * Copyright (c) 2017, Andrej Bauer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package si.lodrant.chitchat;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.jooby.Err;
import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Response;
import org.jooby.ebean.Ebeanby;
import org.jooby.json.Jackson;
import org.jooby.quartz.Quartz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import si.lodrant.chitchat.entities.Message;
import si.lodrant.chitchat.entities.StandardResponse;
import si.lodrant.chitchat.entities.User;
import si.lodrant.chitchat.entities.query.QMessage;
import si.lodrant.chitchat.entities.query.QUser;

/**
 * @author Luka Lodrant
 * @author Lenart Treven
 */
public class App extends Jooby {
	final Logger logger = LoggerFactory.getLogger(App.class);

	{
		use(new Jackson().doWith(mapper -> {
			mapper.setDateFormat(new ISO8601DateFormat());
		}));

		use(new Ebeanby().doWith((ServerConfig conf) -> {
			conf.addClass(Message.class);
			conf.addClass(User.class);
		}));

		use(new Quartz(UserCleanJob.class));

		err((req, rsp, err) -> {
			logger.error("Err thrown: ", err);
			setupJson(rsp).status(err.statusCode())
					.send(new StandardResponse(err.getMessage()));
		});

		get("/", (req, rsp) -> {
			String message = "Welcome to the ChitChat server.\n"
					+ "Let me be your guide: https://github.com/lodrantl/ChitChat/\n";
			rsp.status(200)
					.type(MediaType.plain)
					.send(message);
		}).produces(MediaType.plain);

		get("/users", (req, rsp) -> {
			setupJson(rsp);

			logger.info("Returning list of users");
			List<User> users = new QUser().findList();
			rsp.status(200)
					.send(users);
		}).produces(MediaType.json);

		post("/users", (req, rsp) -> {
			setupJson(rsp);

			String username = req.param("username")
					.value();
			logger.info(username);
			logger.info("Logging in user {}", username);
			if (!username.isEmpty()) {
				EbeanServer ebean = require(EbeanServer.class);
				List<User> users = new QUser().username.equalTo(username)
						.findList();
				if (users.size() > 0) {
					throw new Err(403, "User already exists");
				} else {
					User user = new User(username, new Date());
					ebean.save(user);
					rsp.status(200)
							.send(new StandardResponse("User logged in"));
				}
			} else {
				throw new Err(400, "Parameter username is empty");
			}
		}).produces(MediaType.json);

		delete("/users", (req, rsp) -> {
			setupJson(rsp);

			String username = req.param("username")
					.value();
			logger.info("Logging out user {}", username);
			if (!username.isEmpty()) {
				EbeanServer ebean = require(EbeanServer.class);
				List<User> users = new QUser().username.equalTo(username)
						.findList();
				if (!users.isEmpty()) {
					ebean.delete(users.get(0));
					rsp.status(200)
							.send(new StandardResponse("User logged out"));
				} else {
					rsp.status(200)
							.send(new StandardResponse("User didn't exist in the first place."));
				}
			} else {
				throw new Err(400, "Parameter username is empty");
			}
		}).produces(MediaType.json);

		get("/messages", (req, rsp) -> {
			setupJson(rsp);

			String username = req.param("username")
					.value();
			logger.info("Getting messages for user {}", username);
			if (!username.isEmpty()) {
				EbeanServer ebean = require(EbeanServer.class);
				List<User> users = new QUser().username.equalTo(username)
						.findList();
				if (!users.isEmpty()) {
					User user = users.get(0);
					refreshUser(user, ebean);
					List<Message> messages = new QMessage().recipient.equalTo(username)
							.findList();
					
					messages.sort((a, b) -> a.getSentAt().compareTo(b.getSentAt()));
					
					rsp.status(200)
							.send(messages);
					ebean.deleteAll(messages);
				} else {
					throw new Err(401, "You are not logged in.");
				}
			} else {
				throw new Err(400, "Parameter username is empty");
			}
		}).produces(MediaType.json);

		post("/messages", (req, rsp) -> {
			setupJson(rsp);
			String username = req.param("username")
					.value();
			logger.info("Sending a message from {}", username);
			Message message = req.body(Message.class);
			if (!username.isEmpty()) {
				EbeanServer ebean = require(EbeanServer.class);
				List<User> senderList = new QUser().username.equalTo(username)
						.findList();
				if (!senderList.isEmpty()) {
					refreshUser(senderList.get(0), ebean);
					if (message.getGlobal()) {
						List<User> active = new QUser().findList();
						List<Message> messages = active.stream()
								.filter(user -> ! username.equals(user.getUsername()))
								.map(user -> new Message(username, true, user.getUsername(), message.getText()))
								.collect(Collectors.toList());
						ebean.saveAll(messages);
					} else {
						if (message.getRecipient() == null || message.getRecipient()
								.isEmpty()) {
							throw new Err(400, "Cannot send a message without a recipient.");
						}
						message.setSender(username);
						message.setSentAt(new Date());
						ebean.save(message);
					}
					rsp.status(200)
							.send(new StandardResponse("Message sent"));
				} else {
					throw new Err(401, "You are not logged in.");
				}
			} else {
				throw new Err(400, "Parameter username is empty");
			}
		}).consumes(MediaType.json)
				.produces(MediaType.json);
	}

	public static void main(final String[] args) {
		run(App::new, args);
	}

	private static Response setupJson(Response r) {
		return r.type(MediaType.json)
				.charset(Charset.forName("UTF-8"));
	}

	private static void refreshUser(User user, EbeanServer es) {
		user.setLastActive(new Date());
		es.update(user);
	}
}
