/**
 * 
 */
package org.gcube.data.speciesplugin.store;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.speciesplugin.utils.SpeciesService;
import org.gcube.data.tr.neo.NeoConstants;
import org.gcube.data.tr.neo.NeoDBProvider;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
public class SpeciesNeoDBProvider implements NeoDBProvider {

	private static final long serialVersionUID = 5143909472409593842L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GraphDatabaseService newDatabase(File location) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(Config.NODE_KEYS_INDEXABLE, NeoConstants.toAttribute(SpeciesService.SPECIES_SERVICE_ID));
		params.put(Config.NODE_AUTO_INDEXING, "true");
		GraphDatabaseService db = new EmbeddedGraphDatabase(location.getAbsolutePath(), params);
		db.index().getNodeAutoIndexer().startAutoIndexingProperty(NeoConstants.toAttribute(SpeciesService.SPECIES_SERVICE_ID));
		return db;
	}

}
