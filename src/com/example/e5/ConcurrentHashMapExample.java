package com.example.e5;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Artush on 1/23/2018.
 */
public class ConcurrentHashMapExample {

	static Map<String, AtomicLong> passengers = new ConcurrentHashMap<>();

	static void increase() {
		for (String airport : passengers.keySet()) {
			for (int i = 0; i < 100; i++) {
				passengers.get(airport).getAndIncrement();
			}
		}
	}

	static void decrease() {
		Random random = new Random();
		for (String airport : passengers.keySet()) {
			for (int i = 0; i < random.nextInt(100); i++) {
				passengers.get(airport).getAndDecrement();
			}
		}
	}

	public static void main(String[] args)
			throws InterruptedException {
		passengers.put("EVN", new AtomicLong());
		passengers.put("CDG", new AtomicLong());
		passengers.put("SVO", new AtomicLong());
		passengers.put("LCY", new AtomicLong());
		passengers.put("BJS", new AtomicLong());

		ExecutorService executorService = Executors.newFixedThreadPool(5);
		executorService.submit(ConcurrentHashMapExample::increase);
		executorService.submit(ConcurrentHashMapExample::increase);
		executorService.submit(ConcurrentHashMapExample::increase);
//		executorService.submit(ConcurrentHashMapExample::decrease);
//		executorService.submit(ConcurrentHashMapExample::decrease);

		executorService.awaitTermination(1, TimeUnit.SECONDS);
		executorService.shutdown();

		System.out.println(passengers);
	}
}
