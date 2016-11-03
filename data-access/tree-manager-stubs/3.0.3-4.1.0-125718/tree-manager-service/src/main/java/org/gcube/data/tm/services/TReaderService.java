/**
 * 
 */
package org.gcube.data.tm.services;

import static java.util.concurrent.TimeUnit.*;
import static org.gcube.common.core.faults.FaultUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.tm.utils.TMStreams.*;
import static org.gcube.data.trees.io.XMLBindings.*;
import static org.gcube.data.trees.streams.TreeStreams.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBERetryEquivalentFault;
import org.gcube.common.core.faults.GCUBEUnrecoverableFault;
import org.gcube.data.streams.Stream;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.context.TReaderContext;
import org.gcube.data.tm.state.TReaderResource;
import org.gcube.data.tm.stubs.AnyHolder;
import org.gcube.data.tm.stubs.GetByIDParams;
import org.gcube.data.tm.stubs.GetByIDsParams;
import org.gcube.data.tm.stubs.GetParams;
import org.gcube.data.tm.stubs.InvalidTreeFault;
import org.gcube.data.tm.stubs.Path;
import org.gcube.data.tm.stubs.UnknownPathFault;
import org.gcube.data.tm.stubs.UnknownTreeFault;
import org.gcube.data.tm.stubs.UnsupportedOperationFault;
import org.gcube.data.tm.stubs.UnsupportedRequestFault;
import org.gcube.data.tm.utils.Utils;
import org.gcube.data.tmf.api.SourceReader;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownPathException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tmf.api.exceptions.UnsupportedRequestException;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.streams.TreeStreams.TreeSerialiser;
import org.globus.wsrf.ResourceException;
import org.w3c.dom.Element;

/**
 * The implementation of the T-Reader service.
 * 
 * @author Lucio Lelii (CNR-ISTI)
 * @author Fabio Simeoni
 * 
 * */
public class TReaderService {

	/**
	 * Returns the resource bound to the call
	 * 
	 * @return the resource
	 * @throws ResourceException if the call specifies no resource or no known resource
	 */
	public TReaderResource resource() throws ResourceException {
		return (TReaderResource) TReaderContext.getContext().getWSHome().find();
	}

	/**
	 * Returns a {@link Tree} in the bound source from its identifier, after pruning it with a given pattern.
	 * 
	 * @param params the request parameters
	 * @return an {@link AnyHolder} with the DOM representation of the {@link Tree}
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * @throws UnknownTreeFault if the tree with the given identifier does not exist in the bound source
	 * @throws InvalidTreeFault if the tree exists in the bound source but does not match the pattern
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public AnyHolder getByID(GetByIDParams params) throws UnsupportedOperationFault, UnsupportedRequestFault,
			UnknownTreeFault, InvalidTreeFault, GCUBEFault {

		try {

			Pattern pattern = Utils.toPattern(params.getPattern());

			SourceReader reader = resource().reader();

			Tree tree = reader.get(params.getRootID(), pattern);
			return Utils.toAnyHolder(tree);
		} catch (UnsupportedOperationException e) {
			throw newFault(new UnsupportedOperationFault(), e);
		} catch (UnsupportedRequestException e) {
			throw newFault(new UnsupportedRequestFault(), e);
		} catch (UnknownTreeException e) {
			throw newFault(new UnknownTreeFault(), e);
		} catch (InvalidTreeException e) {
			throw newFault(new InvalidTreeFault(), e);
		} catch (Exception e) {
			throw newFault(new GCUBERetryEquivalentFault(), e);
		}
	}

	/**
	 * Returns {@link Tree}s in the bound source from their identifiers, after pruning them with a given pattern
	 * 
	 * @param params the request parameters
	 * @return a locator to a remote result set of DOM representation of the {@link Tree}s
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public String getByIDs(GetByIDsParams params) throws UnsupportedOperationFault, UnsupportedRequestFault, GCUBEFault {

		try {

			Pattern pattern = Utils.toPattern(params.getPattern());

			URI rs = new URI(params.getLocator());

			Stream<String> idStream = convert(rs).ofStrings().withDefaults();

			SourceReader reader = resource().reader();

			// compute results
			Stream<Tree> outcome = log(reader.get(idStream, pattern));

			URI rsTrees = publishTreesIn(outcome).withBufferOf(Constants.DEFAULT_DOC_WRITEBUFFER)
					.withTimeoutOf(5, MINUTES).withDefaults(); // will
																													// stop
																													// at
																													// first
																													// unrecoverable

			return rsTrees.toString();
		} catch (URISyntaxException e) {
			throw newFault(new GCUBEUnrecoverableFault(), e);
		} catch (UnsupportedOperationException e) {
			throw newFault(new UnsupportedOperationFault(), e);
		} catch (UnsupportedRequestException e) {
			throw newFault(new UnsupportedRequestFault(), e);
		} catch (Exception e) {
			throw newFault(new GCUBERetryEquivalentFault(), e);
		}
	}

	/**
	 * Returns all the {@link Tree}s in the bound source, after pruning them with a given {@link Pattern}.
	 * 
	 * @param params the request parameters
	 * @return a locator to a remote Result Set with the pruned trees
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public String get(GetParams params) throws UnsupportedOperationFault, UnsupportedRequestFault, GCUBEFault {
		try {

			Pattern pattern = Utils.toPattern(params.getPattern());

			SourceReader reader = resource().reader();

			Stream<Tree> trees = log(reader.get(pattern));

			// publish stream
			URI rs = publish(trees).using(new TreeSerialiser()).withBufferOf(Constants.DEFAULT_DOC_WRITEBUFFER)
					.withTimeoutOf(5, MINUTES).withDefaults(); // stop at first unrecoverable
			
			return rs.toString();
		} catch (UnsupportedOperationException e) {
			throw newFault(new UnsupportedOperationFault(), e);
		} catch (UnsupportedRequestException e) {
			throw newFault(new UnsupportedRequestFault(), e);
		} catch (Exception e) {
			throw newFault(new GCUBERetryEquivalentFault(), e);
		}

	}

	/**
	 * Returns a {@link Node} from the path that connects it to the root of a {@link Tree} in the bound source.
	 * 
	 * @param path the request parameters
	 * @return the node
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * 
	 * @throws UnknownPathFault if the path does not lead to a node
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public AnyHolder getNode(Path path) throws UnsupportedOperationFault, UnsupportedRequestFault, UnknownPathFault,
			GCUBEFault {

		try {

			SourceReader reader = resource().reader();

			Node node = reader.getNode(path.getId());

			// serialise
			Element e = nodeToElement(node);

			return Utils.toHolder(e);
		} catch (UnsupportedOperationException e) {
			throw newFault(new UnsupportedOperationFault(), e);
		} catch (UnsupportedRequestException e) {
			throw newFault(new UnsupportedRequestFault(), e);
		} catch (UnknownPathException e) {
			throw newFault(new UnknownPathFault(), e);
		} catch (Exception e) {
			throw newFault(new GCUBERetryEquivalentFault(), e);
		}
	}

	/**
	 * Returns the {@link Node}s identified by corresponding {@link Path}s into trees of the bound source.
	 * 
	 * @param pathsRs a locator to a Result Set with paths
	 * @return a locator a Result Set with nodes
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public String getNodes(String pathsRs) throws UnsupportedOperationFault, UnsupportedRequestFault, GCUBEFault {

		try {
			
			Stream<org.gcube.data.tmf.api.Path> paths = pathsIn(new URI(pathsRs));
			
			SourceReader reader = resource().reader();
			
			Stream<Node> nodes = log(reader.getNodes(paths));

			// create node writer and returns locator
			URI nodesRs = publishNodesIn(nodes).
					withBufferOf(Constants.DEFAULT_DOC_WRITEBUFFER)
					.withTimeoutOf(5,MINUTES).withDefaults();

			return nodesRs.toString();

		} catch (URISyntaxException e) {
			throw newFault(new GCUBEUnrecoverableFault(), e);
		} catch (UnsupportedOperationException e) {
			throw newFault(new UnsupportedOperationFault(), e);
		} catch (UnsupportedRequestException e) {
			throw newFault(new UnsupportedRequestFault(), e);
		} catch (Exception e) {
			throw newFault(new GCUBERetryEquivalentFault(), e);
		}
	}

}
