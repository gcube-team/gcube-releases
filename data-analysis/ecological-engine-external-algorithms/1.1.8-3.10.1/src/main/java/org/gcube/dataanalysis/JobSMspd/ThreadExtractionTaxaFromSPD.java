package org.gcube.dataanalysis.JobSMspd;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.util.ArrayList;
import java.util.Vector;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.TaxonomyItem;

import org.gcube.data.streams.Stream;

public class ThreadExtractionTaxaFromSPD implements Runnable {
	private ArrayList<String> chunk;
	private ArrayList<ArrayList<String>> informations;
	private ArrayList<String> errors;

	private String dataProvider;
	private String dataProviderUnfold;
	private String dataProviderExpand;
	Vector <TaxonomyItem> taxaList= new Vector <TaxonomyItem>();
	String scope;
	

	public ThreadExtractionTaxaFromSPD(ArrayList<String> chunk, String dataProvider,String dataProviderExpand,String dataProviderUnfold ,String scope) {
		this.chunk = chunk;
		for (String species : chunk) {
		System.out.println(species);
//		AnalysisLogger.getLogger().debug(species);
		}
		this.dataProvider=dataProvider;
		this.dataProviderExpand= dataProviderExpand;
		this.dataProviderUnfold=dataProviderUnfold;
		informations = new ArrayList<ArrayList<String>>();
		errors= new ArrayList<String>();
		this.scope=scope;
		
		
	}
	

	public void run() {

		AnalysisLogger.getLogger().debug("SCOPE *******: "+scope);
		ScopeProvider.instance.set(scope);
		//ScopeProvider.instance.set("/gcube/devsec");
		Manager manager=null;
		try{ 
			manager = manager().build();
		
		for (String species : chunk) {
			if (species != null) {
				String query = new String();
//				if(dataProviderExpand.equals("NO OPTION"))
//				query= "SEARCH BY SN '"+species + "' RETURN occurrence";
//				else
//					query= "SEARCH BY SN '"+species + "' EXPAND WITH CatalogueOfLife RETURN occurrence";	
				
				query=createQueryParameter(species);
				System.out.println("QUERY *******: "+query);
				AnalysisLogger.getLogger().debug("QUERY *******: "+query);
				Stream<ResultElement> stream;
				try {
					
					stream = manager.search(query);
					int i=0;
					while (stream.hasNext()) {
						i++;
						TaxonomyItem ti = (TaxonomyItem) stream.next();
//						AnalysisLogger.getLogger().debug("Inside whiele: "+ti.toString());
						taxaList.add(ti);
						informations.add(crateRowTable(ti));

					}
					if(i==0)
					{
						AnalysisLogger.getLogger().debug(species+" not found.");
						errors.add(species+" not found.");
					}
				} catch (Exception e) {
					errors.add("Exception on "+species+" :"+ e.getMessage());
					AnalysisLogger.getLogger().debug("Exception on "+species+" :"+ e.getMessage());
					e.printStackTrace();
					
				}
				
			}
			
		}
		}catch(Throwable e){
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("An error occurred: "+e.getMessage());
		}
		
	}
	
	private String createQueryParameter(String species)
	{
		String query= "SEARCH BY SN '"+species +"'";
		String where=new String();
		String expand=new String();
		String unfold=new String();
		
		if(dataProvider.equals("ALL"))
			where="";
		else 
			where=" IN "+dataProvider;
		
		if(dataProviderUnfold.equals("NO OPTION"))
			unfold="";
			else
				unfold=" UNFOLD WITH "+dataProviderUnfold;
		
		query= query +unfold;
		
		if(dataProviderExpand.equals("ALL"))
			expand=" EXPAND";
		else{
			if(dataProviderExpand.equals("NO OPTION"))
			expand="";
			else
				expand=" EXPAND WITH "+dataProviderExpand;
		}
		query= query+ expand;
		//if(!expand.equals("")& !dataProviderExpand.equals("NO OPTION") )
			
		
		
		
		query=query+ where;
		query= query +" RETURN TAXON";
		return query;
	}
	
	

	private ArrayList<String> crateRowTable(TaxonomyItem p)
	{
		ArrayList<String> infoOcc= new ArrayList<String>();
		infoOcc.add(p.getScientificName());
		infoOcc.add(p.getScientificNameAuthorship());
		infoOcc.add(p.getCitation());
		infoOcc.add(p.getCredits());	
		infoOcc.add(p.getId());
		infoOcc.add(p.getLsid());
		infoOcc.add(p.getProvider());
		infoOcc.add(p.getRank());

		return infoOcc;
		
	}
	public ArrayList<ArrayList<String>> getInfo()
	{
		return informations;
	}
	public ArrayList<String> getErrors()
	{
		return errors;
	}
	public Vector<TaxonomyItem >getTaxaList()
	{
		return taxaList;
	}
}
