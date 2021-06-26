package top.didasoft.pure.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import quickfix.*;

public class CustomFixAcceptor implements SmartLifecycle, FixService {

    private static final Logger log = LoggerFactory.getLogger(CustomFixAcceptor.class);

    private static final String BEGIN_STRING = "FIX.4.4";
    private static final String TARGET_COMPID = "FALCON1";
    private static final String SENDER_COMPID = "FALCON2";

    private ThreadedSocketAcceptor socketAcceptor = null;

    private boolean isRunning = false;

    public CustomFixAcceptor() {
        SessionSettings settings = new SessionSettings();


        settings.setString(SessionFactory.SETTING_CONNECTION_TYPE, SessionFactory.ACCEPTOR_CONNECTION_TYPE);
        settings.setString(Acceptor.SETTING_SOCKET_ACCEPT_PORT, "3232");
        settings.setBool(Session.SETTING_PERSIST_MESSAGES, true);
        settings.setString(FileStoreFactory.SETTING_FILE_STORE_PATH, "/Users/jerrywang/work/qfjsvr");

        SessionID sessionID = new SessionID(BEGIN_STRING, SENDER_COMPID, TARGET_COMPID);
        settings.setBool(sessionID, Session.SETTING_NON_STOP_SESSION, true);

        ThreadedSocketAcceptor.Builder builder = null;
        try {
            builder = ThreadedSocketAcceptor.newBuilder()
                    .withApplication(new LoggingFixApplication())
                    .withSettings(settings)
                    .withQueueCapacity(10000)
                    .withLogFactory(new SLF4JLogFactory(settings))
                    .withMessageStoreFactory(new FileStoreFactory(settings));
            socketAcceptor = builder.build();
        } catch (ConfigError configError) {
            log.error("build error", configError);
        }



    }

    @Override
    public void start() {
        if (socketAcceptor != null) {
            log.info("Starting server...");
            try {
                isRunning = true;
                socketAcceptor.start();
            } catch (ConfigError configError) {
                log.error("start error", configError);
            }
        }
    }

    @Override
    public void stop() {
        if (socketAcceptor != null) {
            log.info("Stopping server...");
            isRunning = false;
            socketAcceptor.stop(false);
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
