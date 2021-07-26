package top.didasoft.core.ibm.mq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.backoff.FixedBackOff;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsListenerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JmsListenerConfiguration.class);

    @Bean
    DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory() {
            @Override
            protected DefaultMessageListenerContainer createContainerInstance() {
                return new DefaultMessageListenerContainer() {
                    @Override
                    protected void handleListenerSetupFailure(Throwable ex, boolean alreadyRecovered) {
                        super.handleListenerSetupFailure(ex, alreadyRecovered);
                        log.info("recovered: {}", alreadyRecovered);
                    }
                };
            }

            @Override
            protected void initializeContainer(DefaultMessageListenerContainer container) {
                super.initializeContainer(container);

                log.info("Customize container");


            }

        };

        configurer.configure(factory, connectionFactory);

        FixedBackOff fixedBackOff = new FixedBackOff(5000L, 10);

        factory.setBackOff(fixedBackOff);



        return factory;
    }
}
