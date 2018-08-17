package com.test.samples.users;

import com.test.samples.users.User;
import com.test.serde.JsonDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class UserConsumerMain {

    public static final String TOPIC = "topic-users";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(GROUP_ID_CONFIG, "topic.users.consumer");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Will be used in deserialising User JSON objects which are stored as value of kafka records
        // Ex: { "id": "user100", "name": "john" }
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props);
        TopicPartition topicPartition = new TopicPartition(TOPIC, 0);
        consumer.assign(Collections.singleton(topicPartition));
        consumer.seek(topicPartition, 0);

        while (true) {
            ConsumerRecords<String, User> userRecords = consumer.poll(5000);

            userRecords.forEach(record -> {
                LocalDateTime dateTime = Instant.ofEpochMilli(record.timestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                System.out.println(
                        dateTime + " " + record.key() + " " + record.value());
            });
        }
    }

}

