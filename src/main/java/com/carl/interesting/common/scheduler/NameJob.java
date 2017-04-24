package com.carl.interesting.common.scheduler;

import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

public abstract class NameJob implements Job, InterruptableJob {
    public NameJob() {
    }
    
    /**
     * @param arg0
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        Object nameObj = context.getJobDetail().getJobDataMap().get("name");
        if (nameObj == null) {
            throw new NullPointerException(
                    "You must specify job name within JobDataMap which can gotten by JobExecutionContext.getJobDetail.getJobDataMap");
        }
        Thread.currentThread()
                .setName((String) nameObj + Thread.currentThread().getId());
    }
    
    /**
     * @throws UnableToInterruptJobException
     */
    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }
}
