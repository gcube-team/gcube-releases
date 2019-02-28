package org.gcube.data.analysis.tabulardata.service.tabular.metadata;

import org.gcube.data.analysis.tabulardata.commons.utils.Licence;

public class LicenceMetadata implements TabularResourceMetadata<Licence> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1331719074247661518L;

	private Licence licence;
	
	public LicenceMetadata(){}
		
	public LicenceMetadata(Licence licence) {
		super();
		this.licence = licence;
	}



	@Override
	public void setValue(Licence value) {
		this.licence = value;
	}

	@Override
	public Licence getValue() {
		return licence;
	}

}
