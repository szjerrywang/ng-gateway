package top.didasoft.pure.rest.core;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class LoadBalancerClientFactory
		//extends NamedContextFactory<LoadBalancerClientSpecification>
		implements ReactiveLoadBalancer.Factory<ServiceInstance> {

	/**
	 * Property source name for load balancer.
	 */
	//public static final String NAMESPACE = "loadbalancer";

	/**
	 * Property for client name within the load balancer namespace.
	 */
	//public static final String PROPERTY_NAME = NAMESPACE + ".client.name";
	private final DiscoveryClient discoveryClient;

	private Map<String, ReactiveLoadBalancer<ServiceInstance>> loadBalancers = new ConcurrentHashMap();

	public LoadBalancerClientFactory(DiscoveryClient discoveryClient) {
		//super(LoadBalancerClientConfiguration.class, NAMESPACE, PROPERTY_NAME);
		this.discoveryClient = discoveryClient;
	}

//	public String getName(Environment environment) {
//		return environment.getProperty(PROPERTY_NAME);
//	}

	@Override
	public ReactiveLoadBalancer<ServiceInstance> getInstance(String serviceId) {
		loadBalancers.computeIfAbsent(serviceId, k -> {return new RoundRobinLoadBalancer(k, new DiscoveryClientServiceInstanceSupplier(discoveryClient, k));});
		return loadBalancers.get(serviceId); //, () -> {return new RoundRobinLoadBalancer(serviceId, new DiscoveryClientServiceInstanceSupplier(discoveryClient, serviceId))});
		//return getInstance(serviceId, ReactorServiceInstanceLoadBalancer.class);
	}

}
