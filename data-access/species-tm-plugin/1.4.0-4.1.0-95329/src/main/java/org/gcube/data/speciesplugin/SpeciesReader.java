package org.gcube.data.speciesplugin;

import static org.gcube.data.streams.dsl.Streams.*;

import org.gcube.data.speciesplugin.utils.SourceIdFiller;
import org.gcube.data.streams.Stream;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tmf.impl.AbstractReader;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 */
public class SpeciesReader extends AbstractReader {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(SpeciesReader.class);

	private SpeciesSource source;
	protected transient SourceIdFiller sourceIdFiller;
	
	/**
	 * Creates an instance for a {@link SpeciesSource}
	 * @param source the source
	 */
	public SpeciesReader(SpeciesSource source) {
		this.source=source;
		this.sourceIdFiller = new SourceIdFiller(source.id());
	}
	
	protected SourceIdFiller getSourceIdFiller()
	{
		if (sourceIdFiller == null) sourceIdFiller = new SourceIdFiller(source.id());
		return sourceIdFiller;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tree get(String id, Pattern pattern) throws UnknownTreeException,InvalidTreeException, Exception {
		
		log.trace("retrieving tree {} from {} with "+pattern,id,source.id());
		
		Tree tree = source.store().get(id,pattern); 
		
		SourceIdFiller sourceIdFiller = getSourceIdFiller();
		return sourceIdFiller.yield(tree);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stream<Tree> get(Pattern pattern) throws Exception {
		log.trace("retrieving trees from {} with {}",source.id(),pattern);
		SourceIdFiller sourceIdFiller = getSourceIdFiller();
		return pipe(convert(source.store().get(pattern))).through(sourceIdFiller);
	}
	
}
