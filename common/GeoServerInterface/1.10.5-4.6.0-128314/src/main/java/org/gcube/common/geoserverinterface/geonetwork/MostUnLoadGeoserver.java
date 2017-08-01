package org.gcube.common.geoserverinterface.geonetwork;

import java.util.ArrayList;

import org.gcube.common.geoserverinterface.GeoserverCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MostUnLoadGeoserver implements GeoserverSortInterface {

	private static final Logger logger = LoggerFactory.getLogger(MostUnLoadGeoserver.class);
	
	private String geoserverUsername;
	private String geoserverPassword;

	public MostUnLoadGeoserver(String geoserverUsername, String geoserverPassword) {
		this.geoserverUsername = geoserverUsername;
		this.geoserverPassword = geoserverPassword;
	}

	public ArrayList<String> sortGeoserverList(ArrayList<String> list) {

		int[] intList = null;

		int listSize = list.size();
		intList = new int[listSize];

		for (int i = 0; i < listSize; i++) {
			GeoserverCaller geoCaller = new GeoserverCaller(list.get(i), this.geoserverUsername, this.geoserverPassword);
			try {
				intList[i] = geoCaller.listLayers().size();
				logger.debug("geoserver "+ list.get(i) + "- number of layers " + intList[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ArrayList<String> geoserverOrderedList = new ArrayList<String>();
		int[] pointer = new int[intList.length];
		pointer = this.selectionSort(intList);

		for (int i = 0; i < intList.length; i++) {
			geoserverOrderedList.add(list.get((pointer[i])));
		}

		return geoserverOrderedList;

	}

	/**
	 * Find index of minimum (lowest) value in array using loop
	 * 
	 * @param numbers
	 * @return
	 */
	private static int getIndexMinValue(int[] numbers) {
		int minValue = numbers[0];
		int indexMin = 0;
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] < minValue) {
				minValue = numbers[i];
				indexMin = i;
			}
		}
		return indexMin;
	}

	/**
	 * 
	 * @param x
	 * @return int array of pointer to order element array
	 */
	public int[] selectionSort(int[] x) {
		int[] pointer = new int[x.length];

		for (int i = 0; i < x.length; i++)
			pointer[i] = i;

		for (int i = 0; i < x.length - 1; i++) {
			int minIndex = i; // Index of smallest remaining value.
			for (int j = i + 1; j < x.length; j++) {
				if (x[minIndex] > x[j]) {
					minIndex = j; // Remember index of new minimum
				}
			}

			int temp = pointer[i];
			pointer[i] = pointer[minIndex];
			pointer[minIndex] = temp;

			// if (minIndex != i) {
			// //... Exchange current element with smallest remaining.
			// int temp = x[i];
			// x[i] = x[minIndex];
			// x[minIndex] = temp;
			// }
		}

		return pointer;
	}
}
