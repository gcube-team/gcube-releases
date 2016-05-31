/**
 * 
 */
package org.gcube.data.tm.services;

import static org.gcube.common.core.faults.FaultUtils.*;
import static org.gcube.data.tm.utils.Utils.*;
import static org.gcube.data.trees.streams.TreeStreams.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBERetryEquivalentFault;
import org.gcube.common.core.faults.GCUBEUnrecoverableFault;
import org.gcube.data.streams.Stream;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.context.TWriterContext;
import org.gcube.data.tm.state.TWriterResource;
import org.gcube.data.tm.stubs.AnyHolder;
import org.gcube.data.tm.stubs.InvalidTreeFault;
import org.gcube.data.tm.stubs.UnknownTreeFault;
import org.gcube.data.tm.stubs.UnsupportedOperationFault;
import org.gcube.data.tm.stubs.UnsupportedRequestFault;
import org.gcube.data.tm.utils.Utils;
import org.gcube.data.tmf.api.SourceWriter;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tmf.api.exceptions.UnsupportedRequestException;
import org.gcube.data.trees.data.Tree;

/**
 * The implementation of the T-Writer service.
 * 
 * @author Lucio Lelii (CNR-ISTI)
 * @author Fabio Simeoni
 * 
 * */
public class TWriterService {

	/**
	 * Returns the state of the service.
	 * 
	 * @return the state
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public TWriterResource resource() throws Exception {
		return (TWriterResource) TWriterContext.getContext().getWSHome().find();
	}

	/**
	 * Adds a {@link Tree} to the bound source.
	 * 
	 * @param tree an {@link AnyHolder} with the DOM representation of the tree
	 * @return an {@link AnyHolder} with the DOM representation of the added tree, possibly annotated with metadata
	 *         added at the time of addition.
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * @throws InvalidDocumentFault if the tree is invalid
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public AnyHolder add(AnyHolder tree) throws UnsupportedOperationFault, UnsupportedRequestFault, InvalidTreeFault,
			GCUBEFault {

		try {

			SourceWriter writer = resource().writer();
			
			return toAnyHolder(writer.add(toTree(tree)));

		} catch (UnsupportedOperationException e) {
			throw newFault(new UnsupportedOperationFault(), e);
		} catch (UnsupportedRequestException e) {
			throw newFault(new UnsupportedRequestFault(), e);
		} catch (InvalidTreeException e) {
			throw newFault(new InvalidTreeFault(), e);
		} catch (Exception e) {
			throw newFault(new GCUBERetryEquivalentFault(), e);
		}

	}

	/**
	 * Adds many {@link Tree}s to the bound source.
	 * 
	 * @param locator a locator to a remote Result Set with the trees
	 * @return a locator to a local Result Set with {@link AddOutcome}s.
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public String addRS(String locator) throws UnsupportedOperationFault, UnsupportedRequestFault, GCUBEFault {

		try {

			//from resultset to tree stream
			Stream<Tree> inputTrees = treesIn(new URI(locator));

			SourceWriter writer = resource().writer();
			
			//from input tree stream to output tree stream
			Stream<Tree> outcomes = writer.add(inputTrees);
			
			URI outcomeLocator = publishTreesIn(outcomes)
					.withBufferOf(Constants.DEFAULT_ADDOUTCOME_WRITEBUFFER)
					.withDefaults();

			return outcomeLocator.toString();

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
	 * Updates a {@link Tree} in the bound source.
	 * 
	 * @param delta an {@link AnyHolder} with the DOM representation of the delta tree that captures the changes to the
	 *            tree
	 *  @return an {@link AnyHolder} with the DOM representation of the updated tree, possibly annotated with metadata
	 *         added at the time of update.
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * @throws UnknownTreeFault if the tree does not exist in the bound source
	 * @throws InvalidDocumentFault if the tree is invalid
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public AnyHolder update(AnyHolder delta) throws UnsupportedOperationFault, UnsupportedRequestFault, UnknownTreeFault,
			InvalidTreeFault, GCUBEFault {

		
		try {
			
			SourceWriter writer = resource().writer();
				
			return Utils.toAnyHolder(writer.update(toTree(delta)));
			
			
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
	 * Updates {@link Tree}s in the bound source.
	 * 
	 * @param locator a locator to a remote Result Set with the trees
	 * @return a locator to a Result Set with {@link UpdateFailure}s
	 * @throws UnsupportedOperationFault if the data source does not support this operation
	 * @throws UnsupportedRequestFault if the target plugin does not support this particular request
	 * @throws GCUBEFault if the operation fails for an unexpected error
	 */
	public String updateRS(String locator) throws UnsupportedOperationFault, UnsupportedRequestFault, GCUBEFault {

		try {
			
			SourceWriter writer = resource().writer();
			
			Stream<Tree> deltaStream = treesIn(new URI(locator));
			
			Stream<Tree> failures = writer.update(deltaStream);

			URI outcomeLocator = publishTreesIn(failures)
						.withBufferOf(Constants.DEFAULT_UPDATEOUTCOME_WRITEBUFFER)
						.withDefaults(); //stop at first unrecoverable

			return outcomeLocator.toString();

		} catch (URISyntaxException e) {
			throw newFault(new GCUBEUnrecoverableFault(), e);
		}catch (UnsupportedOperationException e) {
			throw newFault(new UnsupportedOperationFault(), e);
		} catch (UnsupportedRequestException e) {
			throw newFault(new UnsupportedRequestFault(), e);
		} catch (Exception e) {
			throw newFault(new GCUBERetryEquivalentFault(), e);
		}
	}


}
