package com.github.stormcat24.elastiquartz.task;

import com.github.stormcat24.elastiquartz.config.Configuration;
import com.github.stormcat24.elastiquartz.exception.SystemException;
import com.github.stormcat24.elastiquartz.quartz.MessagePublishJob;
import com.github.stormcat24.elastiquartz.provider.CronProvider;
import com.github.stormcat24.elastiquartz.schema.CronDefinition;
import com.github.stormcat24.elastiquartz.server.QuartzJobFactory;
import lombok.SneakyThrows;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author stormcat24
 */
@Component
public class CronCheckTask {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Configuration configuration;

    @Autowired
    private QuartzJobFactory quartzJobFactory;

    private Scheduler scheduler;

    private final String JOB_GROUP = "publish";

    private static final Logger logger = LoggerFactory.getLogger(CronCheckTask.class);

    @PostConstruct
    public void init() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.setJobFactory(quartzJobFactory);
            scheduler.start();
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    // check every minutes
    @Scheduled(fixedRate = 60 * 1000)
    @SneakyThrows
    public void check() {

        if (existsExecutingJobs()) {
            logger.info("Exists executing job now. Skip update cron definition");
            return;
        }

        CronProvider cronProvider = applicationContext.getBean("cronProviderFactory", CronProvider.class);
        Map<String, List<CronDefinition>> cronDefMap = cronProvider.getCronDefinitionMap();

        logger.info("Got cron definitions.");
        boolean validate = true;

        if (!validate) {
            throw new SystemException("validation error");
        }

        scheduler.clear();

        for (Map.Entry<String, List<CronDefinition>> entry : cronDefMap.entrySet()) {

            String target = entry.getKey();
            for (CronDefinition cronDef : entry.getValue()) {
                JobDetail jobDetail = newJob(MessagePublishJob.class)
                        .withIdentity(UUID.randomUUID().toString(), JOB_GROUP).build();

                CronTrigger cronTrigger = createTrigger(cronDef);

                JobDataMap dataMap = jobDetail.getJobDataMap();
                dataMap.put("target", target);
                dataMap.put("message", cronDef.getMessage());

                scheduler.scheduleJob(jobDetail, cronTrigger);
            }
        }

    }

    private boolean existsExecutingJobs() throws SchedulerException {
        return !scheduler.getCurrentlyExecutingJobs().isEmpty();
    }

    private CronTrigger createTrigger(CronDefinition cronDef) {

        return newTrigger()
                .withIdentity(String.format("%s_%s", configuration.getCronTarget(), UUID.randomUUID().toString()))
                .withSchedule(cronSchedule(cronDef.getCron()))
                .build();

    }
}
