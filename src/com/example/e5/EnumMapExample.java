package com.example.e5;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Artush on 1/23/2018.
 *
 * Example shows that the EnumMap's iterator does not fail-fast.
 */
public class EnumMapExample {

	public static void main(String[] args) {
		Map<Currency, Float> rate = new EnumMap<>(Currency.class);

		rate.put(Currency.USD, 480F);
		rate.put(Currency.EUR, 560F);
		rate.put(Currency.RUB, 8.5F);
		rate.put(Currency.AMD, 1F);

		for (Currency currency : rate.keySet()) {
			System.out.println(currency);
			rate.put(Currency.CNY, 75.25F);
		}
	}
}

enum Currency {
	USD,
	EUR,
	AMD,
	RUB,
	CNY
}
