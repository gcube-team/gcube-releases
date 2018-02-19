package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=aquamapsTypesNS)
@XmlEnum(String.class)
public enum OrderDirection {
ASC,DESC
}
