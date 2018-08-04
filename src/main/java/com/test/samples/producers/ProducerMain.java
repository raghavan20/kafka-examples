package com.test.samples.producers;

import com.test.samples.Commons;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Continuously generates Kafka records containing sequentially generated keys and values.
 */
public class ProducerMain {

    private static final String TOPIC = "topic-test";
    private static final int PARTITION = 0;

    public static void main(String[] args) throws InterruptedException {
        Properties configs = new Properties();
        configs.put("key.serializer", Serdes.String().serializer().getClass());
        configs.put("value.serializer", Serdes.String().serializer().getClass());
        configs.put("bootstrap.servers", "localhost:9092");

        Producer<String, String> producer = new KafkaProducer<String, String>(configs);

        new Thread() {
            public void run() {
                long counter = 0;

                while (true) {
                    ProducerRecord<String, String> record = new ProducerRecord<>(
                            TOPIC,
                            PARTITION,
                            System.currentTimeMillis(),
                            "record-key" + counter,
                            "record-value" + counter, Collections.emptyList());

                    // sends record asynchronously; does not wait for acknowledgement from Kafka broker (leader of partition)
                    Future<RecordMetadata> future = producer.send(record);

                    counter++;
                    sleepMillis(1000);
                }

            }

            private void sleepMillis(long millis) {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        Commons.waitUntilAppIsAskedToQuit(producer);
    }


}
