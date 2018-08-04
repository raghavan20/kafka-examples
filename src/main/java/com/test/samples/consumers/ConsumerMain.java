package com.test.samples.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Properties;

public class ConsumerMain {

    public static final String TOPIC = "topic-usage-records";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "consumer.main");
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", LongDeserializer.class);

        KafkaConsumer<String, Long> consumer = new KafkaConsumer<>(props);
        // used with automatic assignments
        consumer.subscribe(Collections.singleton(TOPIC));

        while (true) {
            // can fetch more than 1 record
            ConsumerRecords<String, Long> records = consumer.poll(5000);

            System.out.println(">> Obtained records of size: " + records.count());

            records.forEach(record -> {
                LocalDateTime dateTime = Instant.ofEpochMilli(record.timestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                System.out.println(
                        dateTime + " " + record.key() + " " + record.value());
            });
        }
    }

}
