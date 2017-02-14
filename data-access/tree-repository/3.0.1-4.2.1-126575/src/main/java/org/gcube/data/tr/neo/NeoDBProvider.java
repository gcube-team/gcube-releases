package org.gcube.data.tr.neo;

import java.io.File;
import java.io.Serializable;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * A provider of {@link GraphDatabaseService} instances.
 * @author Fabio Simeoni
 *
 */
public interface NeoDBProvider extends Serializable {

	/**
	 * Returns a database at a given location.
	 * @param location the location
	 * @return the database
	 */
	GraphDatabaseService newDatabase(File location);
}
