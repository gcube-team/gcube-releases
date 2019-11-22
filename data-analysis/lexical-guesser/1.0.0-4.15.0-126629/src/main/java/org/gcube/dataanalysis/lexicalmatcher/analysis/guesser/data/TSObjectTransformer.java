package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data;

import java.math.BigDecimal;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.graph.GraphFramer;



public class TSObjectTransformer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static CategoryOrderedList transform2List(DBObjectTranslator dbo, LexicalEngineConfiguration config){
		return transform2List(dbo,config,null);
	}
	
	public static CategoryOrderedList transform2List(DBObjectTranslator dbo, LexicalEngineConfiguration config, String filter){
		CategoryOrderedList col = new CategoryOrderedList(config);
		for (Category cat:dbo.categories){
			if ((filter==null) || filter.equalsIgnoreCase(cat.getName()))
				col.addCategory(cat);
		}
		return col;
	}
	
	
	
	public static void transform2Graph(DBObjectTranslator dbo){
		
		GraphFramer starter = new GraphFramer("Time Series Graph");
		BigDecimal total = new BigDecimal(dbo.totalCatElements);
//		total = new BigDecimal(100).divide(total,2,BigDecimal.ROUND_HALF_UP);
		for (Category cat:dbo.categories){
			
			BigDecimal bd = new BigDecimal(cat.getNumberOfElements());
			
			bd = bd.divide(total,4,BigDecimal.ROUND_HALF_UP);
			bd = bd.multiply(new BigDecimal(100));
			bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
//			double perc = bd.doubleValue()*100;
			
			String builtname = cat.getName()+":"+bd+"% ";
			
			starter.graphDisplayer.addVertex(builtname);
		}
		for (RelationEdge rel:dbo.relations){
			Category cat = dbo.getCategoryfromIndex(rel.getFrom());
			BigDecimal bd = new BigDecimal(cat.getNumberOfElements());
			bd = bd.divide(total,4,BigDecimal.ROUND_HALF_UP);
			bd = bd.multiply(new BigDecimal(100));
			bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
//			double perc = bd.doubleValue()*100;
			
			String name1 = cat.getName()+":"+bd+"% ";
			
			cat = dbo.getCategoryfromIndex(rel.getTo());
			bd = new BigDecimal(cat.getNumberOfElements());
			bd = bd.divide(total,4,BigDecimal.ROUND_HALF_UP);
			bd = bd.multiply(new BigDecimal(100));
			bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
//			perc = bd.doubleValue()+100;
			
			String name2 = cat.getName()+":"+bd+"% ";
			starter.graphDisplayer.addEdge(name1,name2,new BigDecimal(rel.getWeigth()).divide(new BigDecimal(dbo.totalCatElements),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
//			starter.graphDisplayer.addEdge(name1,name2,0);
		}
		
//		starter.graphDisplayer.generateRandomGraph();
		starter.graphDisplayer.generateUpTo5StarGraph();
		
		starter.go();
	}
}
