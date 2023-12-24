package org.example.threads;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CompletableFutureSandbox {
    public void asyncTest() throws InterruptedException {
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
}
