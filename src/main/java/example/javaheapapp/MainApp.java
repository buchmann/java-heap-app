package com.example.javaheapapp;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class MainApp {
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }
}

@RestController
class HeapLoadController {

    private static HashMap<Object, Object> myMap = new HashMap<>();
    private static List<Object> garbageList = new ArrayList<>();
    private static int counter = 0;
    private static int gcLoadPercentage = 30; // Simulate a high GC load
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final Random random = new Random();

    @PostConstruct
    public void startHeapSimulation() {
        // Schedule memory allocation and garbage load simulation every 2 seconds
        scheduler.scheduleAtFixedRate(this::increaseHeapLoad, 0, 10, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::simulateGarbageCollectorStruggle, 0, 15, TimeUnit.SECONDS);
    }

    @GetMapping("/status")
    public String status() {
        return String.format("Current Heap Entries: %d, Garbage Objects: %d", myMap.size(), garbageList.size());
    }

    private void increaseHeapLoad() {
        // Add large strings to the map, simulating memory load
        for (int i = 0; i < 100; i++) {
            String largeString = "LargeString" + new String(new char[1000000]).replace('\0', 'g') + counter;
            myMap.put("key" + counter, largeString);
            counter++;
        }
        System.out.println("Added large data to heap, Map size: " + myMap.size());
    }

    private void simulateGarbageCollectorStruggle() {
        // Continuously add and remove objects to simulate GC load
        for (int i = 0; i < gcLoadPercentage * 100; i++) {
            Object garbage = new Object();
            garbageList.add(garbage);
        }

        // Remove half of the garbage to create pressure on the garbage collector
        if (garbageList.size() > 10000) {
            garbageList.subList(0, garbageList.size() / 2).clear();
            System.gc(); // Suggest GC after clearing some objects
        }

        System.out.println("Simulating garbage collection load, Garbage list size: " + garbageList.size());
    }

    @GetMapping("/setGcLoad")
    public String setGcLoad(@RequestParam(value = "percentage", defaultValue = "30") int gcLoadPercentage) {
        if (gcLoadPercentage < 0 || gcLoadPercentage > 100) {
            return "GC load percentage must be between 0 and 100.";
        }
        this.gcLoadPercentage = gcLoadPercentage;
        return String.format("Garbage collection load set to %d%%.", gcLoadPercentage);
    }

    @GetMapping("/releaseMemory")
    public String releaseMemory() {
        // Clear half of the heap load to free up space
        if (myMap.size() > 100) {
            int sizeBefore = myMap.size();
            int toRemove = myMap.size() / 2;
            for (int i = 0; i < toRemove; i++) {
                myMap.remove("key" + i);
            }
            System.gc(); // Suggest garbage collection after clearing
            return String.format("Released %d entries from heap. New size: %d", sizeBefore - toRemove, myMap.size());
        }
        return "Not enough entries to release.";
    }
}

