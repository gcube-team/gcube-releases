package org.gcube.data.spd.wormsplugin;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.wormsplugin.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.wormsplugin.capabilities.ExpansionCapabilityImpl;
import org.gcube.data.spd.wormsplugin.capabilities.NamesMappingImpl;

import aphia.v1_0.AphiaRecord;
import aphia.v1_0.Source;
import aphia.v1_0.Vernacular;

public class WormsPlugin extends AbstractPlugin {

	private GCUBELog logger = new GCUBELog(WormsPlugin.class);
	public static String credits = "This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with World Register of Marine Species (http://www.marinespecies.org/)";
	public static aphia.v1_0.AphiaNameServiceBindingStub binding;
	public static String baseurl;

	@Override
	public void initialize(ServiceEndpoint res) throws Exception {
		setUseCache(true);
		try {
			for (AccessPoint ap:res.profile().accessPoints()) {
				if (ap.name().equals("portType")){
					baseurl = ap.address();								
				}
			}

			binding = (aphia.v1_0.AphiaNameServiceBindingStub)
					new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort(new URL(baseurl));

		}
		catch (javax.xml.rpc.ServiceException jre) {
			logger.error("error contacting worms service", jre);
			throw jre;
		}
		super.initialize(res);
	}


	@SuppressWarnings("serial")
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return new HashSet<Capabilities>(){{add(Capabilities.Classification);add(Capabilities.NamesMapping);add(Capabilities.Expansion);}};
	}

	@Override
	public ClassificationCapability getClassificationInterface() {
		return new ClassificationCapabilityImpl();
	}

	@Override
	public MappingCapability getMappingInterface() {
		return new NamesMappingImpl();
	}

	@Override
	public ExpansionCapability getExpansionInterface() {
		return new ExpansionCapabilityImpl();
	}


	@Override
	public String getRepositoryName() {
		return "WoRMS";
	}

	@Override
	public String getDescription() {
		return "Plugin for WoRMS";
	}


	public void search(String word, ObjectWriter<ResultItem> writer, String type) throws ExternalRepositoryException {

		try {

			DataProvider dp = new DataProvider("worms");
			dp.setName("Worms");
			AphiaRecord[] records = null;
			List<CommonName> listCommNames = new ArrayList<CommonName> ();

			final int offsetlimit=50;
			int offset =1;
			do{
				if (type.equals("vernacular"))
					records = binding.getAphiaRecordsByVernacular(word, true, offset);
				else if (type.equals("scientific"))
					records = binding.getAphiaRecords(word, true, false, false, offset);

				if (records!=null){
					for (AphiaRecord record : records){
						//logger.debug("found record in worms");
						Source[] sources = null;

						if (record==null || (sources= binding.getSourcesByAphiaID(record.getAphiaID()))==null)
							continue;
						for (Source source : sources){
							//logger.debug("found source in worms");
							if (source==null)
								continue;
							if (source.getReference()!=null){
								//								logger.debug("source has fulltext not null");
								DataSet ds = new DataSet(record.getAphiaID()+"||"+source.getReference().hashCode());
								ds.setName(source.getReference());
								if (source.getUrl()!=null) ds.setCitation(source.getUrl());
								ds.setDataProvider(dp);
								ResultItem item = new ResultItem(record.getAphiaID()+"", record.getScientificname());
								//								Calendar now = Calendar.getInstance();
								//								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								//								String credits ="Biodiversity data published by: "+ds.getName()+" (Accessed through WoRMS web service, "+format.format(now.getTime())+")";
								AphiaRecord[] synonyms = binding.getAphiaSynonymsByID(record.getAphiaID());
								if (synonyms!=null && synonyms.length>0){
									Product product = new Product(ProductType.Synonym, item.getId());
									product.setCount(synonyms.length);
									item.setProducts(Collections.singletonList(product));
								}
								Vernacular[] vernaculars = binding.getAphiaVernacularsByID(record.getAphiaID());
								if (vernaculars!=null){
									//									logger.debug("found vernacular name");
									for (Vernacular vernacular : vernaculars) {										
										if (vernacular.getLanguage_code()!=null){
											CommonName a = new CommonName(vernacular.getLanguage(),vernacular.getVernacular());
											listCommNames.add(a);		
										}
									}
								}

								item.setScientificNameAuthorship(record.getAuthority());
								item.setLsid(record.getLsid());

								item.setCitation(record.getCitation());		
								item.setCredits(Utils.createCredits());			

								item.setCommonNames(listCommNames);


								item.setDataSet(ds);

								if (record.getRank()!=null)
									item.setRank(record.getRank());
								else if (record.getScientificname().equals("Biota"))
									item.setRank("Superdomain");


								item.setParent(Utils.retrieveTaxon(binding.getAphiaClassificationByID(record.getAphiaID()), record.getAphiaID()));
								if (writer.isAlive())
									writer.write(item);
								else
									break;

							}
						}
					}
				}
				offset+=offsetlimit;
			} while (records!=null && records.length==offsetlimit);

		} catch (RemoteException e) {
			logger.error("error contacting WoRMS service", e);
			throw new ExternalRepositoryException(e);
		} catch (Throwable e) {
			logger.error("error searching in WoRMS service", e);
		}
	}


	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo("" +
				"http://www.marinespecies.org/images/banner1.jpg", 
				"http://www.marinespecies.org/",
				"The World Register of Marine Species (WoRMS) is a database that hopes to provide an authoritative and comprehensive list of names of marine organisms. The content of the registry is edited and maintained by scientific specialists on each group of organism. These taxonomists control the quality of the information, which is gathered from several regional and taxon-specific databases. WoRMS maintains valid names of all marine organisms, but also provides information on synonyms and invalid names. WoRMS is continuously updated since new species are constantly being discovered and described by scientists. In addition, the nomenclature and taxonomy of existing species is often corrected or changed as new research is constantly being published. WoRMS maintenance and development relies on financial contributions, the time contributed by its editorial board, and support of its host institution VLIZ.");
		return info;
	}



	@Override
	public void searchByScientificName(String word,
			ObjectWriter<ResultItem> writer, Condition... properties) throws ExternalRepositoryException{ {

				search(word, writer, "scientific");

			}

	}

}
