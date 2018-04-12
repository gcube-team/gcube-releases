package org.gcube.usecases.ws.thredds;

import org.gcube.data.transfer.library.utils.ScopeUtils;
import org.gcube.data.transfer.model.plugins.thredds.DataSetScan;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.usecases.ws.thredds.engine.impl.ThreddsController;
import org.gcube.usecases.ws.thredds.faults.InternalException;

public class ThreddsCatalogTests {

	public static void main(String[] args) throws InternalException {
		TokenSetter.set("/gcube/devsec");

		System.out.println("Current scope is "+ScopeUtils.getCurrentScope());

		//		SyncEngine engine=SyncEngine.get();
		//		
		//		String[] toCheckTokens=new String[] {
		//				"123da564-2af2-4023-90f5-01a934d80754-98187548",
		//				"678abd6f-1a39-4e52-9c14-9288bf28a2ed-98187548",
		//				"f851ba11-bd3e-417a-b2c2-753b02bac506-98187548",
		//				"1dd10a45-4b04-4fba-b878-ed6e40d235db-843339462",
		//				"feda0617-cd9d-4841-b6f0-e047da5d32ed-98187548",
		//				"5741e3e4-dbde-46fa-828d-88da609e0517-98187548",
		//				"97cfc53e-7f71-4676-b5e0-bdd149c8460f-98187548",
		//				"6a16458f-d514-4c83-b012-6f2b6bf19794-843339462",
		//				"84bcb500-100e-4d35-868d-d6f3dbb95ade-843339462",
		//				"9dd56598-4092-460d-bfe4-91ecff66290a-843339462",
		//				"adb3f8f5-bf55-4d31-b951-c60139ff8b85-843339462",
		//				"39f574c0-d439-4c29-a652-01d97472a4fb-843339462",
		//				"3701435c-1c36-494f-ae31-4d002910e27e-843339462",
		//				"cc491b9c-c75b-41a8-a999-4c9f6f25263d-843339462",
		//				"74673327-645e-4b0b-8382-2edd663f3a38-843339462",
		//				"65f3fc8b-1bcc-4ae7-a71f-97e671a27cb7-843339462",
		//				"3240d5ec-72e6-4e03-b0c7-eaa24d91ea80-843339462",
		//		};
		//		
		//		
		//		for(String token:toCheckTokens) {
		//			System.out.println("Checking catalog for token "+token);
		//			for(CatalogBean bean:engine.getAvailableCatalogsByToken(token)) 
		//				System.out.println(bean.getName()+" in "+bean.getPath()+" Default : "+bean.getIsDefault());
		//			
		//			System.out.println("************************************");
		//			System.out.println();
		//		}
		//		


		ThreddsController controller=new ThreddsController("",TokenSetter.getCurrentToken());

		ThreddsInfo info=controller.getThreddsInfo();
		String [] paths=new String[] {
				"/data/content/thredds/newer",
				"/data/content/thredds/public",
				"/data/content/thredds/public/netcdf",
				"/data/content/thredds/public/netcdf/GPTest",
		};

		for(String path:paths) {
			if(info.getCatalogByFittingLocation(path)!=null) {
				DataSetScan ds=(DataSetScan) info.getDataSetFromLocation(path);
				if(ds==null)
					System.out.println("Catalog for "+path+"\t is null.");
				else System.out.println("Catalog for "+path+"\t : "+ds.getLocation()+"\t "+ds.getName());
			}else System.out.println("No catalog for path "+path);
		}

	}

}
