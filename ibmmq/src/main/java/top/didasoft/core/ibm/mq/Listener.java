package top.didasoft.core.ibm.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Listener {
//  static boolean warned = false;

  private static final Logger log = LoggerFactory.getLogger(Listener.class);

  @JmsListener(destination = "PTSMQ219.DATA.OUT")
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
