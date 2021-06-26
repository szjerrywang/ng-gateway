package top.didasoft.pure.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import quickfix.*;

public class CustomFixInitiator implements SmartLifecycle, FixService {

    private static final Logger log = LoggerFactory.getLogger(CustomFixInitiator.class);

    private static final String BEGIN_STRING = "FIX.4.4";
    private static final String SENDER_COMPID = "FALCON1";
    private static final String TARGET_COMPID = "FALCON2";

    private ThreadedSocketInitiator socketInitiator = null;

    private boolean isRunning = false;

    public CustomFixInitiator() {
        SessionSettings settings = new SessionSettings();

        settings.setString(SessionFactory.SETTING_CONNECTION_TYPE, SessionFactory.INITIATOR_CONNECTION_TYPE);
        settings.setBool(Session.SETTING_PERSIST_MESSAGES, true);
        settings.setString(FileStoreFactory.SETTING_FILE_STORE_PATH, "/Users/jerrywang/work/qfj");

        SessionID sessionID = new SessionID(BEGIN_STRING, SENDER_COMPID, TARGET_COMPID);
        settings.setString(sessionID, Initiator.SETTING_SOCKET_CONNECT_HOST, "127.0.0.1");
        settings.setString(sessionID, Initiator.SETTING_SOCKET_CONNECT_PORT, "3232");
        settings.setLong(sessionID, Session.SETTING_HEARTBTINT, 1L);
        settings.setBool(sessionID, Session.SETTING_NON_STOP_SESSION, true);


        ThreadedSocketInitiator.Builder builder = null;
        try {
            builder = ThreadedSocketInitiator.newBuilder()
                    .withReconnectThreads(1)
                    .withApplication(new LoggingFixApplication())
                    .withSettings(settings)
                    .withQueueCapacity(10000)
                    .withLogFactory(new SLF4JLogFactory(settings))
                    .withMessageStoreFactory(new FileStoreFactory(settings));
            socketInitiator = builder.build();
        } catch (ConfigError configError) {
            log.error("build error", configError);
        }



    }

    @Override
    public void start() {
        if (socketInitiator != null) {
            log.info("Starting initiator...");
            try {
                isRunning = true;
                socketInitiator.start();
            } catch (ConfigError configError) {
                log.error("start error", configError);
            }
        }
    }

    @Override
    public void stop() {
        if (socketInitiator != null) {
            log.info("Stopping initiator...");
            isRunning = false;
            socketInitiator.stop(false);
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
