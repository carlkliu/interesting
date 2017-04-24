package com.carl.interesting.common.session;

import java.util.Iterator;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.carl.interesting.common.constants.KeyConstant;
import com.carl.interesting.common.scheduler.NameJob;

/**
 * Job for handling HTTP request session GC
 * 
 * @author xupingzheng
 * @version [version, 12 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class SessionGcJob extends NameJob {
    /**
     * default timeout ,30 minutes
     */
    private Long defaultTimeout = Long
            .getLong(KeyConstant.HTTP_REQUEST_SESSION_TIMEOUT);
    
    /**
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        // HTTP request session GC
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, CustomSession>> it = CustomSessionManagement.map
                .entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CustomSession> entry = it.next();
            if (currentTime
                    - (long) entry.getValue().getLastTime() > defaultTimeout) {
                it.remove();
            }
        }
    }
}
