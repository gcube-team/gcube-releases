package org.gcube.spatial.data.gis;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList.RESTShortWorkspace;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.gis.model.Costants;
import org.gcube.spatial.data.gis.symbology.StyleUtils;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.opengis.metadata.Metadata;

public class PublishTest {

	/**
	 * @param args
	 */
	
	private static final String defaultScope="/gcube/devsec";
	
	
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set(defaultScope);
		GISInterface gisInterface=GISInterface.get();
		GeoServerRESTReader reader=gisInterface.getGeoServerReader(ResearchMethod.MOSTUNLOAD, false);
		System.out.println("***************Workspaces : ");		
		Iterator<RESTShortWorkspace> it=reader.getWorkspaces().iterator();
		while(it.hasNext()){
			RESTShortWorkspace work=it.next();
			System.out.println(work.getName());
		}
		
		
//		String geoTiffPath = "/home/fabio/Downloads/aquamaps-lprognathodesfalcifer20121207161043540cet.geotiff";
//		File geoTiff=new File(geoTiffPath);
//		String workspace="aquamaps";
//		String storeName=geoTiff.getName()+"_store";
//		String coverageName="";
//		String srs="EPSG:4326";
//		ProjectionPolicy policy=ProjectionPolicy.REPROJECT_TO_DECLARED;
//		String defaultStyle="raster";
//		double[] bbox=Costants.WORLD_BOUNDING_BOX;
//		Metadata theMeta=new DefaultMetadata();
//		GNInsertConfiguration config=new GNInsertConfiguration("view-group", "datasets", "_none_", true);
//		gisInterface.addGeoTIFF(workspace, storeName, coverageName, geoTiff, srs, policy, defaultStyle, bbox, theMeta, config,LoginLevel.DEFAULT);
		
		
		//*************** publish Style
		
		String nameStyle="TestStyle"+System.currentTimeMillis();
		String attributeName="probability";
		Integer maxClasses=5;
		Color c1= Color.YELLOW;
		Color c2= Color.RED;
		Class typeValue =Double.class;
		Double maxValue=1d;
		Double minValue=0d;
		String sldStyle=StyleUtils.createStyle(nameStyle, attributeName, maxClasses, c1, c2, typeValue, maxValue, minValue);
		System.out.println("Generated Style is : ");
		System.out.println(sldStyle);
//		System.out.println(gisInterface.publishStyle(, nameStyle));
		
		
		//************** 
		
	}

	
	
	
	
}
