package top.didasoft.pure.rest.core;

import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;

public class CustomLoadBalancedRetryFactory implements LoadBalancedRetryFactory {

    @Override
    public LoadBalancedRetryPolicy createRetryPolicy(String service, ServiceInstanceChooser serviceInstanceChooser) {
        return new LoadBalancedRetryPolicy() {
            @Override
            public boolean canRetrySameServer(LoadBalancedRetryContext context) {
                return false;
            }

            @Override
            public boolean canRetryNextServer(LoadBalancedRetryContext context) {
                return true;
            }

            @Override
            public void close(LoadBalancedRetryContext context) {

            }

            @Override
            public void registerThrowable(LoadBalancedRetryContext context, Throwable throwable) {

            }

            @Override
            public boolean retryableStatusCode(int statusCode) {
                return false;
            }
        };
    }

    @Override
    public BackOffPolicy createBackOffPolicy(String service) {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(10);
        return fixedBackOffPolicy;
    }
}
