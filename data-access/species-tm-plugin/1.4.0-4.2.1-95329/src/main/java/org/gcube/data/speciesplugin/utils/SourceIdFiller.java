/**
 * 
 */
package org.gcube.data.speciesplugin.utils;

import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.trees.data.Tree;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SourceIdFiller implements Generator<Tree, Tree> {

	protected String sourceId;

	/**
	 * @param sourceId
	 */
	public SourceIdFiller(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tree yield(Tree element) throws StreamSkipSignal, StreamStopSignal {
		element.setSourceId(sourceId);
		return element;
	}

}
