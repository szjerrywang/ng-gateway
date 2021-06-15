package top.didasoft.pure.rest.core;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceSupplier;
import reactor.core.publisher.Flux;

import java.util.List;

public class DiscoveryClientServiceInstanceSupplier implements ServiceInstanceSupplier {

	private final DiscoveryClient delegate;

	private final String serviceId;

	public DiscoveryClientServiceInstanceSupplier(DiscoveryClient delegate,
			String serviceId) {
		this.delegate = delegate;
		this.serviceId = serviceId;
	}

	@Override
	public Flux<ServiceInstance> get() {
		List<ServiceInstance> instances = this.delegate.getInstances(this.serviceId);
		return Flux.fromIterable(instances);
	}

	public String getServiceId() {
		return this.serviceId;
	}

}
