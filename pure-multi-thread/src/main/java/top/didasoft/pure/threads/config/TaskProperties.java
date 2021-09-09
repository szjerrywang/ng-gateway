package top.didasoft.pure.threads.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "top.tasks")
public class TaskProperties {
    private int noOfThreads = 1;

    public int getNoOfThreads() {
        return noOfThreads;
    }

    public void setNoOfThreads(int noOfThreads) {
        this.noOfThreads = noOfThreads;
    }

    private int noOfTasks = 1000;

    public int getNoOfTasks() {
        return noOfTasks;
    }

    public void setNoOfTasks(int noOfTasks) {
        this.noOfTasks = noOfTasks;
    }

    private long delayMs = 50;

    public long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }

}
