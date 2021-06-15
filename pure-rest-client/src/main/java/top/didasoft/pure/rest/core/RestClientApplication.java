package top.didasoft.pure.rest.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SpringBootConfiguration
@ImportAutoConfiguration({MyRestTemplateAutoConfiguration.class})
//@ImportAutoConfiguration({JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, LoadBalancerAutoConfiguration.class, RestTemplateAutoConfiguration.class})
public class RestClientApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RestClientApplication.class);

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @Bean
    RestTemplate restTemplate() {
        return this.restTemplateBuilder.build();
    }

    @Autowired
    RestTemplate restTemplate1;

    public static void main(String args[]) {
        SpringApplication.run(RestClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //restTemplateBuilder = restTemplateBuilder.requestFactory(new CustomClientHttpRequestFactorySupplier());
//        restTemplateBuilder = restTemplateBuilder.detectRequestFactory(false);
//        restTemplateBuilder = restTemplateBuilder.requestFactory(MyHttpClientFactory.class);

        //final RestTemplate restTemplate1 = restTemplateBuilder.build();

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setQueueCapacity(1);
        threadPoolTaskExecutor.afterPropertiesSet();
        threadPoolTaskExecutor.setThreadNamePrefix("restclient");

        ArrayList<ListenableFuture<?>> futures = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ListenableFuture<?> listenable = threadPoolTaskExecutor.submitListenable(() -> {
                log.info("Requesting...");
                try {
                    ResponseEntity<String> responseEntity = restTemplate1.getForEntity("http://localhost:8080/delay?delayMs=10", String.class);
                    log.info("Response: {}", responseEntity.getBody());
                } catch (Exception e) {
                    log.error("rest client exception", e);
                }
            });
            futures.add(listenable);
        }

        log.info("Waiting all requests to completed.");
        CompletableFuture<?> completableFuture = new CompletableFuture<>();
        List<? extends CompletableFuture<?>> completableFutures = futures.stream().map(listenableFuture -> listenableFuture.completable()).collect(Collectors.toList());
        completableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).get();

        log.info("All requests completed.");
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(30);
        threadPoolTaskExecutor.shutdown();

//        log.info(restTemplate1.getRequestFactory().toString());
//
//        log.info("restTemplate 1: {}", restTemplate1.toString());
//
//        ClientHttpRequestFactory requestFactory = restTemplate1.getRequestFactory();

//        if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
//            HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
//                    (HttpComponentsClientHttpRequestFactory) requestFactory;
//
//
//            CloseableHttpClient httpClient = (CloseableHttpClient) httpComponentsClientHttpRequestFactory.getHttpClient();
//
//            httpClient.close();
//
//
//        }


//        RestTemplate restTemplate2 = restTemplateBuilder.build();
//
//        log.info("restTemplate 2: {}", restTemplate2.toString());
//
//        log.info(restTemplate2.getRequestFactory().toString());
    }
}
