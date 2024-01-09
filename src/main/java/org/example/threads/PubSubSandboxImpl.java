package org.example.threads;

import org.example.Sandbox;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PubSubSandboxImpl implements Sandbox {
    @Override
    public void runner() {
        try {
            PubSubExample.main(new String[]{""});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


class PubSubExample {
    public static void main(String[] args) {
        // Create a publisher
        Publisher publisher = new Publisher();

        // Create subscribers
        Subscriber subscriber1 = new Subscriber("Subscriber 1");
        Subscriber subscriber2 = new Subscriber("Subscriber 2");
        Subscriber subscriber3 = new Subscriber("Subscriber 3");

        // Add subscribers to the publisher
        publisher.subscribe(subscriber1::onMessageReceived);
        publisher.subscribe(subscriber2::onMessageReceived);
        publisher.subscribe(subscriber3::onMessageReceived);

        // Publish messages
        publisher.publish("Hello, Subscribers!");
        publisher.publish("Hello again, Subscribers!");
        publisher.publish("Hello again again, Subscribers!");

        // Delay to allow subscribers to receive messages
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Shutting down subscribers
        subscriber1.shutdown();
        subscriber2.shutdown();
        subscriber3.shutdown();
    }
}

// Publisher class
class Publisher {

    private final List<Consumer<String>> subscribers = new ArrayList<>();

    // Subscribe to messages
    public void subscribe(Consumer<String> subscriber) {
        subscribers.add(subscriber);
    }

    // Method to publish messages to subscribers
    public void publish(String message) {
        for (Consumer<String> subscriber : subscribers) {
            CompletableFuture.runAsync(() -> subscriber.accept(message));
        }
    }
}

// Subscriber class
class Subscriber {

    private final String name;
    private boolean running;

    public Subscriber(String name) {
        this.name = name;
        this.running = true;
        startListening();
    }

    // Method to receive messages
    public void onMessageReceived(String message) {
        if (running) {
            System.out.println(name + " received message: " + message);
        } else {
            System.out.println(name + " is no longer active.");
        }
    }

    // Start listening for messages
    private void startListening() {
        CompletableFuture.runAsync(() -> {
            while (running) {
                // Continue listening for messages
            }
        });
    }

    // Method to stop listening
    public void shutdown() {
        running = false;
    }
}

