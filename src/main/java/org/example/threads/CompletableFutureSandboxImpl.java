package org.example.threads;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.example.Sandbox;

public class CompletableFutureSandboxImpl implements Sandbox {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void runner() {
        try {
            matrixSum(5000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void asyncTest() {
        Supplier<String> task = () -> {
            // Simulate a long-running task
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.printf("Looping in thread: %s%n", i);
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Result of the long-running task";
        };

        CompletableFuture<String> future = CompletableFuture.supplyAsync(task);
        future.thenAccept(result -> System.out.println(result));
        System.out.println("Main thread can do other tasks while waiting for the completion. The main thread can also finish before the child thread, resulting in the possibility of incomplete tasks.");
//        Thread.sleep(5000);
        System.out.println("Done");
    }

    public void matrixSum(int matrixSize) throws Exception {
        // Create matrix
        List<List<Integer>> matrix = new ArrayList<>();
        for (int i = 1; i <= matrixSize; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < matrixSize; j++) {
                row.add(i);
            }
            matrix.add(row);
        }

        // Time sequential summation
        Instant start = Instant.now();
        int sum = 0;
        for (List<Integer> row : matrix) {
            for (Integer num : row) {
                sum += num;
            }
        }
        LOGGER.info("Sequential Sum: [{}], Time: [{}]", sum, Duration.between(start, Instant.now()).toMillis());

        // Threaded sum
        start = Instant.now();
        ExecutorService executorService = Executors.newFixedThreadPool(matrix.size());
        CompletableFuture<Long>[] tasks = new CompletableFuture[matrix.size()];

        // Calculate sum of each row asynchronously
        for (int i = 0; i < matrix.size(); i++) {
            final int finalI = i;
            tasks[i] = CompletableFuture.supplyAsync(() -> {
                long rowSum = 0;
                for (Integer num : matrix.get(finalI)) {
                    rowSum += num;
                }
                return rowSum;
            }, executorService);
        }

        // Wait for all futures to complete and calculate the total sum
        long totalSum = CompletableFuture.allOf(tasks)
                .thenApply(v -> {
                    long runSum = 0;
                    for (CompletableFuture<Long> task : tasks) {
                        try {
                            runSum += task.get(); // Get the result from each CompletableFuture
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    return runSum;
                }).get();

        executorService.shutdown();

        LOGGER.info("CompletableFuture Sum: [{}], Time: [{}]", totalSum, Duration.between(start, Instant.now()).toMillis());
    }
}
