package org.gcube.vremanagement.vremodeler.impl.util;

public class Triple<A,B,C> extends Pair<A, B> {

	private C third;
	
	public Triple(A first, B second, C third) {
		super(first, second);
		this.third = third;
	}

	public C getThird() {
		return third;
	}


}
