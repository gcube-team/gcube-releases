package org.gcube.data.tr.neo.nodes;

import java.io.File;

import org.gcube.data.tr.neo.NeoDBProvider;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * Default implementation of {@link NeoDBProvider}.
 * @author Fabio Simeoni
 *
 */
@SuppressWarnings("serial")
public class DefaulNeoDBProvider implements NeoDBProvider {

	@Override
	public GraphDatabaseService newDatabase(File location) {
			return new EmbeddedGraphDatabase(location.getAbsolutePath());
	};
}
