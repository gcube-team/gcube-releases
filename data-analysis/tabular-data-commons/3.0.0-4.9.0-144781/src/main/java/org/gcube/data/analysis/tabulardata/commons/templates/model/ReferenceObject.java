package org.gcube.data.analysis.tabulardata.commons.templates.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlSeeAlso({TimeDimensionReference.class, DimensionReference.class, LocaleReference.class, FormatReference.class})
public abstract class ReferenceObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	abstract public boolean check(Class<? extends DataType> datatype);
	
}
