package org.gcube.data.analysis.tabulardata.commons.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.ReferenceObject;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeDimensionReference extends ReferenceObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PeriodType period;
	
	private String formatIdentifier;
	
	@SuppressWarnings("unused")
	private TimeDimensionReference(){}
	
	public TimeDimensionReference(PeriodType period, String formatIdentifier){
		this.period= period;		
		this.formatIdentifier = formatIdentifier;
	}

	/**
	 * @return the period
	 */
	public PeriodType getPeriod() {
		return period;
	}

	public String getFormatIdentifier() {
		return formatIdentifier;
	}

	@Override
	public boolean check(Class<? extends DataType> datatype) {
		return period!=null && period.getTimeFormatById(formatIdentifier)!=null;
	}
	
	
}
