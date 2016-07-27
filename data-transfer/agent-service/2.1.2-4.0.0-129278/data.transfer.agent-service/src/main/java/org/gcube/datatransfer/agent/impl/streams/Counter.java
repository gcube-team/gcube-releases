package org.gcube.datatransfer.agent.impl.streams;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.trees.data.Tree;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class Counter implements Generator<Tree, Tree> {
	public int total=0;
	protected GCUBELog logger = new GCUBELog(this.getClass());
	
	@Override
	public Tree yield(Tree element) throws StreamSkipSignal,
			StreamStopSignal {
		total++;
		//logger.debug("TreeGenerator: total="+total);
		return element;
	}
}
