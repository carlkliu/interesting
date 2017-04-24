package com.carl.interesting.common.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.carl.interesting.common.scheduler.ScheduleFactory;
import com.carl.interesting.common.scheduler.ScheduleJobFactory;
import com.carl.interesting.common.util.LogUtil;

import net.sf.json.JSONObject;

/**
 * Scheduler for handling session GC
 * 
 * @author Carl Liu
 * @version [version, 3 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class SessionScheduler {
    private static final Log LOG = LogFactory.getLog(SessionScheduler.class);
    
    /**
     * start scheduler
     * 
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public void initOperation() {
        try {
            Scheduler operationScheduler = ScheduleFactory.getScheduler();
            String key = "session";
            Class<? extends Job> clazz = SessionGcJob.class;
            JSONObject params = new JSONObject();
            params.put("key", key);
            // create a timing devices cronScheduler job
            ScheduleJobFactory.createCronJob(operationScheduler,
                    "JobName_" + key,
                    "JobGroup_" + key,
                    "TriggerName_" + key,
                    "TriggerGroup_" + key,
                    "0/5 * * * * ? *",
                    clazz,
                    params);
            LOG.info("The session shceduler has been crearted!");
            operationScheduler.start();
        }
        catch (SchedulerException e) {
            LogUtil.logError(LOG, e, "Failed to execute session scheduler!");
        }
    }
}
