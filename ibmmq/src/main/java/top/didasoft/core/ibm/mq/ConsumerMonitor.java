package top.didasoft.core.ibm.mq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsumerMonitor {

    private static final Logger log = LoggerFactory.getLogger(ConsumerMonitor.class);

    @Autowired
    JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void start() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Collection<MessageListenerContainer> listenerContainers =
                        jmsListenerEndpointRegistry.getListenerContainers();
                log.info("Number of listener containers: {}", listenerContainers.size());

                for (MessageListenerContainer messageListenerContainer : listenerContainers) {
                    DefaultMessageListenerContainer defaultMessageListenerContainer =
                            (DefaultMessageListenerContainer) messageListenerContainer;
                    int activeConsumerCount = defaultMessageListenerContainer.getActiveConsumerCount();
                    int scheduledConsumerCount = defaultMessageListenerContainer.getScheduledConsumerCount();

                    log.info("Number of active consumer count: {}, scheduled consumer count: {}",
                            activeConsumerCount, scheduledConsumerCount);

                }
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        scheduledExecutorService.shutdown();
        try {
            scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
