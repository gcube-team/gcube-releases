package gr.cite.gos.resources;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.clustermanager.layers.DataCreatorGos;
import gr.cite.geoanalytics.dataaccess.entities.coverage.dao.CoverageDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;


@Service
//@Path("/LayerManagement")
public class LayerManagementResource {

	private static Logger logger = LoggerFactory.getLogger(LayerManagementResource.class);

	private ShapeDao shapeDao;
	private CoverageDao coverageDao;
	private DataCreatorGos dataCreatorGos;

	@Inject
	public LayerManagementResource(ShapeDao shapeDao, CoverageDao coverageDao, DataCreatorGos dataCreatorGos) {
		this.shapeDao = shapeDao;
		this.coverageDao = coverageDao;
		this.dataCreatorGos = dataCreatorGos;
	}

	@PostConstruct
	private void createZnodeData() {
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				try {
					List<String> layerIDs = existingLayerIDs();
					dataCreatorGos.create(layerIDs);
				} catch (Exception e) {
					logger.error("SEVERE ERROR! Could not notify zookeeper that this GOS is alive. Could not also send any information about its datasets!", e);
				}
			}
		});
	}

	public List<String> existingLayerIDs() throws JsonProcessingException {
		List<String> shapeLayers = shapeDao.getAllLayerIDs();
		List<String> coverageLayers = coverageDao.getAllLayerIDs();
		shapeLayers.addAll(coverageLayers);
		return shapeLayers;
	}
}
