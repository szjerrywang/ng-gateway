package top.didasoft.ibmmq.cli.commands;

import com.fasterxml.jackson.databind.JavaType;
import org.apache.kafka.common.header.Headers;

public interface Jackson2JavaTypeMapper extends ClassMapper {

	/**
	 * The precedence for type conversion - inferred from the method parameter or message
	 * headers. Only applies if both exist.
	 */
	enum TypePrecedence {
		INFERRED, TYPE_ID
	}

	void fromJavaType(JavaType javaType, Headers headers);

	JavaType toJavaType(Headers headers);

	TypePrecedence getTypePrecedence();

	/**
	 * Set the precedence for evaluating type information in message properties.
	 * When using {@code @KafkaListener} at the method level, the framework attempts
	 * to determine the target type for payload conversion from the method signature.
	 * If so, this type is provided by the {@code MessagingMessageListenerAdapter}.
	 * <p> By default, if the type is concrete (not abstract, not an interface), this will
	 * be used ahead of type information provided in the {@code __TypeId__} and
	 * associated headers provided by the sender.
	 * <p> If you wish to force the use of the  {@code __TypeId__} and associated headers
	 * (such as when the actual type is a subclass of the method argument type),
	 * set the precedence to {@link Jackson2JavaTypeMapper.TypePrecedence#TYPE_ID}.
	 * @param typePrecedence the precedence.
	 * @since 2.2
	 */
	default void setTypePrecedence(TypePrecedence typePrecedence) {
		throw new UnsupportedOperationException("This mapper does not support this method");
	}

	void addTrustedPackages(String... packages);

	/**
	 * Remove the type information headers.
	 * @param headers the headers.
	 * @since 2.2
	 */
	default void removeHeaders(Headers headers) {
		// NOSONAR
	}

}
