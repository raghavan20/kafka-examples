package com.test.samples.basic;

import com.test.samples.Commons;
import com.test.samples.basic.ConsumerMain;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Future;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

/**
 * Continuously generates Kafka records containing sequentially generated keys and values.
 * <p>
 * Also see {@link ConsumerMain}
 */
public class ProducerMain {

  private static final String TOPIC = "topic-test";
  private static final int PARTITION = 0;

  public static void main(String[] args) throws InterruptedException {
    Properties configs = new Properties();
    configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configs.put(KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
    configs.put(VALUE_SERIALIZER_CLASS_CONFIG, Serdes.Long().serializer().getClass());

    Producer<String, Long> producer = new KafkaProducer<>(configs);

    new Thread() {
      public void run() {
        long counter = 0;

        while (true) {
          ProducerRecord<String, Long> record = new ProducerRecord<>(
              TOPIC,
              PARTITION,
              System.currentTimeMillis(),
              "key" + counter,
              counter, Collections.emptyList());

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
