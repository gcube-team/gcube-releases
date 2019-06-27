package org.gcube.data.publishing.gCatFeeder.collectors.dm.model.ckan;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.gcube.data.publishing.gCatFeeder.collectors.dm.DataMinerCollectorProperties;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.InternalAlgorithmDescriptor;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.Parameter;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.UserIdentity;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.InternalConversionException;
import org.gcube.data.publishing.gCatFeeder.utils.ContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GCatModel implements CatalogueFormatData {

	private static ObjectMapper mapper=new ObjectMapper();
	
	private static String profileXML=null;
	
	String profileID=DataMinerCollectorProperties.getProperty(DataMinerCollectorProperties.CKAN_RESOURCE_TYPE);
	
	public static void setProfile(String toSet) {
		profileXML=toSet;
	}
	
	public GCatModel(InternalAlgorithmDescriptor desc) {
		item=new CkanItem();
//		item.setAuthor(desc.getAuthor());
		
		item.setTitle(desc.getName()+" in "+ContextUtils.getCurrentScopeName());
		item.setLicense_id("CC-BY-NC-SA-4.0");
//		item.setMaintainer(desc.getMaintainer());
		item.setName(item.getTitle().toLowerCase().toLowerCase().replaceAll(" ", "_"));
		for(String tag:desc.getTags()) {
			item.getTags().add(new CkanItem.Tag(fixTag(tag)));
		}
		item.getTags().add(new CkanItem.Tag(ContextUtils.getCurrentScopeName()));
		item.getTags().add(new CkanItem.Tag("WPS"));
		item.getTags().add(new CkanItem.Tag("Analytics"));
		item.getExtras().add(new CKanExtraField("system:type", profileID));
		
		item.setPrivateFlag(desc.getPrivateFlag());
		
		for(Parameter param: desc.getInputParameters())
			item.getExtras().add(new CKanExtraField(profileID+":Input Parameter", 
					String.format("%1$s [%2$s] %3$s : %4$s", 
							param.getName(),param.getType(),
							((param.getValue()!=null&&!param.getValue().isEmpty())?"default : "+param.getValue():""),
							param.getDescription())));
		
		
		for(Parameter param: desc.getOutputParameters())
			item.getExtras().add(new CKanExtraField(profileID+":Output Parameter", 
					String.format("%1$s [%2$s] %3$s : %4$s", 
							param.getName(),param.getType(),
							((param.getValue()!=null&&!param.getValue().isEmpty())?"default : "+param.getValue():""),
							param.getDescription())));
		
		
		//Algorithm Description
//		item.getExtras().add(new CKanExtraField(profileID+":Process Description", desc.getDescription()));
		
		item.setNotes(desc.getDescription());
		
		
		// Algorithm Users
		
		item.getExtras().add(new CKanExtraField(profileID+":Process Author",desc.getAuthor().asStringValue()));
		item.getExtras().add(new CKanExtraField(profileID+":Process Maintainer",desc.getAuthor().asStringValue()));
		
		if(desc.getGuiLink()!=null) 
			resources.add(new CkanResource("Gateway Link",desc.getGuiLink(),"HTTP","Link to the GUI designed to operate with DataMiner"));
		if(desc.getWpsLink()!=null)
			resources.add(new CkanResource("WPS Link", desc.getWpsLink(), "WPS","WPS Link to the "+DataMinerCollectorProperties.getProperty(DataMinerCollectorProperties.CKAN_RESOURCE_TYPE)));
		
	}
	
	
	private String profile=profileXML;
	
	private CkanItem item=null;
	private ArrayList<CkanResource> resources=new ArrayList<>();
	
	
	@Override
	public String toCatalogueFormat() throws InternalConversionException {
		try{
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
		mapper.writeValue(baos, this);
		return baos.toString();
		}catch(Throwable t) {
			throw new InternalConversionException("Unable to convert",t);
		}
	}


	/**
	 * (Common) Title
	 * (Common) Description
	 * (Common) Tags: free list of keywords
	 * (Common) License 
	 * (Common) Visibility: either public or private
	 * (Common) Version
	 * (Common) Author: the creator of metadata. Only one occurrence is supported; 
	 * (Common) Maintainer: 
	 * (Method specific) Creator: the author of the method (with email and ORCID). Repeatable field; 
	 * (Method specific) Creation date: when the method has been released; 
	 * (Method specific) Input: Repeatable field;
	 * (Method specific) Output: Repeatable field; 
	 * (Method specific) RelatedPaper: a reference to an associated paper;
	 * (Method specific) Restrictions On Use: an optional text 
	 * (Method specific) Attribution requirements: the text to use to acknowledge method usage; 
	 */


	static final String fixTag(String toFix) {
		String fixedTag=toFix.replaceAll(":", " ").
				replaceAll("[\\(\\)\\\\/]", "-").replaceAll("[’']","_");
		if(fixedTag.length()>100)				
			fixedTag=fixedTag.substring(0,96)+"...";
		return fixedTag.trim();
	}
	
	static final String identityString(UserIdentity id) {
		StringBuilder builder=new StringBuilder(id.getLastName()+", ");
		builder.append(id.getFirstName()+", ");
		if(id.getEmail()!=null) builder.append(id.getEmail()+", ");
		if(id.getOrcid()!=null) builder.append(id.getOrcid()+", ");
		return builder.toString().substring(0,builder.lastIndexOf(","));
	}
}
