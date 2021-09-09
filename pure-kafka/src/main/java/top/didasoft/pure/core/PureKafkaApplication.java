package top.didasoft.pure.core;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.EC2Actions;
import com.amazonaws.auth.policy.actions.IdentityManagementActions;
import com.amazonaws.auth.policy.actions.SecurityTokenServiceActions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;

import com.amazonaws.services.identitymanagement.model.*;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InvalidPartitionsException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@SpringBootConfiguration
@ImportAutoConfiguration(value = {KafkaAutoConfiguration.class})
//@ComponentScan(basePackages = {"top.didasoft.pure.core"})
//@SpringBootApplication
@EnableConfigurationProperties(CommandProperties.class)
public class PureKafkaApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PureKafkaApplication.class);
    private static final long operationTimeout = 30;
    private static final long closeTimeout = 30;

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

    @Autowired
    private CommandProperties commandProperties;


//    @Bean
//    NewTopic testTopic() {
//        return new NewTopic(TOPIC_NAME, 3, (short) 1);
//    }

    //    @Bean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
//    public KafkaStreamsConfiguration defaultKafkaStreamsConfig(Environment environment) {
//        Map<String, Object> streamsProperties = this.kafkaProperties.buildStreamsProperties();
//        if (this.kafkaProperties.getStreams().getApplicationId() == null) {
//            String applicationName = environment.getProperty("spring.application.name");
//            if (applicationName == null) {
//                throw new InvalidConfigurationPropertyValueException("spring.kafka.streams.application-id", null,
//                        "This property is mandatory and fallback 'spring.application.name' is not set either.");
//            }
//            streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName);
//        }
//        return new KafkaStreamsConfiguration(streamsProperties);
//    }
//
//    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME)
//    public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(
//            @Qualifier(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
//                    ObjectProvider<KafkaStreamsConfiguration> streamsConfigProvider) {
//
//        KafkaStreamsConfiguration streamsConfig = streamsConfigProvider.getIfAvailable();
//        if (streamsConfig != null) {
//            return new StreamsBuilderFactoryBean(streamsConfig);
//        }
//        else {
//            throw new UnsatisfiedDependencyException(KafkaStreamsDefaultConfiguration.class.getName(),
//                    KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME, "streamsConfig", "There is no '" +
//                    KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME + "' " + KafkaStreamsConfiguration.class.getName() +
//                    " bean in the application context.\n" +
//                    "Consider declaring one or don't use @EnableKafkaStreams.");
//        }
//    }
//
//    //    @Autowired
////    private DefaultKafkaConsumerFactory<String, String> consumerFactory;
//    @Bean
//    public KStream<?, ?> kStream(StreamsBuilder kStreamBuilder) {
//        KStream<String, String> stream = kStreamBuilder.stream(TOPIC_NAME);
//        KGroupedStream<Object, String> groupedStream = stream.groupBy(new KeyValueMapper<String, String, Object>() {
//            @Override
//            public Object apply(String key, String value) {
//                return key + value.substring(0, 1);
//            }
//        });
//        //groupedStream.aggregate().
//        // Fluent KStream API
//        return stream;
//    }
//
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
//        log.info("container factory class : {}", kafkaListenerContainerFactory.getClass().getCanonicalName());
        String cmdName = commandProperties.getName();

        switch (cmdName == null ? "" : cmdName) {
            case "consume":
                consume();
                break;

            case "checkConsumers":
                getConsumers();
                break;

            case "createInstance":
                createInstance();
                break;

            case "describeInstance":
                describeInstance();
                break;

            case "stopInstance":
                stopInstance();
                break;
            case "terminateInstance":
                terminateInstance();
                break;

            case "createRole":
                createRoleAndInstanceProfile();
                break;

            case "launchUseInstanceProfile":
                launchUseInstanceProfile();
                break;

            case "createTopics":
                createTopics();
                break;

            default:
                log.info("No valid command specified.");
                break;
        }
    }

    private void launchUseInstanceProfile() {
//        AWSCredentials awsCredentials = new BasicAWSCredentials(commandProperties.getAwsAccessKey(), commandProperties.getAwsSecretKey());
//        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        InstanceProfileCredentialsProvider instanceProfileCredentialsProvider = InstanceProfileCredentialsProvider.getInstance();
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = AmazonEC2ClientBuilder.standard();
        AmazonEC2 amazonEC2 = amazonEC2ClientBuilder.withCredentials(instanceProfileCredentialsProvider).withRegion(commandProperties.getDefaultRegion()).build();

        try {
            log.info("Starting instance...");
            TagSpecification tagSpecification = new TagSpecification()
                    .withResourceType(ResourceType.Instance)
                    .withTags(new Tag().withKey("purpose").withValue("scaleup"));
            RunInstancesRequest runRequest = new RunInstancesRequest(commandProperties.getAmiId(), 1, 1)
                    .withInstanceType(InstanceType.T3Large)
                    .withKeyName(commandProperties.getKeyName())
                    .withSecurityGroupIds(commandProperties.getSecurityGroupId())
                    .withSubnetId(commandProperties.getSubnetId())
                    .withTagSpecifications(tagSpecification)
                    .withUserData(Base64.getEncoder().encodeToString(commandProperties.getUserData().getBytes(StandardCharsets.UTF_8)));

            RunInstancesResult runInstancesResult = amazonEC2.runInstances(runRequest);
            Instance instance = runInstancesResult.getReservation().getInstances().get(0);
            log.info("Started instance and getting state");

            DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
                    .withInstanceIds(instance.getInstanceId())
                    .withFilters(new Filter("tag:purpose", Collections.singletonList("scaleup"))

                    );
            DescribeInstancesResult describeInstancesResult = amazonEC2.describeInstances(describeInstancesRequest);
            Instance instanceDescribe = describeInstancesResult.getReservations().get(0).getInstances().get(0);
            InstanceState state = instanceDescribe.getState();


            while (!"running".equals(state.getName())) {
                Thread.sleep(3000);
                describeInstancesResult = amazonEC2.describeInstances(describeInstancesRequest);
                instanceDescribe = describeInstancesResult.getReservations().get(0).getInstances().get(0);
                state = instanceDescribe.getState();
            }
            log.info("Started successfully {}, ip address {}", instanceDescribe.getInstanceId(), instanceDescribe.getPublicIpAddress());
        } catch (Exception e) {
            log.error("Run instance error", e);
        }

    }

    private static final String ROLENAME = "test-ec2All";
    private static final String INSTANCE_PROFILE_NAME = "test-ec2role";

    private void createRoleAndInstanceProfile() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(commandProperties.getAwsAccessKey(), commandProperties.getAwsSecretKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonIdentityManagement iam = AmazonIdentityManagementClient.builder()
                .withRegion(commandProperties.getDefaultRegion())
                .withCredentials(awsStaticCredentialsProvider)
                .build();

        String policyDocument = new Policy().withStatements(
                new Statement(Statement.Effect.Allow)
                        .withActions(SecurityTokenServiceActions.AssumeRole)
                        .withPrincipals(new Principal(Principal.Services.AmazonEC2))
        ).toJson();

        log.info("Policy document: {}", policyDocument);

        String accessPolicyDocument = new Policy().withStatements(
                new Statement(Statement.Effect.Allow)
                        .withActions(EC2Actions.AllEC2Actions, IdentityManagementActions.PassRole
                        , IdentityManagementActions.ListInstanceProfiles
                        )
                        .withResources(new Resource("*"))

        ).toJson();

        log.info("Access policy document: {}", accessPolicyDocument);

        try {

//            CreateRoleResult createRoleResult = iam.createRole(new CreateRoleRequest()
//                    .withRoleName(ROLENAME).withAssumeRolePolicyDocument(policyDocument));
//
//            String arn = createRoleResult.getRole().getArn();
//
//            log.info("Role arn: {}", arn);

            PutRolePolicyResult putRolePolicyResult = iam.putRolePolicy(
                    new PutRolePolicyRequest()
                            .withPolicyName("test-runInstances")
                            .withPolicyDocument(accessPolicyDocument)
                            .withRoleName(ROLENAME));

            CreateInstanceProfileResult instanceProfile = iam.createInstanceProfile(new CreateInstanceProfileRequest()
                    .withInstanceProfileName(INSTANCE_PROFILE_NAME));
            iam.addRoleToInstanceProfile(new AddRoleToInstanceProfileRequest().withInstanceProfileName(INSTANCE_PROFILE_NAME)
                    .withRoleName(ROLENAME));

            log.info("Instance profile arn: {}", instanceProfile.getInstanceProfile().getArn());

        }
        catch (Exception e) {
            log.error("error when call iam", e);
        }


    }

    private void terminateInstance() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(commandProperties.getAwsAccessKey(), commandProperties.getAwsSecretKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = AmazonEC2ClientBuilder.standard();
        AmazonEC2 amazonEC2 = amazonEC2ClientBuilder.withCredentials(awsStaticCredentialsProvider).withRegion(commandProperties.getDefaultRegion()).build();

        try {
            log.info("Terminating instance...");

            TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest()
                    .withInstanceIds(commandProperties.getInstanceId());
//            StopInstancesRequest stopInstancesRequest = new StopInstancesRequest()
//                    .withInstanceIds(commandProperties.getInstanceId());
            TerminateInstancesResult terminateInstancesResult = amazonEC2.terminateInstances(terminateInstancesRequest);
            int size = terminateInstancesResult.getTerminatingInstances().size();

            log.info("Terminated instance number {}", size);




        } catch (Exception e) {
            log.error("Run instance error", e);
        }
    }

    private void stopInstance() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(commandProperties.getAwsAccessKey(), commandProperties.getAwsSecretKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = AmazonEC2ClientBuilder.standard();
        AmazonEC2 amazonEC2 = amazonEC2ClientBuilder.withCredentials(awsStaticCredentialsProvider).withRegion(commandProperties.getDefaultRegion()).build();

        try {
            log.info("Stopping instance...");

            StopInstancesRequest stopInstancesRequest = new StopInstancesRequest()
                    .withInstanceIds(commandProperties.getInstanceId());
            StopInstancesResult stopInstancesResult = amazonEC2.stopInstances(stopInstancesRequest);
            int size = stopInstancesResult.getStoppingInstances().size();

            log.info("Stopping instance number {}", size);




        } catch (Exception e) {
            log.error("Run instance error", e);
        }
    }

    private void describeInstance() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(commandProperties.getAwsAccessKey(), commandProperties.getAwsSecretKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = AmazonEC2ClientBuilder.standard();
        AmazonEC2 amazonEC2 = amazonEC2ClientBuilder.withCredentials(awsStaticCredentialsProvider).withRegion(commandProperties.getDefaultRegion()).build();

        try {
            log.info("Describe instance...");


            DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
                    .withInstanceIds(commandProperties.getInstanceId());
            DescribeInstancesResult describeInstancesResult = amazonEC2.describeInstances(describeInstancesRequest);
            Instance instanceDescribe = describeInstancesResult.getReservations().get(0).getInstances().get(0);
            InstanceState state = instanceDescribe.getState();


//            while (!InstanceStateName.Running.name().equals(state.getName())) {
//                Thread.sleep(3000);
//                describeInstancesResult = amazonEC2.describeInstances(describeInstancesRequest);
//                instanceDescribe = describeInstancesResult.getReservations().get(0).getInstances().get(0);
//                state = instanceDescribe.getState();
//            }
            log.info("Describe successfully {}, ip address {}, state {}", instanceDescribe.getInstanceId(), instanceDescribe.getPublicIpAddress()
                    , instanceDescribe.getState().getName());
            log.info("Running state name {}", InstanceStateName.Running.name());
        } catch (Exception e) {
            log.error("Run instance error", e);
        }

    }

    private void createTopics() {
        NewTopic newTopic = new NewTopic(TOPIC_NAME, 3, (short) 1);

        Set<NewTopic> newTopics = new HashSet<>();
        newTopics.add(newTopic);

        AdminClient adminClient = null;
        try {
            adminClient = AdminClient.create(kafkaProperties.buildAdminProperties());
        } catch (Exception e) {

            log.error("Could not create admin", e);

        }
        if (adminClient != null) {
            try {
                addTopicsIfNeeded(adminClient, newTopics);
            } catch (Exception e) {

                log.error("Could not create or modify topics", e);

            } finally {

                adminClient.close(this.closeTimeout, TimeUnit.SECONDS);
            }
        }
    }

    private void addTopicsIfNeeded(AdminClient adminClient, Collection<NewTopic> topics) {
        if (topics.size() > 0) {
            Map<String, NewTopic> topicNameToTopic = new HashMap<>();
            topics.forEach(t -> topicNameToTopic.compute(t.name(), (k, v) -> v = t));
            DescribeTopicsResult topicInfo = adminClient
                    .describeTopics(topics.stream()
                            .map(NewTopic::name)
                            .collect(Collectors.toList()));
            List<NewTopic> topicsToAdd = new ArrayList<>();
            Map<String, NewPartitions> topicsToModify = checkPartitions(topicNameToTopic, topicInfo, topicsToAdd);
            if (topicsToAdd.size() > 0) {
                addTopics(adminClient, topicsToAdd);
            }
            if (topicsToModify.size() > 0) {
                modifyTopics(adminClient, topicsToModify);
            }
        }
    }

    private Map<String, NewPartitions> checkPartitions(Map<String, NewTopic> topicNameToTopic,
                                                       DescribeTopicsResult topicInfo, List<NewTopic> topicsToAdd) {

        Map<String, NewPartitions> topicsToModify = new HashMap<>();
        topicInfo.values().forEach((n, f) -> {
            NewTopic topic = topicNameToTopic.get(n);
            try {
                TopicDescription topicDescription = f.get(this.operationTimeout, TimeUnit.SECONDS);
                if (topic.numPartitions() < topicDescription.partitions().size()) {
                    if (log.isInfoEnabled()) {
                        log.info(String.format(
                                "Topic '%s' exists but has a different partition count: %d not %d", n,
                                topicDescription.partitions().size(), topic.numPartitions()));
                    }
                } else if (topic.numPartitions() > topicDescription.partitions().size()) {
                    if (log.isInfoEnabled()) {
                        log.info(String.format(
                                "Topic '%s' exists but has a different partition count: %d not %d, increasing "
                                        + "if the broker supports it", n,
                                topicDescription.partitions().size(), topic.numPartitions()));
                    }
                    topicsToModify.put(n, NewPartitions.increaseTo(topic.numPartitions()));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (TimeoutException e) {
                throw new KafkaException("Timed out waiting to get existing topics", e);
            } catch (ExecutionException e) {
                topicsToAdd.add(topic);
            }
        });
        return topicsToModify;
    }

    private void addTopics(AdminClient adminClient, List<NewTopic> topicsToAdd) {
        CreateTopicsResult topicResults = adminClient.createTopics(topicsToAdd);
        try {
            topicResults.all().get(this.operationTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for topic creation results", e);
        } catch (TimeoutException e) {
            throw new KafkaException("Timed out waiting for create topics results", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) { // Possible race with another app instance
                log.debug("Failed to create topics", e.getCause());
            } else {
                log.error("Failed to create topics", e.getCause());
                throw new KafkaException("Failed to create topics", e.getCause()); // NOSONAR
            }
        }
    }

    private void modifyTopics(AdminClient adminClient, Map<String, NewPartitions> topicsToModify) {
        CreatePartitionsResult partitionsResult = adminClient.createPartitions(topicsToModify);
        try {
            partitionsResult.all().get(this.operationTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for partition creation results", e);
        } catch (TimeoutException e) {
            throw new KafkaException("Timed out waiting for create partitions results", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof InvalidPartitionsException) { // Possible race with another app instance
                log.debug("Failed to create partitions", e.getCause());
            } else {
                log.error("Failed to create partitions", e.getCause());
                if (!(e.getCause() instanceof UnsupportedVersionException)) {
                    throw new KafkaException("Failed to create partitions", e.getCause()); // NOSONAR
                }
            }
        }
    }


    private void createInstance() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(commandProperties.getAwsAccessKey(), commandProperties.getAwsSecretKey());
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = AmazonEC2ClientBuilder.standard();
        AmazonEC2 amazonEC2 = amazonEC2ClientBuilder.withCredentials(awsStaticCredentialsProvider).withRegion(commandProperties.getDefaultRegion()).build();

        try {
            IamInstanceProfileSpecification iamInstanceProfileSpecification = new IamInstanceProfileSpecification().withName("MYInstanceProfile");
            log.info("Starting instance...");
            TagSpecification tagSpecification = new TagSpecification()
                    .withResourceType(ResourceType.Instance)
                    .withTags(new Tag().withKey("purpose").withValue("scaleup"));
            RunInstancesRequest runRequest = new RunInstancesRequest(commandProperties.getAmiId(), 1, 1)
                    .withInstanceType(InstanceType.T3Large)
                    .withKeyName(commandProperties.getKeyName())
                    .withSecurityGroupIds(commandProperties.getSecurityGroupId())
                    .withSubnetId(commandProperties.getSubnetId())
                    .withTagSpecifications(tagSpecification)
                    .withIamInstanceProfile(iamInstanceProfileSpecification)
                    .withUserData(Base64.getEncoder().encodeToString(commandProperties.getUserData().getBytes(StandardCharsets.UTF_8)));

            RunInstancesResult runInstancesResult = amazonEC2.runInstances(runRequest);
            Instance instance = runInstancesResult.getReservation().getInstances().get(0);
            log.info("Started instance and getting state");

            DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
                    .withInstanceIds(instance.getInstanceId());
            DescribeInstancesResult describeInstancesResult = amazonEC2.describeInstances(describeInstancesRequest);
            Instance instanceDescribe = describeInstancesResult.getReservations().get(0).getInstances().get(0);
            InstanceState state = instanceDescribe.getState();


            while (!"running".equals(state.getName())) {
                Thread.sleep(3000);
                describeInstancesResult = amazonEC2.describeInstances(describeInstancesRequest);
                instanceDescribe = describeInstancesResult.getReservations().get(0).getInstances().get(0);
                state = instanceDescribe.getState();
            }
            log.info("Started successfully {}, ip address {}", instanceDescribe.getInstanceId(), instanceDescribe.getPublicIpAddress());
        } catch (Exception e) {
            log.error("Run instance error", e);
        }
    }

    private void consume() {
        MessageListenerContainer container = kafkaListenerContainerFactory.createContainer(TOPIC_NAME);

        container.setupMessageListener(new MessageListener<String, String>() {
            @Override
            public void onMessage(ConsumerRecord<String, String> data) {
                log.info("received: {}", data.toString());
            }


        });

        container.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Shutdown hook is running, closing container...");
                if (container != null) {
                    //
                    container.stop();
                }
            }
        });

//        Thread.sleep(10000);
//        getConsumers();
//        Thread.sleep(5000);
//
//        container.stop();
    }

    private void getConsumers() {
        AdminClient adminClient = null;
        try {
            adminClient = AdminClient.create(kafkaProperties.buildAdminProperties());
        } catch (Exception e) {

            log.error("Could not create admin", e);

        }
        if (adminClient != null) {
            try {
                int assignedPartition = 0;
                DescribeConsumerGroupsResult describeConsumerGroupsResult = adminClient.describeConsumerGroups(Collections.singletonList(kafkaProperties.getConsumer().getGroupId()));
                Map<String, ConsumerGroupDescription> groupDescriptionMap = describeConsumerGroupsResult.all().get();
                for (ConsumerGroupDescription consumerGroupDescription : groupDescriptionMap.values()) {
                    log.info("Consumer group members: {}", consumerGroupDescription.members().toString());
                    for (MemberDescription memberDescription : consumerGroupDescription.members()) {
                        Set<TopicPartition> topicPartitions = memberDescription.assignment().topicPartitions();
                        for (TopicPartition topicPartition : topicPartitions) {
                            if (TOPIC_NAME.equals(topicPartition.topic())) {
                                assignedPartition++;
                                log.info("Partition: {}", topicPartition.partition());
                            }
                        }
                    }
                }
            } catch (Exception e) {

                log.error("Could not get consumers", e);

            } finally {

                adminClient.close(this.closeTimeout, TimeUnit.SECONDS);
            }
        }
    }
}
