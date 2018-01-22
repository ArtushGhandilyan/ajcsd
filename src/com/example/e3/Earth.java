package com.example.e3;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Artush on 1/22/2018.
 */
public class Earth
		implements Serializable {

	private static final long serialVersionUID = 12144L;

	private static Earth instance;

	private long creationDate;

	private Earth() {
	}

	public Earth(long creationDate) {
		this.creationDate = creationDate;
	}

	public static Earth getInstance() {
		if (instance == null) {
			instance = new Earth(new Date().getTime());
		}

		return instance;
	}

    private Object readResolve() throws ObjectStreamException {
        return instance;
    }

    public long getCreationDate() {
        return creationDate;
    }
}
