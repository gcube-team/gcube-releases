package org.gcube.spatial.data.sdi.engine.impl.metadata.templates;

import java.util.ArrayList;

import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataHandler;
import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataUtils;
import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataUtils.Position;
import org.gcube.spatial.data.sdi.engine.impl.metadata.templates.ThreddsOnlineTemplate.ThreddsOnlineRequest;
import org.gcube.spatial.data.sdi.model.ParameterType;
import org.gcube.spatial.data.sdi.model.metadata.TemplateDescriptor;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class ThreddsOnlineTemplate extends AbstractMetadataTemplate<ThreddsOnlineRequest> {

	
	
	
	private static ArrayList<ParameterType> EXPECTED_PARAMETERS=new ArrayList<ParameterType>();
	private static String TEMPLATE_ID=TemplateInvocationBuilder.THREDDS_ONLINE.ID;
	private static String TEMPLATE_NAME="Thredds Online Resources";
	private static String FILENAME="ThreddsOnlineResources.ftlx";
	private static InsertionPoint INSERTION=new InsertionPoint(Position.sibling_after, "//gmd:identificationInfo"); 
	private static TemplateDescriptor DESCRIPTOR;
	
	static {
		EXPECTED_PARAMETERS.add(new ParameterType(TemplateInvocationBuilder.THREDDS_ONLINE.CATALOG, "The thredds catalog name"));
		EXPECTED_PARAMETERS.add(new ParameterType(TemplateInvocationBuilder.THREDDS_ONLINE.FILENAME, "The dataset's file name"));
		EXPECTED_PARAMETERS.add(new ParameterType(TemplateInvocationBuilder.THREDDS_ONLINE.HOSTNAME, "Thredds hostname"));
		
		DESCRIPTOR=new TemplateDescriptor(TEMPLATE_ID, TEMPLATE_NAME, "Template for online resources exposed by thredds.", "http://sdi-d4s.d4science.org",EXPECTED_PARAMETERS);
	}
	
	
	public ThreddsOnlineTemplate() {
		super(FILENAME, INSERTION, DESCRIPTOR);
	}
	
	
	@Getter
	@AllArgsConstructor
	@ToString
	public static class ThreddsOnlineRequest{
		private String hostname;
		private String catalog;
		private String filename;
		private String gisViewerLink;
	}

	@Override
	public ThreddsOnlineRequest getInstantiationRequest(MetadataHandler handler, TemplateInvocation invocation) throws InvalidTemplateInvocationException,Exception{
			if(!invocation.getToInvokeTemplateID().equals(TEMPLATE_ID)) throw new InvalidTemplateInvocationException("Invalid template ID : "+invocation.getToInvokeTemplateID());
			String filename =getParameter(TemplateInvocationBuilder.THREDDS_ONLINE.FILENAME, invocation.getTemplateParameters(), true, null);
			String catalog =getParameter(TemplateInvocationBuilder.THREDDS_ONLINE.CATALOG, invocation.getTemplateParameters(), true, null);
			String hostname =getParameter(TemplateInvocationBuilder.THREDDS_ONLINE.HOSTNAME, invocation.getTemplateParameters(), true, null);
			String uuid=handler.getUUID();
			String gisLink=MetadataUtils.getGisLinkByUUID(uuid);
			return new ThreddsOnlineRequest(hostname, catalog, filename, gisLink);
		
	}

}
