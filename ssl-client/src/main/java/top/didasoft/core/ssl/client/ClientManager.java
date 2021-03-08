package top.didasoft.core.ssl.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import top.didasoft.core.ssl.client.config.ClientConfigProperties;


@Component
public class ClientManager {

    private static final Logger log = LoggerFactory.getLogger(ClientManager.class);

    @Autowired
    ClientConfigProperties clientConfigProperties;

    private DiscardClient discardClient;

    @EventListener
    public void handleContextRefreshEvent(ContextRefreshedEvent contextRefreshedEventE) {
        log.info("Context Refreshed Event received.");

        if (discardClient != null) {
            discardClient.shutdown();
        }

        discardClient = new DiscardClient(
                clientConfigProperties.getHostName(),
                clientConfigProperties.getPort(),
                clientConfigProperties.getTimeoutms());

        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("Client Run");

        simpleAsyncTaskExecutor.execute(discardClient);
    }

    @EventListener
    public void handleContextClosedEvent(ContextClosedEvent contextClosedEvent) {
        if (discardClient != null) {
            discardClient.shutdown();
        }
    }
}
