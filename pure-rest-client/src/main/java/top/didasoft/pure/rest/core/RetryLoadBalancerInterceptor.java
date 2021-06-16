package top.didasoft.pure.rest.core;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URI;

public class RetryLoadBalancerInterceptor implements ClientHttpRequestInterceptor {

	private LoadBalancerClient loadBalancer;

	private LoadBalancerRetryProperties lbProperties;

	private LoadBalancerRequestFactory requestFactory;

	private LoadBalancedRetryFactory lbRetryFactory;

	public RetryLoadBalancerInterceptor(LoadBalancerClient loadBalancer,
			LoadBalancerRetryProperties lbProperties,
			LoadBalancerRequestFactory requestFactory,
			LoadBalancedRetryFactory lbRetryFactory) {
		this.loadBalancer = loadBalancer;
		this.lbProperties = lbProperties;
		this.requestFactory = requestFactory;
		this.lbRetryFactory = lbRetryFactory;

	}

	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
										final ClientHttpRequestExecution execution) throws IOException {
		final URI originalUri = request.getURI();
		final String serviceName = originalUri.getHost();
		Assert.state(serviceName != null,
				"Request URI does not contain a valid hostname: " + originalUri);
		final LoadBalancedRetryPolicy retryPolicy = this.lbRetryFactory
				.createRetryPolicy(serviceName, this.loadBalancer);
		RetryTemplate template = createRetryTemplate(serviceName, request, retryPolicy);
		return template.execute(context -> {
			ServiceInstance serviceInstance = null;
			if (context instanceof LoadBalancedRetryContext) {
				LoadBalancedRetryContext lbContext = (LoadBalancedRetryContext) context;
				serviceInstance = lbContext.getServiceInstance();
			}
//			if (serviceInstance == null) {
				serviceInstance = this.loadBalancer.choose(serviceName);
//			}
			ClientHttpResponse response = RetryLoadBalancerInterceptor.this.loadBalancer
					.execute(serviceName, serviceInstance,
							this.requestFactory.createRequest(request, body, execution));
			int statusCode = response.getRawStatusCode();
			if (retryPolicy != null && retryPolicy.retryableStatusCode(statusCode)) {
				byte[] bodyCopy = StreamUtils.copyToByteArray(response.getBody());
				response.close();
				throw new ClientHttpResponseStatusCodeException(serviceName, response,
						bodyCopy);
			}
			return response;
		}, new LoadBalancedRecoveryCallback<ClientHttpResponse, ClientHttpResponse>() {
			// This is a special case, where both parameters to
			// LoadBalancedRecoveryCallback are
			// the same. In most cases they would be different.
			@Override
			protected ClientHttpResponse createResponse(ClientHttpResponse response,
					URI uri) {
				return response;
			}
		});
	}

	private RetryTemplate createRetryTemplate(String serviceName, HttpRequest request,
			LoadBalancedRetryPolicy retryPolicy) {
		RetryTemplate template = new RetryTemplate();
		BackOffPolicy backOffPolicy = this.lbRetryFactory
				.createBackOffPolicy(serviceName);
		template.setBackOffPolicy(
				backOffPolicy == null ? new NoBackOffPolicy() : backOffPolicy);
		template.setThrowLastExceptionOnExhausted(true);
		RetryListener[] retryListeners = this.lbRetryFactory
				.createRetryListeners(serviceName);
		if (retryListeners != null && retryListeners.length != 0) {
			template.setListeners(retryListeners);
		}
		template.setRetryPolicy(!this.lbProperties.isEnabled() || retryPolicy == null
				? new NeverRetryPolicy() : new InterceptorRetryPolicy(request,
						retryPolicy, this.loadBalancer, serviceName));
		return template;
	}

}
