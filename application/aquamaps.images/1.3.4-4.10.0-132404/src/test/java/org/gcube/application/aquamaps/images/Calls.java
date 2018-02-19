package org.gcube.application.aquamaps.images;


import org.gcube.application.aquamaps.images.exceptions.ImageNotFoundException;
import org.gcube.application.aquamaps.images.model.MapItem;
import org.gcube.application.aquamaps.images.model.ProductType;

import com.j256.ormlite.dao.CloseableIterator;

public class Calls {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//		GCUBEScope scope=GCUBEScope.getScope("/gcube/devsec");
		//		PublisherInterface pub=PublisherServiceCall.getCall(scope, "", true);
		//		String genus=
		//		try{
		//			for(AquaMap map:pub.getMapsBySpecies(new String[]{genus+"_"+species}, false, false, null)){
		//				if(map.getMapType().equals(ObjectType.SpeciesDistribution)){
		////					if(map.getResource().getSearchId()==suitableID) info.setSuitableURI(getEarthURI(map.getFiles()));
		////					else if(map.getResource().getSearchId()==suitable2050ID) info.setSuitable2050URI(getEarthURI(map.getFiles()));
		////					else if(map.getResource().getSearchId()==nativeID) info.setNativeURI(getEarthURI(map.getFiles()));
		////					else if(map.getResource().getSearchId()==native2050ID) info.setNative2050URI(getEarthURI(map.getFiles()));
		//				}
		//			}
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		//		
		//		





		//		
		//		Properties prop=new Properties();
		//		prop.setProperty(Common.FETCH_ROUTINE_INTERVAL_MINUTES, "10");
		//		prop.setProperty(Common.SCOPE_PROP, "/gcube/devsec");
		//		prop.setProperty(Common.SUITABLE_2050_PROP, "287");
		//		prop.setProperty(Common.SUITABLE_PROP, "275");
		//		prop.setProperty(Common.NATIVE_2050_PROP, "285");
		//		prop.setProperty(Common.NATIVE_PROP, "297");
		//		
		//		
		//		while(true){
		//			try{
		//				SpeciesInfoImportThread.start(prop);
		//				Thread.sleep(10*1000*60);
		//			}catch(InterruptedException e){}
		//		}
		//

		while(true){
			try{				
				System.out.println(Common.get().getProduct(ProductType.GIS, 1, "cadulusaequatorialis"));
			}catch(ImageNotFoundException e){
				System.out.println(" MAP NOT FOUND, listing...");
				CloseableIterator<MapItem> it=null;
				try{
					it=Common.get().getMapDao().closeableIterator();
					while(it.hasNext()){
						System.out.println(it.next());
					}
				}catch(Exception e1){
					e1.printStackTrace();
				}finally{
					it.close();
				}



			}finally{
				try{
					Thread.sleep(60*1000);
				}catch(InterruptedException e){}
			}
		}

		//		GCUBEScope scope=GCUBEScope.getScope("/gcube/devsec");
		//		AquaMapsServiceInterface as=AquaMapsServiceCall.getCall(scope, "", true);
		//		DataManagementInterface dmService=DataManagementCall.getCall(scope, "", true);
		//		
		//		int hspenID=0;
		//		for(Field f:dmService.getDefaultSources()){
		//			if(f.getName().equals(ResourceType.HSPEN+"")) {
		//				hspenID=f.getValueAsInteger();
		//				break;
		//			}
		//		}
		//		
		//		File toRead=as.getCSVSpecies(hspenID, null, null);
		//		System.out.println(toRead.getAbsolutePath());
	}

}
