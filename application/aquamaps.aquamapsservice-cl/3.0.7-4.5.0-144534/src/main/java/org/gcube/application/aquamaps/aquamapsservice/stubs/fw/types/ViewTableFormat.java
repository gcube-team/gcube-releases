package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(namespace=DM_target_namespace)
@XmlEnum(String.class)
public enum ViewTableFormat {
JSON
}
