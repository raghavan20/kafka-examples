package com.test.samples.orders;


import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * Schema when deserializing is obtained through KafkaAvroDeserializer from Schema Registry.
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

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(GROUP_ID_CONFIG, "topic.orders.consumer");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        // schema registry is available from Confluent
        props.put("schema.registry.url", "http://localhost:8081");

        KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(TOPIC));

        while (true) {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(3000);
            for (ConsumerRecord order : records) {
                System.out.println("key: " + order.key() + " value: " + order.value());
            }
        }
    }

}
