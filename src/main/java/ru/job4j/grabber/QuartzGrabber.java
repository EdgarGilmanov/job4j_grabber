package ru.job4j.grabber;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.model.Post;
import ru.job4j.store.Store;
import ru.job4j.parser.Parser;
import java.util.List;
import java.util.Properties;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Qartz framework makes it easy to organize
 * work in schedule mode. The mode is defined in rabbit.properties.
 * An important point - we must implement the {@link org.quartz.Job} interface.
 * Performing repetitions will be implemented by him.
 */
public class QuartzGrabber implements Grab {
    private static final Logger LOGGER = Logger.getLogger(QuartzGrabber.class);
    private final Properties cfg;

    public QuartzGrabber(Properties cfg) {
        this.cfg = cfg;
    }

    @Override
    public void init(Parser parse, Store store) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            data.put("parse", parse);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInHours(Integer.parseInt(cfg.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            LOGGER.info("Rabbit initialized successfully");
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * This class implements repeating actions, which {@link ru.job4j.grabber.Grab} must be scheduled
     */
    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Store store = (Store) context.getJobDetail().getJobDataMap().get("store");
            Parser parse = (Parser) context.getJobDetail().getJobDataMap().get("parse");
            List<Post> newPosts = parse.getAllPosts(1);
            LOGGER.info("Rabbit started to work");
            for (Post post : newPosts) {
                store.save(post);
            }
        }
    }
}
