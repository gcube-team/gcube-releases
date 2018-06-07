package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types;

import javax.xml.bind.annotation.XmlEnum;


@XmlEnum(String.class)
public enum FileType {
	
	@Deprecated
	XML,
	@Deprecated
	JPG,
	
	InternalProfile,ExternalMeta,Image,
	Layer,WMSContext
	
}
