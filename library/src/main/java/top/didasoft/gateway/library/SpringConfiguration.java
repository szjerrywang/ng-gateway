package top.didasoft.gateway.library;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("top.didasoft.gateway.library.controller")
//@EnableWebMvc
public class SpringConfiguration {

//    @Bean
//    public HomeController homeController() {
//        return new HomeController();
//    }
}
