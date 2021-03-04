package top.didasoft.core.ibm.mq;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.spring.boot.MQConnectionFactoryCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MQFactoryCustomizer  implements MQConnectionFactoryCustomizer {
    private static final Logger log = LoggerFactory.getLogger(MQFactoryCustomizer.class);

    @Override
    public void customize(MQConnectionFactory mqConnectionFactory) {
        log.info("Start to customize connectionfactory");

//        System.setProperty("javax.net.ssl.keyStore","file:///Users/jerrywang/self/mycode/ng-gateway/ibmmq/src/main/resources/tiger.jks");
//        System.setProperty("javax.net.ssl.keyStorePassword","123456");

//        log.info(mqConnectionFactory.getSSLCipherSuite());
//        log.info(mqConnectionFactory.getHostName());
        //mqConnectionFactory.setStringProperty();

    }
}
