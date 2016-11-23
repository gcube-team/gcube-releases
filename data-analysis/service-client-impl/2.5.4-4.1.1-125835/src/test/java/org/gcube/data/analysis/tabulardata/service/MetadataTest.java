package org.gcube.data.analysis.tabulardata.service;

import java.util.Calendar;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.service.impl.tabular.TabularResourceObject;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.ValidSinceMetadata;
import org.junit.Test;

public class MetadataTest {

	@Test
	public void setMetadata(){
		TabularResourceObject tro = new TabularResourceObject(new TabularResource( 12,TabularResourceType.STANDARD, "", "", Calendar.getInstance(), "type", false));
		ValidSinceMetadata vlm = new ValidSinceMetadata(Calendar.getInstance());
		tro.setMetadata(vlm);
		NameMetadata nm = new NameMetadata();
		nm.setValue("name");
		tro.setMetadata(nm);
	
		System.out.println(tro.getMetadata(ValidSinceMetadata.class).getValue());
						
		System.out.println("-- all metadata --");
		for (TabularResourceMetadata<?> trm : tro.getAllMetadata())
			System.out.println(trm.getValue());
		
	}
	
}
