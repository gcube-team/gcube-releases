package gr.uoa.di.madgik.workflow.adaptor.search.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

public class SimpleMergeSearchPlanGenerator implements Generator 
{

	@Override
	public PlanNode generate() 
	{
		Set<String> leftDataSourceInstanceIds = new HashSet<String>();
		leftDataSourceInstanceIds = new HashSet<String>();
		leftDataSourceInstanceIds.add("001c2f00-abcc-11e0-afbe-c703ad50473c");
		String leftDataSourceCql = "((((gDocCollectionID == \"6b03f4e0-9d66-11de-8d97-a04a2d1ca936\") and (gDocCollectionLang == \"en\"))) and (0b8e7480-39e9-4084-8948-7bce81c6e4be = ap*)) project 0b2634e2-0cfe-47c9-9337-b08959c65f50 42104edd-727f-44be-95ca-6861727b6507 190ee77a-2f46-4cc9-afa3-846c76ba01d6 15a1330f-2bba-4ee2-8716-2ec51e46eb88 6e30f0c1-8260-4867-9041-1a517eff0f72 7d9131ae-f99c-489d-936c-0cc581343aa3 6580b662-acbf-4f0c-9c56-5699e63b259f fb78425f-412f-4c03-8cae-57f07dc1079e 758bb84b-fc86-4459-bda0-d549bffeaabc 9b2943b3-210f-4e99-9816-512f097c7697 d579aee7-0933-46dd-b147-3c33bd9e72c6 26a4cc45-cbe4-453f-bca5-537108a71758";
		PlanNode leftDataSourceNode = new DataSourceNode(leftDataSourceInstanceIds, null, leftDataSourceCql, null);
		
		Set<String> rightDataSourceInstanceIds = new HashSet<String>();
		rightDataSourceInstanceIds = new HashSet<String>();
		rightDataSourceInstanceIds.add("7c23f090-a331-11e0-855d-cb4345c5ce23");
		String rightDataSourceCql = "((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (0b8e7480-39e9-4084-8948-7bce81c6e4be = ap*)) project 0b2634e2-0cfe-47c9-9337-b08959c65f50 42104edd-727f-44be-95ca-6861727b6507 190ee77a-2f46-4cc9-afa3-846c76ba01d6 15a1330f-2bba-4ee2-8716-2ec51e46eb88 6e30f0c1-8260-4867-9041-1a517eff0f72 7d9131ae-f99c-489d-936c-0cc581343aa3 6580b662-acbf-4f0c-9c56-5699e63b259f fb78425f-412f-4c03-8cae-57f07dc1079e 758bb84b-fc86-4459-bda0-d549bffeaabc 9b2943b3-210f-4e99-9816-512f097c7697 d579aee7-0933-46dd-b147-3c33bd9e72c6 26a4cc45-cbe4-453f-bca5-537108a71758";
		PlanNode rightDataSourceNode = new DataSourceNode(rightDataSourceInstanceIds, null, rightDataSourceCql, null);
		
		ArrayList<PlanNode> mergeChildren = new ArrayList<PlanNode>();
		mergeChildren.add(leftDataSourceNode);
		mergeChildren.add(rightDataSourceNode);
		PlanNode mergeNode = new OperatorNode("merge", new HashMap<String, String>(), mergeChildren, null);
		//String cql = "((((gDocCollectionID == &quot;3572c6f0-2f5e-11df-a838-c20ddc2e724e&quot;) and (gDocCollectionLang == &quot;en&quot;))) and (819777d3-400f-41ee-8e4d-03bcb1e66a1d any map)) project 52b0886f-07e3-43cd-83a9-94cf0d707667 2fa6ce83-2549-4c58-8384-e0b375c9d300";
	
		return mergeNode;
	}
}
