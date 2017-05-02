package org.gcube.data.simulfishgrowthdata.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Test {
	private String foo;
	private String bar;

	public Test() {
		super();
	}

	public Test(String foo, String bar) {
		this();
		this.foo = foo;
		this.bar = bar;
	}

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	public String getBar() {
		return bar;
	}

	public void setBar(String bar) {
		this.bar = bar;
	}

}
