package org.gcube.data.oai.tmplugin;

import static org.gcube.data.streams.dsl.Streams.pipe;
import static org.gcube.data.trees.data.Nodes.t;

import java.util.List;

import org.gcube.data.oai.tmplugin.repository.Repository;
import org.gcube.data.oai.tmplugin.repository.Set;
import org.gcube.data.oai.tmplugin.repository.Summary;
import org.gcube.data.oai.tmplugin.utils.Utils;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tmf.impl.AbstractReader;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAIReader extends AbstractReader {

	private static final long serialVersionUID = -3638289705441179714L;

	private static Logger log = LoggerFactory.getLogger(OAIReader.class);

	private final OAISource source;
	private final Repository repository;
	private final List<Set> sets;


	public OAIReader(OAISource source, Repository repository,List<Set> sets) {
		this.source=source;
		this.sets=sets;
		this.repository=repository;
	}

	@Override
	public Tree get(String id, Pattern pattern) throws UnknownTreeException,
	InvalidTreeException, Exception {

		log.info("retrieving tree {} after pruning it with {}",id,source.id());
		
		String decodId = Utils.idDecoder(id);

		Tree tree = null;
		try{
			tree = repository.get(decodId, sets);
		}
		catch(Exception e) {
			throw new UnknownTreeException(e);
		}	

		return prune(tree,pattern);
	}

	//shared helper
	private Tree prune(Tree tree,Pattern pattern) throws InvalidTreeException {
		try {
			pattern.prune(tree);
		}
		catch(Exception e) {
			throw new InvalidTreeException(e);
		}

		//replace tree with an identical one bound to source
		return t(source.id(),tree.id(),tree.edges().toArray(new Edge[0]));
	}

	@Override
	public Stream<Tree> get(final Pattern pattern) throws Exception {

		log.info("retrieving trees from {} after pruning them with {}",source.id(),pattern);

		Generator<Tree,Tree> pruner = new Generator<Tree,Tree>() {
			@Override
			public Tree yield(Tree tree) {
				try {
					return prune(tree,pattern);
				} catch (InvalidTreeException e) {
					log.warn("error retrieving trees",e);
				}
				return tree;				
			}
		};

		Stream<Tree> trees = repository.getAllIn(sets);
		
		return pipe(trees).through(pruner);
	}

	public Summary summary() throws Exception {
		return repository.summary(sets);
	}
}
