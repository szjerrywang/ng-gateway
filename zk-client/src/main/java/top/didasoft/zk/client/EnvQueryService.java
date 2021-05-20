package top.didasoft.zk.client;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;

@Service
public class EnvQueryService implements EnvironmentAware {

    private static final Logger log = LoggerFactory.getLogger(EnvQueryService.class);

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        AbstractEnvironment abstractEnvironment = (AbstractEnvironment) this.environment;
        Map<String, Object> systemEnvironment = abstractEnvironment.getSystemEnvironment();

        MapUtils.debugPrint(
                System.out, "SYstem properties: ", systemEnvironment
        );

        MutablePropertySources propertySources = abstractEnvironment.getPropertySources();
        log.info("All properties: " + propertySources.toString());

        propertySources.forEach(propertySource -> {
            if (propertySource instanceof EnumerablePropertySource) {
                EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource) propertySource;
                log.info("PropertySource name {}, class {}", enumerablePropertySource.getName(), enumerablePropertySource.getClass().getSimpleName());
                Arrays.stream(enumerablePropertySource.getPropertyNames()).forEach(name -> {
                    log.info("Property name {} ==> value {}", name, enumerablePropertySource.getProperty(name));
                        }
                );
            }
            else {
                log.info("PropertySource name {}, class {}", propertySource.getName(), propertySource.getClass().getSimpleName());
            }
        });
    }
}
