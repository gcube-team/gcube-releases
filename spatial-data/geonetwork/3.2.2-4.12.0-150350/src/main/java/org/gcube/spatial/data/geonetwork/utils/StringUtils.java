package org.gcube.spatial.data.geonetwork.utils;

import java.util.Random;
import java.util.Set;

public class StringUtils {

	public static final String generateRandomString(int length){
		String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < length) {
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;
	}

	
	public static final String generateNewRandom(Set<String> existing,int length){
		String toReturn=generateRandomString(length);
		while(existing.contains(toReturn))
			toReturn=generateRandomString(length);
		return toReturn;
	}
}


