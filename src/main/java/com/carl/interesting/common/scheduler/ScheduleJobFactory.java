package com.carl.interesting.common.scheduler;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.carl.interesting.common.util.LogUtil;

import net.sf.json.JSONObject;

/**
 * To create scheduler job
 * 
 * @author Carl Liu
 * @version [version, Jul 18, 2015]
 * @see [about class/method]
 * @since [product/module version]
 */
public class ScheduleJobFactory {
    private static final Log LOG = LogFactory.getLog(ScheduleJobFactory.class);
    
    /***
     * create a simpleScheduler job
     * 
     * @param scheduler: the scheduler
     * @param jobName: the job name
     * @param jobGroup: the job group
     * @param triggerName: the trigger name
     * @param triggerGroup: the trigger group
     * @param ssb: the schedBuilder (eg:SimpleScheduleBuilder ssb =
     * SimpleScheduleBuilder.simpleSchedule() .withIntervalInMinutes(1)
     * .repeatForever();)
     * @param jobClass: the class which implement
     * job/StatefulJob(@DisallowConcurrentExecution)
     * @param params [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static void createSimpleJob(Scheduler scheduler, String jobName,
            String jobGroup, String triggerName, String triggerGroup,
            SimpleScheduleBuilder ssb, Class<? extends Job> jobClass,
            JSONObject params) {
        // build a schedule job
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .build();
        job.getJobDataMap().put("name", jobName);
        // set job params
        if (params != null) {
            job.getJobDataMap().put("params", params);
        }
        // build a schedule simpleTrigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .startNow()
                .withSchedule(ssb)
                .build();
        try {
            // create a new schedule job
            scheduler.scheduleJob(job, trigger);
        }
        catch (SchedulerException e) {
            LogUtil.logError(LOG,
                    e,
                    "error with create " + scheduler + " job!");
        }
    }
    
    /***
     * create a cronScheduler job
     * 
     * @param scheduler: the scheduler
     * @param jobName: the job name
     * @param jobGroup: the job group
     * @param triggerName: the trigger name
     * @param triggerGroup: the trigger group
     * @param cronExpression: the cron expression
     * @param jobClass: the class which implements
     * job/StatefulJob(@DisallowConcurrentExecution)
     * @param params: the param need to trransfer
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static void createCronJob(Scheduler scheduler, String jobName,
            String jobGroup, String triggerName, String triggerGroup,
            String cronExpression, Class<? extends Job> jobClass,
            JSONObject params) {
        // build a schedule job
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .build();
        job.getJobDataMap().put("name", jobName);
        // set job params
        if (params != null) {
            job.getJobDataMap().put("params", params);
        }
        // build a schedule cronTrigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        try {
            // create a new schedule job
            scheduler.scheduleJob(job, trigger);
        }
        catch (SchedulerException e) {
            LogUtil.logError(LOG,
                    e,
                    "error with create " + scheduler + " job!");
        }
    }
    
    /***
     * delete job
     * 
     * @param scheduler
     * @param jobName
     * @param jobGroup [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static void deleteJob(Scheduler scheduler, String jobName,
            String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            // Interrupt all instances of the identified InterruptableJob
            // executing in this Scheduler instance.
            boolean interruptFlag = scheduler.interrupt(jobKey);
            if (!interruptFlag)
                LOG.warn("error with interrupt the job:" + jobName);
            // get all triggers by job
            List<? extends Trigger> listTrigger = scheduler
                    .getTriggersOfJob(jobKey);
            // pause and remove the indicated trigger from the scheduler.
            for (Trigger trigger : listTrigger) {
                scheduler.pauseTrigger(trigger.getKey());
                boolean UnscJobFlag = scheduler.unscheduleJob(trigger.getKey());
                if (!UnscJobFlag)
                    LOG.warn(
                            "error with pause and remove the indicated trigger from the scheduler :"
                                    + trigger.getKey().getName());
            }
            // delete the identified Job from the Scheduler and any associated
            // triggers
            if (scheduler.checkExists(jobKey)) {
                boolean delJobFlag = scheduler.deleteJob(jobKey);
                if (delJobFlag)
                    LOG.warn(
                            "error with delete the identified Job from the Scheduler"
                                    + jobName);
            }
        }
        catch (SchedulerException e) {
            LogUtil.logError(LOG,
                    e,
                    "error with delete job :" + jobName + " " + jobGroup);
        }
    }
}
