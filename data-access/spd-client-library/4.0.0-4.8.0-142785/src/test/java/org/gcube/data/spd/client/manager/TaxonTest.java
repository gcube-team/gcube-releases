package org.gcube.data.spd.client.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxonTest {

	private static Logger logger = LoggerFactory.getLogger(TaxonTest.class);
	
	
	
	/*public static void main(String[] args) throws Exception{
		getTaxonById();
		getChilds();
	}
	
			
	public static void getChilds() throws UnsupportedPluginException, UnsupportedCapabilityException, InvalidIdentifierException{
		ScopeProvider.instance.set("/gcube/devsec");
		Classification classification = classification().build();
		
		Stream<TaxonomyItem> item = classification.getTaxonChildrenById("CatalogueOfLife:12640178");
		
		System.out.println("parent id is cataloguesOfLife:12640178");
		
		while (item.hasNext()){
			System.out.println("item:"+ item.next());
		}
		
		
	}
	
	public static void getChildenTree() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		Classification classification = classification().build();
		
		
		Stream<TaxonomyItem> item = classification.getTaxonTreeById("CatalogueOfLife:11946467");
		
		int i =0;
		while (item.hasNext()){
			TaxonomyItem subitem = item.next();
			System.out.println("item ("+i+++") "+ subitem.getId()+" !! "+ subitem.getRank()+" -- "+subitem.getParent());
		}
		
	}
	
	public static void getSynonyms() throws IdNotValidException, Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		Classification classification = classification().build();
		Stream<TaxonomyItem> item = classification.getSynonymsById("WoRMS:273810");
		
		int i =0;
		while (item.hasNext()){
			System.out.println("item ("+i+++") "+ item.next());
		}
		
	}
	
	public static void getTaxonById() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		Classification classification = classification().build();
		Stream<TaxonomyItem> item = classification.getTaxaByIds(convert(Collections.singletonList("CatalogueOfLife:12640178")));
		
		System.out.println("taxon ----");
		int i =0;
		while (item.hasNext()){
			System.out.println("item ("+i+++") "+ item.next());
		}
		
	}*/

	
}
