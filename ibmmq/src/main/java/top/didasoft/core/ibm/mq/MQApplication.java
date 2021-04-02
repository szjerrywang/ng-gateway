package top.didasoft.core.ibm.mq;


import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.didasoft.core.ibm.mq.config.MQAppConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootApplication
//@EnableJms
//@EnableScheduling
public class MQApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MQApplication.class);

    @Autowired
    MQAppConfig mqAppConfig;

//    @Autowired
//    JmsTemplate jmsTemplate;
//
//    @Autowired
//    MQConnectionFactory connectionFactory;

    public static final void main(String[] args) {
        SpringApplication.run(MQApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        MQEnvironment.hostname = mqAppConfig.getHost();
        MQEnvironment.port = mqAppConfig.getPort();
        MQEnvironment.channel = mqAppConfig.getChannel();
        MQEnvironment.userID = mqAppConfig.getUserId();
        MQEnvironment.password = mqAppConfig.getPassword();
        MQEnvironment.properties.put(MQConstants.APPNAME_PROPERTY, mqAppConfig.getAppName());

        MQQueueManager queueManager = new MQQueueManager(mqAppConfig.getqMgrName());

        MQQueue queue = null;

        try {
            queue = queueManager.accessQueue(mqAppConfig.getOutQueueName(), CMQC.MQOO_INPUT_AS_Q_DEF);

            //CMQC.MQOO_BROWSE
            MQMessage myMessage = new MQMessage();
            myMessage.writeInt(25);

            String name = "Charlie Jordan";
            myMessage.writeInt(name.length());
            myMessage.writeString(name);
            //myMessage.writeBytes(name);

// Use the default put message options...
            MQPutMessageOptions pmo = new MQPutMessageOptions();
//            pmo.options

// put the message
            //queue.put(myMessage,pmo);

            MQMessage getMessage = new MQMessage();
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.options += MQConstants.MQGMO_WAIT;
            gmo.waitInterval = 1000;
            queue.get(getMessage, gmo);



//            queue.get();
        }
        catch (Exception e)
        {
            log.error("Exception: ", e);
        }
        finally {
            if (queue != null) {
                queue.close();
            }
        }
    }

    private void runScheduler() throws ExecutionException, InterruptedException {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        List<ScheduledFuture<?>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(new MetricsTask(i), 1, TimeUnit.SECONDS);
            futures.add(scheduledFuture);
        }
        for (int i = 0; i < 100; i++) {
            Object o = futures.get(0).get();
        }

        scheduler.shutdown();

//        scheduler
//            scheduler.s
        //scheduler.scheduleAtFixedRate(() -> log.info("hee"), 1, 1, TimeUnit.SECONDS);

//        ConcurrentTaskExecutor taskExecutor = new ConcurrentTaskExecutor()

////        MQConnectionFactory mqConnectionFactory = (MQConnectionFactory) ((CachingConnectionFactory)jmsTemplate.getConnectionFactory()).getTargetConnectionFactory();
////        log.info(mqConnectionFactory.toString());
////        //log.info(mqConnectionFactory.getHostName());
////        Connection connection = mqConnectionFactory.createConnection();
////        Session session = connection.createSession();
//
//        log.info("Transport type: {}", connectionFactory.getTransportType());
//        //log.info("JMS", connectionFactory.getPr)
//        //connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
//        //connectionFactory.setIntProperty(WMQConstants.JMS_IBM_FORMAT, WMQConstants.);
//        InputStream inputStream = MQApplication.class.getClassLoader().getResourceAsStream("request.xml");
//        String text = new BufferedReader(
//                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
//        .lines()
//                .collect(Collectors.joining("\n"));
//
//
//
//        //String msg = text;
//        // Send a single message with a timestamp
//        //String msg = "Hello from IBM MQ at " + new Date();
//        //"20210309771";//
//        String correlationID = RandomString.make();
//
//        Pattern p = Pattern.compile("O6Ws46D1", Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(text);
//
//        String msg = m.replaceFirst(correlationID);
//
//        // The default SimpleMessageConverter class will be called and turn a String
//        // into a JMS TextMessage
//        log.info("Sending msg: {} with correlation id {}", msg, correlationID);
//
//        Session session = connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
//        Queue queue = session.createQueue("PTSMQ219.DATA.IN");
//
//        MQQueue mqQueue = (MQQueue) queue;
//        mqQueue.setMessageBodyStyle(WMQConstants.WMQ_MESSAGE_BODY_MQ);
//
//
//        MessageProducer producer = session.createProducer(mqQueue);
//        Message message = session.createTextMessage(msg);
////                Message message = session.createMessage();
//        message.setJMSCorrelationID(correlationID);
//        log.info("Message property JMS_IBM_FORMAT: {}", message.getStringProperty(WMQConstants.JMS_IBM_FORMAT));
//        log.info(message.getClass().toString());
//        producer.send(message);
//
//        //"PTSMQ219.DATA.IN");
////        jmsTemplate.send("PTSMQ219.DATA.IN", new MessageCreator() {
////            @Override
////            public Message createMessage(Session session) throws JMSException {
////                Message message = session.createTextMessage(msg);
//////                Message message = session.createMessage();
////                message.setJMSCorrelationID(correlationID);
////                log.info(message.getClass().toString());
//////                message.set
////
////                return message;
////            }
////        });
//
//        //jmsTemplate.convertAndSend("PTSMQ219.DATA.IN", msg);
    }
}
