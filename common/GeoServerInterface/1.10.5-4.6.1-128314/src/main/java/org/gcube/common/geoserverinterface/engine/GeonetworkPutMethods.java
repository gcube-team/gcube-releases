package org.gcube.common.geoserverinterface.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.gcube.common.geoserverinterface.Constants;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;
import org.gcube.common.geoserverinterface.HttpMethodCall;
import org.gcube.common.geoserverinterface.bean.BoundsRest;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.bean.MetadataInfo;
import org.gcube.common.geoserverinterface.bean.iso.EnvironmentConfiguration;
import org.gcube.common.geoserverinterface.bean.iso.ISOMetadataFactory;
import org.gcube.common.geoserverinterface.geonetwork.csw.MetadataISO19139;
import org.gcube.common.geoserverinterface.geonetwork.utils.StringValidator;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.xml.XML;
import org.opengis.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GeonetworkPutMethods {

	private static final Logger logger = LoggerFactory.getLogger(GeonetworkPutMethods.class);
	
	/**
	 * @uml.property  name="hMC"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private HttpMethodCall HMC = null;
	private final String suffixWMS = "SERVICE=WMS";
	private final String APPLICATIONXML = "application/xml";
	private final String METADATAINSERT = "metadata.insert";
	private final String TEXTXML = "text/xml";
	private final String NOTDEFINED = "Not defined";
	private final String CSW = "csw";
	private final String ERRORGEOSERVERURL = "Error in metadata insert ... Geoserver wms url must be not empty";
	private final String ERRORLAYERNAME = "Error in metadata insert... Layer Name must be not empty";
	private final String GROUPLAYERSLIST = "Layer-Group layers list:";
	private final String XMLHARVESTINGRUN = "xml.harvesting.run";
	private final String METADATADELETE = "metadata.delete";
	private final String GROUPSTILESLIST = "Layer-Group stiles list";
	private final String INSERTMETADATA = "Insert metadata: ";
	private final String INSERTMETADATAOK = "Insert metadata <- OK";



	public GeonetworkPutMethods(HttpMethodCall hmc){
		this.HMC = hmc;
	}

	/**
	 * 
	 * @param fileIdentifier
	 * @param workspace
	 * @param layerTitle
	 * @param layerName
	 * @param layerDescription
	 * @param category
	 * @param geoServerWmsUrl
	 * @return
	 * @throws Exception
	 */
	public String insertMetadata(String fileIdentifier, String workspace, String layerTitle, String layerName, String layerDescription, GeonetworkCategory category, String geoServerWmsUrl) throws Exception {

		String res = null;

		MetadataInfo metadataInfo = this.createMetadataInfo(fileIdentifier, workspace, layerTitle, layerName, layerDescription, geoServerWmsUrl);

		MetadataISO19139 metadata = new MetadataISO19139(metadataInfo);

		String query = "<request>"
				+ "<group>"+Constants.getGeoNetworkPublishGroupId()+"</group>"
				+ "<category>"+category.toString().toLowerCase()+"</category>"
				+ "<styleSheet>_none_</styleSheet>"
				+ "<data>"
				+ "<![CDATA["
				+ metadata.getISO19139()+"]]>" + "</data>" + "</request>";

		try {

			//			System.out.println(INSERTMETADATA + query);
			//			res = HMC.CallPost(METADATAINSERT, query, TEXTXML);
			//			System.out.println(INSERTMETADATAOK);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 
	 * @param fileIdentifier
	 * @param workspace
	 * @param layerTitle
	 * @param layerName
	 * @param layerDescription
	 * @param geoServerWmsUrl
	 * @return
	 * @throws Exception
	 */
	public String insertMetadataByCswTransaction(String fileIdentifier, String workspace, String layerTitle, String layerName, String layerDescription, String geoServerWmsUrl) throws Exception {

		String res = null;

		MetadataInfo metadataInfo = this.createMetadataInfo(fileIdentifier, workspace, layerTitle, layerName, layerDescription, geoServerWmsUrl);

		//	  	MetadataISO19139 metadata = new MetadataISO19139(fileIdentifier, layerTitle, layerName, layerDescription, geoServerWmsUrl);

		MetadataISO19139 metadata = new MetadataISO19139(metadataInfo);

		String query = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" version=\"2.0.2\" service=\"CSW\">"
				+ "<csw:Insert>"
				+ metadata.getISO19139() 
				+ "</csw:Insert>"
				+ "</csw:Transaction>";

		try {
			res = HMC.CallPost(CSW, query, APPLICATIONXML);
			//			System.out.println(INSERTMETADATAOK);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;

	}

	private MetadataInfo createMetadataInfo(String fileIdentifier, String workspace, String title, String name, String description, String url) throws Exception{

		MetadataInfo metadataInfo = new MetadataInfo();

		if(!StringValidator.isValidateString(name))
			throw new Exception(ERRORLAYERNAME);
		else
			metadataInfo.setName(name);

		if(!StringValidator.isValidateString(url))
			throw new Exception(ERRORGEOSERVERURL);
		else
			metadataInfo.setUrl(url+= "?" + this.suffixWMS);

		if(!StringValidator.isValidateString(fileIdentifier))
			metadataInfo.setFileIdentifier(UUID.randomUUID().toString());
		else
			metadataInfo.setFileIdentifier(fileIdentifier);

		//if workspace exists, it's added to layer name
		if(StringValidator.isValidateString(workspace)){ 
			metadataInfo.setName(workspace+":"+name);
		}

		if(!StringValidator.isValidateString(title))
			metadataInfo.setTitle(NOTDEFINED);
		else
			metadataInfo.setTitle(title);

		if(!StringValidator.isValidateString(description))
			metadataInfo.setDescription(NOTDEFINED);
		else
			metadataInfo.setDescription(description);

		return metadataInfo;
	}


	private void validateMetadataInfo(MetadataInfo metadataInfo) throws Exception{

		if(!StringValidator.isValidateString(metadataInfo.getName()))
			throw new Exception(ERRORLAYERNAME);

		if(!StringValidator.isValidateString(metadataInfo.getUrl()))
			throw new Exception(ERRORGEOSERVERURL);

		//add SERVICE=WMS
		String url = metadataInfo.getUrl(); 
		metadataInfo.setUrl(url+= "?"+this.suffixWMS);

		if(!StringValidator.isValidateString(metadataInfo.getFileIdentifier()))
			metadataInfo.setFileIdentifier(UUID.randomUUID().toString());

		//if workspace exists, it's added to layer name
		if(StringValidator.isValidateString(metadataInfo.getWorkspace())){ 
			String workspace = metadataInfo.getWorkspace();
			metadataInfo.setName(workspace+":"+metadataInfo.getName());
		}

		if(!StringValidator.isValidateString(metadataInfo.getTitle()))
			metadataInfo.setTitle(NOTDEFINED);

		if(!StringValidator.isValidateString(metadataInfo.getDescription()))
			metadataInfo.setDescription(NOTDEFINED);

	}

	public String insertNETCDFMetadata(String TitleName,String LayerName,String description, String FileUrl) throws Exception{

		MetadataInfo metadataInfo = new MetadataInfo();
		metadataInfo.setAbst("NETCDF data file "+TitleName);
		metadataInfo.setCategory(GeonetworkCategory.DATASETS);
		metadataInfo.setDescription(description);
		metadataInfo.setEastBoundLongitude("-180");
		metadataInfo.setWestBoundLongitude("180");
		metadataInfo.setNorthBoundLongitude("90");
		metadataInfo.setSouthBoundLongitude("-90");
		metadataInfo.setTitle(TitleName);
		metadataInfo.setEnabled(true);
		metadataInfo.setUrl(FileUrl);
		metadataInfo.setName(LayerName);
		metadataInfo.setFileIdentifier(UUID.randomUUID().toString());
		return insertRawMetadata(metadataInfo);
	}

	public String insertRawMetadata(MetadataInfo metadataInfo) throws Exception {

		String res = null;

		MetadataISO19139 metadata = new MetadataISO19139(metadataInfo);

		String query = "<request>" + "<group>"+Constants.getGeoNetworkPublishGroupId()+"</group>" + "<category>"
				+ metadataInfo.getCategory().toString().toLowerCase()
				+ "</category>" + "<styleSheet>_none_</styleSheet>" + "<data>"
				+ "<![CDATA[" + metadata.getISO19139() + "]]>" + "</data>"
				+ "</request>";

		try {
			//			System.out.println(INSERTMETADATA + query);
			res = HMC.CallPost(METADATAINSERT, query, TEXTXML);
			logger.info(INSERTMETADATAOK);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}


	public String insertMetadata(MetadataInfo metadataInfo) throws Exception {

		String res = null;

		this.validateMetadataInfo(metadataInfo);

		MetadataISO19139 metadata = new MetadataISO19139(metadataInfo);

		String query = "<request>" + "<group>"+Constants.getGeoNetworkPublishGroupId()+"</group>" + "<category>"
				+ metadataInfo.getCategory().toString().toLowerCase()
				+ "</category>" + "<styleSheet>_none_</styleSheet>" + "<data>"
				+ "<![CDATA[" + metadata.getISO19139() + "]]>" + "</data>"
				+ "</request>";

		try {
			//			System.out.println(INSERTMETADATA + query);
			res = HMC.CallPost(METADATAINSERT, query, TEXTXML);
			logger.info(INSERTMETADATAOK);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/* modified by francesco cerasuolo */
	public String insertMetadataByFeatureType(FeatureTypeRest featureTypeRest, GeonetworkCategory category, String geoServerWmsUrl) throws Exception{
		return insertMetadataByFeatureType(featureTypeRest, category, geoServerWmsUrl, null, null);
	}

	public String insertMetadataByFeatureType(FeatureTypeRest featureTypeRest, GeonetworkCategory category, String geoServerWmsUrl, String description, String abstr) throws Exception{


		if(!StringValidator.isValidateString(featureTypeRest.getName()))
			throw new Exception(ERRORLAYERNAME);

		if(!StringValidator.isValidateString(geoServerWmsUrl))
			throw new Exception(ERRORGEOSERVERURL);

		MetadataInfo metadataInfo = new MetadataInfo();

		metadataInfo.setFileIdentifier(UUID.randomUUID().toString());
		metadataInfo.setName(featureTypeRest.getName());

		if(!StringValidator.isValidateString(featureTypeRest.getTitle()))
			metadataInfo.setTitle(NOTDEFINED);
		else
			metadataInfo.setTitle(featureTypeRest.getTitle());

		BoundsRest bounds = featureTypeRest.getNativeBoundingBox();
		metadataInfo.setWestBoundLongitude(bounds.getMinx()+"");
		metadataInfo.setEastBoundLongitude(bounds.getMaxx()+"");
		metadataInfo.setSouthBoundLongitude(bounds.getMiny()+"");
		metadataInfo.setNorthBoundLongitude(bounds.getMaxy()+"");

		// added by francesco cerasuolo
		if (description!=null)
			metadataInfo.setDescription(description);
		if (abstr!=null)
			metadataInfo.setAbst(abstr);

		//if workspace exists, it's added to layer name
		if(StringValidator.isValidateString(featureTypeRest.getWorkspace()))
			metadataInfo.setName(featureTypeRest.getWorkspace()+":"+featureTypeRest.getName());

		//add SERVICE=WMS
		metadataInfo.setUrl(geoServerWmsUrl+= "?"+this.suffixWMS);

		MetadataISO19139 metadataISO = new MetadataISO19139(metadataInfo);

		return actuallySendMetadata(metadataISO.getISO19139(), category.toString(),false);
	}


	public String insertMetadataByFeatureType(FeatureTypeRest featureTypeRest,String defaultStyle, GeonetworkCategory category, String geoServerWmsUrl, Metadata toPublish) throws Exception{


		if(!StringValidator.isValidateString(featureTypeRest.getName()))
			throw new Exception(ERRORLAYERNAME);

		if(!StringValidator.isValidateString(geoServerWmsUrl))
			throw new Exception(ERRORGEOSERVERURL);

		//if workspace exists, it's added to layer name
		String layerName=StringValidator.isValidateString(featureTypeRest.getWorkspace())?
					featureTypeRest.getWorkspace()+":"+featureTypeRest.getName():featureTypeRest.getName();
					
		BoundsRest bounds = featureTypeRest.getNativeBoundingBox();
		String bbox=bounds.getMiny()+","+bounds.getMinx()+","+bounds.getMaxy()+","+bounds.getMaxx();
				
		
		((DefaultMetadata) toPublish).setDistributionInfo(ISOMetadataFactory.getDistributionByLayer(layerName, geoServerWmsUrl, defaultStyle, bbox, EnvironmentConfiguration.getConfiguration()));
				

		return actuallySendMetadata(XML.marshal(toPublish), category.toString(), true);		
	}


	public String insertMetadataByGroupRest(GroupRest groupRest, GeonetworkCategory category, String geoServerWmsUrl) throws Exception{

		String res = null;

		if(!StringValidator.isValidateString(groupRest.getName()))
			throw new Exception(ERRORLAYERNAME);

		if(!StringValidator.isValidateString(geoServerWmsUrl))
			throw new Exception(ERRORGEOSERVERURL);

		MetadataInfo metadataInfo = new MetadataInfo();

		metadataInfo.setFileIdentifier(UUID.randomUUID().toString());
		metadataInfo.setName(groupRest.getName());
		metadataInfo.setTitle(groupRest.getName());

		ArrayList<String> groupList = groupRest.getLayers();
		String tempAbs = GROUPLAYERSLIST;

		for (String layer : groupList){
			tempAbs += " " + layer + " ";
		}

		metadataInfo.setAbst(tempAbs);

		String description = GROUPSTILESLIST;

		Collection<String> stylesList =  groupRest.getStyles();

		for (String style : stylesList){
			description += " " + style + " ";
		}

		//add SERVICE=WMS
		metadataInfo.setUrl(geoServerWmsUrl+= "?"+this.suffixWMS);

		metadataInfo.setDescription(description);

		MetadataISO19139 metadata = new MetadataISO19139(metadataInfo);

		String query = "<request>"
				+ "<group>"+Constants.getGeoNetworkPublishGroupId()+"</group>"
				+ "<category>"+category.toString().toLowerCase()+"</category>"
				+ "<styleSheet>_none_</styleSheet>"
				+ "<data>"
				+ "<![CDATA["
				+ metadata.getISO19139()+"]]>" + "</data>" + "</request>";

		try {

			//			System.out.println(INSERTMETADATA + query);
			res = HMC.CallPost(METADATAINSERT, query, TEXTXML);
			logger.info(INSERTMETADATAOK);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String deleteMetadataById(String id){

		String res = null;
		try {
			res = HMC.CallPost(METADATADELETE, 
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
							"<request>" +
							"<id>"+id+"</id>"+
							"</request>",
							APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

		public String updateHarvesting(String id) {
			String res = null;
			try {
				res = HMC.CallPost(XMLHARVESTINGRUN, 
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
								"<request> " +
								"<id>"+id+"</id>"+
						"</request>", 
						APPLICATIONXML);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return res;
		}

	
	private String actuallySendMetadata(String theMetadata,String category,boolean validate){
		String res=null;
		try {
		String query = "<request>"
				+ "<group>"+Constants.getGeoNetworkPublishGroupId()+"</group>"
				+ "<category>"+category.toLowerCase()+"</category>"
				+ "<styleSheet>_none_</styleSheet>"
				+ (validate?"<validate>on</validate>":"")
				+ "<data>"
				+ "<![CDATA["
				+ theMetadata+"]]>" + "</data>" + "</request>";

			//			System.out.println(INSERTMETADATA + query);
			res = HMC.CallPost(METADATAINSERT, query, TEXTXML);
			logger.info(INSERTMETADATAOK);


		} catch (Exception e) {
			logger.error("Unable to send meta ",e);
		}
		
		return res;
	}
	
	
	//******************** URLs
	
	
	
	
}
