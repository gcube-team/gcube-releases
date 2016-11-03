package gr.cite.geoanalytics.logicallayer;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermShapeMessenger;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.servicelayer.ShapeInfo.ShapeInfoMessenger;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class RetryingLayerOperations extends NodeAwareLayerOperationsDecorator{

	private NodePicker nodePicker;
	private TaxonomyManager taxonomyManager;
	
	private ThreadLocal<Set<String>> map = new ThreadLocal<>();
	private ThreadLocal<Boolean> firstEntryStatus = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue(){
            return false;
        }
	};
	
	public RetryingLayerOperations() { }
	
	public ThreadLocal<Set<String>> getMap() {
		return map;
	}

	public void setMap(ThreadLocal<Set<String>> map) {
		this.map = map;
	}

	public ThreadLocal<Boolean> getFirstEntryStatus() {
		return firstEntryStatus;
	}

	public void setFirstEntryStatus(ThreadLocal<Boolean> firstEntryStatus) {
		this.firstEntryStatus = firstEntryStatus;
	}
	
	@Inject
	public RetryingLayerOperations(NodeAwareLayerOperations nodeAwareLayerOperationsDecorated) {
		super(nodeAwareLayerOperationsDecorated);
	}
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Override
	public String getNodeFromURL() {
		return super.getNodeFromURL();
	}
	
	@Override
	public void setNode(URL url) {
		super.setNode(url);
	}
	
	@Override
	public URL getNode() {
		return super.getNode(); 
	}

	@Inject
	public void setNodePicker(NodePicker nodePicker) {
		this.nodePicker = nodePicker;
	}
	
	public void policy(Exception e) throws Exception{
		if (map.get().isEmpty()){
			throw new RuntimeException();
		}
		String falseNode = getNodeFromURL();
		map.get().remove(falseNode);
		String nextNode = nodePicker.pickNode(map.get());
		setNode(nodePicker.getNodeURL(nextNode));
	}
	
	@Override
	public Set<String> getAttributeValuesOfShapesByTerm(TaxonomyTermMessenger taxonomyTermMessenger, Attribute attr) throws Exception {
		try{
			if (!(firstEntryStatus.get())){
				map.set(nodePicker.getNodesForLayerTermId(taxonomyTermMessenger.getId()));
			}
			return super.getAttributeValuesOfShapesByTerm(taxonomyTermMessenger, attr);
		}catch(Exception e){
			policy(e);
			return this.getAttributeValuesOfShapesByTerm(taxonomyTermMessenger, attr);
		}
	}

	@Override
	public void generateShapeBoundary(TaxonomyTermMessenger layerTermMessenger, TaxonomyTermMessenger boundaryTermMessenger, PrincipalMessenger principalMessenger) throws Exception {
		try{
			if (!(firstEntryStatus.get())){
				map.set(nodePicker.getNodesForLayerTermId(layerTermMessenger.getId()));
			}
			super.generateShapeBoundary(layerTermMessenger, boundaryTermMessenger, principalMessenger);
		}catch(Exception e){
			policy(e);
			this.generateShapeBoundary(layerTermMessenger, boundaryTermMessenger, principalMessenger);
		}
	}

	@Override
	public List<ShapeMessenger> getShapesOfTerm(String termName, String termTaxonomy) throws Exception {
		try{
			if (!(firstEntryStatus.get())){
				TaxonomyTerm tt = taxonomyManager.findTermByNameAndTaxonomy(termName, termTaxonomy, false);
				map.set(nodePicker.getNodesForLayerTermId(tt.getId().toString()));
			}
			return super.getShapesOfTerm(termName, termTaxonomy);
		}catch(Exception e){
			policy(e);
			return this.getShapesOfTerm(termName, termTaxonomy);
		}
	}

	@Override
	public List<TaxonomyTermShapeMessenger> findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTermMessenger) throws Exception {
		try{
			if (!(firstEntryStatus.get())){
				map.set(nodePicker.getNodesForLayerTermId(layerTermMessenger.getId()));
			}
			return super.findTermMappingsOfLayerShapes(layerTermMessenger);
		}catch(Exception e){
			policy(e);
			return this.findTermMappingsOfLayerShapes(layerTermMessenger);
		}
	}

	@Override
	public List<ShapeInfoMessenger> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception {
		try{
			if (!(firstEntryStatus.get())){
				TaxonomyTerm layerTerm = taxonomyManager.findTermByNameAndTaxonomy(termName, termTaxonomy, false);
				map.set(nodePicker.getNodesForLayerTermId(layerTerm.getId().toString()));
			}
			return super.getShapeInfoForTerm(termName, termTaxonomy);
		}catch(Exception e){
			policy(e);
			return this.getShapeInfoForTerm(termName, termTaxonomy);
		}
	}
}
