package org.gcube.vremanagement.vremodel.cl.stubs.types;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.vremanagement.vremodel.cl.Constants;

@XmlRootElement(namespace=Constants.TYPES_NAMESPACE)
public class ReportList {
	
	@XmlElement(namespace=Constants.TYPES_NAMESPACE, name="list")
	private List<Report> reports;

	protected ReportList() {
		super();
	}

	public ReportList(List<Report> reports) {
		super();
		this.reports = reports;
	}

	public List<Report> reports() {
		return reports;
	}

	public void reports(List<Report> reports) {
		this.reports = reports;
	}
}
