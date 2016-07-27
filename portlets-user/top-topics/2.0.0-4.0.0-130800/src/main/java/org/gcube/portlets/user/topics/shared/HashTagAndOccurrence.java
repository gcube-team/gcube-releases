package org.gcube.portlets.user.topics.shared;

public class HashTagAndOccurrence implements Comparable<HashTagAndOccurrence>{
	private String hashtag;
	private Integer occurrence;
	private double weight;
	public HashTagAndOccurrence(String hashtag, Integer occurrence) {
		super();
		this.hashtag = hashtag;
		this.occurrence = occurrence;
	}
	public HashTagAndOccurrence(String hashtag, Integer occurrence, double weight) {
		super();
		this.hashtag = hashtag;
		this.occurrence = occurrence;
		this.weight = weight;
	}
	public String getHashtag() {
		return hashtag;
	}
	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}
	public Integer getOccurrence() {
		return occurrence;
	}
	public void setOccurrence(Integer occurrence) {
		this.occurrence = occurrence;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "HashTagAndOccurrence [hashtag=" + hashtag + ", occurrence="
				+ occurrence + ", weight=" + weight + "]";
	}
	@Override
	public int compareTo(HashTagAndOccurrence o) {
		return Double.compare(o.getWeight(), this.weight);
	}
}
