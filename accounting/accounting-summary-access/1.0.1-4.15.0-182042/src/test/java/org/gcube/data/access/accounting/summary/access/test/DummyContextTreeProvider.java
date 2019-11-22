package org.gcube.data.access.accounting.summary.access.test;

import org.gcube.accounting.accounting.summary.access.impl.ContextTreeProvider;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;

public class DummyContextTreeProvider implements ContextTreeProvider {

	
	@Override
	public ScopeDescriptor getTree(Object context) {
		
		
		// SOBIG DATA GAteway
		
		
		ScopeDescriptor toReturn=new ScopeDescriptor("SoBigData Gateway", "20508");
		ScopeDescriptor group1=new ScopeDescriptor("D4Science Catalog","1348907");
		group1.getChildren().add(new ScopeDescriptor("Resource Catalog", "/d4science.research-infrastructures.eu/SoBigData/ResourceCatalogue"));
		toReturn.getChildren().add(group1);
		ScopeDescriptor group2=new ScopeDescriptor("SoBigData Exploratories","asdgkljhr");
		group2.getChildren().add(new ScopeDescriptor("CityOfCitizens","/d4science.research-infrastructures.eu/SoBigData/CityOfCitizens"));
		group2.getChildren().add(new ScopeDescriptor("Migration Studies","/d4science.research-infrastructures.eu/SoBigData/MigrationStudies"));
		group2.getChildren().add(new ScopeDescriptor("Social Debates","/d4science.research-infrastructures.eu/SoBigData/SocietalDebates"));
		group2.getChildren().add(new ScopeDescriptor("SportDataScience","/d4science.research-infrastructures.eu/SoBigData/SportsDataScience"));
		group2.getChildren().add(new ScopeDescriptor("WellBeingAndEconomy","/d4science.research-infrastructures.eu/SoBigData/WellBeingAndEconomy"));
		toReturn.getChildren().add(group2);		
		ScopeDescriptor group3=new ScopeDescriptor("SoBigData Lab and Services","fvjbhdfgjkh");
		group2.getChildren().add(new ScopeDescriptor("E-Learning_Area","/d4science.research-infrastructures.eu/SoBigData/E-Learning_Area"));
		group2.getChildren().add(new ScopeDescriptor("M-ATLAS","/d4science.research-infrastructures.eu/SoBigData/M-ATLAS"));
		group2.getChildren().add(new ScopeDescriptor("SMAPH","/d4science.research-infrastructures.eu/SoBigData/SMAPH"));
		group2.getChildren().add(new ScopeDescriptor("SoBigDataLab","/d4science.research-infrastructures.eu/SoBigData/SoBigDataLab"));
		group2.getChildren().add(new ScopeDescriptor("TagMe","/d4science.research-infrastructures.eu/SoBigData/TagMe"));
		toReturn.getChildren().add(group3);
		ScopeDescriptor group4=new ScopeDescriptor("SoBigData Communities Management","cvmbnerotfg8");
		group4.getChildren().add(new ScopeDescriptor("SoBigData.eu", "/d4science.research-infrastructures.eu/gCubeApps/SoBigData.eu"));
		group4.getChildren().add(new ScopeDescriptor("SoBigData.it", "/d4science.research-infrastructures.eu/gCubeApps/SoBigData.it"));
		toReturn.getChildren().add(group4);		
		return toReturn;
	}
}
