package app;

import static com.sun.jersey.api.client.Client.*;
import static java.util.concurrent.TimeUnit.*;
import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.extensions.HttpExtension.Method.*;
import static utils.TestUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import org.gcube.common.authorization.library.BannedService;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.extensions.HttpExtension.Method;

import utils.TestUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.header.OutBoundHeaders;


public class Request {

	private String path="";
	private String scope = TestUtils.scope;
	
	private OutBoundHeaders headers = new OutBoundHeaders();
	private Method method = GET;
	private String body = null;
	private boolean logged = false;
	
	public static Request request() {
		return new Request();
	}
	
	private Request() {
		
	}
	
	public Request at(String path) {
		this.path=path;
		return this;
	}
	
	public Request logging() {
		logged=true;
		return this;
	}
	
	public Request inScope(String scope) {
		this.scope=scope;
		return this;
	}
	
	public Request with(String body) {
		this.body=body;
		return this;
	}
	
	public Request with(String name, String value) {
		this.headers.add(name, value);
		return this;
	}
	
	public Request using(Method method) {
		this.method=method;
		return this;
	}
	
	public String path() {
		return path;
	}
	
	public String body() {
		return body;
	}
	
	public Method method() {
		return method;
	}
	
	public String scope() {
		return scope;
	}
	
	ClientResponse make(final int port) {

		
		// we make a scoped call in a separate thread, with which we then synchronize for completion.
		// this helps isolate the caller's thread (Main normally) from the app's thread,
		// starting with the scope itself.
		final CountDownLatch latch = new CountDownLatch(1);
		
		class Box {

			volatile UniformInterfaceException failure;
			volatile ClientResponse response;
			
		}

		final Box box = new Box();

		new Thread() {

			public void run() {

				ScopeProvider.instance.set(scope);
				AuthorizationProvider.instance.set(new UserInfo("test", new ArrayList<String>(), new ArrayList<BannedService>()));

				try {
					
					Client client = create();
				
					if (logged) 
						client.addFilter(new LoggingFilter(System.err));
					
					Builder builder = client.resource(address(path,port))
							.entity(body).header(scope_header, scope);
					
					for (Entry<String,List<Object>> header : headers.entrySet())
						for (Object value : header.getValue())
							builder.header(header.getKey(), value);
					
					if (method==DELETE)
						builder.delete();
					else {
						
						System.err.println("making request @ "+address(path,port));
						
						ClientResponse response = builder.method(method.name(),ClientResponse.class);
					
						//throws an exception if there response has error status
						if (response.getStatus()>300)
							throw new UniformInterfaceException(response);
						
						box.response=response;
					}
					
					
				} catch (UniformInterfaceException t) {
					box.failure=t;
				}

				latch.countDown();
			};
		}.start();

		try {
		
			if (!latch.await(2000, MILLISECONDS))
				throw new RuntimeException("application has not responded in time");

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		if (box.failure !=null)
			throw box.failure;
		
		else
			return box.response;
			
	}
	
	private String address(String path, long port) {
		
		path = (path.isEmpty() || path.startsWith("/"))?path:"/"+path;
		
		return "http://localhost:" + port+ "/" + context_root+path;
	}
	
}
