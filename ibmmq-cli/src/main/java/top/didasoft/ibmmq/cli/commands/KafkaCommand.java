package top.didasoft.ibmmq.cli.commands;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.didasoft.ibmmq.cli.CommandRunnable;
import top.didasoft.ibmmq.cli.model.TradeEntryDO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

@Command(name = "kafka", description = "A command that test kafka")
public class KafkaCommand implements CommandRunnable {

    private static final Logger log = LoggerFactory.getLogger(KafkaCommand.class);

    @Option(name = {"-v", "--verbose"}, description = "Enables verbose mode")
    protected boolean verbose = false;

    @Option(name = {"--broker"}, title = "Broker", arity = 1, description = "Kafka Broker")
    protected String broker = "127.0.0.1:9092";

    @Option(name = {"--topic"}, title = "Topic", arity = 1, description = "Kafka Topic name")
    protected String topic = "myevent";

    @Option(name = {"--key"}, title = "Key", arity = 1, description = "Kafka Message Key")
    protected String key;

    @Option(name = {"--numPartition"}, title = "Number of partition", arity = 1, description = "Number of partition")
    protected int numPartition = 6;

    @Option(name = {"--replFactor"}, title = "Replfactor", arity = 1, description = "Replication Factor")
    protected short replFactor = (short) 1;

    @Option(name = {"--groupName"}, title = "ConsumerGroupName", arity = 1, description = "Consumer Group Name")
    protected String groupName;

    public enum Operation {query, createTopic, produceMsg, consumer};

    @Option(name = {"--auto-commit"}, title = "Autocommit", description = "enable or disable auto commit")
    protected boolean autoCommit = false;


    @Option(name = {"--operation"}, title = "Operation", arity = 1, description = "Specify kafka operation")
    protected Operation operation = Operation.query;


    @Override
    public int run() {
        switch (operation) {
            case query:
                return query();
            case createTopic:
                return createTopic();
            case produceMsg:
                return produceMsg();
            case consumer:
                return consumer();
        }
        return 0;
    }

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private int consumer() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", broker);
        props.setProperty("group.id", groupName);
        props.setProperty("enable.auto.commit", Boolean.toString(autoCommit));
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "top.didasoft.ibmmq.cli.commands.JsonDeserializer");
        final KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(props);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Shutdown hook is running...");
                if (consumer != null) {
                    closed.set(true);
                    consumer.wakeup();
                }
            }
        });

        final Map<Integer, Long> offsets = new HashMap<>();

        try {
            consumer.subscribe(Arrays.asList(topic));
            while (!closed.get()) {
                for (Map.Entry<Integer, Long> offset : offsets.entrySet()) {
                    consumer.seek(new TopicPartition(topic, offset.getKey()), offset.getValue());
                }
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(10000));
                offsets.clear();
                for (ConsumerRecord<String, Object> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
//                try {
//                    Thread.sleep(0);
//                } catch (InterruptedException e) {
//                    log.error("Interrupted", e);
//                    break;
//                }
                    offsets.put(record.partition(), record.offset());
                }
            }
        } catch (WakeupException e) {
            // Ignore exception if closing
            if (!closed.get()) throw e;
        } catch (Exception e) {

            log.error("Exception", e);
            if (!closed.get()) throw e;
        } finally {
            log.info("Close consumer...");
            consumer.close();
        }

        return 0;
    }

    private int produceMsg() {


        String entryId = RandomStringUtils.randomAlphanumeric(16);
        if (key == null) {
            key = RandomStringUtils.randomNumeric(12);
        }
        TradeEntryDO tradeEntry = new TradeEntryDO();


        tradeEntry.setTradeEntryId(entryId);
        tradeEntry.setTradeDate(LocalDate.now());
        tradeEntry.setExecutionTime(LocalDateTime.now());
        tradeEntry.setPrimaryAccountNo(key);

        Properties props = new Properties();
        props.put("bootstrap.servers", broker);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "top.didasoft.ibmmq.cli.commands.JsonSerializer");

        Producer<String, Object> producer = new KafkaProducer<>(props);

        try {

            ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, key, tradeEntry);
            producerRecord.headers().add(AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME, TradeEntryDO.class.getCanonicalName().getBytes(StandardCharsets.UTF_8));
            Future<RecordMetadata> recordMetadataFuture = producer.send(producerRecord);

            RecordMetadata recordMetadata = recordMetadataFuture.get();

            log.info("Record sent. Partition {}, Offset {}, Timestamp {}", recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
        } catch (Exception e) {
            log.error("send error", e);
            return -1;
        } finally {
            producer.close();
        }
        return 0;
    }

    private int createTopic() {
        Map<String, Object> conf = new HashMap<>();

        conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, broker);

        Admin admin = Admin.create(conf);

        try {
            CreateTopicsResult topicsResult = admin.createTopics(Collections.singletonList(new NewTopic(topic, numPartition, replFactor)));

            Set<String> names = topicsResult.values().keySet();
            for (String name : names) {

                log.info("Topic {} created. partition {}, replifactor {}", name, topicsResult.numPartitions(name).get()
                        , topicsResult.replicationFactor(name).get());

            }
        } catch (Exception e) {
            log.error("Kafka exception", e);
            return -1;
        } finally {
            admin.close();
        }
        //log.info("Result: {}", topicsResult.toString());
        return 0;
    }

    private int query() {

        Map<String, Object> conf = new HashMap<>();

        conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, broker);

        Admin admin = Admin.create(conf);

        final Charset charset = StandardCharsets.UTF_8;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = null;


        try {
            DescribeClusterResult describeClusterResult = admin.describeCluster();
            String clusterId = describeClusterResult.clusterId().get();
            log.info("Cluster ID: {}", clusterId);

            ListTopicsResult listTopicsResult = admin.listTopics();
            Set<String> topicNames = listTopicsResult.names().get();
            Map<String, TopicListing> stringTopicListingMap = listTopicsResult.namesToListings().get();


            ps = new PrintStream(baos, true, charset.name());

            MapUtils.debugPrint(ps, null, stringTopicListingMap);

            log.info("Topics: {}", baos.toString());

            DescribeTopicsResult describeTopicsResult = admin.describeTopics(topicNames);
            Map<String, TopicDescription> topics = describeTopicsResult.all().get();

            ps.close();
            baos.reset();

            ps = new PrintStream(baos, true, charset.name());

            MapUtils.debugPrint(ps, null, topics);

            log.info("Topic info: {}", baos.toString());


            for (TopicDescription topicDescription : topics.values()) {

                Set<TopicPartition> topicPartitions = new HashSet<>();
                for (TopicPartitionInfo topicPartitionInfo : topicDescription.partitions()) {
                    topicPartitions.add(new TopicPartition(topicDescription.name(), topicPartitionInfo.partition()));
                }
            }


            //new ListConsumerGroupsOptions(new HashSet<ConsumerGroupState>());

            ListConsumerGroupsResult listConsumerGroupsResult = admin.listConsumerGroups();

            Collection<ConsumerGroupListing> consumerGroupListings = listConsumerGroupsResult.all().get();

            for (ConsumerGroupListing consumerGroupListing : consumerGroupListings) {
                log.info("Consumer group {}", consumerGroupListing.groupId());

                queryOffsets(admin, topic, consumerGroupListing.groupId());

                DescribeConsumerGroupsResult describeConsumerGroupsResult = admin.describeConsumerGroups(Collections.singletonList(consumerGroupListing.groupId()));
                Map<String, ConsumerGroupDescription> consumerGroupDescriptionMap = describeConsumerGroupsResult.all().get();
                ps.close();
                baos.reset();

                ps = new PrintStream(baos, true, charset.name());

                MapUtils.debugPrint(ps, null, consumerGroupDescriptionMap);

                log.info("Consumer group: {}", baos.toString());


                for (ConsumerGroupDescription consumerGroupDescription : consumerGroupDescriptionMap.values()) {
                    for (MemberDescription memberDescription : consumerGroupDescription.members()) {
                        Set<TopicPartition> topicPartitions = memberDescription.assignment().topicPartitions();
                        ListConsumerGroupOffsetsOptions listConsumerGroupOffsetsOptions = new ListConsumerGroupOffsetsOptions();
                        listConsumerGroupOffsetsOptions.topicPartitions(new ArrayList<>(topicPartitions));
                        ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = admin.listConsumerGroupOffsets(consumerGroupListing.groupId(), listConsumerGroupOffsetsOptions);

                        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();

                        ps.close();
                        baos.reset();

                        ps = new PrintStream(baos, true, charset.name());

                        MapUtils.debugPrint(ps, null, topicPartitionOffsetAndMetadataMap);

                        log.info("Consumer group offset: {}", baos.toString());
                    }
                }
//                admin.describe
            }

        } catch (Exception e) {
            log.error("Kafka exception", e);
            return -1;
        } finally {
            if (ps != null) {
                ps.close();
            }
            try {
                baos.close();
            } catch (IOException e) {
                //ignore
            }
            admin.close();
        }

        return 0;
    }

    private void queryOffsets(Admin admin, String topicName, String consumerGroup) throws ExecutionException, InterruptedException {
        DescribeTopicsResult describeTopicsResult = admin.describeTopics(Collections.singletonList(topicName));
        Map<String, TopicDescription> topics = describeTopicsResult.all().get();

        TopicDescription topicDescription = topics.get(topicName);

        if (topicDescription == null) {
            log.error("Topic not exists. {}", topicName);
            return;
        }

        Map<TopicPartition, OffsetSpec> offsetSpecHashMap = new HashMap<>();

        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (TopicPartitionInfo topicPartitionInfo : topicDescription.partitions()) {
            TopicPartition topicPartition = new TopicPartition(topicDescription.name(), topicPartitionInfo.partition());
            offsetSpecHashMap.put(topicPartition, OffsetSpec.latest());
            topicPartitions.add(topicPartition);
        }
        ListOffsetsResult listOffsetsResult = admin.listOffsets(offsetSpecHashMap, new ListOffsetsOptions());
        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> offsetsResultInfoMap = listOffsetsResult.all().get();

        for (Map.Entry<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> entry : offsetsResultInfoMap.entrySet()) {
            log.info("Topic {}, Partition {}, Latest Offset {}"
                    , entry.getKey().topic(), entry.getKey().partition(), entry.getValue().offset());

        }
        ListConsumerGroupOffsetsOptions listConsumerGroupOffsetsOptions = new ListConsumerGroupOffsetsOptions();
        listConsumerGroupOffsetsOptions.topicPartitions(topicPartitions);
        ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = admin.listConsumerGroupOffsets(consumerGroup, listConsumerGroupOffsetsOptions);
        Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();
        for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsetAndMetadataMap.entrySet()) {
            log.info("Topic {}, Partition {}, Consumer Group Offset {}"
                    , entry.getKey().topic(), entry.getKey().partition(), entry.getValue().offset()
            );
        }


    }
}
