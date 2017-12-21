package org.gcube.data.analysis.excel.metadata.format;

import java.util.List;

public class GenericMeasureFormat extends CatchMeasureFormat{

	
	public GenericMeasureFormat (String reference, List<String> measures)
	{
		super (reference,measures);
	}
	


	@Override
	public boolean isCatchValue () 
	{
		return false;
	}

}
