package top.didasoft.core.ibm.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("top.mq")
public class MQAppConfig {

    private String host;

    private int port;

    private String qMgrName;

    private String channel;

    private String userId;

    private String password;

    private String outQueueName;

    private String inQueueName;

    private String appName;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getqMgrName() {
        return qMgrName;
    }

    public void setqMgrName(String qMgrName) {
        this.qMgrName = qMgrName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
