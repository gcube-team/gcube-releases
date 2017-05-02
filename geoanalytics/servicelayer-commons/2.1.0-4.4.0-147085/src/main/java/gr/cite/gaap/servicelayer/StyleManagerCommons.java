package gr.cite.gaap.servicelayer;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.cite.clustermanager.exceptions.NoAvailableGos;
import gr.cite.clustermanager.layers.DataCreatorGeoanalytics;
import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.clustermanager.model.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.servicelayer.GeospatialBackendClustered;
import gr.cite.geoanalytics.dataaccess.entities.style.Style;
import gr.cite.geoanalytics.dataaccess.entities.style.dao.StyleDao;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.gos.client.GeoserverManagement;

@Service
public class StyleManagerCommons {

	public static Logger logger = LoggerFactory.getLogger(StyleManagerCommons.class);
	
	@Autowired private StyleDao styleDao;
	
	//the following is part of the client to exchange information with the gos nodes
	@Autowired private GeospatialBackendClustered geospatialBackendClustered;
	//these two are part of the Zookeeper Cluster management (monitoring and editing) 
	@Autowired private DataMonitor dataMonitor;
	@Autowired private DataCreatorGeoanalytics dataCreatorGeoanalytics;
	//this is for managing the geoserver instances
	@Autowired private GeoserverManagement geoserverManagement;
	//this is for traffic shaping
	@Autowired private TrafficShaper trafficShaper;
	
	public StyleManagerCommons() {}
	
	public boolean addStyle() {
		return true;
	}
	
	public boolean removeStyle() {
		return true;
	}
	
	public boolean updateLayerStyle() {
		return true;
	}
	
	public List<Style> listAllStyles() throws CustomException {
		List<Style> styles = styleDao.getAll();
		if (styles == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Styles not found");
		}else {
			return styles;
		}
	}
	
	@Transactional
	public void createStyle(Style style) throws Exception {
		if (style != null) {
			if (this.styleDao.create(style) == null) {
				throw new Exception("Could not create " + style);
			}
			
			Set<GosDefinition> gosDefinitions = trafficShaper.getAllGosEndpoints();
			
			for(GosDefinition gd : gosDefinitions) {
				geoserverManagement.addStyle(gd.getGosEndpoint(), style.getName(), style.getContent());
			}
			
			logger.info("Created " + style + " successfully!");
		}
	}
	
	@Transactional
	public void deleteStyle(Style style) throws Exception {
		this.styleDao.delete(style);
		//TODO:delete from Geoserver and from layer references
		
		Set<GosDefinition> gosDefinitions = trafficShaper.getAllGosEndpoints();
		
		for(GosDefinition gd : gosDefinitions) {
			geoserverManagement.removeStyle(gd.getGosEndpoint(), style.getName());
		}
	}
	
	public Style findStyleById(String id) throws Exception {
		Style style = styleDao.read(UUID.fromString(id));
		if (style == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Style not found");
		}
		return style;
	}
	
	public Style findStyleById(UUID id) throws Exception {
		Style style = styleDao.read(id);
		if (style == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Style not found");
		}
		return style;
	}
	
	@Transactional
	public void checkStyleNotExists(String name) throws Exception {
		Style style = this.styleDao.findStyleByName(name);
		if (style != null) {
			throw new CustomException(HttpStatus.CONFLICT, "Style \"" + name + "\" already exists!");
		}
	}
	
	@Transactional
	public void editStyle(Style style, String name, String description) throws Exception {
		
		Set<GosDefinition> gosDefinitions = trafficShaper.getAllGosEndpoints();
		
		for(GosDefinition gd : gosDefinitions) {
			geoserverManagement.removeStyle(gd.getGosEndpoint(), style.getName());
			geoserverManagement.addStyle(gd.getGosEndpoint(), name, style.getContent());
		}
		
		style.setName(name);
		style.setDescription(description);
		this.styleDao.update(style);
		
		logger.info(style + " has been edited successfully!");
	}
	
	public List<String> getAllStyles() throws NoAvailableGos, IOException {
		
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();
		
		return geoserverManagement.getAllStyles(gosDefinition.getGosEndpoint());
		
	}
	
}
