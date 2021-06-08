package top.didasoft.pure.core;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.stereotype.Component;

@Component
public class MyTomcatConnectorCustomizer implements TomcatConnectorCustomizer {

    private static final Logger log = LoggerFactory.getLogger(MyTomcatConnectorCustomizer.class);


    @Override
    public void customize(Connector connector) {

        log.info(connector.getProtocolHandlerClassName());
        ProtocolHandler handler = connector.getProtocolHandler();
        if (handler instanceof AbstractHttp11Protocol) {
            AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol<?>) handler;
            protocol.setMaxKeepAliveRequests(100);
            protocol.setKeepAliveTimeout(1000);
        }
    }
}
