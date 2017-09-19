package org.gcube.data.tml.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.*;
import static org.gcube.data.tml.Utils.*;
import static org.gcube.data.trees.streams.TreeStreams.*;

import java.net.URI;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.exceptions.InvalidTreeException;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.gcube.data.tml.stubs.TWriterStub;
import org.gcube.data.tml.stubs.Types.NodeHolder;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.streams.TreeStreams;

public class DefaultTWriter implements TWriter {

	private final ProxyDelegate<TWriterStub> delegate;

	public DefaultTWriter(ProxyDelegate<TWriterStub> delegate) {
		this.delegate = delegate;
	}

	@Override
	public Tree add(final Tree tree) throws InvalidTreeException {
		
		notNull("tree",tree);
		
		
		Call<TWriterStub,Tree> call = new Call<TWriterStub,Tree>() {
			@Override
			public Tree call(TWriterStub endpoint) throws Exception {
				
				NodeHolder response = endpoint.add(new NodeHolder(tree)); 
				
				return response.asTree();  
			}
		};
		
		try {
			
			Tree e =  delegate.make(call);
			
			if (e==null)
				throw new ServiceException("unexpected null response");
			
			return e;
		}
		catch(Exception e) {
			
			throw again(e).as(InvalidTreeException.class);
		}
	}
	
	@Override
	public Stream<Tree> add(Stream<Tree> trees) {
		
		URI locator = TreeStreams.publishTreesIn(trees).withDefaults();
		return add(locator);
	}
	
	@Override
	public Stream<Tree> add(final URI locator) {
		
		notNull("resultset locator",locator);
		
		Call<TWriterStub,String> call = new Call<TWriterStub,String>() {
			@Override
			public String call(TWriterStub endpoint) throws Exception {
				return endpoint.addStream(locator.toString());  
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
	public Tree update(final Tree delta) throws InvalidTreeException, UnknownTreeException {
		
		notNull("delta tree",delta);
		
		Call<TWriterStub,Tree> call = new Call<TWriterStub,Tree>() {
			@Override
			public Tree call(TWriterStub endpoint) throws Exception {
				
				NodeHolder response = endpoint.update(new NodeHolder(delta)); 
				
				return response.asTree();  
				
			}
		};
		
		try {
			
			Tree e =  delegate.make(call);
			
			if (e==null)
				throw new ServiceException("unexpected null response");
			
			return e;
		}
		catch(Exception e) {
			
			throw again(e).as(UnknownTreeException.class, InvalidTreeException.class);
		}
	}
	
	
	@Override
	public Stream<Tree> update(Stream<Tree> deltas) {
		
		URI locator = TreeStreams.publishTreesIn(deltas).withDefaults();
		return update(locator);
	}
	
	@Override
	public Stream<Tree> update(final URI locator) {
		
		notNull("resultset locator",locator);
		
		Call<TWriterStub,String> call = new Call<TWriterStub,String>() {
			@Override
			public String call(TWriterStub endpoint) throws Exception {
				return endpoint.updateStream(locator.toString());  
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
}
