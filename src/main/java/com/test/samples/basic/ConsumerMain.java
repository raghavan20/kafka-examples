package com.test.samples.basic;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * Consumes records containing key and values which are counters.
 * <p>
 * See {@link ProducerMain}
 * </p>
 */
public class ConsumerMain {

  public static final String TOPIC = "topic-test";

  public static void main(String[] args) {
    Properties props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(GROUP_ID_CONFIG, "topic.test.consumer");
    props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);

    KafkaConsumer<String, Long> consumer = new KafkaConsumer<>(props);
    // Kafka automatically assigns partitions to consumers
    consumer.subscribe(Collections.singleton(TOPIC));

    while (true) {
      // can fetch more than 1 record
      ConsumerRecords<String, Long> records = consumer.poll(5000);

      System.out.println(">> Obtained records of size: " + records.count());

      records.forEach(record -> {
        LocalDateTime dateTime = Instant.ofEpochMilli(record.timestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println(dateTime + " " + record.key() + " " + record.value());
      });
    }
  }

}
