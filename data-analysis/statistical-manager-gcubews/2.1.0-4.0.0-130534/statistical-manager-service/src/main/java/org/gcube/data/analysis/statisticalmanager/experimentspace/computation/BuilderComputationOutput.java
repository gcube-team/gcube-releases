package org.gcube.data.analysis.statisticalmanager.experimentspace.computation;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.data.analysis.statisticalmanager.SMOperationType;
import org.gcube.data.analysis.statisticalmanager.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.exception.HLManagementException;
import org.gcube.data.analysis.statisticalmanager.exception.HibernateManagementException;
import org.gcube.data.analysis.statisticalmanager.exception.ISException;
import org.gcube.data.analysis.statisticalmanager.exception.PersistenceManagementException;
import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.gcube.data.analysis.statisticalmanager.persistence.SMPersistenceManager;
import org.gcube.data.analysis.statisticalmanager.util.ServiceUtil;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMComputation;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMFile;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMObject;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMResource;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class BuilderComputationOutput {

	private static Logger logger = LoggerFactory.getLogger(BuilderComputationOutput.class);

	private String portalLogin;
	private String scope;
	private SMComputation computation;

	public BuilderComputationOutput(String portalLogin,SMComputation computation) {
		this.portalLogin = portalLogin;
		this.scope = ScopeProvider.instance.get();
		this.computation = computation;
	}

	private SMTable serializeTabular(OutputTable output) throws ISException, HibernateManagementException{

		String template = output.getTemplateNames().get(0).toString();
		SMTable table = new SMTable(template);
		table.setPortalLogin(portalLogin);
		table.setAlgorithm(computation.getAlgorithm());
		table.setResourceType(SMResourceType.TABULAR.ordinal());
		table.setResourceId(output.getTableName());
		table.setDescription(output.getDescription());
		table.setName(output.getName());
		table.setProvenance(SMOperationType.COMPUTED.ordinal());
		table.setCreationDate(Calendar.getInstance());
		table.setOperationId(computation.getOperationId());

		SMPersistenceManager.addCreatedResource(table);

		return table;
	}

	private SMFile serializeFile(PrimitiveType output) throws HLManagementException, ISException, HibernateManagementException, PersistenceManagementException{
		try{
		logger.debug("---------- serialize File ");
		WorkspaceFolder appFolder = ServiceUtil.getWorkspaceSMFolder(ServiceUtil.getWorkspaceHome(portalLogin));
		logger.debug("---------- created application folder ");
		File outputFile = (File) output.getContent();
		
		String fileName =generateUniqueName(output.getName());
		logger.debug("---------- fileName : " + fileName);

		logger.debug("Going to store FILE "+outputFile.getAbsolutePath()+" size "+outputFile.length()+" as "+fileName+" with description "+output.getDescription());
		ExternalFile f = appFolder.createExternalFileItem(fileName,	output.getDescription(), null, outputFile);
		String url = f.getPublicLink();
		SMFile file = new SMFile(f.getMimeType(), fileName, url);
		file.setPortalLogin(portalLogin);
		file.setAlgorithm(computation.getAlgorithm());
		file.setResourceType(SMResourceType.FILE.ordinal());
		file.setResourceId(UUID.randomUUID().toString());
		file.setDescription(output.getDescription());
		file.setName(outputFile.getName());
		file.setProvenance(SMOperationType.COMPUTED.ordinal());
		file.setCreationDate(Calendar.getInstance());
		file.setOperationId(computation.getOperationId());

		SMPersistenceManager.addCreatedResource(file);

		return file;
		}catch(StatisticalManagerException e){
			throw e;
		}catch(InternalErrorException e){
			throw new PersistenceManagementException("Unable to get public link",e);
		}catch(Exception e){
			throw new PersistenceManagementException("Unable to create external file",e);
		}
	}

	private SMObject serializePrimitiveObject(PrimitiveType primitiveObject) {

		switch (primitiveObject.getType()) {
		case STRING:
			SMObject object = new SMObject(
					(String) primitiveObject.getContent());
			object.setPortalLogin(portalLogin);
			object.setAlgorithm(computation.getAlgorithm());
			object.setName(PrimitiveTypes.STRING.toString());
			object.setResourceType(SMResourceType.OBJECT.ordinal());
			object.setDescription(primitiveObject.getDescription());
			object.setProvenance(SMOperationType.COMPUTED.ordinal());
			object.setCreationDate(Calendar.getInstance());
			object.setOperationId(computation.getOperationId());
			return object;
		default:
			break;
		}
		return null;
	}

	private SMObject serializeImage(PrimitiveType primitiveObject) throws HLManagementException, PersistenceManagementException{		
		try{
			logger.debug("---------- serialize Image ");

		WorkspaceFolder appFolder = ServiceUtil	.getWorkspaceSMFolder(ServiceUtil.getWorkspaceHome(portalLogin));
		logger.debug("---------- create application folder ");

		@SuppressWarnings("unchecked")
		Map<String, Image> map = (Map<String, Image>) primitiveObject
				.getContent();
		
		String folderName = generateUniqueName(primitiveObject.getName());
		logger.debug("---------- folder name "+ folderName);
		WorkspaceFolder subFolder = appFolder.createFolder(folderName,"SM Image");
		
		
		for (Entry<String, Image> entry : map.entrySet()) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(ImageTools.toBufferedImage(entry.getValue()), "PNG",
					os);			
			if(logger.isDebugEnabled()){
				ByteArrayOutputStream osLog = new ByteArrayOutputStream();
				boolean writeFlag=ImageIO.write(ImageTools.toBufferedImage(entry.getValue()), "PNG",
						osLog);				
				logger.debug("Going to store IMG "+entry.getKey()+" writeFlag "+writeFlag+", size="+osLog.size()+" as "+entry.getKey()+" with description "+primitiveObject.getDescription());
				
			}		
			
			ExternalFile f = subFolder.createExternalImageItem(entry.getKey(),
					primitiveObject.getDescription(), null,
					new ByteArrayInputStream(os.toByteArray()));
			logger.debug("Uploaded IMG, obtained url "+f.getPublicLink());
		}

		// TO DO ADD FOLDER URL

		SMObject resource = new SMObject(subFolder.getPath());
		resource.setPortalLogin(portalLogin);
		resource.setResourceType(SMResourceType.OBJECT.ordinal());
		resource.setResourceId(UUID.randomUUID().toString());
		resource.setName(PrimitiveTypes.IMAGES.toString());
		resource.setDescription(primitiveObject.getDescription());
		resource.setProvenance(SMOperationType.COMPUTED.ordinal());
		resource.setCreationDate(Calendar.getInstance());
		resource.setOperationId(computation.getOperationId());
		return resource;
		}catch(Exception e){
			throw new PersistenceManagementException("Unable to serialize images", e);
		}
	}

	private SMObject serializeMap(PrimitiveType primitiveObject) throws PersistenceManagementException, HLManagementException{
		try{
		@SuppressWarnings("unchecked")
		Map<String, StatisticalType> map = (Map<String, StatisticalType>) primitiveObject
				.getContent();
		Map<String, SMResource> outputs = new LinkedHashMap<String, SMResource>();
		for (Entry<String, StatisticalType> entry : map.entrySet()) {
			SMResource resource = serialize(entry.getValue());
			outputs.put(entry.getKey(), resource);
		}

		File file = File.createTempFile("output", "sm");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			XStream xstream = new XStream();
			xstream.toXML(outputs, fos);
		} finally {
			IOUtils.closeQuietly(fos);
		}
		logger.debug("Create tmp file " + file.getAbsolutePath());

		WorkspaceFolder appFolder = ServiceUtil.getWorkspaceSMFolder(ServiceUtil.getWorkspaceHome(portalLogin));
		
		
		String fileName = generateUniqueName(primitiveObject.getName());
		logger.debug("---------- filename "+ fileName);
		
		logger.debug("Going to store MAP "+file.getAbsolutePath()+" size "+file.length()+" as "+fileName+" with description "+primitiveObject.getDescription());
		ExternalFile f = appFolder.createExternalFileItem(fileName, primitiveObject.getDescription(),null, file);
		String url = f.getPublicLink();
		logger.debug("Obtained public link "+url);

		SMObject resource = new SMObject(url);
		resource.setPortalLogin(portalLogin);
		resource.setResourceType(SMResourceType.OBJECT.ordinal());
		resource.setResourceId(UUID.randomUUID().toString());
		resource.setName(PrimitiveTypes.MAP.toString());
		resource.setDescription(primitiveObject.getDescription());
		resource.setProvenance(SMOperationType.COMPUTED.ordinal());
		resource.setCreationDate(Calendar.getInstance());
		resource.setOperationId(computation.getOperationId());
		return resource;
		}catch(Exception e){
			throw new PersistenceManagementException("Unable to serialize map",e);
		}
	}

	public SMResource serialize(StatisticalType object) throws PersistenceManagementException, ISException, HibernateManagementException, HLManagementException {
		logger.debug("Serializing computation output under scope "+scope);
		ScopeProvider.instance.set(scope);
		
		if (object instanceof OutputTable) {
			return serializeTabular((OutputTable) object);
		}

		if (object instanceof PrimitiveType) {
			PrimitiveType primitiveObject = (PrimitiveType) object;
			if (primitiveObject.getType() == PrimitiveTypes.MAP) {
				return serializeMap(primitiveObject);
			} else if (primitiveObject.getType() == PrimitiveTypes.IMAGES) {
				return serializeImage(primitiveObject);
			} else if (primitiveObject.getType() == PrimitiveTypes.FILE) {
				return serializeFile(primitiveObject);
			} else {
				return serializePrimitiveObject(primitiveObject);
			}
		}
		logger.error("throw Exception into serialize resource");
		throw new PersistenceManagementException("Object type is not valid, "+object.toString());
	}

	
	private String generateUniqueName(String baseName){
		return baseName+"_"+ServiceUtil.getDateTime()+"_CMP"+this.computation.getOperationId();
	}
	
}
