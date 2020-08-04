package com.slj.util.util;

import java.util.Random;

public class PrimaryKeyGenerator {
	public static long generateId() {
		long time = System.currentTimeMillis();
		time = (time << 19) | (1 << 16);
		long parent = Math.abs(new Random().nextInt());
		long size = parent % 65535;
		long id = time | size;
		return id;
	}
	
}
