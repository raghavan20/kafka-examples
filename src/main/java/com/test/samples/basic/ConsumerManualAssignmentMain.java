package com.test.samples.basic;

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

/**
 * Consumes records containing key and values which are counters. Assigns a partition of topic to this
 * consumer app. Also controls the offset in the partition to read from when the app is started.
 * <p>
 * See {@link ProducerMain}
 * </p>
 */
public class ConsumerManualAssignmentMain {

  public static final String TOPIC = "topic-test";

  public static void main(String[] args) {
    Properties props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(GROUP_ID_CONFIG, "topic.test.consumer.manual");
    props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

    TopicPartition topicPartition = new TopicPartition(TOPIC, 0);

    // manually assigning partition to this consumer
    consumer.assign(Collections.singleton(topicPartition));

    // if required to process the topic from the beginning rather than using last offset
    consumer.seek(topicPartition, 0);

    while (true) {
      // fetches more than one record if available
      ConsumerRecords<String, String> records = consumer.poll(5000);

      System.out.println(">> Obtained records of size: " + records.count());

      records.forEach(record -> {
        LocalDateTime dateTime = Instant.ofEpochMilli(record.timestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println(dateTime + " " + record.key() + " " + record.value());
      });
    }
  }

}

