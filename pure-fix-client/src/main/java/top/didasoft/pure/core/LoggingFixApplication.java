package top.didasoft.pure.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

public class LoggingFixApplication implements Application {

    private static final Logger log = LoggerFactory.getLogger(LoggingFixApplication.class);

    @Override
    public void onCreate(SessionID sessionId) {
        log.info("Session created: {}", sessionId.toString());
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log.info("Session logon: {}", sessionId.toString());
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info("Session logout: {}", sessionId.toString());
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        log.info("Session send admin message: {}, message: {}", sessionId.toString(), message.toRawString());
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log.info("Session receive admin message: {}, message: {}", sessionId.toString(), message.toRawString());

    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        log.info("Session send app message: {}, message: {}", sessionId.toString(), message.toRawString());

    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        log.info("Session receive app message: {}, message: {}", sessionId.toString(), message.toRawString());

    }
}
