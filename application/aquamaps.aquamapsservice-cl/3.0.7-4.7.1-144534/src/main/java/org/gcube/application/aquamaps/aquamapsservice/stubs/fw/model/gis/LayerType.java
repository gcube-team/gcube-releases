package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.gisTypesNS;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=gisTypesNS)
public enum LayerType {
Environment,PointMap,Prediction,Biodiversity
}
