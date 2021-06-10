package top.didasoft.pure.core;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.embedded.TomcatWebServerFactoryCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


public class MyTomcatWebServerFactoryCustomizer extends TomcatWebServerFactoryCustomizer {
    private GracefulShutdown gracefulShutdown;

    public MyTomcatWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties, GracefulShutdown gracefulShutdown) {
        super(environment, serverProperties);
        this.gracefulShutdown = gracefulShutdown;
    }

    @Override
    public void customize(ConfigurableTomcatWebServerFactory factory) {
        super.customize(factory);

        factory.addConnectorCustomizers(new MyTomcatConnectorCustomizer(), gracefulShutdown);
    }
}
