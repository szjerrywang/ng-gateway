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

import javax.jms.Connection;
import javax.jms.Session;

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
        MQConnectionFactory mqConnectionFactory = (MQConnectionFactory) ((CachingConnectionFactory)jmsTemplate.getConnectionFactory()).getTargetConnectionFactory();
        log.info(mqConnectionFactory.toString());
        //log.info(mqConnectionFactory.getHostName());
        Connection connection = mqConnectionFactory.createConnection();
        Session session = connection.createSession();
    }
}
