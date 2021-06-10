package top.didasoft.pure.rest.core;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.util.function.Supplier;

public class CustomClientHttpRequestFactorySupplier implements Supplier<ClientHttpRequestFactory>, DisposableBean {

    private MyHttpClientFactory myHttpClientFactory = new MyHttpClientFactory();

    @Override
    public ClientHttpRequestFactory get() {
        return myHttpClientFactory;
    }

    @Override
    public void destroy() throws Exception {
        myHttpClientFactory.destroy();
    }
}
