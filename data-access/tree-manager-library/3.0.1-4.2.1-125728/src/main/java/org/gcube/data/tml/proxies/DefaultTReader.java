package org.gcube.data.tml.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.tml.Utils.*;
import static org.gcube.data.trees.patterns.Patterns.*;
import static org.gcube.data.trees.streams.TreeStreams.*;

import java.net.URI;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.clients.exceptions.UnsupportedRequestException;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.tml.Utils.PathSerialiser;
import org.gcube.data.tml.exceptions.InvalidTreeException;
import org.gcube.data.tml.exceptions.UnknownPathException;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.gcube.data.tml.stubs.TReaderStub;
import org.gcube.data.tml.stubs.Types.LookupRequest;
import org.gcube.data.tml.stubs.Types.LookupStreamRequest;
import org.gcube.data.tml.stubs.Types.QueryRequest;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;

public class DefaultTReader implements TReader {

	private final ProxyDelegate<TReaderStub> delegate;

	public DefaultTReader(ProxyDelegate<TReaderStub> delegate) {
		this.delegate = delegate;
	}

	@Override
	public Tree get(String id) throws UnknownTreeException {

		try {
			return get(id,tree());
		}
		catch(InvalidTreeException e) {
			//cannot really happen under the tree() pattern, but we need to satisfy the compiler
			//without introducing false declarations in the signature
			throw again(e).asServiceException();
		}

	}

	@Override
	public Tree get(final String id, final Pattern pattern) throws UnknownTreeException, InvalidTreeException {

		notNull("identifier", id);
		notNull("pattern",pattern);
		
		
		Call<TReaderStub,Tree> call = new Call<TReaderStub,Tree>() {
		
			@Override
			public Tree call(TReaderStub endpoint) throws Exception {
				
				LookupRequest params = new LookupRequest(id, pattern);
				return endpoint.lookup(params).asTree();  
			}
		};
		
		try {
			
			Tree e =  delegate.make(call);
			
			if (e==null)
				throw new ServiceException("unexpected null response");
			
			return e;
		}
		catch(Exception e) {
			
			throw again(e).as(UnknownTreeException.class,InvalidTreeException.class);
		}
	}
	

	@Override
	public Stream<Tree> get(Stream<String> ids,Pattern pattern) {
		
		URI locator = Streams.publishStringsIn(ids).withDefaults();
		
		return get(locator,pattern);
	}
	
	
	@Override
	public Stream<Tree> get(final URI locator, final Pattern pattern) throws UnsupportedOperationException,
			UnsupportedRequestException, ServiceException {
		
		notNull("stream locator",locator);
		notNull("pattern",pattern);
		
		Call<TReaderStub,String> call = new Call<TReaderStub,String>() {
			@Override
			public String call(TReaderStub endpoint) throws Exception {
				
				LookupStreamRequest request = new LookupStreamRequest(locator.toString(),pattern);
				
				return endpoint.lookupStream(request);
			}
		};
		
		try {
			String outcomeLocator = delegate.make(call);
			
			if (outcomeLocator==null)
				throw new ServiceException("unexpected null response");
			
			return treesIn(URI.create(outcomeLocator));
		}
		catch(Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public Stream<Tree> get(final Pattern pattern) {
		
		notNull("pattern",pattern);
		
		Call<TReaderStub,String> call = new Call<TReaderStub,String>() {
			@Override
			public String call(TReaderStub endpoint) throws Exception {
				
				QueryRequest request = new QueryRequest(pattern);
				
				return endpoint.query(request);
			}
		};
		
		try {
			
			String outcomeLocator = delegate.make(call);
			
			if (outcomeLocator==null)
				throw new ServiceException("unexpected null response");
			
			return treesIn(URI.create(outcomeLocator));
		}
		catch(Exception e) {
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public Node getNode(final String... path) throws UnknownPathException {
		
		notNull("path",path);
		
		Call<TReaderStub,Node> call = new Call<TReaderStub,Node>() {
			@Override
			public Node call(TReaderStub service) throws Exception {
				
				return service.lookupNode(new Path(path)).asNode();  
			}
		};
		
		try {
			
			Node node = delegate.make(call);
			
			if (node==null)
				throw new ServiceException("unexpected null response");
			
			return node;
		}
		catch(Exception e) {
			throw again(e).as(UnknownPathException.class);
		}

	}
	
	@Override
	public Stream<Node> getNodes(Stream<Path> paths) {
		
		URI pathRs = publish(paths).using(new PathSerialiser()).withDefaults();
		
		return getNodes(pathRs);
	}
	
	
	@Override
	public Stream<Node> getNodes(final URI paths) {
		
		Call<TReaderStub,String> call = new Call<TReaderStub,String>() {
			@Override
			public String call(TReaderStub endpoint) throws Exception {
				return endpoint.lookupNodeStream(paths.toString());
			}
		};
		
		try {
			String locator = delegate.make(call); 
			
			return nodesIn(URI.create(locator));
		}
		catch(Exception e) {
			throw again(e).asServiceException();
		}
		
		
	}
}
