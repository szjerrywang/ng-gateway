package top.didasoft.pure.rest.core;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SpringBootConfiguration
@Import({MyRestTemplateAutoConfiguration.class})
public class RestClientApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RestClientApplication.class);

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public static void main(String args[])
    {
        SpringApplication.run(RestClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        restTemplateBuilder = restTemplateBuilder.detectRequestFactory(false);
//        restTemplateBuilder = restTemplateBuilder.requestFactory(MyHttpClientFactory.class);

        final RestTemplate restTemplate1 = restTemplateBuilder.build();

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.afterPropertiesSet();
        threadPoolTaskExecutor.setThreadNamePrefix("restclient");

        ArrayList<ListenableFuture<?>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ListenableFuture<?> listenable = threadPoolTaskExecutor.submitListenable(() -> {
                try {
                    ResponseEntity<String> responseEntity = restTemplate1.getForEntity("http://localhost:8080/hello", String.class);
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
        completableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));

        log.info("All requests completed.");
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
