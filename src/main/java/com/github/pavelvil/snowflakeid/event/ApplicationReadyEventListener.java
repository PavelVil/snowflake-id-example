package com.github.pavelvil.snowflakeid.event;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Profile("generator-test")
public class ApplicationReadyEventListener {

    private static final String SERVER_URL = "http://localhost:8080/api/v1/id/generator/next-id";

    private static final int NUM_THREADS = 30;

    private static final int NUM_REQUESTS = 10_000;

    private final RestTemplate restTemplate;

    public ApplicationReadyEventListener() {
        this.restTemplate = new RestTemplate();
    }

    @EventListener
    public void onEvent(ApplicationReadyEvent event) throws Exception {
        Set<Long> uniqueIds = Collections.synchronizedSet(new HashSet<>());

        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.execute(() -> {
                for (int j = 0; j < NUM_REQUESTS; j++) {
                    long id = getNextId();
                    if (!uniqueIds.add(id)) {
                        System.out.println("Duplicate ID found: " + id);
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("Total unique IDs: " + uniqueIds.size());
        System.out.println("Total requests: " + (NUM_THREADS * NUM_REQUESTS));
    }

    private Long getNextId() {
        return restTemplate.getForObject(SERVER_URL, Long.class);
    }

}
