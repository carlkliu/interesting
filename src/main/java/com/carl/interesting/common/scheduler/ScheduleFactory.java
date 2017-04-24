package com.carl.interesting.common.scheduler;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.carl.interesting.common.constants.KeyConstant;

/**
 * Scheduler Factory: used to create a scheduler
 * 
 * @author Pingzheng Xu
 * @version [version, Nov 17, 2014]
 * @see [about class/method]
 * @since [product/module version]
 */
public class ScheduleFactory {
    private static Scheduler scheduler = null;
    
    private ScheduleFactory() {
    }
    
    /***
     * get scheduler instance
     * 
     * @return [explain parameter]
     * @return Scheduler [explain return type]
     * @throws SchedulerException
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static Scheduler getScheduler() throws SchedulerException {
        if (scheduler == null) {
            StdSchedulerFactory sf = new StdSchedulerFactory(
                    "quartz.properties");
            Properties pros = new Properties();
            pros.put("org.quartz.threadPool.threadCount",
                    KeyConstant.QUARTZ_THREAD_COUNT);
            sf.initialize(pros);
            scheduler = sf.getScheduler();
        }
        return scheduler;
    }
}
