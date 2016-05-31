package org.gcube.vremanagement.vremodeler.impl.util;

public class Pair<A,B> {
	
	A first;
	B second;
		
	public Pair(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public A getFirst() {
		return first;
	}
	public void setFirst(A first) {
		this.first = first;
	}
	public B getSecond() {
		return second;
	}
	public void setSecond(B second) {
		this.second = second;
	}
	
	

}
