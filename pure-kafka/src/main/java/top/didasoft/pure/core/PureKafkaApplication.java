package top.didasoft.pure.core;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@SpringBootConfiguration
@ImportAutoConfiguration(value = {KafkaAutoConfiguration.class})
//@ComponentScan(basePackages = {"top.didasoft.pure.core"})
//@SpringBootApplication
//@EnableConfigurationProperties(ServerProperties.class)
public class PureKafkaApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PureKafkaApplication.class);

    public static void main(String args[]) {
        SpringApplication.run(PureKafkaApplication.class, args);
    }

    public static void pause(long timeInMilliSeconds) {

        long timestamp = System.currentTimeMillis();


        do {

        } while (System.currentTimeMillis() < timestamp + timeInMilliSeconds);

    }

    private static final String TOPIC_NAME = "testPure";
    private static final String CLIENT_ID = "testClient";
    private static final String CONSUMER_GROUP = "testGroup";

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    NewTopic testTopic() {
        return new NewTopic(TOPIC_NAME, 3, (short) 1);
    }

    @Bean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig(Environment environment) {
        Map<String, Object> streamsProperties = this.kafkaProperties.buildStreamsProperties();
        if (this.kafkaProperties.getStreams().getApplicationId() == null) {
            String applicationName = environment.getProperty("spring.application.name");
            if (applicationName == null) {
                throw new InvalidConfigurationPropertyValueException("spring.kafka.streams.application-id", null,
                        "This property is mandatory and fallback 'spring.application.name' is not set either.");
            }
            streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName);
        }
        return new KafkaStreamsConfiguration(streamsProperties);
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME)
    public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(
            @Qualifier(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
                    ObjectProvider<KafkaStreamsConfiguration> streamsConfigProvider) {

        KafkaStreamsConfiguration streamsConfig = streamsConfigProvider.getIfAvailable();
        if (streamsConfig != null) {
            return new StreamsBuilderFactoryBean(streamsConfig);
        }
        else {
            throw new UnsatisfiedDependencyException(KafkaStreamsDefaultConfiguration.class.getName(),
                    KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME, "streamsConfig", "There is no '" +
                    KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME + "' " + KafkaStreamsConfiguration.class.getName() +
                    " bean in the application context.\n" +
                    "Consider declaring one or don't use @EnableKafkaStreams.");
        }
    }

    //    @Autowired
//    private DefaultKafkaConsumerFactory<String, String> consumerFactory;
    @Bean
    public KStream<?, ?> kStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> stream = kStreamBuilder.stream(TOPIC_NAME);
        KGroupedStream<Object, String> groupedStream = stream.groupBy(new KeyValueMapper<String, String, Object>() {
            @Override
            public Object apply(String key, String value) {
                return key + value.substring(0, 1);
            }
        });
        //groupedStream.aggregate().
        // Fluent KStream API
        return stream;
    }

    @Autowired
    private KafkaListenerContainerFactory kafkaListenerContainerFactory;

    private volatile boolean isQuit = false;

    @Override
    public void run(String... args) throws Exception {

//        Consumer<String, String> consumer = consumerFactory.createConsumer(CONSUMER_GROUP, CLIENT_ID);
//        consumer.subscribe(Collections.singleton(TOPIC_NAME));
//
//        while(!isQuit) {
//            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
//        }
        log.info("container factory class : {}", kafkaListenerContainerFactory.getClass().getCanonicalName());


        MessageListenerContainer container = kafkaListenerContainerFactory.createContainer(TOPIC_NAME);

        container.setupMessageListener(new MessageListener<String, String>() {
            @Override
            public void onMessage(ConsumerRecord<String, String> data) {
                log.info("received: {}", data.toString());
            }


        });

        container.start();

        Thread.sleep(5000);

        container.stop();
    }
}
