package org.gcube.portlets.user.speciesdiscovery.client.model;


import java.io.Serializable;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ClassificationModel extends BaseModelData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	//USED FOR STATIC FIELDS
	public ClassificationModel(String id, String name, String baseTaxonId, String baseTaxonName, boolean isLeaf) {
		setId(id);
		setName(name);
		setIsLeaf(isLeaf);
		setBaseTaxonId(baseTaxonId);
		setBaseTaxonName(baseTaxonName);
	}
	
	
	public ClassificationModel(String id, String name, String classificationRank, String baseTaxonId, String baseTaxonName, boolean isLeaf, int counter) {
		setId(id);
		setName(name);
		setIsLeaf(isLeaf);
		setBaseTaxonName(baseTaxonName);
		setClassificationRank(classificationRank);
		setCountOf(counter);
	}
	
	
	public ClassificationModel() {}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ClassificationModel) {
			ClassificationModel mobj = (ClassificationModel) obj;
			return getId().equals(mobj.getId());
		}
		return super.equals(obj);
	}
	
	public String getId(){
		return get(ConstantsSpeciesDiscovery.ID);
	}
	
	private void setId(String id) {
		set(ConstantsSpeciesDiscovery.ID, id);
	}
	
	private void setName(String name) {
		set(ConstantsSpeciesDiscovery.NAME, name);
	}
	
	public String getName() {
		return get(ConstantsSpeciesDiscovery.NAME);
	}
	
	private void setIsLeaf(boolean isLeaf) {
		set(ConstantsSpeciesDiscovery.ISLEAF, isLeaf);
	}
	
	private void setBaseTaxonId(String baseTaxonId) {
		set(ConstantsSpeciesDiscovery.BASETAXONID, baseTaxonId);
	}
	
	public String getBaseTaxonId() {
		return get(ConstantsSpeciesDiscovery.BASETAXONID);
	}
	
	
	private void setBaseTaxonName(String baseTaxonName) {
		set(ConstantsSpeciesDiscovery.BASETAXONNAME, baseTaxonName);
	}
	
	public String getBaseTaxonName() {
		return get(ConstantsSpeciesDiscovery.BASETAXONNAME);
	}
	
	public boolean isLeaf() {
		Boolean isLeaf = (Boolean) get(ConstantsSpeciesDiscovery.ISLEAF);
		return isLeaf.booleanValue();
	}
	
	public int getCountOf(){
		return (Integer) get(ConstantsSpeciesDiscovery.COUNTOF);
	}
	
	public void setCountOf(int count){
		set(ConstantsSpeciesDiscovery.COUNTOF, count);
	}
	
	public void incrCountOf(){
		int count = getCountOf();
		set(ConstantsSpeciesDiscovery.COUNTOF, count+1);
	}
	
	public String getClassificationRank(){
		return get(ConstantsSpeciesDiscovery.RANK);
	}
	
	public void setClassificationRank(String classificationRank){
		set(ConstantsSpeciesDiscovery.RANK, classificationRank);
	}
}
