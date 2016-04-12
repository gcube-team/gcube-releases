package org.gcube.data.analysis.tabulardata.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

public class TableReferenceReplacer {

	private Expression currentExpression;

	private HashMap<TableId,List<ColumnReference>> referenceMap=new HashMap<TableId,List<ColumnReference>>();
	
	private int totalReferenceCount=0;
	
	public TableReferenceReplacer(Expression originalExpression) throws MalformedExpressionException {
		super();
		this.currentExpression = (Expression) originalExpression.clone();
		this.currentExpression.validate();
		scanForReferences(this.currentExpression);
		for(List<ColumnReference> references:referenceMap.values())totalReferenceCount+=references.size();
	}
	
	public Set<TableId> getTableIds(){
		return referenceMap.keySet();
	}
	
	public int getReferenceCount(){
		return totalReferenceCount;
	}
	
	public int getReferenceCount(TableId id){
		try{
			return referenceMap.get(id).size();
		}catch(Exception e){
			return 0;
		}
	}
	
	public List<ColumnReference> getReferences(TableId id){
		if(referenceMap.containsKey(id))
			return new ArrayList<ColumnReference>(referenceMap.get(id));
		else return Collections.emptyList();
	}
	
	
	public TableReferenceReplacer replaceColumnReference(ColumnReference toReplace, ColumnReference toSet){
		List<ColumnReference> theList=referenceMap.get(toReplace.getTableId());
		if(theList!=null){
			List<Integer> found=new ArrayList<Integer>();
			//look for matching
			for(int i=theList.size()-1;i>=0;i--){
				ColumnReference ref=theList.get(i);
				if(toReplace.equals(ref)) found.add(i);				
			}
			//remove matching, update values and pin
			for(Integer index:found){
				ColumnReference ref=theList.remove(index.intValue());
				ref.setColumnId(toSet.getColumnId());
				ref.setTableId(toSet.getTableId());
				ref.setType(toSet.getType());
				pinColumnReference(ref);
			}			
		}
		return this;
	}
		
	public TableReferenceReplacer replaceTableId(TableId toReplace,TableId toSet){
		if(referenceMap.containsKey(toReplace)){
			for(ColumnReference ref: referenceMap.get(toReplace)){
				ref.setTableId(toSet);
				pinColumnReference(ref);
			}
			referenceMap.remove(toReplace);
		}
		return this;
	}
	
	public TableReferenceReplacer replaceAllTableIds(TableId toSet){
		for(TableId key:referenceMap.keySet()){
			replaceTableId(key,toSet);
		}
		return this;
	}
	
	private void scanForReferences(Expression toScan){
		if(toScan instanceof LeafExpression){
			if(toScan instanceof ColumnReference) pinColumnReference((ColumnReference) toScan); 
		}else {
			//Composite
			if(toScan instanceof MultipleArgumentsExpression){
				for(Expression expr:((MultipleArgumentsExpression) toScan).getArguments()) 
					scanForReferences(expr);					
			}else if(toScan instanceof BinaryExpression){
				BinaryExpression binary=(BinaryExpression)toScan;
				scanForReferences(binary.getLeftArgument());
				scanForReferences(binary.getRightArgument());
			}else if(toScan instanceof UnaryExpression){
				scanForReferences(((UnaryExpression)toScan).getArgument());
			}else if(toScan instanceof ExternalReferenceExpression){
				scanForReferences(((ExternalReferenceExpression)toScan).getSelectArgument());
				scanForReferences(((ExternalReferenceExpression)toScan).getExternalCondition());
			}else if(toScan instanceof TextReplaceMatchingRegex){
				scanForReferences(((TextReplaceMatchingRegex)toScan).getToCheckText());
				scanForReferences(((TextReplaceMatchingRegex)toScan).getRegexp());
				scanForReferences(((TextReplaceMatchingRegex)toScan).getReplacingValue());
			}else if(toScan instanceof SubstringByIndex){
				scanForReferences(((SubstringByIndex)toScan).getSourceString());
				scanForReferences(((SubstringByIndex)toScan).getFromIndex());
				scanForReferences(((SubstringByIndex)toScan).getToIndex());
			}
		}
	}
	/**
	 * Puts ref into the referenceSet
	 * 
	 * @param ref
	 */
	private void pinColumnReference(ColumnReference ref){
		TableId id=ref.getTableId();
		if(!referenceMap.containsKey(id)) referenceMap.put(id, new ArrayList<ColumnReference>());
		referenceMap.get(id).add(ref);
	}
	
	public Expression getExpression() {
		return currentExpression;
	}
}
