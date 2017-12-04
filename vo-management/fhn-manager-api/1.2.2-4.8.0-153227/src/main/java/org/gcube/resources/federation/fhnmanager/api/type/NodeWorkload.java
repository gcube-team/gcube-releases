package org.gcube.resources.federation.fhnmanager.api.type;

import javax.xml.bind.annotation.XmlAttribute;

public class NodeWorkload {

	// @XmlAttribute
	private double nowWorkload;

	// @XmlAttribute
	private double lastHourWorkload;

	// @XmlAttribute
	private double lastDayWorkload;

	// @XmlAttribute
	private double allTimeAverageWorkload;

	public NodeWorkload() {

	}

	@XmlAttribute
	public double getNowWorkload() {
		return nowWorkload;
	}

	public void setNowWorkload(double nowWorkload) {
		this.nowWorkload = nowWorkload;
	}

	@XmlAttribute
	public double getLastHourWorkload() {
		return lastHourWorkload;
	}

	public void setLastHourWorkload(double lastHourWorkload) {
		this.lastHourWorkload = lastHourWorkload;
	}

	@XmlAttribute
	public double getLastDayWorkload() {
		return lastDayWorkload;
	}

	public void setLastDayWorkload(double lastDayWorkload) {
		this.lastDayWorkload = lastDayWorkload;
	}

	@XmlAttribute
	public double getAllTimeAverageWorkload() {
		return allTimeAverageWorkload;
	}

	public void setAllTimeAverageWorkload(double allTimeAverageWorkload) {
		this.allTimeAverageWorkload = allTimeAverageWorkload;
	}
}
