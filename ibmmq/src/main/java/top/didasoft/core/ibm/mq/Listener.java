package top.didasoft.core.ibm.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.didasoft.core.ibm.mq.config.MQAppConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class Listener {
//  static boolean warned = false;

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    MQAppConfig mqAppConfig;

    @Autowired
    JmsListenerEndpointRegistry endpointRegistry;


    public void shutdown() {
        endpointRegistry.getListenerContainers().forEach((container) -> {
            if (container.isRunning()) {
                log.debug("Shutting down listener: " + container.getClass().getName());
                container.stop();
            }
        });
    }

    @Autowired
    private ConfigurableApplicationContext context;

    private AtomicInteger round = new AtomicInteger(0);

    private AtomicLong currentMsgID = new AtomicLong(0);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedDelay = 1000)
    public void sendMessage() {
        if (round.get() >= mqAppConfig.getTotal())
            return;
        try {
            for (int i = 0; i < mqAppConfig.getTps(); i++) {

                jmsTemplate.convertAndSend(mqAppConfig.getInQueueName(), "Message" + currentMsgID.incrementAndGet() + " at " + dateFormat.format(new Date()));
            }
        } catch (JmsException e) {
            log.error("sent error", e);
        }

        int current = round.incrementAndGet();
        log.info("Sent round {} messages", current);

        if (current >= mqAppConfig.getTotal()) {

            CompletableFuture.supplyAsync(() -> {
                log.info("Round is done. delay 5s to receive the message and close the application.");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                log.info("Closing the application.");
                this.shutdown();
                System.exit(SpringApplication.exit(context));
                return "OK";
            });

        }
    }

    @JmsListener(destination = "${top.mq.outQueueName}")
    public void receiveMessage(String msg) {

        log.info("Received message is: " + msg);

//    System.out.println();
//    System.out.println("========================================");
//    System.out.println("Received message is: " + msg);
//    System.out.println("========================================");

    }

//  void infinityWarning() {
//    if (!warned) {
//      warned = true;
//      System.out.println();
//      System.out.println("========================================");
//      System.out.println("MQ JMS Listener started for queue: " + Application.qName);
//      System.out.println("NOTE: This program does not automatically end - it continues to wait");
//      System.out.println("      for more messages, so you may need to hit BREAK to end it.");
//      System.out.println("========================================");
//    }
//  }
}
