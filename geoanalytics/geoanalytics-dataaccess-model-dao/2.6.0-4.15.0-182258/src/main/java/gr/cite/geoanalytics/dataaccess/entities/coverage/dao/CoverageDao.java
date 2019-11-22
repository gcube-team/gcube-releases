package gr.cite.geoanalytics.dataaccess.entities.coverage.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;

public interface CoverageDao extends Dao<Coverage, UUID> {

	Coverage findCoverageByLayer(UUID layer);

	List<String> getAllLayerIDs();

	int deleteById(UUID layer);
}
