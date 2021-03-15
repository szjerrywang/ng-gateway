package top.didasoft.core.ibm.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("top.mq")
public class MQAppConfig {

    private String outQueueName;

    private String inQueueName;

    private int tps;

    private int total;

    public String getInQueueName() {
        return inQueueName;
    }

    public String getOutQueueName() {
        return outQueueName;
    }

    public void setInQueueName(String inQueueName) {
        this.inQueueName = inQueueName;
    }

    public void setOutQueueName(String outQueueName) {
        this.outQueueName = outQueueName;
    }

    public int getTotal() {
        return total;
    }

    public int getTps() {
        return tps;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setTps(int tps) {
        this.tps = tps;
    }
}
