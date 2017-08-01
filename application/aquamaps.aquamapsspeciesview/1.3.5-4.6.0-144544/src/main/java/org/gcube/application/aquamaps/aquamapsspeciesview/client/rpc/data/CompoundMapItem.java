package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data;


import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

public class CompoundMapItem extends BaseModelData implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 771187805363300236L;

	
	public static final String IMAGE_COUNT="image_count";
	public static final String GIS="gis";
	public static final String ALGORITHM="algorithm";
	public static final String CREATION_DATE="creation_date";
	public static final String FILESET_ID="fileset_id";
	public static final String LAYER_ID="layer_id";
	public static final String IMAGE_LIST="image_list";
	public static final String THUMBNAIL="thumbnail";
	public static final String TITLE="title";
	public static final String TYPE="type_field"; //biodiversity || species distribution
	public static final String SPECIES_LIST="species_list";
	public static final String AUTHOR="author";
	public static final String COVERAGE="coverage_field";
	public static final String RESOURCE_ID="resource_id";
	public static final String DATA_GENERATION_TIME="data_generation_time";
	public static final String LAYER_URL="layer_url";
	public static final String LAYER_PREVIEW="layer_preview";
	public static final String CUSTOM="custom";
	
	public CompoundMapItem() {
		// TODO Auto-generated constructor stub
	}
	
	public CompoundMapItem(Integer imageCount,Boolean gis, String algorithm, Long creationDate, 
			String fileSetId,String layerId,String csvImageList,String thumbnail,String title,String type, String csvSpeciesList,
			String author,String coverage,Integer resourceId,Long dataGenerationTime,String layerUrl,String LayerPreview, Boolean custom){
		setAlgorithm(algorithm);
		setAuthor(author);
		setCoverage(coverage);
		setCreationDate(creationDate);
		setDataGenerationTime(dataGenerationTime);
		setFileSetId(fileSetId);
		setGis(gis);
		setImageCount(imageCount);
		setImageList(csvImageList);
		setImageThumbNail(thumbnail);
		setLayerId(layerId);
		setLayerPreview(LayerPreview);
		setLayerUrl(layerUrl);
		setResourceId(resourceId);
		setSpeciesList(csvSpeciesList);
		setTitle(title);
		setType(type);		
		setCustom(custom);
	}
	
	
	public CompoundMapItem(Map<String,Object> properties){
		try{if(properties.containsKey(IMAGE_COUNT))setImageCount(Integer.parseInt((String)properties.get(IMAGE_COUNT)));}catch(Exception e){}
		try{if(properties.containsKey(GIS))setGis(Integer.parseInt((String)properties.get(GIS))==1);}catch(Exception e){}
		try{if(properties.containsKey(CUSTOM))setCustom(Integer.parseInt((String)properties.get(CUSTOM))==1);}catch(Exception e){}
		try{if(properties.containsKey(ALGORITHM))setAlgorithm((String)properties.get(ALGORITHM));}catch(Exception e){}
		try{if(properties.containsKey(CREATION_DATE))setCreationDate(Long.parseLong((String)properties.get(CREATION_DATE)));}catch(Exception e){}
		try{if(properties.containsKey(FILESET_ID))setFileSetId((String)properties.get(FILESET_ID));}catch(Exception e){}
		try{if(properties.containsKey(LAYER_ID))setLayerId((String)properties.get(LAYER_ID));}catch(Exception e){}
		try{if(properties.containsKey(IMAGE_LIST))setImageList((String)properties.get(IMAGE_LIST));}catch(Exception e){}
		try{if(properties.containsKey(THUMBNAIL))setImageThumbNail((String)properties.get(THUMBNAIL));}catch(Exception e){}
		try{if(properties.containsKey(TITLE))setTitle((String)properties.get(TITLE));}catch(Exception e){}
		try{if(properties.containsKey(TYPE))setType((String)properties.get(TYPE));}catch(Exception e){}
		try{if(properties.containsKey(SPECIES_LIST))setSpeciesList((String)properties.get(SPECIES_LIST));}catch(Exception e){}
		try{if(properties.containsKey(AUTHOR))setAuthor((String)properties.get(AUTHOR));}catch(Exception e){}
		try{if(properties.containsKey(COVERAGE))setCoverage((String)properties.get(COVERAGE));}catch(Exception e){}
		try{if(properties.containsKey(RESOURCE_ID))setResourceId(Integer.parseInt((String)properties.get(RESOURCE_ID)));}catch(Exception e){}
		try{if(properties.containsKey(DATA_GENERATION_TIME))setDataGenerationTime(Long.parseLong((String)properties.get(DATA_GENERATION_TIME)));}catch(Exception e){}
		try{if(properties.containsKey(LAYER_URL))setLayerUrl((String)properties.get(LAYER_URL));}catch(Exception e){}
		try{if(properties.containsKey(LAYER_PREVIEW))setLayerPreview((String)properties.get(LAYER_PREVIEW));}catch(Exception e){}
		
	}
	
	
	
	
	public Integer getImageCount(){return get(IMAGE_COUNT);}
	public void setImageCount(Integer count){set(IMAGE_COUNT,count);}
	public Boolean isGis(){return get(GIS);}
	public void setGis(Boolean gis){set(GIS,gis);} 
	public Boolean isCustom(){return get(CUSTOM);}
	public void setCustom(Boolean custom){set(CUSTOM,custom);} 
	public String getAlgorithm(){return get(ALGORITHM);}
	public void setAlgorithm(String algorithm){set(ALGORITHM,algorithm);}
	public Long getCreationDate(){return get(CREATION_DATE);}
	public void setCreationDate(Long date){set(CREATION_DATE,date);}
	public String getFileSetId(){return get(FILESET_ID);}
	public void setFileSetId(String id){set(FILESET_ID,id);}
	public String getLayerId(){return get(LAYER_ID);}
	public void setLayerId(String id){set(LAYER_ID,id);}
	public String getImageList(){return get(IMAGE_LIST);}
	public void setImageList(String csvList){set(IMAGE_LIST,csvList);}
	public String getImageThumbNail(){return get(THUMBNAIL);}
	public void setImageThumbNail(String url){set(THUMBNAIL,url);}
	public String getTitle(){return get(TITLE);}
	public void setTitle(String title){set(TITLE,title);}
	public String getType(){return get(TYPE);}
	public void setType(String type){set(TYPE,type);}
	public String getSpeciesList(){return get(SPECIES_LIST);}
	public void setSpeciesList(String csvList){set(SPECIES_LIST,csvList);}
	public String getAuthor(){return get(AUTHOR);}
	public void setAuthor(String author){set(AUTHOR,author);}
	public String getCoverage(){return get(COVERAGE);}
	public void setCoverage(String coverage){set(COVERAGE,coverage);}
	public Integer getResourceId(){return get(RESOURCE_ID);}
	public void setResourceId(Integer id){set(RESOURCE_ID,id);}
	public Long getDataGenerationTime(){return get(DATA_GENERATION_TIME);}
	public void setDataGenerationTime(Long time){set(DATA_GENERATION_TIME,time);}
	public String getLayerUrl(){return get(LAYER_URL);}
	public void setLayerUrl(String url){set(LAYER_URL,url);}
	public String getLayerPreview(){return get(LAYER_PREVIEW);}
	public void setLayerPreview(String previewUri){set(LAYER_PREVIEW,previewUri);}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompoundMapItem [getImageCount()=");
		builder.append(getImageCount());
		builder.append(", isGis()=");
		builder.append(isGis());
		builder.append(", getAlgorithm()=");
		builder.append(getAlgorithm());
		builder.append(", getCreationDate()=");
		builder.append(getCreationDate());
		builder.append(", getFileSetId()=");
		builder.append(getFileSetId());
		builder.append(", getLayerId()=");
		builder.append(getLayerId());
		builder.append(", getImageList()=");
		builder.append(getImageList());
		builder.append(", getImageThumbNail()=");
		builder.append(getImageThumbNail());
		builder.append(", getTitle()=");
		builder.append(getTitle());
		builder.append(", getType()=");
		builder.append(getType());
		builder.append(", getSpeciesList()=");
		builder.append(getSpeciesList());
		builder.append(", getAuthor()=");
		builder.append(getAuthor());
		builder.append(", getCoverage()=");
		builder.append(getCoverage());
		builder.append(", getResourceId()=");
		builder.append(getResourceId());
		builder.append(", getDataGenerationTime()=");
		builder.append(getDataGenerationTime());
		builder.append(", getLayerUrl()=");
		builder.append(getLayerUrl());
		builder.append(", getLayerPreview()=");
		builder.append(getLayerPreview());
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
