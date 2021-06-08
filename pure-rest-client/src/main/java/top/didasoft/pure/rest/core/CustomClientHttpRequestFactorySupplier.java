package top.didasoft.pure.rest.core;

import org.springframework.http.client.ClientHttpRequestFactory;

import java.util.function.Supplier;

public class CustomClientHttpRequestFactorySupplier implements Supplier<ClientHttpRequestFactory> {

    @Override
    public ClientHttpRequestFactory get() {
        return null;
    }
}
