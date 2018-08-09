package org.gcube.data.tr;

import static org.gcube.data.streams.dsl.Streams.*;

import org.gcube.data.streams.Stream;
import org.gcube.data.tmf.api.SourceReader;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tmf.impl.AbstractReader;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link SourceReader} for {@link TreeSource}s.
 * 
 * @author Fabio Simeoni
 *
 */
public class Reader extends AbstractReader {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(Reader.class);

	private TreeSource source;
	
	/**
	 * Creates an instance for a {@link TreeSource}
	 * @param source the source
	 */
	public Reader(TreeSource source) {
		this.source=source;
	}
	
	@Override
	public Tree get(String id, Pattern pattern) throws UnknownTreeException,InvalidTreeException, Exception {
		
		log.trace("retrieving tree {} from {} with "+pattern,id,source.id());
		
		Tree tree = source.store().get(id,pattern); 
		
		tree.setSourceId(source.id());
		
		return tree;
	}

	@Override
	public Stream<Tree> get(Pattern pattern) throws Exception {
		
		log.trace("retrieving trees from {} with {}",source.id(),pattern);
	
		return pipe(convert(source.store().get(pattern))).through(new SourceDecorator(source.id()));
	}
	
	

}
