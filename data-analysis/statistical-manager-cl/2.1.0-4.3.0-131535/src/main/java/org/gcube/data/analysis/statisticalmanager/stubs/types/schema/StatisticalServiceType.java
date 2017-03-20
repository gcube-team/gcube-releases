package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import javax.xml.bind.annotation.XmlEnum;


@XmlEnum(String.class)
public enum StatisticalServiceType {
	TABULAR,
	FILE,
	PRIMITIVE,
	LIST,
	ENUM,
	TABULAR_LIST,
	COLUMN_LIST,
	COLUMN

}
