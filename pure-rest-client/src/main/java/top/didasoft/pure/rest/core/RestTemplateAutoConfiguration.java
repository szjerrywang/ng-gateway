package top.didasoft.pure.rest.core;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@AutoConfigureAfter(HttpMessageConvertersAutoConfiguration.class)
@ConditionalOnClass(RestTemplate.class)
public class RestTemplateAutoConfiguration {

	private final ObjectProvider<HttpMessageConverters> messageConverters;

	private final ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers;

	public RestTemplateAutoConfiguration(ObjectProvider<HttpMessageConverters> messageConverters,
			ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers) {
		this.messageConverters = messageConverters;
		this.restTemplateCustomizers = restTemplateCustomizers;
	}

	@Bean
	CustomClientHttpRequestFactorySupplier customClientHttpRequestFactorySupplier() {
		return new CustomClientHttpRequestFactorySupplier();
	}

	@Bean
	@ConditionalOnMissingBean
	public RestTemplateBuilder restTemplateBuilder() {
		RestTemplateBuilder builder = new RestTemplateBuilder();
		HttpMessageConverters converters = this.messageConverters.getIfUnique();
		if (converters != null) {
			builder = builder.messageConverters(converters.getConverters());
		}

		List<RestTemplateCustomizer> customizers = this.restTemplateCustomizers.orderedStream()
				.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(customizers)) {
			builder = builder.customizers(customizers);
		}

		builder = builder.requestFactory(customClientHttpRequestFactorySupplier());
		return builder;
	}

}
