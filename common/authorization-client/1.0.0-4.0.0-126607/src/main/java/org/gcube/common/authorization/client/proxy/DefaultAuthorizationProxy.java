package org.gcube.common.authorization.client.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.client.Binder;
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.BannedService;
import org.gcube.common.authorization.library.BannedServices;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.common.scope.api.ScopeProvider;

public class DefaultAuthorizationProxy implements AuthorizationProxy {

	private final ProxyDelegate<String> delegate;

	public DefaultAuthorizationProxy(ProxyDelegate<String> config){
		this.delegate = config;
	}

	private static Map<String, AuthorizationEntryCache> cache = new HashMap<String, AuthorizationEntryCache>();

	@Override
	public String generate(final String userName, final List<String> roles) {
		Call<String, String> call = new Call<String, String>() {

			@Override
			public String call(String endpoint) throws Exception {
				StringBuilder rolesQueryString = new StringBuilder(); 
				for (String role: roles)
					rolesQueryString.append(role).append(",");
				rolesQueryString.deleteCharAt(rolesQueryString.lastIndexOf(","));
				String callUrl = endpoint+"/generate/"+userName+"?roles="+rolesQueryString.toString();
				URL url = new URL(callUrl);
				HttpURLConnection connection = makeRequest(url, "POST");
				if (connection.getResponseCode()!=200) throw new Exception("error contacting authorization service");
				try(BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)connection.getContent()));){
					StringBuilder result = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null) 
						result.append(line);
					return result.toString();
				}
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public AuthorizationEntry get(final String token) throws ObjectNotFound{
		Call<String, AuthorizationEntry> call = new Call<String, AuthorizationEntry>() {

			@Override
			public AuthorizationEntry call(String endpoint) throws Exception {
				
				URL url = new URL(endpoint+"/retrieve/"+token);
				HttpURLConnection connection = makeRequest(url, "GET");
				if (connection.getResponseCode()==404) throw new ObjectNotFound("token "+token+" not found");
				if (connection.getResponseCode()!=200) throw new Exception("error contacting authorization service");
				if (connection.getContentLengthLong()<=0) return null;

				try(InputStream stream = (InputStream)connection.getContent();){
					AuthorizationEntry entry = (AuthorizationEntry)Binder.getContext().createUnmarshaller().unmarshal(stream);
					cache.put(token, new AuthorizationEntryCache(entry));
					return entry;
				}

			}
		};
		if (cache.containsKey(token) && cache.get(token).isValid())
			return cache.get(token).getEntry();
		try {
			return delegate.make(call);
		} catch (ObjectNotFound e) {
			throw e;
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public BannedService deny(final String userName,  final  String serviceClass, final String serviceName) {
		Call<String, BannedService> call = new Call<String, BannedService>() {
			@Override
			public BannedService call(String endpoint) throws Exception {
				URL url = new URL(endpoint+"/deny/"+userName+"/"+serviceClass+"/"+serviceName);
				HttpURLConnection connection = makeRequest(url, "POST");

				if (connection.getResponseCode()!=200 && connection.getResponseCode()!=200) throw new Exception("error contacting authorization service");
				if (connection.getContentLengthLong()<=0) return null;

				try(InputStream stream = (InputStream)connection.getContent();){
					BannedService service = (BannedService)Binder.getContext().createUnmarshaller().unmarshal(stream);
					return service;
				}
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public void allow(final String userName, final  String serviceClass, final String serviceName) {
		Call<String, Empty> call = new Call<String, Empty>() {

			@Override
			public Empty call(String endpoint) throws Exception {
				URL url = new URL(endpoint+"/deny/"+userName+"/"+serviceClass+"/"+serviceName);
				HttpURLConnection connection = makeRequest(url, "DELETE");
				if (!(connection.getResponseCode()>=200 && connection.getResponseCode()<=206)) throw new Exception("error contacting authorization service");
				return new Empty();

			}
		};

		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<BannedService> getBannedServices(final String userName) {
		Call<String, List<BannedService>> call = new Call<String, List<BannedService>>() {

			@Override
			public List<BannedService> call(String endpoint) throws Exception {

				URL url = new URL(endpoint+"/deny/"+userName);

				HttpURLConnection connection = makeRequest(url, "GET");
				if (connection.getResponseCode()!=200) throw new Exception("error contacting authorization service");
				if (connection.getContentLengthLong()<=0) return Collections.emptyList();

				try(InputStream stream = (InputStream)connection.getContent();){
					BannedServices services = (BannedServices)Binder.getContext().createUnmarshaller().unmarshal(stream);
					if (services.get()==null) return Collections.emptyList();
					else return services.get();
				}

			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}


	private HttpURLConnection makeRequest(URL url, String method) throws Exception{
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestProperty(Constants.SCOPE_HEADER_ENTRY, ScopeProvider.instance.get());
		connection.setRequestMethod(method);
		return connection;
	}
}
