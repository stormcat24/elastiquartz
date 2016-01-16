package com.github.stormcat24.elastiquartz.server;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author stormcat24
 */
@Component
public class QuartzJobFactory implements JobFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {

        JobDetail jobDetail = bundle.getJobDetail();
        Class<? extends Job> jobType = jobDetail.getJobClass();

        return applicationContext.getBean(jobType);
    }
}
