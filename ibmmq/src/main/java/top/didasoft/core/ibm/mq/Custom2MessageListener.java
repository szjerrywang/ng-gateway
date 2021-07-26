package top.didasoft.core.ibm.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

public class Custom2MessageListener {
    private static final Logger log = LoggerFactory.getLogger(Custom2MessageListener.class);

    @JmsListener(destination = "DEV.QUEUE.2")
    public void receive(String message)  {

        log.info("received: {}", message);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "DEV.QUEUE.3")
    public void receive3(String message)  {

        log.info("received: {}", message);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
