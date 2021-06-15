package top.didasoft.pure.rest.core;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
//import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;

public class BlockingLoadBalancerClient implements LoadBalancerClient {

    private final LoadBalancerClientFactory loadBalancerClientFactory;

    //private final RoundRobinLoadBalancer loadBalancer;

    public BlockingLoadBalancerClient(
            LoadBalancerClientFactory loadBalancerClientFactory) {
        this.loadBalancerClientFactory = loadBalancerClientFactory;
    }

    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request)
            throws IOException {
        ServiceInstance serviceInstance = choose(serviceId);
        if (serviceInstance == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }
        return execute(serviceId, serviceInstance, request);
    }

    @Override
    public <T> T execute(String serviceId, ServiceInstance serviceInstance,
                         LoadBalancerRequest<T> request) throws IOException {
        try {
            return request.apply(serviceInstance);
        } catch (IOException iOException) {
            throw iOException;
        } catch (Exception exception) {
            ReflectionUtils.rethrowRuntimeException(exception);
        }
        return null;
    }

    @Override
    public URI reconstructURI(ServiceInstance serviceInstance, URI original) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
    }

    @Override
    public ServiceInstance choose(String serviceId) {
		ReactiveLoadBalancer<ServiceInstance> loadBalancer = loadBalancerClientFactory
				.getInstance(serviceId);
		if (loadBalancer == null) {
			return null;
		}
        Response<ServiceInstance> loadBalancerResponse = Mono.from(loadBalancer.choose())
                .block();
        if (loadBalancerResponse == null) {
            return null;
        }
        return loadBalancerResponse.getServer();
    }

}
