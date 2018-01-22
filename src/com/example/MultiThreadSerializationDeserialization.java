package com.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author Artush on 1/21/2018.
 *
 * This class parallel run 5 threads with following tasks.
 * Creator: Create 100 new objects and add in queue1.
 * Serializer: Take object from queue, serialize it and add filePath to queue2
 * Deserializer: Take filePath from queue2, deserialize it and add to queue1
 *
 * Queue1 and Queue2 is blocking queue, shared with 5 thread (creator, 2 seralizer, 2 deserializer)
 * Main thread periodically print queues and actions which performed on that interval.
 * Each thread after doing his action, put info about it (time, actionName, object) in actions ConcurrentSkipListSet.
 *
 */
public class MultiThreadSerializationDeserialization
		implements Serializable {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss S");

	public static void main(String[] args)
			throws InterruptedException {
		new MultiThreadSerializationDeserialization().start();
	}

	private void start()
			throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);

		LinkedBlockingQueue<Country> queue1 = new LinkedBlockingQueue<>();
		LinkedBlockingQueue<String> queue2 = new LinkedBlockingQueue<>();

		ConcurrentSkipListSet<Action> actions = new ConcurrentSkipListSet<>(Comparator.comparing(Action::getTimestamp));

		ExecutorService executorService = Executors.newFixedThreadPool(5);
		executorService.submit(new Creator(latch, actions, queue1));
		executorService.submit(new Serializer(latch, actions, queue1, queue2));
		executorService.submit(new Serializer(latch, actions, queue1, queue2));
		executorService.submit(new Deserializer(latch, actions, queue1, queue2));
		executorService.submit(new Deserializer(latch, actions, queue1, queue2));

		latch.countDown();

		while (true) {
			if (!actions.isEmpty()) {
				System.err.println("---------------------------------------------------");
				System.err.println("Queue 1 - size: " + queue1.size() + " " + queue1);
				System.err.println("Queue 2 - size: " + queue2.size() + " " + queue2);
				System.err.println();
				System.err.println("\t\tGod\t\t  \t\tQ1\t\t  \t\tQ2\t\t\tTime");
				for (Action action : actions) {
					System.err.println(action);
				}
				actions.clear();
				System.err.println();
				System.err.println();
			}
			Thread.sleep(50L);
		}
	}

	class Creator
			implements Runnable {

		final CountDownLatch latch;
		ConcurrentSkipListSet<Action> actions;
		Queue<Country> queue;

		Creator(CountDownLatch latch, ConcurrentSkipListSet<Action> actions,
		        LinkedBlockingQueue<Country> queue) {
			this.latch = latch;
			this.actions = actions;
			this.queue = queue;
		}

		@Override
		public void run() {
			int i = 0;
			while (++i < 100) {
				try {
					latch.await();
					Country country = new Country("name" + i, String.valueOf(i), (int) (Math.random() * 1000000));
					queue.add(country);
					actions.add(new Action("Creator", country.code));
					Thread.sleep(50L);
				}
				catch (Exception e) {
					System.err.println("Creator: " + e.getMessage());
				}
			}
		}
	}

	class Serializer
			implements Runnable {

		final CountDownLatch latch;
		ConcurrentSkipListSet<Action> actions;

		LinkedBlockingQueue<Country> queue1;
		Queue<String> queue2;

		Serializer(CountDownLatch latch, ConcurrentSkipListSet<Action> actions,
				LinkedBlockingQueue<Country> queue1, LinkedBlockingQueue<String> queue2) {
			this.latch = latch;
			this.actions = actions;
			this.queue1 = queue1;
			this.queue2 = queue2;
		}

		@Override
		public void run() {
			long sleepDuration;
			while (true) {
				try {
					latch.await();
					Country country = queue1.take();
					queue2.add(serialize(country));
					actions.add(new Action("Serializer", country.code));
					sleepDuration = (int) (Math.random() * 100 + 1);
					Thread.sleep(sleepDuration);

				}
				catch (Exception e) {
					System.err.println("Serializer: " + e.getMessage());
				}
			}
		}

		String serialize(Country country)
				throws IOException {
			String filePath = country.code;

			try(FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
				objectOutputStream.writeObject(country);
			}

			return filePath;
		}
	}

	class Deserializer
			implements Runnable {

		final CountDownLatch latch;
		ConcurrentSkipListSet<Action> actions;

		Queue<Country> queue1;
		LinkedBlockingQueue<String> queue2;

		Deserializer(CountDownLatch latch, ConcurrentSkipListSet<Action> actions,
				LinkedBlockingQueue<Country> queue1, LinkedBlockingQueue<String> queue2) {
			this.latch = latch;
			this.actions = actions;
			this.queue1 = queue1;
			this.queue2 = queue2;
		}

		@Override
		public void run() {
			long sleepDuration;
			while (true) {
				try {
					latch.await();
					String filePath = queue2.take();
					queue1.add(deserialize(filePath));
					actions.add(new Action("Deserializer", filePath));
					sleepDuration = (int) (Math.random() * 100 + 1);
					Thread.sleep(sleepDuration);
				}
				catch (Exception e) {
					System.err.println("Deserializer: " + e.getMessage());
				}
			}
		}

		Country deserialize(String filePath)
				throws IOException, ClassNotFoundException {

			try(FileInputStream fileInputStream = new FileInputStream(filePath);
			    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
				return (Country) objectInputStream.readObject();
			}
		}
	}

	class Action {
		Long timestamp;
		String name;
		String item;

		Action(String name, String item) {
			this.timestamp = new Date().getTime();
			this.name = name;
			this.item = item;
		}

		@Override
		public String toString() {
			switch (name) {
				case "Creator":
					return "\t\t" + item + "\t\t->\t\t[]\t\t--\t\t[]\t\t\t" + sdf.format(new Date(timestamp));
				case "Serializer":
					return "\t\t[]\t\t--\t\t" + item + "\t\t->\t\t[]\t\t\t" + sdf.format(new Date(timestamp));
				case "Deserializer":
					return "\t\t[]\t\t--\t\t[]\t\t<-\t\t" + item + "\t\t\t" + sdf.format(new Date(timestamp));
			}

			return name + " " + item;
		}

		Long getTimestamp() {
			return timestamp;
		}
	}

	class Country
			implements Serializable {

		private static final long serialVersionUID = 1L;

		String name;
		String code;
		transient Integer population;

		public Country() {
		}

		public Country(String name, String code, Integer population) {
			this.name = name;
			this.code = code;
			this.population = population;
		}

		@Override
		public String toString() {
			return code;
		}
	}
}
