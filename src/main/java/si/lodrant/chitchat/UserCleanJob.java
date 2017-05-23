package si.lodrant.chitchat;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.jooby.quartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.ebean.EbeanServer;
import si.lodrant.chitchat.entities.User;
import si.lodrant.chitchat.entities.query.QUser;

public class UserCleanJob implements Job {
	final static Logger logger = LoggerFactory.getLogger(App.class);
	
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
