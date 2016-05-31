package org.gcube.data.spd.wordssplugin;

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
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.wordssplugin.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.wordssplugin.capabilities.NamesMappingImpl;
import org.gcube.data.spd.wordssplugin.capabilities.ExpansionCapabilityImpl;

import aphia.v1_0.AphiaRecord;
import aphia.v1_0.Source;
import aphia.v1_0.Vernacular;

public class WordssPlugin extends AbstractPlugin {

	private GCUBELog logger = new GCUBELog(WordssPlugin.class);
	public static String credits = "This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with the World Register of Deep-Sea Species (WoRDSS) (http://www.marinespecies.org/deepsea/)";
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
			logger.error("error contacting WoRDSS service", jre);
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
	public void searchByScientificName(String word, ObjectWriter<ResultItem> writer, Condition... properties) throws ExternalRepositoryException{
		search(word, writer);
	}

	@Override
	public String getRepositoryName() {
		return "WoRDSS";
	}

	@Override
	public String getDescription() {
		return "Plugin for WoRDSS";
	}


	public void search(String word, ObjectWriter<ResultItem> writer) throws ExternalRepositoryException{

		try {

			DataProvider dp = new DataProvider("WoRDSS");
			dp.setName("WoRDSS");
			AphiaRecord[] records = null;
			List<CommonName> listCommNames = new ArrayList<CommonName> ();

			final int offsetlimit=50;
			int offset =1;
			do{

				records = binding.getAphiaRecords(word, true, false, false, offset);

				if (records!=null){
					for (AphiaRecord record : records){
						//						logger.debug("found record in worms ");
						Source[] sources = null;

						if (record==null || (sources= binding.getSourcesByAphiaID(record.getAphiaID()))==null)
							continue;
						for (Source source : sources){
							//							logger.debug("found source in worms");
							if (source==null)
								continue;
							if (source.getReference()!=null){
								//								logger.debug("found record in worms " + record.getAphiaID() + " " + source.getReference());
								//								logger.debug("source has fulltext not null");
								DataSet ds = new DataSet(record.getAphiaID()+"||"+source.getReference().hashCode());
								ds.setName(source.getReference());
								if (source.getUrl()!=null) ds.setCitation(source.getUrl());
								ds.setDataProvider(dp);
								ResultItem item = new ResultItem(record.getAphiaID()+"", record.getScientificname());

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
								item.setRank(record.getRank());
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
			logger.error("error contacting WoRDSS service", e);
			throw new ExternalRepositoryException(e);
		} catch (Throwable e) {
			logger.error("error searching in WoRDSS service", e);
		}
	}


	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo("" +
				"http://www.marinespecies.org/deepsea/images/banner.png", 
				"http://www.marinespecies.org/deepsea/",
				"The World Register of Deep-Sea Species (WoRDSS) is a taxonomic database of deep-sea species based on the World Register of Marine Species (WoRMS). This site was launched in December 2012 as a project of the International Network for Scientific Investigation of Deep-sea Ecosystems (INDEEP).");
		return info;
	}





}
