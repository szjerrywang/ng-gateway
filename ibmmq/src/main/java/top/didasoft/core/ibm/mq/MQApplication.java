package top.didasoft.core.ibm.mq;

import com.ibm.mq.jms.MQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJms
public class MQApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MQApplication.class);

    @Autowired
    JmsTemplate jmsTemplate;

//    @Autowired
//    MQConnectionFactory connectionFactory;

    public static final void main(String[] args)
    {
        SpringApplication.run(MQApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        MQConnectionFactory mqConnectionFactory = (MQConnectionFactory) ((CachingConnectionFactory)jmsTemplate.getConnectionFactory()).getTargetConnectionFactory();
//        log.info(mqConnectionFactory.toString());
//        //log.info(mqConnectionFactory.getHostName());
//        Connection connection = mqConnectionFactory.createConnection();
//        Session session = connection.createSession();

        InputStream inputStream = MQApplication.class.getClassLoader().getResourceAsStream("request.xml");
        String text = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .lines()
                .collect(Collectors.joining("\n"));

        String msg = text;
        // Send a single message with a timestamp
        //String msg = "Hello from IBM MQ at " + new Date();

        // The default SimpleMessageConverter class will be called and turn a String
        // into a JMS TextMessage
        log.info("Sending msg: {}", msg);

        jmsTemplate.send("PTSMQ219.DATA.IN", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(msg);
//                Message message = session.createMessage();
                message.setJMSCorrelationID("819278696639627285");
//                message.set

                return message;
            }
        });

        //jmsTemplate.convertAndSend("PTSMQ219.DATA.IN", msg);
    }
}
