package com.example.e4;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Artush on 1/22/2018.
 */
public class CollectionSerialization {

	public static void main(String[] args)
			throws IOException,
			ClassNotFoundException {
		new CollectionSerialization().start();
	}

	void start()
			throws IOException,
			ClassNotFoundException {
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
		List<Integer> deserializedList1 = deserialize(serialize(list));
		List<Integer> deserializedList2 = deserialize(serialize(list));

		System.out.println(list);
		System.out.println(deserializedList1);
		System.out.println(deserializedList2);
	}

	String serialize(List<Integer> list)
			throws IOException {
		String filePath = "list";
		try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(list);
		}
		return filePath;
	}

	List<Integer> deserialize(String filePath)
			throws IOException,
			ClassNotFoundException {

		try (FileInputStream fileInputStream = new FileInputStream(filePath);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
			return (List<Integer>) objectInputStream.readObject();
		}
	}
}
