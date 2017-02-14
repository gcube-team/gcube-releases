package gr.cite.geoanalytics.logicallayer;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import gr.cite.clustermanager.DataMonitor;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermShapeMessenger;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.servicelayer.ShapeInfo.ShapeInfoMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

public class ZookeeperLogicalLayerBroker extends AbstractLogicalLayerBroker {

	private DataMonitor dataMonitor;
	private NodeAwareLayerOperations remoteNodeClient;
	private Protocol protocol;
	

	@Inject
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	@Inject
	public void setRemoteNodeClient(NodeAwareLayerOperations remoteNodeClient) {
		this.remoteNodeClient = remoteNodeClient;
	}
	
	@Inject
	public void setDataMonitor(DataMonitor dataMonitor) {
		this.dataMonitor = dataMonitor;
	}
	
	public String getHost() {
		return dataMonitor.getHost();
	}

	@Override
	public Map<String, Set<String>> getServerKeyData() {
		return dataMonitor.getServerKeyData();
	}

	@Override
	public Map<String, Set<String>> getLayerKeyData() {
		return dataMonitor.getLayerKeyData();
	}
	
	@Override
	public Map<String, String> getServerToGeoserverData() {
		return dataMonitor.getServerToGeoserverData();
	}
	
	public URL getNodeURL(String node) throws Exception {
		return new URL(this.protocol.getName() + node);
	}
	
	@Override
	public Set<String> getAttributeValuesOfShapesByTerm(TaxonomyTermMessenger layerTerm, Attribute attr) throws Exception {
		remoteNodeClient.setNode(getNodeURL(pickNodeForLayerTermId(layerTerm.getId().toString())));
		return remoteNodeClient.getAttributeValuesOfShapesByTerm(layerTerm, attr);
	}

	@Override
	public List<ShapeMessenger> getShapesOfTerm(String layerTerm, String termTaxonomy) throws Exception {
		TaxonomyTerm tt = taxonomyManager.findTermByNameAndTaxonomy(layerTerm, termTaxonomy, false);
		remoteNodeClient.setNode(getNodeURL(pickNodeForLayerTermId(tt.getId().toString())));
		return remoteNodeClient.getShapesOfTerm(layerTerm, termTaxonomy);
	}
	
	@Override
	public void generateShapeBoundary(TaxonomyTermMessenger layerTerm, TaxonomyTermMessenger boundaryTerm, PrincipalMessenger user) throws Exception {
		remoteNodeClient.setNode(getNodeURL(pickNodeForLayerTermId(layerTerm.getId().toString())));
		remoteNodeClient.generateShapeBoundary(layerTerm, boundaryTerm, user);
	}

	@Override
	public List<TaxonomyTermShapeMessenger> findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTerm) throws Exception {
		remoteNodeClient.setNode(getNodeURL(pickNodeForLayerTermId(layerTerm.getId())));
		return remoteNodeClient.findTermMappingsOfLayerShapes(layerTerm);
	}

	@Override
	public List<ShapeInfoMessenger> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception {
		TaxonomyTerm layerTerm = taxonomyManager.findTermByNameAndTaxonomy(termName, termTaxonomy, false);
		remoteNodeClient.setNode(getNodeURL(pickNodeForLayerTermId(layerTerm.getId().toString())));
		return remoteNodeClient.getShapeInfoForTerm(termName, termTaxonomy);
	}
	
}
