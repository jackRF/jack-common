// package com.sitco.point;

// import java.util.HashMap;
// import java.util.Map;

// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.clients.producer.ProducerConfig;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.apache.kafka.common.serialization.StringSerializer;
// import org.kie.soup.commons.util.Maps;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.annotation.EnableKafka;
// import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
// import org.springframework.kafka.config.KafkaListenerContainerFactory;
// import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
// import org.springframework.kafka.core.DefaultKafkaProducerFactory;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.core.ProducerFactory;
// import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

// @EnableKafka
// @Configuration
// public class KafkaConfig{
//     @Value("${spring.kafka.bootstrap-servers}")
//     private String bootstrapServers;
//     @Value("${spring.kafka.consumer.enable-auto-commit}")
//     private Boolean autoCommit;
//     @Value("${spring.kafka.consumer.auto-commit-interval}")
//     private Integer autoCommitInterval;
//     @Value("${spring.kafka.consumer.group-id}")
//     private String groupId;
//     @Value("${spring.kafka.consumer.max-poll-records}")
//     private Integer maxPollRecords;
//     @Value("${spring.kafka.consumer.auto-offset-reset}")
//     private String autoOffsetReset;

//     @Value("${spring.kafka.producer.acks}")
//     private String acks;
//     @Value("${spring.kafka.producer.retries}")
//     private Integer retries;
//     @Value("${spring.kafka.producer.batch-size}")
//     private Integer batchSize;
//     @Value("${spring.kafka.producer.buffer-memory}")
//     private Integer bufferMemory;

//     /**
//      *  生产者配置信息
//      */
//     @Bean
//     public Map<String, Object> producerConfigs() {
//         Map<String, Object> props = new HashMap<String, Object>();
//         props.put(ProducerConfig.ACKS_CONFIG, acks);
//         props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//         props.put(ProducerConfig.RETRIES_CONFIG, retries);
//         props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
//         props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
//         props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
//         props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//         props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//         return props;
//     }
//     /**
//      *  生产者工厂
//      */
//     @Bean
//     public ProducerFactory<String, String> producerFactory() {
//         return new DefaultKafkaProducerFactory<>(producerConfigs());
//     }

//     /**
//      *  生产者模板
//      */
//     @Bean
//     public KafkaTemplate<String, String> kafkaTemplate() {
//         return new KafkaTemplate<>(producerFactory());
//     }
//     @Bean
//     KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> batchFactory() {
//         ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//         factory.setConsumerFactory(new DefaultKafkaConsumerFactory<String, String>(consumerConfigs()));
//         factory.setConcurrency(1);
//         factory.setBatchListener(true);
//         factory.getContainerProperties().setPollTimeout(3000);
//         return factory;
//     }

//    @Bean
//     public Map<String, Object> consumerConfigs() {
//         Map<String, Object> propsMap = new HashMap<>();
//         propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//         propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//         propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
//         propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
//         propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
//         propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
//         propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
//         propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
//         return propsMap;
//     }
// }