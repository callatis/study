package org.callatis.study.concurrency;

public interface ErrorRateMonitor {

    /** 
    * generic SDK to be used by all APIs as their error rate monitor.
    * A service that has Rest APIs will call this API whenever it services an API, 
    * at the very end, to record whether the servicing was a success or an error. 
    */
    public void record(boolean isError);

    /**
     * Calls this every minute and would like to know what the error rate was across the last given number of seconds.
     * @param timeWindowSec number of seconds to go back and average - should be <= constructor-given numBuckets - 1. 
     * @return # errors / (# errors + # successes)
     */
    public double getErrorRate(long timeWindowSec);

}
