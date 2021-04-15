package top.didasoft.ibmmq.cli.commands;

import org.apache.kafka.common.header.Headers;

public interface ClassMapper {

	void fromClass(Class<?> clazz, Headers headers);

	Class<?> toClass(Headers headers);

}