package org.gcube.common;

import java.util.List;

import org.apache.axiom.om.OMElement;
import org.gcube.common.Harvester;
import org.gcube.common.data.Header;
import org.gcube.common.data.Record;
import org.gcube.common.data.RecordIterator;
import org.gcube.common.repository.Identify;
import org.gcube.common.repository.MetadataFormat;
import org.gcube.common.repository.Set;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		Harvester harvester = null;
		try {
//				harvester = new Harvester("http://api.openaire.eu/oai_pmh");
					
					harvester = new Harvester("http://oai.pensoft.eu");
			//resumptionToken
//			harvester = new Harvester("http://www.escholarship.org/uc/oai");
//			harvester = new Harvester("http://ws.pangaea.de/oai/");
			//			harvester = new Harvester("http://catarina.udlap.mx/u_dl_a/tales/oai/requestETD.jsp");
		} catch (Exception e) {
			System.out.println("error during harvest operation");
		}
		
		if (harvester != null){
//			Identify identify = harvester.identify();
//
//			System.out.println(identify.getRepositoryName());
//			System.out.println(identify.getProtocolVersion());
//
//			List<MetadataFormat> metadataformats = harvester.listMetadataFormats();
//			for(MetadataFormat metaformat : metadataformats) {
//				System.out.println(metaformat.getSchema());
//			}
//
			//**********Listing sets
//			System.out.println("***Listing sets");
//			List<Set> sets = harvester.listSets();
//			for(Set set : sets) {http://ijict.org/index.php/ijoat/oai?verb=ListRecords&metadataPrefix=oai_dc&set=ijoat:EA
//				System.out.println(set.getSetSpec());
//			}
			
			//*********+sets
//
//			//		Listing identifiers
//			System.out.println("***Listing identifiers");
//			List<Header> identifiers = harvester.listIdentifiers("oai_dc");
//			for(Header identifier:identifiers) {
//				System.out.println(identifier.getIdentifier());
//			}                    

			//	Getting a record giving an identifier
//			System.out.println("***Getting a record giving an identifier");
//			Record record = harvester.getRecord("oai:ciria.udlap.mx:u-dl-a/tesis/1011010008981", null);
//			System.out.println(record.getMetadata().getTitleList().get(0));

			//	Getting all the records

			System.out.println("***Getting all the records");
			
//			System.out.println("Size: " + harvester.getResumptionToken().getCompleteListSize());
//			System.out.println("getCursor: " + harvester.getResumptionToken().getCursor());
//			System.out.println("getExpirationDate: " + harvester.getResumptionToken().getExpirationDate());

			RecordIterator it =  harvester.listRecords(null, null, "zed", "oai_dc");
			
			//openaire
//			RecordIterator it =  harvester.listRecords("oai_dc", "4ed80f2e-e30b-4e6f-8048-98c8c6115a1c");
//			RecordIterator it =  harvester.listRecords("oai_dc");
			int tot = 0;
			int valid = 0;
			int deleted = 0;
			while(it.hasNext()) {
				
				Record record1 = it.next();    
				tot++;
				String id = record1.getHeader().getIdentifier();
				if (record1==null || record1.getMetadata()==null || record1.IsDeleted()){
					deleted++;
					System.out.println(valid + " valid - " + deleted + " deleted" );
				
				}else {
					valid++;
//					OMElement first = record1.getMetadataElement().getFirstElement();
//					System.out.println("*************************************");
//					System.out.println(first.toString());
//					System.out.println("*************************************");
//					System.out.println();					
					
					try{
						String title = record1.getMetadata().getTitleList().get(0);
						System.out.println(valid + ": " + id + " - " +title);
					}catch (Exception e) {
						System.out.println("Error retrieving Title of record: " + id + " in " + harvester.getBaseUrl());
					}
					
					
//					List<String> sets = record1.getHeader().getSpecList();
//					for (int i=0; i<sets.size(); i++) {
//
//						System.out.println("-> set: " + sets.get(i));
//					}
					
					}
					
			
//				System.out.println(" ******getSchemaLocation*** " + record1.getMetadata().getSchemaLocation());
//				System.out.println(record1.getMetadata().getTitleList().get(0));
				// process record here.                         
			}
			System.out.println("tot: " + tot + " - valid: " + valid + " - deleted: " + deleted);
		}

		 
	}

}