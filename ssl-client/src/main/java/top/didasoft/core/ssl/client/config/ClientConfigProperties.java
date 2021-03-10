package top.didasoft.core.ssl.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dida.server")
public class ClientConfigProperties {
    
    private String hostName;
    private int port;
    private boolean ssl;
    private long timeoutms;

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimeoutms() {
        return timeoutms;
    }

    public void setTimeoutms(long timeoutms) {
        this.timeoutms = timeoutms;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}