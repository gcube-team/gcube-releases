package org.gcube.vremanagement.vremodeler.utils;

import java.io.StringReader;

import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;

import com.thoughtworks.xstream.XStream;

public class Utils {

	public static String toXML(DeployReport report){
		return new XStream().toXML(report);
	}
	
	public static DeployReport fromXML(String report){
		return (DeployReport) new XStream().fromXML(new StringReader(report));
	}
	
}
