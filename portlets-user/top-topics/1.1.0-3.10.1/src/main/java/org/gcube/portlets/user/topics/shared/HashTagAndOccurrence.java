package org.gcube.portlets.user.topics.shared;

public class HashTagAndOccurrence implements Comparable<HashTagAndOccurrence>{
	private String hashtag;
	private Integer occurrence;
	public HashTagAndOccurrence(String hashtag, Integer occurrence) {
		super();
		this.hashtag = hashtag;
		this.occurrence = occurrence;
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
	@Override
	public String toString() {
		return "HashTagAndOccurrence [hashtag=" + hashtag + ", occurrence="
				+ occurrence + "]";
	}
	@Override
	public int compareTo(HashTagAndOccurrence o) {
		if (this.occurrence == o.getOccurrence()) return 0;
		return (this.occurrence > o.getOccurrence()) ? 1 : -1;
	}
	
	
}
