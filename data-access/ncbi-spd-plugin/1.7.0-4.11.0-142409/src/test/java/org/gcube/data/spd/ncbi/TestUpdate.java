package org.gcube.data.spd.ncbi;

import java.io.IOException;
import java.sql.SQLException;

import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUpdate {
	static Logger logger = LoggerFactory.getLogger(TestUpdate.class);
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws ExternalRepositoryException 
	 * @throws MethodNotSupportedException 
	 * @throws IdNotValidException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws SQLException, IOException, IllegalAccessException, IdNotValidException, MethodNotSupportedException, ExternalRepositoryException, InterruptedException {

//		ConnectionPool.connectToTemplate1();
//		ConnectionPool.createNewDb();
//		ConnectionPool.connectToDb();
		
//				Boolean flag = false;
//				for(String s : NcbiPlugin.names){
//					if (!(Utils.SQLTableExists(s)))
//						flag = true;
//					break;
//				}
//		
//				if (flag)
//					Utils.createDB();

		UpdateThread thread = new UpdateThread(0);	

		
		
		// Test retrieveTaxaByName
//		Thread.sleep(200000);
//		ClassificationCapabilityImpl b = new ClassificationCapabilityImpl();
//
//		//		TaxonomyItem tax = b.retrieveTaxonById("641286");
//		//		System.out.println(tax);
//		// Test retrieveTaxonChildsByTaxonId
//		//				System.out.println(b.retrieveTaxonChildsByTaxonId(172402+""));	
//
//		b.getSynonymnsById(new ObjectWriter<TaxonomyItem>() {
//
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public boolean write(TaxonomyItem arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public boolean write(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		}, 
//		"89-1"
//				);
//		
		
		// *************************************
		//		Utils.createTableCit();
		//		Boolean flag = false;
		//		for(String s : NcbiPlugin.names){
		//			if (!(NcbiPlugin.SQLTableExists(s)))
		//				flag = true;
		//			break;
		//		}
		//		if (flag)
		//			Utils.createDB();
		//
		//		new UpdateThread();	

		//	logger.trace(Utils.getCitation(13687));






	}

}
