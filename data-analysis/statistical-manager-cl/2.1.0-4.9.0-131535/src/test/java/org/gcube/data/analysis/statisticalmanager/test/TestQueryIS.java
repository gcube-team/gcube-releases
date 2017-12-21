package org.gcube.data.analysis.statisticalmanager.test;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URI;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestQueryIS {

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
	public static void main(String[] args) {
		ScopeProvider.instance.set("/gcube/devsec");
		W3CEndpointReference epr = null;
		try {
//			GCUBEScope scope = GCUBEScope
//					.getScope(ScopeProvider.instance.get());
//			ISClient client = GHNContext.getImplementation(ISClient.class);
//			WSResourceQuery wsquery = client.getQuery(WSResourceQuery.class);
//			wsquery.addAtomicConditions(new AtomicCondition("//gc:ServiceName",
//					"statistical-manager-gcubews"));
//			//
//			EndpointReferenceType ert = null;
//
//			for (RPDocument d : client.execute(wsquery, scope)) {
//				System.out.println(d.getEndpoint());
//
//				for (String value : d.evaluate("//computation/text()")) {
//					String computationId = "29704";
//					if (computationId.equals(value)) {
//						ert = d.getEndpoint();
//						break;
//					}
//				}
//			}
//			if (ert != null) {
//				W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
//				builder.address(ert.getAddress().toString());
//
//				ReferencePropertiesType props = ert.getProperties();
//
//				if (props != null && props.get_any() != null)
//					for (MessageElement element : props.get_any())
//						if (element.getLocalName().equals(
//								AddressingUtils.keyElement)) {
//							Element key = org.gcube.common.clients.builders.AddressingUtils
//									.key(element.getNamespaceURI(),
//											element.getValue());
//							builder.referenceParameter(key);
//							break;
//						}
//				System.out.println("vecchio :" + builder.build().toString());
//			}
			XQuery query = queryFor(ServiceInstance.class);
			URI u = new URI(
					"http://gcube-system.org/namespaces/data/analysis/statisticalmanager");
			query.addNamespace("sm", u);
			query.addCondition(
					"$resource/Data/gcube:ServiceName/text() eq 'statistical-manager-gcubews'")
					.addCondition(
							"$resource/Data//sm:computation/text() eq '29704'");
			DiscoveryClient<ServiceInstance> clients = clientFor(ServiceInstance.class);
			
			List<ServiceInstance> props = clients.submit(query);
			W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
			System.out.println("++" + props);
			for (ServiceInstance s : props) {
				System.out.println(props);
				org.gcube.common.resources.gcore.ServiceInstance.Properties ps =s.properties();
				Element key = key("http://gcube-system.org/namespaces/data/analysis/statisticalmanager", s.key());
				builder.referenceParameter(key);
				builder.address(s.endpoint().toString());
				System.out.println("nuovo :" + builder.build().toString());
				// Document doc = convertStringToDocument(s.toString());
				// NodeList list = doc.getElementsByTagName("Data");
				// for (int i = 0; i < list.getLength(); i++) {
				// Node node = list.item(i);
				// System.out.println(node.getLocalName());
				// }

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Element key(String namespace, String value) {
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

}
