package org.gcube.common.geoserverinterface.geonetwork;

import java.util.ArrayList;
import java.util.Random;

public class RandomGeoserver implements GeoserverSortInterface {
	
	public RandomGeoserver(){
		
	}

	public ArrayList<String> sortGeoserverList(ArrayList<String> list) {

		ArrayList<String> geoserverOrderedList = new ArrayList<String>();
		int[] pointer = new int[list.size()];

		pointer = shuffleArray(list.size());

		for (int i : pointer)
			geoserverOrderedList.add(list.get(i));

		return geoserverOrderedList;
	}

	private int[] shuffleArray(int size) {
		Random rgen = new Random(); // Random number generator
		int[] cards = new int[size];

		// --- Initialize the array to the ints 0-size
		for (int i = 0; i < cards.length; i++) {
			cards[i] = i;
		}

		// --- Shuffle by exchanging each element randomly
		for (int i = 0; i < cards.length; i++) {
			int randomPosition = rgen.nextInt(cards.length);
			int temp = cards[i];
			cards[i] = cards[randomPosition];
			cards[randomPosition] = temp;
		}

		return cards;
	}

}
