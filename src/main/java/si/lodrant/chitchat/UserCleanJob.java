/**
 * BSD 2-Clause License
 *
 * Copyright (c) 2017, Luka Lodrant, Lenart Treven
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

import com.google.inject.Inject;
import io.ebean.EbeanServer;
import org.jooby.quartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.lodrant.chitchat.entities.User;
import si.lodrant.chitchat.entities.query.QUser;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserCleanJob implements Job {
    final Logger logger = LoggerFactory.getLogger(App.class);

    @Inject
    private EbeanServer ebean;

    @Override
    @Scheduled("5m; repeat=*")
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Looking for expired users");

        List<User> users = new QUser().findList();

        long now = new Date().getTime();
        List<User> expired = users.stream().filter(u -> (now - u.getLastActive().getTime() > 15 * 60 * 1000))
                .collect(Collectors.toList());

        if (expired.size() > 0) {
            ebean.deleteAll(expired);
            logger.info("Deleted {} expired users", expired.size());
        }
    }

}
