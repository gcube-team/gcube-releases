package org.gcube.data.publishing.gCatFeeder.catalogues;

import org.gcube.data.publishing.gCatFeeder.catalogues.model.PublishReport;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.CatalogueInteractionException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.PublicationException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.WrongObjectFormatException;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.ControllerConfiguration;
import org.gcube.data.publishing.gCatFeeder.model.InternalConversionException;

public interface CatalogueController {

	public PublishReport publishItem(CatalogueFormatData toPublish) throws WrongObjectFormatException,CatalogueInteractionException,PublicationException, InternalConversionException;
	
	public void configure (ControllerConfiguration config);
}
