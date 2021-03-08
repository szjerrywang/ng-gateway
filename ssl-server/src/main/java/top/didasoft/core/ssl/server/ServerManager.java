package top.didasoft.core.ssl.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import top.didasoft.core.ssl.server.config.ServerConfigProperties;

@Component
public class ServerManager {

    private static final Logger log = LoggerFactory.getLogger(ServerManager.class);

    @Autowired
    ServerConfigProperties serverConfigProperties;

    private DiscardServer discardServer;

    @EventListener
    public void handleContextRefreshEvent(ContextRefreshedEvent contextRefreshedEventE) {
        log.info("Context Refreshed Event received.");

        if (discardServer != null) {
            discardServer.shutdown();
        }

        discardServer = new DiscardServer(serverConfigProperties.getPort());

        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("Server Run");

        simpleAsyncTaskExecutor.execute(discardServer);
    }

    @EventListener
    public void handleContextClosedEvent(ContextClosedEvent contextClosedEvent) {
        if (discardServer != null) {
            discardServer.shutdown();
        }
    }
}
