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

import org.jooby.Err;
import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Mutant;
import org.jooby.Response;
import org.jooby.Route;
import org.jooby.ebean.Ebeanby;
import org.jooby.json.Jackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import si.lodrant.chitchat.entities.CResponse;
import si.lodrant.chitchat.entities.Message;
import si.lodrant.chitchat.entities.User;
import si.lodrant.chitchat.entities.query.QMessage;
import si.lodrant.chitchat.entities.query.QUser;

/**
 * @author jooby generator
 */
public class App extends Jooby {
	final static Logger logger = LoggerFactory.getLogger(App.class);

	{
		use(new Jackson().doWith(mapper -> {
			mapper.setDateFormat(new ISO8601DateFormat());
		}));

		use(new Ebeanby().doWith((ServerConfig conf) -> {
			conf.addClass(Message.class);
			conf.addClass(User.class);
		}));
		
		  err((req, rsp, err) -> {
			    logger.error("Err thrown: ", err);
			    s(rsp).status(err.statusCode()).send(new CResponse(err.getMessage()));
			  });

		get("/", (req, rsp) -> {
			String message = "Welcome to the ChitChat server.\n"
					+ "Let me be your guide: https://github.com/andrejbauer/ChitChat/";
			rsp.status(200).type(MediaType.plain).send(message);
		});

		get("/users", (req, rsp) -> {
			logger.info("Returning list of users");
			List<User> users = new QUser().findList();
			s(rsp).status(200).send(users);
		});

		post("/users", (req, rsp) -> {
			Mutant username = req.param("username");
			logger.info(username.value());
			logger.info("Logging in user {}", username.value());
			if (username.isSet()) {
				EbeanServer ebean = require(EbeanServer.class);
				List<User> users = new QUser().username.equalTo(username.value()).findList();
				if (users.size() > 0) {
					throw new Err(403, "User already exists");
				} else {

					User user = new User(username.value(), new Date());
					ebean.save(user);
					s(rsp).status(200).send(new CResponse("User logged in"));
				}
			} else {
				throw new Err(400, "Cannot log you in if I do not know who you are (parameter missing)");
			}
		});

		delete("/users", (req, rsp) -> {
			Mutant username = req.param("username");
			logger.info("Logging out user {}", username.value());
			if (username.isSet()) {
				EbeanServer ebean = require(EbeanServer.class);
				List<User> users = new QUser().username.equalTo(username.value()).findList();
				if (!users.isEmpty()) {
					ebean.delete(users.get(0));
					s(rsp).status(200).send(new CResponse("User logged out"));
				} else {
					s(rsp).status(200).send(new CResponse("User didn't exist in the first place."));
				}
			} else {
				throw new Err(400, "Cannot delete you if I do not know who you are (parameter missing)");
			}
		});

		get("/messages", (req, rsp) -> {
			Mutant username = req.param("username");
			logger.info("Getting messages for user {}", username.value());
			if (username.isSet()) {
				EbeanServer ebean = require(EbeanServer.class);
				List<User> users = new QUser().username.equalTo(username.value()).findList();
				if (!users.isEmpty()) {
					ebean.delete(users.get(0));
					List<Message> messages = new QMessage().recipient.equalTo(username.value()).findList();
					s(rsp).status(200).send(messages);
				} else {
					throw new Err(401, "You are not logged in.");
				}
			} else {
				throw new Err(400, "Cannot delete you if I do not know who you are (parameter missing)");
			}
		});

		post("/messages", (req, rsp) -> {
			Mutant username = req.param("username");
			logger.info("Sending a message from {}", username.value());
			Message message = req.body(Message.class);
			if (username.isSet()) {
				EbeanServer ebean = require(EbeanServer.class);
				List<User> senderList = new QUser().username.equalTo(username.value()).findList();
				if (!senderList.isEmpty()) {
					if (message.getGlobal()) {
						List<User> active = new QUser().findList();
						for (User user : active) {
							ebean.save(new Message(username.value(), true, user.getUsername(), message.getText()));
						}
					} else {
						message.setSender(username.value());
						ebean.save(message);
					}
					s(rsp).status(200).send(new CResponse("Message sent"));
				} else {
					throw new Err(401, "You are not logged in.");
				}
			} else {
				throw new Err(400, "Cannot send messages if I do not know who you are (parameter missing)");
			}

		}).consumes("application/json");
	}

	public static void main(final String[] args) {
		run(App::new, args);
	}
	
	private static Response s(Response r) {
		return r.type(MediaType.json).charset(Charset.forName("UTF-8"));
	}
}
