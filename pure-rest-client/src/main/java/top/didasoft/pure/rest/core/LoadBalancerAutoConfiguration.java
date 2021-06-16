package top.didasoft.pure.rest.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@AutoConfigureBefore(RestTemplateAutoConfiguration.class)
@EnableConfigurationProperties({SimpleDiscoveryProperties.class, LoadBalancerRetryProperties.class})
public class LoadBalancerAutoConfiguration {
    @Autowired
    SimpleDiscoveryProperties simpleDiscoveryProperties;

    @Bean
    SimpleDiscoveryClient simpleDiscoveryClient() {
        return new SimpleDiscoveryClient(simpleDiscoveryProperties);
    }

    @Bean
    LoadBalancerClientFactory loadBalancerClientFactory() {
        return new LoadBalancerClientFactory(simpleDiscoveryClient());
    }

    @Bean
    public LoadBalancerClient loadBalancerClient(
    ) {
        return new BlockingLoadBalancerClient(loadBalancerClientFactory());
    }


//    @Autowired(required = false)
//    private List<RestTemplate> restTemplates = Collections.emptyList();

    @Autowired(required = false)
    private List<LoadBalancerRequestTransformer> transformers = Collections.emptyList();

//    @Bean
//    public SmartInitializingSingleton loadBalancedRestTemplateInitializerDeprecated(
//            final ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers) {
//        return () -> restTemplateCustomizers.ifAvailable(customizers -> {
//            for (RestTemplate restTemplate : this.restTemplates) {
//                for (RestTemplateCustomizer customizer : customizers) {
//                    customizer.customize(restTemplate);
//                }
//            }
//        });
//    }

    @Bean
    @ConditionalOnMissingBean
    public LoadBalancerRequestFactory loadBalancerRequestFactory(
            LoadBalancerClient loadBalancerClient) {
        return new LoadBalancerRequestFactory(loadBalancerClient, this.transformers);
    }

    @Bean
    public LoadBalancerInterceptor loadBalancerInterceptor(
            LoadBalancerClient loadBalancerClient,
            LoadBalancerRequestFactory requestFactory) {
        return new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
    }
    @Autowired
    private LoadBalancerRetryProperties properties;

    @Bean
    LoadBalancedRetryFactory loadBalancedRetryFactory() {
        return new CustomLoadBalancedRetryFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryLoadBalancerInterceptor retryInterceptor(
            LoadBalancerClient loadBalancerClient,
            LoadBalancerRetryProperties properties,
            LoadBalancerRequestFactory requestFactory,
            LoadBalancedRetryFactory loadBalancedRetryFactory) {
        return new RetryLoadBalancerInterceptor(loadBalancerClient, properties,
                requestFactory, loadBalancedRetryFactory);
    }

//    @Bean
//    public RestTemplateCustomizer restTemplateCustomizer(
//            final LoadBalancerInterceptor loadBalancerInterceptor) {
//        return restTemplate -> {
//            List<ClientHttpRequestInterceptor> list = new ArrayList<>(
//                    restTemplate.getInterceptors());
//            list.add(loadBalancerInterceptor);
//            restTemplate.setInterceptors(list);
//        };
//    }

    @Bean
    public RestTemplateCustomizer restTemplateCustomizer(
            final RetryLoadBalancerInterceptor loadBalancerInterceptor) {
        return restTemplate -> {
            List<ClientHttpRequestInterceptor> list = new ArrayList<>(
                    restTemplate.getInterceptors());
            list.add(loadBalancerInterceptor);
            restTemplate.setInterceptors(list);
        };
    }
}
