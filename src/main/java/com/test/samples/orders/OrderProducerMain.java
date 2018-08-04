package com.test.samples.orders;


import com.test.samples.Commons;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Schema for Orders is registered with Avro Schema Registry through KafkaAvroSerializer
 * <p>
 * ```
 * curl -s localhost:8081/subjects/topic-orders-value/versions/1 | jq .
 * {
 * "subject": "topic-orders-value",
 * "version": 1,
 * "id": 21,
 * "schema": "{\"type\":\"record\",\"name\":\"Order\",\"namespace\":\"examples\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"userId\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"int\"}]}"
 * }```
 * </p>
 */
public class OrderProducerMain {

    private static final String TOPIC = "topic-orders";
    private static final String ORDER_SCHEMA_FILE = "src/main/resources/order.schema";
    private static final List<String> userIds = Arrays.asList("user1", "user2", "user3", "user4", "user5");
    private static final AtomicInteger orderCounter = new AtomicInteger(1);
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Does binary encoding with Avro
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("schema.registry.url", "http://localhost:8081");


        Schema schema = new Schema.Parser().parse(new File(ORDER_SCHEMA_FILE));
        KafkaProducer<String, GenericRecord> producer = new KafkaProducer<String, GenericRecord>(props);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    GenericRecord order = generateOrder(schema);
                    String orderId = order.get("orderId").toString();
                    System.out.println("Creating new order with order id: " + orderId);
                    producer.send(new ProducerRecord<String, GenericRecord>(TOPIC, orderId, order));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "producer-thread").start();

        Commons.waitUntilAppIsAskedToQuit(producer);

    }

    private static GenericRecord generateOrder(Schema schema) {
        // each records holds Avro schema for Order
        GenericRecord order = new GenericData.Record(schema);
        order.put("orderId", "order-" + orderCounter.getAndIncrement());
        order.put("userId", userIds.get(random.nextInt(userIds.size()))); // max bound is not included
        order.put("value", random.nextInt(100));
        return order;
    }
}
