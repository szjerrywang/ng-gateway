package top.didasoft.pure.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.beans.BeansEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.servlet.WebMvcEndpointManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.env.EnvironmentEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.management.ThreadDumpEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.JvmMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootConfiguration
@ImportAutoConfiguration(value = {DispatcherServletAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class,  WebMvcAutoConfiguration.class, ServletManagementContextAutoConfiguration.class, ErrorMvcAutoConfiguration.class, JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class, WebMvcEndpointManagementContextConfiguration.class, InfoEndpointAutoConfiguration.class, InfoContributorAutoConfiguration.class, EnvironmentEndpointAutoConfiguration.class, BeansEndpointAutoConfiguration.class,
        CompositeMeterRegistryAutoConfiguration.class, MetricsAutoConfiguration.class, MetricsEndpointAutoConfiguration.class, JvmMetricsAutoConfiguration.class, SimpleMetricsExportAutoConfiguration.class, ThreadDumpEndpointAutoConfiguration.class})
//@ComponentScan(basePackages = {"top.didasoft.pure.core"})
//@SpringBootApplication
@EnableConfigurationProperties(ServerProperties.class)
public class PureApplication {

    @Bean
    HelloWorldController helloWorldController() {
        return new HelloWorldController();
    }

//    @Bean
//    MyTomcatConnectorCustomizer myTomcatConnectorCustomizer() {
//        return new MyTomcatConnectorCustomizer();
//    }

    @Bean
    MyTomcatWebServerFactoryCustomizer myTomcatWebServerFactoryCustomizer(Environment env, ServerProperties serverProperties) {
        return new MyTomcatWebServerFactoryCustomizer(env, serverProperties);
    }

    public static void main(String args[]) {
        SpringApplication.run(PureApplication.class, args);
    }

    public static void pause(long timeInMilliSeconds) {

        long timestamp = System.currentTimeMillis();


        do {

        } while (System.currentTimeMillis() < timestamp + timeInMilliSeconds);

    }
}
