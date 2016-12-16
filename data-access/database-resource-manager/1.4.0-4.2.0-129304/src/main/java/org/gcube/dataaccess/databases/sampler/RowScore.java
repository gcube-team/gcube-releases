package org.gcube.dataaccess.databases.sampler;

/**
 * Class that allows to describe
 */
public class RowScore implements Comparable<RowScore> {

	private Object row;
	private int score;

	public RowScore(Object r, int s) {

		row = r;
		score = s;

	}

	// to get the row
	public Object getRow() {

		return row;
	}

	// to get the score
	public int getScore() {

		return score;
	}

	// to compare two RowScore objects in order to sort a list of this objects
	@Override
	public int compareTo(RowScore o) {
		// TODO Auto-generated method stub

		if (this.score > o.getScore())
			return 1;

		if (this.score == o.getScore())
			return 0;

		if (this.score < o.getScore())
			return -1;

		return 0;
	}

}
