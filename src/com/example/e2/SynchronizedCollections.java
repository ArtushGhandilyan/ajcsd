package com.example.e2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Artush on 1/17/2018.
 * Compare speed of syncronized list and CopyOnWriteArrayList
 */
public class SynchronizedCollections {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		List<Integer> list1 = Collections.synchronizedList(new ArrayList<>());
		List<Integer> list2 = new CopyOnWriteArrayList<>();

		fillList(list1, 100);
		fillList(list2, 100);

		System.out.println("List synchronized:");
		checkList(list1);

		System.out.println("CopyOnWriteArrayList:");
		checkList(list2);
	}

	private static void checkList(List<Integer> list) throws ExecutionException, InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);

		ExecutorService ex = Executors.newFixedThreadPool(2);
		Future<Long> f1 = ex.submit(new ListRunner(0, 50, list, latch));
		Future<Long> f2 = ex.submit(new ListRunner(50, 100, list, latch));

		latch.countDown();

		System.out.println("Thread 1: " + f1.get() / 1000);
		System.out.println("Thread 2: " + f2.get() / 1000);
	}

	private static void fillList(List<Integer> list, int count) {
		for (int i = 0; i < count; i++) {
			list.add(i);
		}
	}

	private static class ListRunner implements Callable<Long> {
		int start;
		int end;
		List<Integer> list;
		CountDownLatch latch;

		public ListRunner(int start, int end, List<Integer> list, CountDownLatch latch) {
			this.start = start;
			this.end = end;
			this.list = list;
			this.latch = latch;
		}

		@Override
		public Long call() throws InterruptedException {
			latch.await();

			long startTime = System.nanoTime();
			for (int i = start; i < end; i++) {
				list.get(i);
			}
			return System.nanoTime() - startTime;
		}
	}
}
