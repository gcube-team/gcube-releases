package org.gcube.dataanalysis.executor.nodes.transducers;

import org.gcube.dataanalysis.ecoengine.transducers.OccurrencePointsIntersector;

public class OccurrenceIntersectionNode extends OccurrenceMergingNode {

	public OccurrenceIntersectionNode() {
		super.processor = new OccurrencePointsIntersector();
	}
	


}
