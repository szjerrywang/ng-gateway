package top.didasoft.pure.rest.core;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class MyHttpClientFactory extends HttpComponentsClientHttpRequestFactory {

    private static Logger log = LoggerFactory.getLogger(MyHttpClientFactory.class);

    public MyHttpClientFactory() {
        HttpClientConnectionManager poolingConnManager
                = new PoolingHttpClientConnectionManager() {
            @Override
            public void shutdown() {
                super.shutdown();
                log.info("Connection Manager shutdown");
            }
        };
        CloseableHttpClient client
                = HttpClients.custom().setConnectionManager(poolingConnManager)
                .build();
        setHttpClient(client);

    }



//    public MyHttpClientFactory(HttpClient httpClient) {
//        super(httpClient);
//
//    }
}
