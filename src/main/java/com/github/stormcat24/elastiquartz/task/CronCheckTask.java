package com.github.stormcat24.elastiquartz.task;

import com.github.stormcat24.elastiquartz.config.Configuration;
import com.github.stormcat24.elastiquartz.provider.CronProvider;
import com.github.stormcat24.elastiquartz.schema.CronDefinition;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author stormcat24
 */
@Component
public class CronCheckTask {

    @Autowired
    @Qualifier("cronProviderFactory")
    private CronProvider cronProvider;

    @Autowired
    private Configuration configuration;

    @Autowired
    private TaskFactory taskFactory;

    private Scheduler scheduler;

    private final String JOB_GROUP = "publish";

    private static final Logger logger = LoggerFactory.getLogger(CronCheckTask.class);

    @PostConstruct
    public void init() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.setJobFactory(taskFactory);
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // check every minutes
    @Scheduled(fixedRate = 60 * 1000)
    public void check() {

        if (existsExecutingJobs()) {
            logger.info("Exists executing job now. Skip update cron definition");
            return;
        }

        System.out.println("---------");
        System.out.println(cronProvider.hashCode());
        Map<String, List<CronDefinition>> cronDefMap = cronProvider.getCronDefinitionMap();

        logger.info("Got cron definitions.");
        // TODO CronExpression.isValidExpression
        // TODO if has errors, do not update
        boolean validate = true;

        if (!validate) {
            throw new RuntimeException("validation error");
        }

        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEndsWith(JOB_GROUP));

            for (JobKey key : jobKeys) {
                System.out.println(key.toString());
            }
            scheduler.clear();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear current jobs.");
        }

        for (Map.Entry<String, List<CronDefinition>> entry : cronDefMap.entrySet()) {

            String target = entry.getKey();
            for (CronDefinition cronDef : entry.getValue()) {
                JobDetail jobDetail = newJob(MessagePublishTask.class)
                        .withIdentity(UUID.randomUUID().toString(), JOB_GROUP).build();

                CronTrigger cronTrigger = createTrigger(cronDef);

                JobDataMap dataMap = jobDetail.getJobDataMap();
                dataMap.put("target", target);
                dataMap.put("message", cronDef.getMessage());

                try {
                    scheduler.scheduleJob(jobDetail, cronTrigger);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private boolean existsExecutingJobs() {
        try {
            return !scheduler.getCurrentlyExecutingJobs().isEmpty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CronTrigger createTrigger(CronDefinition cronDef) {

        return newTrigger()
                .withIdentity(String.format("%s_%s", configuration.getCronTarget(), UUID.randomUUID().toString()))
                .withSchedule(cronSchedule(cronDef.getCron()))
                .build();

    }
}
