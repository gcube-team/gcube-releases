package org.gcube.data.spd.specieslink;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.parser.DarwinSimpleRecord;
import org.gcube.data.spd.parser.RecordsIterator;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public class SpeciesLinkPlugin extends  AbstractPlugin{

	GCUBELog logger = new GCUBELog(SpeciesLinkPlugin.class);

	public static String baseurl;
	public static String model;
	public static int limit;

	public final static String credits ="This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with speciesLink (http://splink.cria.org.br/)";
	public final static String citation = "Accessed through: speciesLink at http://splink.cria.org.br/ on XDATEX";


	//	public static String baseurl = "http://tapir.cria.org.br/tapirlink/tapir.php/specieslink";
	//	public static String model = "dwc_simple";

	@Override
	public void initialize(ServiceEndpoint res) throws Exception {
		logger.trace("SpeciesPlugin starting initialization");
		setUseCache(true);
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals("tapir")){

				baseurl = ap.address();
				//				logger.trace("baseurl " + baseurl);
				Iterator<Property> properties = ap.properties().iterator();
				while(properties.hasNext()) {
					Property p = properties.next();		
					if (p.name().equals("model")){							
						model = p.value();
//						System.out.println("model " + model);
					} else if (p.name().equals("limit")){							
						limit = Integer.parseInt(p.value());
					}
				}
			}
		}
		logger.trace("SpeciesPlugin initialized");
		super.initialize(res);
	}


	@Override
	public void update(ServiceEndpoint res) throws Exception {

		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals("tapir")){
				baseurl = ap.address();
				Iterator<Property> properties = ap.properties().iterator();
				while(properties.hasNext()) {
					Property p = properties.next();		
					if (p.name().equals("model")){							
						model = p.value();
					} else if (p.name().equals("limit")){							
						limit = Integer.parseInt(p.value());
					}
				}
			}
		}
		logger.trace("SpeciesPlugin updated");
		super.update(res);
	}

	@Override
	public String getDescription() {
		return ("Species Link Plugin");
	}

	@Override
	public String getRepositoryName() {
		return ("SpeciesLink");
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return new HashSet<Capabilities>(){{add(Capabilities.Occurrence);}};
	}


	@Override
	public OccurrencesCapability getOccurrencesInterface() {
		return new OccurrencesCapabilityImpl(); 
	}


	@Override
	public void searchByScientificName(String scientificName,
			ObjectWriter<ResultItem> writer, Condition... properties) {
		logger.trace("searchByScientificName");
		String f = "";
		try {
			f = Utils.elaborateProps(properties);
		} catch (Exception e) {
			logger.error("error elaborating properties",e);
			return;
		}

		String filter = "http://rs.tdwg.org/dwc/dwcore/ScientificName%20like%20%22" + scientificName.replace(" ", "%20") + "%22" + f + "&orderBy=http://rs.tdwg.org/dwc/dwcore/ScientificName&orderBy=http://rs.tdwg.org/dwc/dwcore/InstitutionCode";
//		System.out.println(filter);
		//logger.trace(filter);
//		int total = 0;
		int count = 0;
		boolean flag = false;
		DarwinSimpleRecord element = null;
		DarwinSimpleRecord element1 = null;
		
		
		try{		
			RecordsIterator set = new RecordsIterator(baseurl, filter, model, limit, false);
			Iterator<DarwinSimpleRecord> it = set.iterator();			
			
			while (it.hasNext()){
//				total++;
//				System.out.println("result found");
				flag = true;
				element = it.next();
				
				if (element1 == null)
					element1 = element;		
				
				//group results by istitutionCode
				if (((element1.institutionCode).toLowerCase()).equals((element.institutionCode).toLowerCase()) & ((element1.scientificName).toLowerCase()).equals((element.scientificName).toLowerCase()))		{	
					count++;
				}
				else{		
//					logger.trace("set result");
					if (setResult(element1, count, writer)){
						element1 = element;
						count = 1;
					}else{
						flag = false;
						break;
					}
				}	
			}

			if (flag){
//				logger.trace("flag true");
				setResult(element1, count, writer);		
			}

		}catch (Exception e) {
//			e.printStackTrace();
			writer.write(new StreamBlockingException("SpeciesLink", ""));
		}

	}

	//Creates a ResultItem and put it in writer
	private boolean setResult(DarwinSimpleRecord element, int count, ObjectWriter<ResultItem> writer){
		
		try{
			
			String scientificName = element.scientificName;
			String collectionCode = element.collectionCode;
			String institutionCode = element.institutionCode;
			
			ResultItem rs = new ResultItem(element.globalUniqueIdentifier, scientificName);

			rs.setScientificNameAuthorship(element.authorYearOfScientificName);

			rs.setCredits(Utils.credits());
			rs.setCitation(Utils.citation());

			DataSet dataSet = new DataSet(collectionCode);			
			dataSet.setName(collectionCode);		
			rs.setDataSet(dataSet);

			DataProvider dp = new DataProvider(institutionCode);
			dp.setName(institutionCode);
			dataSet.setDataProvider(dp);

			//Capabilities
			String key = "http://rs.tdwg.org/dwc/dwcore/ScientificName%20equals%20%22" + scientificName.replace(" ", "%20") + "%22%20and%20http://rs.tdwg.org/dwc/dwcore/CollectionCode%20equals%20%22" + collectionCode + "%22";
			Product prod = new Product(ProductType.Occurrence, key);
			prod.setCount(count);
			rs.setProducts(Collections.singletonList(prod));

			//find taxonomy
			Taxon last = null;
			String kingdom = null;
			if ((kingdom = element.kingdom) != null){	
				if (kingdom.equals(scientificName))
					rs.setRank("kingdom");
				else{
					Taxon k = new Taxon(kingdom);  
					k.setRank("kingdom");
					k.setScientificName(kingdom);   				
					last = k;
				}
			}
			
			String phylum = null;
			if ((phylum = element.phylum) != null){	
				if (phylum.equals(scientificName))
					rs.setRank("phylum");
				else{
					Taxon p = new Taxon(phylum);
					p.setRank("phylum");
					p.setScientificName(phylum);    				   
					if (last != null)
						p.setParent(last);
					last = p;	
				}
			}

			String clazz = null;
			if ((clazz = element.clazz) != null){	
				if (clazz.equals(scientificName))
					rs.setRank("class");
				else{
					Taxon c = new Taxon(clazz);
					c.setRank("class");
					c.setScientificName(clazz);
					if (last != null)
						c.setParent(last);
					last = c;  
				}
			}
			
			String order = null;
			if ((order = element.order) != null){	
				if (order.equals(scientificName))
					rs.setRank("order");
				else{
					Taxon o = new Taxon(order);
					o.setRank("order");
					o.setScientificName(order);
					if (last != null)
						o.setParent(last);
					last = o;   
				}
			}
			
			String family = null;
			if ((family = element.family) != null){	
				if ((family).equals(scientificName))
					rs.setRank("family");
				else
				{
					Taxon f = new Taxon(family);   
					f.setRank("family");
					f.setScientificName(family);  
					if (last != null)
						f.setParent(last);
					last = f;
				}
			}

			if (element.genus != null){	
				if ((element.genus).equals(scientificName))
					rs.setRank("genus");
				else{
					Taxon g = new Taxon(element.genus);  
					g.setRank("genus");
					g.setScientificName(element.genus);    				
					if (last != null)   				
						g.setParent(last);
					last = g;   
				}
			}

			if (rs.getRank() == null)
				rs.setRank("species");

			if (last != null)
				rs.setParent(last);  

			if (writer.isAlive())
				writer.write(rs);

		}catch (Exception e) {
			logger.error("Exception", e);			
			return false;
		}		

		return true;

	}


	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo(
				"http://splink.cria.org.br/images/logo_peq.gif", 
				"http://splink.cria.org.br/",
				"The goal of the speciesLink network is to integrate specie and specimen data available in natural history museums, herbaria and culture collections, making it openly and freely available on the Internet.");
		return info;
	}





}
