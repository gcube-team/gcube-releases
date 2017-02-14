package gr.uoa.di.madgik.workflow.adaptor.search.test;

import java.util.HashSet;
import java.util.Set;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

public class SimpleDatasourceSearchPlanGenerator implements Generator 
{
	
	@Override
	public PlanNode generate() 
	{
	//String cql = "((((gDocCollectionID == &quot;3572c6f0-2f5e-11df-a838-c20ddc2e724e&quot;) and (gDocCollectionLang == &quot;en&quot;))) and (819777d3-400f-41ee-8e4d-03bcb1e66a1d any map)) project 52b0886f-07e3-43cd-83a9-94cf0d707667 2fa6ce83-2549-4c58-8384-e0b375c9d300";
	//	String cql = "((79697524-e3bf-457b-891a-faa3a9b0385f contains map) and (((gDocCollectionID == 3572c6f0-2f5e-11df-a838-c20ddc2e724e) and (gDocCollectionLang == en)))) project 1d6070b7-5d64-40ae-915a-8351fe94df4e 79697524-e3bf-457b-891a-faa3a9b0385f 8db46f79-358b-4dfb-b085-843022818504";
	//	String cql = "((((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))";
		String cql = "((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))";
	//	String cql = "((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))";
	//	String cql = "((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687";
	//	String cql = "((((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687";
	//	String cql = "((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687";
		
		
		Set<String> datasourceInstanceIds = new HashSet<String>();
	//	datasourceInstanceIds.add("5f1da930-6a67-11e0-a344-bb3039c7c23b"); 
	//	datasourceInstanceIds.add("001c2f00-abcc-11e0-afbe-c703ad50473c"); //1
	 	datasourceInstanceIds.add("7c23f090-a331-11e0-855d-cb4345c5ce23"); //119
	//   datasourceInstanceIds.add("342dddd0-a63a-11e0-98cf-e219cd24d91b"); //4
	//	datasourceInstanceIds.add("63cf7850-a63a-11e0-98d0-e219cd24d91b");
	//	datasourceInstanceIds.add("b3e6e600-abe1-11e0-abe8-a7a4579046dd");
	//	datasourceInstanceIds.add("d48100b0-a2fb-11e0-b7e9-abd5e3cba12b");
		
		
		PlanNode FTIndexNode = new DataSourceNode(datasourceInstanceIds, null, cql, null);
		return FTIndexNode;
	}
}
