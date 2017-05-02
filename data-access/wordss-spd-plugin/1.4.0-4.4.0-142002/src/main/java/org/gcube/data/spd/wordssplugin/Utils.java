package org.gcube.data.spd.wordssplugin;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.products.TaxonomyStatus;
import org.gcube.data.spd.model.products.TaxonomyStatus.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aphia.v1_0.wordss.AphiaRecord;
import aphia.v1_0.wordss.Classification;
import aphia.v1_0.wordss.Vernacular;

public class Utils {

	static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static Taxon retrieveTaxon(Classification classification, int aphiaID){
		Taxon taxon = null;
		while (classification.getAphiaID()!=aphiaID){
			Taxon newTaxon = new Taxon(classification.getAphiaID()+"");
			if (taxon!=null) 
				newTaxon.setParent(taxon);
			newTaxon.setRank(classification.getRank());
			//			logger.trace(classification.getRank());
			//			logger.trace(classification.getScientificname());
			newTaxon.setScientificName(classification.getScientificname());
			classification = classification.getChild();
			taxon = newTaxon;
		}
		//		logger.trace(taxon);
		return taxon;
	}


	public static TaxonomyItem retrieveTaxonomy(Classification classification, int aphiaID){
		List<CommonName> listCommNames = new ArrayList<CommonName> ();
		TaxonomyItem taxon = null;

		while (classification.getAphiaID()!=aphiaID){

			int id = classification.getAphiaID();
			TaxonomyItem newTaxon = new TaxonomyItem(id+"");
			if (taxon!=null) 
				newTaxon.setParent(taxon);
			
			newTaxon.setRank(classification.getRank());
			newTaxon.setScientificName(classification.getScientificname());
			newTaxon.setCredits(Utils.createCredits());			
			AphiaRecord record = null;

			try {
				record = WordssPlugin.binding.getAphiaRecordByID(id);
			}catch (Exception e) {
				logger.error("Error getAphiaRecordByID ", e);
			}
			if (record != null){
				newTaxon.setCitation(record.getCitation());
				newTaxon.setLsid(record.getLsid());
				newTaxon.setScientificNameAuthorship(record.getAuthority());
			}

			Vernacular[] vernaculars;
			try {
				vernaculars = WordssPlugin.binding.getAphiaVernacularsByID(classification.getAphiaID());
				if (vernaculars!=null){
					//					logger.debug("found vernacular name");
					for (Vernacular vernacular : vernaculars) {										
						if (vernacular.getLanguage_code()!=null){
							CommonName a = new CommonName(vernacular.getLanguage(),vernacular.getVernacular());
							listCommNames.add(a);		
						}
					}
				}
			} catch (RemoteException e) {
				logger.error("RemoteException", e);
			}

			newTaxon.setCommonNames(listCommNames);
			newTaxon.setStatus(new TaxonomyStatus("accepted", Status.ACCEPTED));

			classification = classification.getChild();
			taxon = newTaxon;
		}
		return taxon;
	}

	//format date
	public static String createDate() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(now.getTime());
		return date;
	}

	public static String createCredits() {
		String cred = WordssPlugin.credits;
		cred = cred.replace("XDATEX",createDate());	
		return cred;
	}
}
