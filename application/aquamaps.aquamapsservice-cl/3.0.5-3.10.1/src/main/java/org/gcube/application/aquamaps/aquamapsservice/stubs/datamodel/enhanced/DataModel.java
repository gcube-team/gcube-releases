package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;

public abstract class DataModel {
	public String toXML(){
		return AquaMapsXStream.getXMLInstance().toXML(this);
	}
	public static Object fromXML(String xml){
		return AquaMapsXStream.getXMLInstance().fromXML(xml);
	}
}
