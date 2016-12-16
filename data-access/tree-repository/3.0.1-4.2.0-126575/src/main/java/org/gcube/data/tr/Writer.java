package org.gcube.data.tr;

import static org.gcube.data.streams.dsl.Streams.*;

import java.util.Calendar;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.delegates.StreamListenerAdapter;
import org.gcube.data.tmf.api.SourceWriter;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tmf.impl.AbstractWriter;
import org.gcube.data.trees.data.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link SourceWriter} for {@link TreeSource}s.
 * 
 * @author Fabio Simeoni
 *
 */
public class Writer extends AbstractWriter {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(Writer.class);
	
	private TreeSource source;
	
	/**
	 * Creates an instance for a {@link TreeSource}.
	 * @param source the source
	 */
	public Writer(TreeSource source) {
		this.source=source;
	}
	
	@Override
	public Tree add(Tree tree) throws InvalidTreeException, Exception {
		Tree added = source.store().add(tree);
		updateProperties();
		return added;
	}

	//helper
	void updateProperties() {
		source.setLastUpdate(Calendar.getInstance());
		source.setCardinality(source.store().cardinality());
	}
	
	@Override
	public Stream<Tree> add(Stream<Tree> treeStream)
			throws Exception {
		
		log.trace("adding trees in batch to "+source.id());
		
		return source.store().add(monitor(treeStream).with(new PropertyUpdater()));
	}
	
	@Override
	public Tree update(Tree delta) throws UnknownTreeException,InvalidTreeException, Exception {
		Tree updated = source.store().update(delta);
		updateProperties();
		return updated;
		
	}
	
	@Override
	public Stream<Tree> update(Stream<Tree> deltaStream)
			throws Exception {
		
		log.trace("updating documents in batch in "+source.id());
		
		return source.store().update(monitor(deltaStream).with(new PropertyUpdater()));
	}
	
	//helper
	private class PropertyUpdater extends StreamListenerAdapter {
		
		@Override
		public void onClose() {
			updateProperties();
		}
	};

}
