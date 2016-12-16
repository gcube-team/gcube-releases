package org.gcube.data.analysis.statisticalmanager.proxies;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.FaultDSL;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.data.analysis.statisticalmanager.stubs.ComputationFactoryStub;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMAlgorithmsRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputations;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationsRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOutput;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameters;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTypeParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperationInfo;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StatisticalManagerDefaultFactory implements
		StatisticalManagerFactory {
	// public static GCUBELog logger = new GCUBELog(
	// StatisticalManagerDefaultFactory.class);
	private final AsyncProxyDelegate<ComputationFactoryStub> delegate;

	public StatisticalManagerDefaultFactory(
			ProxyDelegate<ComputationFactoryStub> delegate) {
		this.delegate = new AsyncProxyDelegate<ComputationFactoryStub>(delegate);
	}

	private Element key(String namespace, String value) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			String keyElementPrefix = "key";
			String keyElement = "ResourceKey";
			Document document = factory.newDocumentBuilder().newDocument();
			Element key = document.createElementNS(namespace, keyElementPrefix
					+ ":" + keyElement);
			key.setAttribute("xmlns:" + keyElementPrefix, namespace);
			key.appendChild(document.createTextNode(value));
			return key;
		} catch (Exception e) {
			throw new RuntimeException(
					"programming error in AddressingUtils#key");
		}
	}

	// public static W3CEndpointReference convert(EndpointReferenceType address)
	// throws IllegalArgumentException {
	//
	// W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
	// builder.address(address.getAddress().toString());
	//
	// ReferencePropertiesType props = address.getProperties();
	//
	// if (props!=null && props.get_any()!=null)
	// for (MessageElement element : props.get_any())
	// if (element.getLocalName().equals(AddressingUtils.keyElement)) {
	// Element key =
	// org.gcube.common.clients.builders.AddressingUtils.key(element.getNamespaceURI(),element.getValue());
	// builder.referenceParameter(key);
	// break;
	// }
	//
	// return builder.build();
	//
	// }

	private W3CEndpointReference getEPRComputationResourceFW(
			String computationId) {
		W3CEndpointReference epr = null;

		try {
			XQuery query = queryFor(ServiceInstance.class);
			URI u = new URI(
					"http://gcube-system.org/namespaces/data/analysis/statisticalmanager");
			query.addNamespace("sm", u);
			query.addCondition(
					"$resource/Data/gcube:ServiceName/text() eq 'statistical-manager-gcubews'")
					.addCondition(
							"$resource/Data//sm:computation/text() eq '"
									+ computationId + "'");
			DiscoveryClient<ServiceInstance> clients = clientFor(ServiceInstance.class);

			List<ServiceInstance> props = clients.submit(query);
			if (props != null && props.size() != 0) {
				W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
				for (ServiceInstance s : props) {
					org.gcube.common.resources.gcore.ServiceInstance.Properties ps = s
							.properties();
					Element key = key(
							"http://gcube-system.org/namespaces/data/analysis/statisticalmanager",
							s.key());
					builder.referenceParameter(key);
					builder.address(s.endpoint().toString());
					return builder.build();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return epr;

	}


	@Override
	public SMListGroupedAlgorithms getAlgorithms(
			SMTypeParameter... typeParameters) {

		SMTypeParameter[] list = typeParameters;
		final SMAlgorithmsRequest request = new SMAlgorithmsRequest(
				list!=null?Arrays.asList(list):Collections.EMPTY_LIST);

		Call<ComputationFactoryStub, SMListGroupedAlgorithms> call = new Call<ComputationFactoryStub, SMListGroupedAlgorithms>() {
			@Override
			public SMListGroupedAlgorithms call(ComputationFactoryStub endpoint)
					throws Exception {
				return endpoint.getAlgorithms(request);
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public SMOperationInfo getComputationInfo(String computationId, String user) {

		W3CEndpointReference epr = getEPRComputationResourceFW(computationId);

		if (epr == null) {
			SMComputation computation = getComputation(computationId);
			SMOperationInfo info = new SMOperationInfo();
			int index = computation.operationStatus();
			info.status(index);
			info.percentage(String.valueOf(0));
			switch (SMOperationStatus.values()[index]) {
			case COMPLETED:
				info.percentage(String.valueOf(100));
				break;
			case FAILED:
				info.percentage(String.valueOf(100));
				break;
			default:
				break;
			}

			return info;
		}

		StatisticalManagerService service = StatisticalManagerDSL.stateful()
				.at(epr).build();

		return service.getComputationInfo(computationId);
	}

	
	
	@Override
	public SMOutput getAlgorithmOutputs(final String algorithm) {
		Call<ComputationFactoryStub, SMOutput> call = new Call<ComputationFactoryStub, SMOutput>() {
			@Override
			public SMOutput call(ComputationFactoryStub endpoint)
					throws Exception {
				return endpoint.getAlgorithmOutputs(algorithm);
			};
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			e.printStackTrace();
			throw FaultDSL.again(e).asServiceException();
		}
	}
	
	
	
	@Override
	public SMParameters getAlgorithmParameters(final String algorithm) {

		Call<ComputationFactoryStub, SMParameters> call = new Call<ComputationFactoryStub, SMParameters>() {

			@Override
			public SMParameters call(ComputationFactoryStub endpoint)
					throws Exception {
				return endpoint.getAlgorithmParameters(algorithm);
			};
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public SMComputations getComputations(final String user,
			final SMTypeParameter... typeParameters) {

		Call<ComputationFactoryStub, SMComputations> call = new Call<ComputationFactoryStub, SMComputations>() {
			@Override
			public SMComputations call(ComputationFactoryStub endpoint)
					throws Exception {

				SMComputationsRequest request = new SMComputationsRequest();
				request.user(user);
				request.page(15);
				request.parameters(typeParameters!=null?Arrays.asList(typeParameters):Collections.EMPTY_LIST);

				return endpoint.getComputations(request);
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public String executeComputation(
			final SMComputationRequest requestComputation) {

		Call<ComputationFactoryStub, String> call = new Call<ComputationFactoryStub, String>() {

			@Override
			public String call(ComputationFactoryStub endpoint)
					throws Exception {
				return endpoint.executeComputation(requestComputation);
			}

		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public SMComputation getComputation(final String computationId) {

		Call<ComputationFactoryStub, SMComputation> call = new Call<ComputationFactoryStub, SMComputation>() {

			@Override
			public SMComputation call(ComputationFactoryStub endpoint)
					throws Exception {

				return endpoint.getComputation(computationId);
			}

		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public void removeComputation(final String computationId) {

		W3CEndpointReference epr = getEPRComputationResourceFW(computationId);

		if (epr == null) {

			Call<ComputationFactoryStub, Empty> call = new Call<ComputationFactoryStub, Empty>() {

				@Override
				public Empty call(ComputationFactoryStub endpoint)
						throws Exception {

					// return endpoint.removeComputation(computationId);
					endpoint.removeComputation(computationId);
					return new Empty();
				}

			};

			try {
				delegate.make(call);
				return;
			} catch (Exception e) {
				throw FaultDSL.again(e).asServiceException();
			}

		}

		StatisticalManagerService service = StatisticalManagerDSL.stateful()
				.at(epr).build();
		service.removeComputation(computationId);

	}

	@Override
	public SMListGroupedAlgorithms getAlgorithmsUser(
			SMTypeParameter... typeParameters) {
		SMTypeParameter[] list = typeParameters;
		final SMAlgorithmsRequest request = new SMAlgorithmsRequest(
				list!=null?Arrays.asList(list):Collections.EMPTY_LIST);

		Call<ComputationFactoryStub, SMListGroupedAlgorithms> call = new Call<ComputationFactoryStub, SMListGroupedAlgorithms>() {
			@Override
			public SMListGroupedAlgorithms call(ComputationFactoryStub endpoint)
					throws Exception {

				return endpoint.getAlgorithmsUser(request);
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public String resubmitComputation(final String computationId) {

		Call<ComputationFactoryStub, String> call = new Call<ComputationFactoryStub, String>() {

			@Override
			public String call(ComputationFactoryStub endpoint)
					throws Exception {

				return endpoint.resubmitComputation(computationId);
			}

		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}

	}



}
