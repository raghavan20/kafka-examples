package com.test.samples.orders;


import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

/**
 * Schema for deserialization is obtained through KafkaAvroDeserializer from Schema Registry.
 * <p>
 * Id of schema is present in the message itself when encoded. Below the value is `21`.
 * <p>
 * ```
 * curl localhost:8081/schemas/ids/21
 * {"schema":"{\"type\":\"record\",\"name\":\"Order\",\"namespace\":\"examples\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"userId\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"int\"}]}"}
 * ```
 */
public class OrderConsumerMain {

    private static final String TOPIC = "topic-orders";

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "topic-orders-consumer");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        // schema registry is available from Confluent
        props.put("schema.registry.url", "http://localhost:8081");

        KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<String, GenericRecord>(props);
        consumer.subscribe(Collections.singleton(TOPIC));

        while (true) {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(3000);
            for (ConsumerRecord order : records) {
                System.out.println("key: " + order.key() + " value: " + order.value());
            }
        }
    }

}
