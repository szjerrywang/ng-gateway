package top.didasoft.pure.rest.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceSupplier;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer {

	private static final Log log = LogFactory.getLog(org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer.class);

	private final AtomicInteger position;

	private final ServiceInstanceSupplier serviceInstanceSupplier;

	private final String serviceId;

	public RoundRobinLoadBalancer(String serviceId,
								  ServiceInstanceSupplier serviceInstanceSupplier) {
		this(serviceId, serviceInstanceSupplier, new Random().nextInt(1000));
	}

	public RoundRobinLoadBalancer(String serviceId,
								  ServiceInstanceSupplier serviceInstanceSupplier,
								  int seedPosition) {
		this.serviceId = serviceId;
		this.serviceInstanceSupplier = serviceInstanceSupplier;
		this.position = new AtomicInteger(seedPosition);
	}

	@Override
	// see original
	// https://github.com/Netflix/ocelli/blob/master/ocelli-core/
	// src/main/java/netflix/ocelli/loadbalancer/RoundRobinLoadBalancer.java
	public Mono<Response<ServiceInstance>> choose(Request request) {
		// TODO: move supplier to Request?
		ServiceInstanceSupplier supplier = this.serviceInstanceSupplier;
		return supplier.get().collectList().map(instances -> {
			if (instances.isEmpty()) {
				log.warn("No servers available for service: " + this.serviceId);
				return new EmptyResponse();
			}
			// TODO: enforce order?
			int pos = Math.abs(this.position.incrementAndGet());

			ServiceInstance instance = instances.get(pos % instances.size());

			return new DefaultResponse(instance);
		});
	}

}