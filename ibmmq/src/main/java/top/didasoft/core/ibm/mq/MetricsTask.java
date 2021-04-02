package top.didasoft.core.ibm.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MetricsTask.class);

    private int taskNumber;

    public MetricsTask(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void run() {
        log.info("Task number {} running.", this.taskNumber);
    }
}
