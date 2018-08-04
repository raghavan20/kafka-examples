package com.test.samples;

import org.apache.kafka.clients.producer.Producer;

import java.util.concurrent.CountDownLatch;


public class Commons {

    public static void waitUntilAppIsAskedToQuit(Producer<?, ?> producer) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                producer.close();
                latch.countDown();
            }
        });

        latch.await();
    }
}
