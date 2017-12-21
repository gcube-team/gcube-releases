/**
 *
 */
package org.gcube.portlets.user.geoexplorer.server.util;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.server.beans.GeoserverBaseUri;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;
import org.gcube.portlets.user.geoexplorer.shared.MetadataItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.ResponsiblePartyItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.citation.CitationItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo.AddressItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo.ContactItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo.OnlineResourceItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo.TelephoneItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.distributioninfo.DigitalTransferOptionsItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.distributioninfo.DistributionInfoItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.distributioninfo.DistributorItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.extent.ExtentItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.extent.GeographicBoundingBoxItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.identification.DataIdentificationItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.identification.KeywordsItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.quality.AlgorithmItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.quality.DataQualityItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.quality.LineageItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.quality.ProcessStepItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.quality.ProcessingItem;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geoutility.GeoWmsServiceUtility;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.gcube.spatial.data.geoutility.wms.WmsUrlValidator;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.MetadataExtensionInformation;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Telephone;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.distribution.Distributor;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.lineage.Algorithm;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.metadata.lineage.Processing;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.quality.Scope;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.util.InternationalString;


/**
 * The Class MetadataConverter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 4, 2016
 */
public class MetadataConverter {


	public static final String GEOSERVER = "/geoserver";
//	private static final String WMS = "wms";
	public static final String SERVICE_WMS = "service=wms";
	protected static Logger logger = Logger.getLogger(MetadataConverter.class);
	public static final String NOT_FOUND = "";

	// transform layer obtained from csw into GeoExplorer LayerItem
	/**
	 * Gets the metadata item from metadata uuid.
	 *
	 * @param geoNetworkReader the geo network reader
	 * @param uuid the uuid
	 * @return the metadata item from metadata uuid
	 * @throws Exception the exception
	 */
	public static MetadataItem getMetadataItemFromMetadataUUID(GeoNetworkReader geoNetworkReader, String uuid) throws Exception{

			MetadataItem metadataItem = new MetadataItem(validateString(uuid));

			if(uuid==null || uuid.isEmpty())
				throw new Exception("uuid is null or empty");

			logger.trace("fetching data for medatada: "+uuid);

			try {

				Metadata meta = geoNetworkReader.getById(uuid);

				if(meta!=null){

					//FILE IDENTIFIER
					metadataItem.setFileIdentifier(validateString(meta.getFileIdentifier()));

					//LANGUAGE
					Locale language = meta.getLanguage();
					String lang = language != null? language.toString():NOT_FOUND;
					metadataItem.setLanguage(lang);

					//CharacterSet
					CharacterSet charset = meta.getCharacterSet();
					String characterSet = convertCharacterSet(charset);
					metadataItem.setCharacterSet(characterSet);

					//parentIdentifier
					String parentId = meta.getParentIdentifier();
					metadataItem.setParentIdentifier(validateString(parentId));

					//SCOPE CODE??
					/*for (ScopeCode sc: meta.getHierarchyLevels()) {
					}*/

					//HierarchyLevelNames
					List<String> hierarchyLevelName = new ArrayList<String>();

					if(meta.getHierarchyLevelNames()!=null){
						for (String levelName : meta.getHierarchyLevelNames()) {
							hierarchyLevelName.add(levelName);
						}
					}

					metadataItem.setHierarchyLevelName(hierarchyLevelName);

					//Contacts
					Collection<? extends ResponsibleParty> contacts = meta.getContacts();
					List<ResponsiblePartyItem> listResponsibleParty = new ArrayList<ResponsiblePartyItem>();

					if(contacts!=null){

						logger.trace("found Responsible Party size: "+contacts.size());

						for (ResponsibleParty responsibleParty : contacts) {
							listResponsibleParty.add(convertResponsiblePartyItem(responsibleParty));
						}
					}

					//set contacts
					metadataItem.setContacts(listResponsibleParty);

					//dateStamp
					if(meta.getDateStamp()!=null)
						metadataItem.setDateStamp(validateString(meta.getDateStamp().toString()));

					//metadataStandardName
					metadataItem.setMetadataStandardName(validateString(meta.getMetadataStandardName()));

					//getMetadataStandardVersion
					metadataItem.setMetadataStandardVersion(validateString(meta.getMetadataStandardVersion()));

					//dataSetURI
					metadataItem.setDataSetURI(validateString(meta.getDataSetUri()));

					Collection<Locale> locales = meta.getLocales();

					//locales??
					if(locales!=null){
						metadataItem.setLocale(new ArrayList<Locale>(locales));
					}

					//getSpatialRepresentationInfo??
					if(meta.getSpatialRepresentationInfo()!=null){

						logger.trace("found Spatial Representation Info size: "+meta.getReferenceSystemInfo().size());
						/*for (SpatialRepresentation spatialRepresentation : sri) {
							if(spatialRepresentation instanceof VectorSpatialRepresentation){
							}
						}*/
						metadataItem.setSpatialRepresentation(meta.getSpatialRepresentationInfo());
					}

					//referenceSystemInfo ??
					if(meta.getReferenceSystemInfo()!=null){
						logger.trace("found Reference System Info size: "+meta.getReferenceSystemInfo().size());
						Collection<? extends ReferenceSystem> refSys = meta.getReferenceSystemInfo();
//						for (ReferenceSystem refSys : meta.getReferenceSystemInfo()) {
//
//						}
						metadataItem.setReferenceSystems(refSys);
					}

					//metadataExtensionInfo ??
					if(meta.getMetadataExtensionInfo()!=null){

						logger.trace("found Metadata Extension Info size: "+meta.getMetadataExtensionInfo().size());
						Collection<? extends MetadataExtensionInformation> extensionsInfo = meta.getMetadataExtensionInfo();
//						for (MetadataExtensionInformation extInfo : meta.getMetadataExtensionInfo()) {
//
//						}
						metadataItem.setExtensionsInfo(extensionsInfo);
					}

					//identificationInfo ??
					if(meta.getIdentificationInfo()!=null){

						logger.trace("found Identification Info size: "+meta.getIdentificationInfo().size());
						DataIdentificationItem identificationItem = new DataIdentificationItem();

						for (Identification info : meta.getIdentificationInfo()) {
							//CREDITS
							if(info.getCredits()!=null){
								Collection<String> credits = info.getCredits();
								identificationItem.setCredits(credits);
							}else
								logger.info("Credits is null for: "+uuid);

							//CITATION
							if(info.getCitation()!=null){
								CitationItem citationItem = convetCitationItem(info.getCitation());
								identificationItem.setCitation(citationItem);

							}else
								logger.info("Citation is null for: "+uuid);

							//ABSTRACT
							String metaAbstract = "";
							if(info.getAbstract()!=null)
								 metaAbstract = info.getAbstract() != null? info.getAbstract().toString(): "";
							else
								logger.info("Abstract is null for: "+uuid);

							identificationItem.setAbstracts(metaAbstract);

							//purpose
							String purpose = info.getPurpose()!=null?info.getPurpose().toString():NOT_FOUND;
							identificationItem.setPurpose(purpose);

							//resourceMaintenance -> advanced

							//graphicOverview -> advanced

							//descriptiveKeywords
							if(info.getDescriptiveKeywords()!=null){

								List<KeywordsItem> listKeywords = new ArrayList<KeywordsItem>();
								Collection<? extends Keywords> keys = info.getDescriptiveKeywords();

								for (Keywords keywords : keys) {

								String type =
									keywords.getType() != null
										? keywords.getType().toString()
										: NOT_FOUND;
								Collection<String> keywordsString =
									convertCollectionInternationalStringToString(keywords.getKeywords());
								CitationItem thesaurusName =
									convetCitationItem(keywords.getThesaurusName());
								listKeywords.add(new KeywordsItem(
									keywordsString, type, thesaurusName)); // ThesaurusName
								}
								identificationItem.setDescriptiveKeywords(listKeywords);
							}

							boolean isDataIdentification = info instanceof DataIdentification;
							logger.trace("is DataIdentification "+isDataIdentification);

							if(isDataIdentification){
								DataIdentification infoDataIdentification =
									(DataIdentification) info;
								List<String> charsets = new ArrayList<String>();
								if (infoDataIdentification.getCharacterSets() != null) {
									for (CharacterSet chars : infoDataIdentification.getCharacterSets()) {
										charsets.add(convertCharacterSet(chars));
									}
								}
								identificationItem.setCharacterSet(charsets);
								identificationItem.setLanguage(new ArrayList<Locale>(
									infoDataIdentification.getLanguages()));
								List<String> topicCategoryNames =
									new ArrayList<String>();
								for (TopicCategory topicCategory : infoDataIdentification.getTopicCategories()) {
									topicCategoryNames.add(topicCategory.name());
								}
								identificationItem.setTopicCategory(topicCategoryNames);
								List<ExtentItem> listExtentItems =
									new ArrayList<ExtentItem>();

								//EXTENT
								for (Extent extent : infoDataIdentification.getExtents()) {

									Collection<? extends GeographicExtent> geoElements = extent.getGeographicElements();
									List<GeographicBoundingBoxItem> geoBBoxItems = new ArrayList<GeographicBoundingBoxItem>();

									if(geoElements!=null){

										for (GeographicExtent geographicExtent : geoElements) {

											GeographicBoundingBoxItem bboxItem = new GeographicBoundingBoxItem();

											bboxItem.setExtentTypeCode(geographicExtent.getInclusion());

											if(geographicExtent instanceof GeographicBoundingBox){

												GeographicBoundingBox bbox =(GeographicBoundingBox) geographicExtent;
												// System.out.println(bbox.toString());
												bboxItem.setEastBoundLongitude(bbox.getEastBoundLongitude());
												bboxItem.setWestBoundLongitude(bbox.getWestBoundLongitude());
												bboxItem.setSouthBoundLatitude(bbox.getSouthBoundLatitude());
												bboxItem.setNorthBoundLatitude(bbox.getNorthBoundLatitude());
												bboxItem.setBBOX(bbox.toString());

											}
											geoBBoxItems.add(bboxItem);
										}
									}

									String description = convertInternationalStringToString(extent.getDescription());
									ExtentItem ext = new ExtentItem(description, geoBBoxItems);
									listExtentItems.add(ext);

//										System.out.println(ext);
								}
								identificationItem.setExtent(listExtentItems);
							}
//							System.out.println(info);
							metadataItem.setIdentificationInfo(identificationItem);
						}
					}

					//getContentInfo ??
					if(meta.getContentInfo()!=null){
						logger.trace("found ContentInfo Info size: "+meta.getContentInfo().size());
						for (ContentInformation contInfo : meta.getContentInfo()) {

						}
					}

					DistributionInfoItem distributionInfoItem = new DistributionInfoItem();

					//distributionInfo ??
					if(meta.getDistributionInfo()!=null && meta.getDistributionInfo()!=null){

						logger.trace("found Distributors size: "+meta.getDistributionInfo().getDistributors().size());
						int i= 0;
						List<DistributorItem> distributorsItem = new ArrayList<DistributorItem>();

						if(meta.getDistributionInfo().getDistributors()!=null){
							for (Distributor item: meta.getDistributionInfo().getDistributors()) {
								ResponsiblePartyItem resp = convertResponsiblePartyItem(item.getDistributorContact());
								distributorsItem.add(new DistributorItem(resp));
								System.out.println(++i +" item Distributor options: "+item);
							}
						}

						distributionInfoItem.setDistributors(distributorsItem);
						// i= 0;
						// for (Format item:
						// meta.getDistributionInfo().getDistributionFormats()) {
						//
						// System.out.println(++i +" item Format options: "+item);
						//
						// }
						List<DigitalTransferOptionsItem> digitalTransfOptItem = new ArrayList<DigitalTransferOptionsItem>();

						//Transfer Options
						for (DigitalTransferOptions  item: meta.getDistributionInfo().getTransferOptions()) {

							String unit = convertInternationalStringToString(item.getUnitsOfDistribution());
//								System.out.println(++i +" item DigitalTransferOptions options: "+item);
							if(item.getOnLines()!=null){
								Collection<? extends OnlineResource> onlineResources = item.getOnLines();
								List<OnlineResourceItem> listOnlineResourceItem = new ArrayList<OnlineResourceItem>();

								for (OnlineResource onlineResource : onlineResources) {
									listOnlineResourceItem.add(convertOnLineResourceItem(onlineResource));

								}
								digitalTransfOptItem.add(new DigitalTransferOptionsItem(item.getTransferSize(), unit, listOnlineResourceItem));
							}
						}
						distributionInfoItem.setTransferOptions(digitalTransfOptItem);
					}

					metadataItem.setDistributionInfo(distributionInfoItem);

					List<DataQualityItem> listDQInfoItem = new ArrayList<DataQualityItem>();

					//DataQualityInfo
					if(meta.getDataQualityInfo()!=null){

						logger.trace("found Data Quality Info size: "+meta.getDataQualityInfo().size());

						for (DataQuality dataQ : meta.getDataQualityInfo()) {

							DataQualityItem dataQualityItem = new DataQualityItem();

							if(dataQ.getLineage()!=null){

								LineageItem lineageItem = new LineageItem();
								String statment = convertInternationalStringToString(dataQ.getLineage().getStatement());
								Collection<? extends ProcessStep> preocessSteps = dataQ.getLineage().getProcessSteps();
								List<ProcessStepItem> listProcessStepItem = new ArrayList<ProcessStepItem>();

								//preocessSteps
								if(preocessSteps!=null){

									for (ProcessStep processStep : preocessSteps) {

										ProcessStepItem processStepItem = new ProcessStepItem();
										//DESCRIPTION
										String descr = convertInternationalStringToString(processStep.getDescription());
										//DATE TIME
										Date dateTime = processStep.getDate();

										//PROCESSING
										if(processStep.getProcessingInformation()!=null){

											Processing proc = processStep.getProcessingInformation();
											ProcessingItem preocessingItem;
											String identifier = proc.getIdentifier()!=null?proc.getIdentifier().toString():"";
											List<CitationItem> softwareCitations = new ArrayList<CitationItem>();

											if(proc.getSoftwareReferences()!=null){
												Collection<? extends Citation>  softwareReference = proc.getSoftwareReferences();

												if(softwareReference!=null){
													for (Citation citation : softwareReference) {
														softwareCitations.add(convetCitationItem(citation));
													}
												}
											}

											String procedureDescription = convertInternationalStringToString(proc.getProcedureDescription());
											List<CitationItem> documentation = new ArrayList<CitationItem>();

											if(proc.getDocumentations()!=null){
												Collection<? extends Citation> docs = proc.getDocumentations();

												if(docs!=null){
													for (Citation citation : docs) {
														documentation.add(convetCitationItem(citation));
													}
												}
											}

											String runTimeParameters = convertInternationalStringToString(proc.getRunTimeParameters());
											List<AlgorithmItem> algorithm = new ArrayList<AlgorithmItem>();
											Collection<? extends Algorithm> algorithms = proc.getAlgorithms();

											if(algorithms!=null){

												for (Algorithm algorithm2 : algorithms) {

													String descrip = convertInternationalStringToString(algorithm2.getDescription());
													CitationItem citationItem = convetCitationItem(algorithm2.getCitation());
													algorithm.add(new AlgorithmItem(citationItem, descrip));
												}
											}
											processStepItem.setProcessingInformation(new ProcessingItem(identifier, softwareCitations, procedureDescription, documentation, runTimeParameters, algorithm));
										}


										//rationale
										String rationale = convertInternationalStringToString(processStep.getRationale());

										List<ResponsiblePartyItem> listProcessor = new ArrayList<ResponsiblePartyItem>();
										if(processStep.getProcessors()!=null){

											for (ResponsibleParty  process: processStep.getProcessors()) {
												listProcessor.add(convertResponsiblePartyItem(process));
											}

										}
										processStepItem.setDescription(descr);
										processStepItem.setRationale(rationale);
										processStepItem.setDateTime(dateTime);
										processStepItem.setProcessor(listProcessor);
//											listProcessStepItem.add(new ProcessStepItem(descr, rationale, dateTime, listProcessor, listProcessItems));
										listProcessStepItem.add(processStepItem);
									}
								}

								lineageItem.setStatement(statment);
								lineageItem.setProcessStep(listProcessStepItem);
								dataQualityItem.setLineage(lineageItem);

							}

							//Data quality scope
							if(dataQ.getScope()!=null){
								ScopeCode codeLevel = dataQ.getScope().getLevel();
								String scopeCode =
									codeLevel != null ? codeLevel.name() : "";
								dataQualityItem.setScope(scopeCode);
							}
							listDQInfoItem.add(dataQualityItem);

						}
						metadataItem.setDataQualityInfo(listDQInfoItem);
					}

					}else
						logger.error("geonetwork response null on querying of metadata with UUID "+uuid);

//						layerItems.add(new LayerItem(metadata.getUUID(), onlineResourceName, onlineResourceName, citationTitle, metadataAbstract, onlineResourceUrl));

				} catch (GNLibException e) {
					logger.error("Error, metadata with UUID "+uuid + " has throw GNLibException exception", e);
//					e.printStackTrace();
				} catch (GNServerException e) {
					logger.error("Error, metadata with UUID "+uuid + " has throw GNServerException exception",e);
//					e.printStackTrace();
				} catch (JAXBException e) {
					logger.error("Error, metadata with UUID "+uuid + " has throw JAXBException exception", e);
				}


			logger.trace("Layer Item converted, return");
			return metadataItem;
		}


	/**
	 * Convert character set.
	 *
	 * @param charset the charset
	 * @return the string
	 */
	public static String convertCharacterSet(CharacterSet charset){
		String characterSet = charset!=null? charset.toString():NOT_FOUND;
		return characterSet;
	}



	/**
	 * converts the layer obtained from GeonetworkMetadata in the GeoExplorer LayerItem.
	 *
	 * @param geoNetworkReader the geo network reader
	 * @param metadata the metadata
	 * @return the layer item from metadata
	 */
	public static LayerItem getLayerItemFromMetadata(GeoNetworkReader geoNetworkReader, GeonetworkMetadata metadata) {
		return getLayerItemFromMetadataUUID(geoNetworkReader, metadata.getUuid());
	}

	/**
	 * converts the layer retrieved from GeonetworkMetadata UUID in the GeoExplorer LayerItem.
	 *
	 * @param geoNetworkReader the geo network reader
	 * @param uuid the uuid
	 * @return the layer item from metadata uuid
	 */
	public static LayerItem getLayerItemFromMetadataUUID(GeoNetworkReader geoNetworkReader, String uuid) {

		//LAYER TITLE
		String citationTitle = "";
		//LAYER NAME
		String layerName = "";
		//Publication Date
		Date publicationDate = null;
		//TOPIC CATEGORY
		String topicCategory="";
		//SCOPE CODE
		String scopeCode = "";
		//GEOSERVER URL
		String geoserverBaseUrlOnlineResource = "";
		//LAYERS Styles
		List<String> styles = new ArrayList<String>();
		//ABSTRACT
		String metaAbstract = "";
//		Keywords
		List<String> listKeywords = new ArrayList<String>();

		//USED TO ADD WMS NOT STANDARD PARAMETER TO GISVIEWER
		HashMap<String, String> mapWmsNotStandard = null;

		boolean isNcWms = false;

		logger.trace("requesting layer item data for medatada: "+uuid);
		try {

			Metadata meta = geoNetworkReader.getById(uuid);
			publicationDate = meta.getDateStamp();

			if(meta.getIdentificationInfo()!=null){
//				logger.trace("found Identification Info size: "+meta.getIdentificationInfo().size());
				for (Identification info : meta.getIdentificationInfo()) {

					if(info!=null){
						Citation citation = info.getCitation();
						if(citation!=null){
							citationTitle = citation.getTitle() != null? citation.getTitle().toString():"";
//								logger.trace("found citation Title: "+citationTitle);
						}else
							logger.info("Title is null for: "+uuid);

						if(info.getAbstract()!=null)
							 metaAbstract = info.getAbstract() != null? info.getAbstract().toString(): "";
						else
							logger.info("Abstract is null for: "+uuid);

						//descriptiveKeywords
						if(info.getDescriptiveKeywords()!=null){
							Collection<? extends Keywords> keys = info.getDescriptiveKeywords();
							for (Keywords keywords : keys) {
								Collection<String> keywordsString =
									convertCollectionInternationalStringToString(keywords.getKeywords());
								listKeywords.addAll(keywordsString); // Keywords
							}
						}

						if(!citationTitle.isEmpty() && !metaAbstract.isEmpty() && listKeywords.size()>0)
							break;
//								logger.trace("found metadata Abstract: "+metadataAbstract);
					}else
						logger.warn("Identification is null for: "+uuid);
				}
			}

			if(meta.getDataQualityInfo()!=null){
				for (DataQuality dataQuality : meta.getDataQualityInfo()) {
					Scope scope = dataQuality.getScope()!=null?dataQuality.getScope():null;
					 ScopeCode code = scope!=null?scope.getLevel():null;
					 scopeCode = code!=null?code.name():"";
					 break;
				}
			}

			String fullWmsPath = "";
//			boolean isOwsService = false;
			boolean foundGeoserverUrl = false;
			GeoserverBaseUri tempBaseUri = null;

			if(meta.getDistributionInfo()!=null && meta.getDistributionInfo()!=null){

				for (DigitalTransferOptions  item: meta.getDistributionInfo().getTransferOptions()) {
//						System.out.println(++i +" item DigitalTransferOptions options: "+item);
					if(item.getOnLines()!=null){
						Collection<? extends OnlineResource> onlineResources = item.getOnLines();

						for (OnlineResource onlineResource : onlineResources) {
							String geoserverUrl = onlineResource.getLinkage()!=null? onlineResource.getLinkage().toString():"";
							layerName= onlineResource.getName()!=null? onlineResource.getName():"";
							logger.trace("onlineResource layerName: "+layerName);


							//FIND ONLINE RESOURCES WITH GEOSERVER WMS PROTOCOL
							if(!geoserverUrl.isEmpty()){
								fullWmsPath = geoserverUrl;

								//IS OWS OR WMS?
								if(GeoWmsServiceUtility.isWMSService(geoserverUrl)){
									logger.trace("found "+SERVICE_WMS+" url "+geoserverUrl);
//									isOwsService = GeoWmsServiceUtility.isOWSSerice(geoserverUrl);
									tempBaseUri = getGeoserverBaseUri(geoserverUrl);

									if(tempBaseUri.getBaseUrl().compareTo(geoserverUrl)!=0){ //base url was found
										foundGeoserverUrl = true;
										geoserverBaseUrlOnlineResource = tempBaseUri.getBaseUrl();
//										layerName= onlineResource.getName()!=null? onlineResource.getName():"";
//										//TODO TEMPORARY - REMOVE WORKSPACE:
//										if(layerName!=null && !layerName.isEmpty())
//											layerName = cleanOnlineResourceName(layerName);
										break;

									}else{
										geoserverBaseUrlOnlineResource = geoserverUrl;
									}
								}

								if(!foundGeoserverUrl)
									logger.trace(SERVICE_WMS+" not found for "+uuid);

							}
						}
					}
				}
			}

			String baseWmsServiceUrl = geoserverBaseUrlOnlineResource;
			String fullWmsUrlRequest = fullWmsPath;

			if(foundGeoserverUrl && !geoserverBaseUrlOnlineResource.isEmpty()){
				logger.trace("validating..."+geoserverBaseUrlOnlineResource);

				String value = WmsUrlValidator.getValueOfParameter(WmsParameters.LAYERS, fullWmsPath);
				if(value!=null && !value.isEmpty() && layerName.compareToIgnoreCase(value)!=0){
					logger.info("Layer Name into wms request IS NOT EQUAL to layer name into OnlineResource Metadata,  assigning layer name like wms parameter: "+value);
					layerName = value;
				}

				//TODO MOVE THIS INTO GISVIEWER
				/*WmsGeoExplorerUrlValidator	validator = new WmsGeoExplorerUrlValidator(geoserverBaseUrlOnlineResource, fullWmsPath, layerName, isOwsService);
				baseWmsServiceUrl = validator.getBaseWmsServiceUrl();
				fullWmsUrlRequest = validator.getFullWmsUrlRequest(true, true);
				layerName = validator.getLayerName();
				versionWms = validator.getValueOfParsedWMSParameter(WmsParameters.VERSION);
				crs = validator.getValueOfParsedWMSParameter(WmsParameters.CRS);

				if(validator.getMapWmsNoStandardParams()!=null){
					mapWmsNotStandard = new HashMap<String, String>();
					mapWmsNotStandard.putAll(validator.getMapWmsNoStandardParams());
				}

				GeoGetStylesUtility geoGS = new GeoGetStylesUtility(fullWmsUrlRequest, validator.getUrlValidator());
				mapWmsNotStandard.putAll(geoGS.getMapNcWmsStyles());
				styles = geoGS.getGeoStyles();
				isNcWms = geoGS.isNcWms();*/

			}else{
				baseWmsServiceUrl = "";
				fullWmsUrlRequest = geoserverBaseUrlOnlineResource;
			}

			logger.trace("storing geoserver url.. "+geoserverBaseUrlOnlineResource +", for uuid: "+uuid);
			logger.trace("storing baseWmsServiceUrl.. "+baseWmsServiceUrl +", for uuid: "+uuid);
			logger.trace("storing fullWmsUrlRequest.. "+fullWmsUrlRequest +", for uuid: "+uuid);
			logger.trace("Conversion for Layer Item with UUID "+uuid+" completed");

			//TODO UPDATE THIS

			return new LayerItem(uuid, citationTitle, layerName, topicCategory, publicationDate, scopeCode, geoserverBaseUrlOnlineResource, baseWmsServiceUrl, fullWmsUrlRequest, false, metaAbstract, listKeywords);

//			LayerItem li = new LayerItem(uuid, citationTitle, layerName, topicCategory, publicationDate, scopeCode,geoserverBaseUrlOnlineResource,baseWmsServiceUrl, fullWmsUrlRequest, false, styles, metaAbstract, listKeywords, mapWmsNotStandard,isNcWms);
//			li.setVersionWMS(versionWms); //TODO //REMOVE THIS
//			li.setCrsWMS(crs); //TODO //REMOVE THIS
//			return li;
		} catch (Exception e) {
			logger.error("Metadata with UUID "+uuid + " has thrown an exception", e);

			String error = "Error on layer conversion with ";
			if(citationTitle!=null && !citationTitle.isEmpty())
				error += citationTitle;
			else
				error += uuid;

			return new LayerItem(uuid, error, layerName, topicCategory, publicationDate, scopeCode, geoserverBaseUrlOnlineResource, "", "", false, metaAbstract, listKeywords);
//			return new LayerItem(uuid, error, layerName, topicCategory, publicationDate, scopeCode,geoserverBaseUrlOnlineResource,"", "", false, styles, metaAbstract, listKeywords, mapWmsNotStandard, isNcWms);
		}

	}

	/**
	 * Convert collection international string to string.
	 *
	 * @param intStrings the int strings
	 * @return the collection
	 */
	public static Collection<String> convertCollectionInternationalStringToString(Collection<? extends InternationalString> intStrings){

		List<String> listString = new ArrayList<String>();

		if(intStrings==null)
			return listString;

		for (InternationalString internationalString : intStrings) {
			listString.add(convertInternationalStringToString(internationalString));
		}

		return listString;
	}

	/**
	 * Convert international string to string.
	 *
	 * @param intStrings the int strings
	 * @return the string
	 */
	public static String convertInternationalStringToString(InternationalString intStrings){

		String tempString = "";

		if(intStrings==null)
			return tempString;

		return intStrings.toString(java.util.Locale.getDefault());
	}


	/**
	 * Validate string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String validateString(String value){

		if(value==null || value.isEmpty())
			return NOT_FOUND;
		return value;
	}

	/**
	 * Remove each string after "?".
	 *
	 * @param uri the uri
	 * @return the string
	 */
	public static String cleanUri(String uri){

		if(uri==null)
			return "";

		// remove each string after "?"
		int index = uri.indexOf("?");
		if (index != -1)
			uri = uri.substring(0, index);

		// remove suffix "/wms" or "/wms/"

		if(uri.contains("/wms"))
			return uri = uri.replaceFirst("(/wms)$", "").replaceFirst("(/wms/)$", "");
		else if(uri.contains("/ows"))
			return uri = uri.replaceFirst("(/ows)$", "").replaceFirst("(/ows/)$", "");

		return uri;

	}

	/**
	 * Gets the geoserver base uri.
	 *
	 * @param uri the uri
	 * @return the input uri without the parameters, (the uri substring from start to index of '?' char (if exists)) if geoserver base url not found,
	 * geoserver url otherwise
	 */
	public static GeoserverBaseUri getGeoserverBaseUri(String uri){

		GeoserverBaseUri geoserverBaseUri = new GeoserverBaseUri();

		if(uri==null)
			return geoserverBaseUri; //uri is empty

//		 Remove each string after "?"

		int end = uri.toLowerCase().lastIndexOf("?");

		if(end==-1){
			logger.trace("char ? not found in geoserver uri, return: "+uri);
			return geoserverBaseUri; //uri is empty
		}

		String geoserverUrl = uri.substring(0, uri.toLowerCase().lastIndexOf("?"));

		int index = geoserverUrl.lastIndexOf(GEOSERVER);

		if(index>-1){ //FOUND the string GEOSERVER into URL
			logger.trace("found geoserver string: "+GEOSERVER+" in "+geoserverUrl);

			//THERE IS SCOPE?
			int lastSlash = geoserverUrl.lastIndexOf("/");
			int includeGeoserverString = index+GEOSERVER.length();
			int endUrl = lastSlash>includeGeoserverString?lastSlash:includeGeoserverString;

			logger.trace("indexs - lastSlash: ["+lastSlash+"],  includeGeoserverString: ["+includeGeoserverString+"], endUrl: ["+endUrl+"]");

			int startScope = includeGeoserverString+1<endUrl?includeGeoserverString+1:endUrl; //INCLUDE SLASH
			String scope = geoserverUrl.substring(startScope, endUrl);

			logger.trace("geoserver url include scope: "+geoserverUrl.substring(includeGeoserverString, endUrl));

			geoserverBaseUri.setBaseUrl(geoserverUrl.substring(0, endUrl));
			geoserverBaseUri.setScope(scope);

			return geoserverBaseUri;

		}else{
			logger.trace("the string 'geoserver' not found in "+geoserverUrl);
			// GET LAST INDEX OF '/' AND CONCATENATE GEOSERVER
			String urlConn = geoserverUrl.substring(0, geoserverUrl.lastIndexOf("/"))+GEOSERVER;
			logger.trace("tentative concatenating string 'geoserver' at http url "+urlConn);

			try {

				if(HttpRequestUtil.urlExists(urlConn, false)){
					logger.trace("url: "+urlConn+" - open a connection, return "+urlConn);
					geoserverBaseUri.setBaseUrl(urlConn);
					return geoserverBaseUri;
				}
				else
					logger.trace("url: "+urlConn+" - not open a connection");

			} catch (Exception e) {
				logger.error("url connection is wrong at :"+urlConn);
			}

			String uriWithoutParameters = uri.substring(0, end);
			logger.trace("url connection, returned: "+uriWithoutParameters);
			geoserverBaseUri.setBaseUrl(uriWithoutParameters);
			return geoserverBaseUri;
		}
	}


	/**
	 * Split :.
	 *
	 * @param name the name
	 * @return the string
	 */
	public static String cleanOnlineResourceName(String name){

		if(name!=null && name.indexOf(":")>0){

			String[] splitName = name.split(":");
			return name = splitName.length==2 ? splitName[1] : name;
		}

		return name;
	}

	/**
	 * Convert responsible party item.
	 *
	 * @param responsibleParty the responsible party
	 * @return the responsible party item
	 */
	public static ResponsiblePartyItem convertResponsiblePartyItem(ResponsibleParty responsibleParty){

		ResponsiblePartyItem rp = new ResponsiblePartyItem();
		rp.setIndividualName(validateString(responsibleParty.getIndividualName()));

		String orgName = responsibleParty.getOrganisationName()!=null? responsibleParty.getOrganisationName().toString():NOT_FOUND;
		rp.setOrganisationName(orgName);

		String posName = responsibleParty.getPositionName()!=null? responsibleParty.getPositionName().toString():NOT_FOUND;
		rp.setPositionName(posName);

		String role = responsibleParty.getRole()!=null?  responsibleParty.getRole().toString():NOT_FOUND;
		rp.setRole(role);

		ContactItem contactItem = new ContactItem();
		Contact contactInfo = responsibleParty.getContactInfo();

		if(contactInfo!=null){

			String contactInstr = contactInfo.getContactInstructions()!=null?contactInfo.getContactInstructions().toString():NOT_FOUND;
			contactItem.setContactInstructions(contactInstr);

			String hos = contactInfo.getHoursOfService()!=null?contactInfo.getHoursOfService().toString():NOT_FOUND;
			contactItem.setHoursOfService(hos);
			AddressItem addressItem = new AddressItem();

			//ADDRESS
			if(contactInfo.getAddress()!=null){

				//FILL ADDRESS
				String fill = contactInfo.getAddress().getAdministrativeArea()!=null?contactInfo.getAddress().getAdministrativeArea().toString():NOT_FOUND;
				addressItem.setAdministrativeArea(fill);

				fill = contactInfo.getAddress().getCity()!=null?contactInfo.getAddress().getCity().toString():NOT_FOUND;
				addressItem.setCity(fill);

				fill = contactInfo.getAddress().getCountry()!=null?contactInfo.getAddress().getCountry().toString():NOT_FOUND;

				addressItem.setCountry(fill);

				addressItem.setPostalCode(validateString(contactInfo.getAddress().getPostalCode()));

				if(contactInfo.getAddress().getDeliveryPoints()!=null){
					Collection<String> deliveryPoints = new ArrayList<String>(contactInfo.getAddress().getDeliveryPoints());
					addressItem.setDeliveryPoints(deliveryPoints);
				}
				if(contactInfo.getAddress().getElectronicMailAddresses()!=null){
					Collection<String> emails = new ArrayList<String>(contactInfo.getAddress().getElectronicMailAddresses());
					addressItem.setElectronicMailAddresses(emails);
				}

			}
			contactItem.setAddress(addressItem);

	//		OnlineResourceItem onLineResItem = new OnlineResourceItem();
	//		//OnlineResource
	//		if(contactInfo.getOnlineResource()!=null){
	//
	//			//FILL OnlineResource
	//		    OnlineResource onlineResource = contactInfo.getOnlineResource();
	//
	//		    String description = onlineResource.getDescription()!=null? onlineResource.getDescription().toString(): NOT_FOUND;
	//
	//			onLineResItem.setDescription(description);
	//
	//			onLineResItem.setLinkage(onlineResource.getLinkage().toString());
	//			onLineResItem.setName(validateString(onlineResource.getName()));
	//			onLineResItem.setProtocol(validateString(onlineResource.getProtocol()));
	//
	//		}
			//FILL OnlineResource
			contactItem.setOnlineResource(convertOnLineResourceItem(contactInfo.getOnlineResource()));

			TelephoneItem tel = new TelephoneItem();
			//TELEPHONE
			if(contactInfo.getPhone()!=null){
				//FILL TELEPHONE
				Telephone phone = contactInfo.getPhone();

				Collection<String> facsimiles = new ArrayList<String>(phone.getFacsimiles());
				tel.setFacsimiles(facsimiles);
				Collection<String> voices = new ArrayList<String>(phone.getVoices());
				tel.setVoices(voices);

			}
		}
		rp.setContactInfo(contactItem);

		return rp;

	}

	/**
	 * Convet citation item.
	 *
	 * @param citation the citation
	 * @return the citation item
	 */
	public static CitationItem convetCitationItem(Citation citation){

		CitationItem citationItem = new CitationItem();

		if(citation==null)
			return citationItem;

		//title
		String citationTitle = citation.getTitle() != null? citation.getTitle().toString():NOT_FOUND;
		citationItem.setTitle(citationTitle);

		//alternateTitles
		Collection<String> alts = convertCollectionInternationalStringToString(citation.getAlternateTitles());
		citationItem.setAlternateTitles(alts);

		//dates
		List<String> dates = new ArrayList<String>();
		if(citation.getDates()!=null){
			Collection<? extends CitationDate> citDates = citation.getDates();
			for (CitationDate citationDate : citDates) {
				dates.add(citationDate.getDate().toString());
				//DATE TYPE?
			}
		}

		citationItem.setDates(dates);

		//Edition
		String edition = citation.getEdition()!=null?citation.getEdition().toString():NOT_FOUND;
		citationItem.setEdition(edition);

		//Edition date
		if(citation.getEditionDate()!=null)
			citationItem.setEditionDate(citation.getEditionDate().getTime());

		List<ResponsiblePartyItem> listPartyItems = new ArrayList<ResponsiblePartyItem>();

		if(citation.getCitedResponsibleParties()!=null){
			for (ResponsibleParty  resp: citation.getCitedResponsibleParties()) {
				listPartyItems.add(convertResponsiblePartyItem(resp));
			}
		}
		citationItem.setCitedResponsibleParty(listPartyItems);

		return citationItem;

	}

	/**
	 * Convert on line resource item.
	 *
	 * @param onlineResource the online resource
	 * @return the online resource item
	 */
	public static OnlineResourceItem convertOnLineResourceItem(OnlineResource onlineResource) {

		OnlineResourceItem item = new OnlineResourceItem();

		if (onlineResource == null)
			return item;

		String description = onlineResource.getDescription() != null ? onlineResource.getDescription().toString() : NOT_FOUND;
		item.setDescription(description);

		String uri = onlineResource.getLinkage() != null ? onlineResource.getLinkage().toString() : NOT_FOUND;
		item.setLinkage(uri);

		item.setName(validateString(onlineResource.getName()));
		item.setProtocol(validateString(onlineResource.getProtocol()));

		return item;

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		String geoserver = "http://www.fao.org/figis/a/wms/?service=WMS&version=1.1.0&request=GetMap&layers=area:FAO_AREAS&styles=Species_prob, puppa&bbox=-180.0,-88.0,180.0,90.0000000694&width=667&height=330&srs=EPSG:4326&format=image%2Fpng";
		System.out.println(MetadataConverter.getGeoserverBaseUri(geoserver));

	}
}
