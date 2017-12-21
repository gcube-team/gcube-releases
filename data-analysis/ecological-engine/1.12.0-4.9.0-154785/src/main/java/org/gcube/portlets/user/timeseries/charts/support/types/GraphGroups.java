/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: GraphGroups.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.types;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GraphGroups implements Serializable {
	private static final long serialVersionUID = -466243452417462863L;
	private Map<String, GraphData> graphs = new LinkedHashMap<String, GraphData>();

	public GraphGroups() {
	}

	public final void addGraph(final String key, final GraphData graph) {
		this.graphs.put(key, graph);
	}

	public final Map<String, GraphData> getGraphs() {
		return this.graphs;
	}
}
