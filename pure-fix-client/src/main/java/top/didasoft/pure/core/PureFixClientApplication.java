package top.didasoft.pure.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import quickfix.*;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootConfiguration
//@ImportAutoConfiguration(value = {KafkaAutoConfiguration.class})
//@ComponentScan(basePackages = {"top.didasoft.pure.core"})
//@SpringBootApplication
//@EnableConfigurationProperties(ServerProperties.class)
public class PureFixClientApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PureFixClientApplication.class);

    public static void main(String args[]) {
        SpringApplication.run(PureFixClientApplication.class, args);
    }

    public static void pause(long timeInMilliSeconds) {

        long timestamp = System.currentTimeMillis();


        do {

        } while (System.currentTimeMillis() < timestamp + timeInMilliSeconds);

    }

    private static final String BEGIN_STRING = "FIX.4.4";
    private static final String SENDER_COMPID = "FALCON1";
    private static final String TARGET_COMPID = "FALCON2";

    @Value("${pure.app.server:false}")
    private boolean isServer;

//    @Bean
//    FixService fixService() {
//
//        return isServer ? new CustomFixAcceptor() : new CustomFixInitiator();
//    }

    private ScheduledExecutorService scheduledExecutorService;
    @Override
    public void run(String... args) throws Exception {

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info("current time: {}", LocalDateTime.now());
            }
        }, 1, 1, TimeUnit.SECONDS);

//        SessionSettings settings = new SessionSettings();
//
//        SessionID sessionID = new SessionID(BEGIN_STRING, SENDER_COMPID, TARGET_COMPID);
//        settings.setString(SessionFactory.SETTING_CONNECTION_TYPE, SessionFactory.INITIATOR_CONNECTION_TYPE);
//        settings.setString(sessionID, Initiator.SETTING_SOCKET_CONNECT_HOST, "127.0.0.1");
//        settings.setString(sessionID, Initiator.SETTING_SOCKET_CONNECT_PORT, "3232");
//        settings.setLong(sessionID, Session.SETTING_HEARTBTINT, 1L);
//        settings.setBool(sessionID, Session.SETTING_NON_STOP_SESSION, true);
//        settings.setString(FileStoreFactory.SETTING_FILE_STORE_PATH, "/Users/jerrywang/work/qfj");
//
//        ThreadedSocketInitiator.Builder builder = ThreadedSocketInitiator.newBuilder()
//                .withReconnectThreads(1)
//                .withApplication(new LoggingFixApplication())
//                .withSettings(settings)
//                .withQueueCapacity(10000)
//                .withLogFactory(new SLF4JLogFactory(settings))
//                .withMessageStoreFactory(new FileStoreFactory(settings));
//
//        socketInitiator = builder.build();
//
//        socketInitiator.start();



        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                scheduledExecutorService.shutdown();
                try {
                    scheduledExecutorService.awaitTermination(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error("wait error", e);
                }
            }
        }));

        Thread.sleep(1500);

//        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//            @Override
//            public void run() {
//                socketInitiator.stop(false);
//            }
//        }));
    }
}
