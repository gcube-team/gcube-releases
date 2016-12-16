package org.gcube.dataanalysis.executor.nodes.transducers;

import org.gcube.dataanalysis.ecoengine.transducers.OccurrencePointsSubtraction;

public class OccurrenceSubtractionNode extends OccurrenceMergingNode {

	public OccurrenceSubtractionNode() {
		super.processor = new OccurrencePointsSubtraction();
	}
	


}
