package org.gcube.data.simulfishgrowthdata.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
	/**
	 * 
	 * all elements of left and right
	 * 
	 * <p>
	 * http://stackoverflow.com/a/5283123/874502
	 * </p>
	 * 
	 * @param left
	 * @param right
	 * @return all elements
	 */
	public <T> List<T> union(List<T> left, List<T> right) {
		Set<T> set = new HashSet<T>();

		set.addAll(left);
		set.addAll(right);

		return new ArrayList<T>(set);
	}

	/**
	 * 
	 * common elements of left and right
	 * 
	 * <p>
	 * http://stackoverflow.com/a/5283123/874502
	 * </p>
	 * 
	 * @param left
	 * @param right
	 * @return commons elements
	 */
	public <T> List<T> intersection(List<T> left, List<T> right) {
		List<T> list = new ArrayList<T>();

		for (T t : left) {
			if (right.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}

	/**
	 * elements existing in left set only
	 * 
	 * @param left
	 *            the big set
	 * @param right
	 *            this will be subtracted
	 * @return left minus right
	 */
	public <T> List<T> complementOfRightInLeft(List<T> left, List<T> right) {
		List<T> list = new ArrayList<T>();

		for (T t : left) {
			if (!right.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}

	public int median(Integer... numbers) {
		Arrays.sort(numbers);
		Integer toRet;
		if (numbers.length % 2 == 0)
			toRet = (int) ((numbers[numbers.length / 2] + numbers[numbers.length / 2 - 1]) / 2);
		else
			toRet = numbers[numbers.length / 2];
		return toRet;
	}

	public String limitLength(final String src, int max) {
		if (src.length()<=max) return src;
		return src.substring(src.length()-max);
	}
}
