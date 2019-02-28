package org.gcube.data.analysis.sdmx.converter;

public class BaseTabmanColumnIDConverter implements ColumnIdConverter {

	private final String TABMAN_PREFIX = "TabmanColumn";
	private int columnIdBaseIndex; 
	
	public BaseTabmanColumnIDConverter() {
		columnIdBaseIndex = TABMAN_PREFIX.length ();
	}
	
	@Override
	public String registry2Local(String registryColumnId) {

		return registryColumnId.substring(columnIdBaseIndex);
	}

	@Override
	public String local2Registry(String localColumnId) {
		return TABMAN_PREFIX+localColumnId;
	}

	@Override
	public String getIdentificator() {
		// TODO Auto-generated method stub
		return null;
	}

}
