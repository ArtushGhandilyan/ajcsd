package com.example.e5;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Artush on 1/23/2018.
 */
public class WeakHashMapExample {

	public static void main(String[] args) throws InterruptedException {
		Map<Order, Float> orders = new WeakHashMap<>();

		orders.put(new Order(1, "something"), 100F);
		orders.put(new Order(2, "something"), 200F);

		Order order3 = new Order(3, "something");
		orders.put(order3, 300F);

		System.out.println(orders.size());

		System.gc();
		Thread.sleep(1000L);

		System.out.println(orders.size());
	}
}


class Order {
	int orderId;
	String description;

	public Order(int orderId, String description) {
		this.orderId = orderId;
		this.description = description;
	}
}