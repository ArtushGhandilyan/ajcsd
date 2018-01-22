package com.example.e3;

import java.io.*;

/**
 * @author Artush on 1/22/2018.
 */
public class SingletonDeserialization {

	public static void main(String[] args)
            throws IOException, ClassNotFoundException {
		new SingletonDeserialization().start();
	}

	void start() throws IOException, ClassNotFoundException {
        Earth earth = Earth.getInstance();
        Earth deserializedEarth1 = deserialize(serialize(earth));
        Earth deserializedEarth2 = deserialize(serialize(earth));

        System.out.println(earth);
        System.out.println(deserializedEarth1);
        System.out.println(deserializedEarth2);
        System.out.println(earth.getCreationDate());
        System.out.println(deserializedEarth1.getCreationDate());
        System.out.println(deserializedEarth2.getCreationDate());
    }

	String serialize(Earth earth)
			throws IOException {
        String filePath = "earth";
		try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(earth);
		}
		return filePath;
	}

	Earth deserialize(String filePath)
			throws IOException,
			ClassNotFoundException {

		try (FileInputStream fileInputStream = new FileInputStream(filePath);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
			return (Earth) objectInputStream.readObject();
		}
	}
}
