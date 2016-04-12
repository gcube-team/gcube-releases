package org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.log;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.framework.accesslogger.model.AccessLogEntry;
import org.gcube.application.framework.core.session.ASLSession;

public class AquaMapsObjectGenerationLogEntry extends AccessLogEntry {

	
	private String message;
	
	
	public AquaMapsObjectGenerationLogEntry(AquaMapsObject submitted, Resource HSPEC) {
		super("AquaMapsObjectGenerationLogEntry");
		StringBuilder builder=new StringBuilder();
		builder.append(Common.OBJECT_TITLE+Common.ATTRIBUTE_VALUE+submitted.getName());
		builder.append(Common.ATTRIBUTE_SEPARATOR);
		builder.append(Common.OBJECT_TYPE+Common.ATTRIBUTE_VALUE+submitted.getType());
		builder.append(Common.ATTRIBUTE_SEPARATOR);
		builder.append(Common.SPECIES_COUNT+Common.ATTRIBUTE_VALUE+submitted.getSelectedSpecies().size());
		builder.append(Common.ATTRIBUTE_SEPARATOR);
		builder.append(Common.GIS+Common.ATTRIBUTE_VALUE+submitted.getGis());
		builder.append(Common.ATTRIBUTE_SEPARATOR);
		builder.append(Common.HSPEC_ID+Common.ATTRIBUTE_VALUE+HSPEC.getSearchId());
		message=builder.toString();
	}

	@Override
	public String getLogMessage() {	
		return message;
	}

	
	
}
