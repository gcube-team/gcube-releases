package org.gcube.data.spd.model.service.types;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum JobType {

	DWCAByChildren,
	DWCAById,
	CSV,
	CSVForOM,
	DarwinCore,
	LayerCreator
}
