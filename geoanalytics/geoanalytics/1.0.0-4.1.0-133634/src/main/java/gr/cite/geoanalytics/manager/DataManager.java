package gr.cite.geoanalytics.manager;

import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;

import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataManager {
	
	public static Logger log = LoggerFactory.getLogger(DataManager.class);
	
	private ImportManager importManager;
	private ViewBuilder builder;
	
	@Inject
	public void setBuilder(ViewBuilder builder) {
		this.builder = builder;
	}
	
	@Inject
	public void setImportManager(ImportManager importManager) {
		this.importManager = importManager;
	}

	@Transactional(rollbackFor={GeoServerBridgeException.class,Exception.class})
	public void importDataToShapesOfLayerUsingTsvAndUpdate(UUID templateLayerTaxonomyTermId, String tsv, String newLayerNameForTaxonomyTerm, UUID taxonomyOfLayerTaxonomyTerm) throws Exception{
		
		TaxonomyTerm taxonomyTerm = this.importManager.createNewLayerForTaxonomyTermOfTsv(templateLayerTaxonomyTermId, newLayerNameForTaxonomyTerm, taxonomyOfLayerTaxonomyTerm, tsv);
		
		try{
			this.importManager.createLayerInDataBaseAndGeoserver(templateLayerTaxonomyTermId, taxonomyTerm);
		}catch(Exception e){
			e.printStackTrace();
			log.error("Error occured during the tsv import", e);
			this.importManager.removeLayer(taxonomyTerm);
			this.importManager.removeDataBaseView(taxonomyTerm);
			//e.printStackTrace();
		}
	}
	
	public void createDataBaseView(String identity, String identityName) throws Exception {
		builder.forIdentity(identity, identityName).createViewStatement().execute();
	}
	
}
