package org.gcube.data.analysis.tabulardata.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case.WhenConstruct;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;

public class PlaceholderReplacer {

	private Expression expression;
	
	private Map<String,List<PlaceholderContainer>> placeholderMap=new HashMap<String, List<PlaceholderContainer>>();
	
	public PlaceholderReplacer(Expression expr) throws MalformedExpressionException {
		this.expression=(Expression) expr.clone();
		this.expression.validate();
		scanForReferences(expression);		
	}
	
	public boolean hasPlaceholder(){
		return !placeholderMap.isEmpty();
	}
	
	
	/**
	 *  
	 * @return the count of placeholder found in the current Expression
	 */
	public Integer getCount(){
		return placeholderMap.size();
	}
	
	/**
	 * Replaces all found placeholders with the passed reference. If a TypedColumnReference is passed, its DataType must be consistent with the one declared in found Placeholders 
	 * 
	 * @param ref
	 * @return
	 * @throws MalformedExpressionException 
	 */
	public PlaceholderReplacer replaceAll(ColumnReference ref) throws MalformedExpressionException{
		for(String id:placeholderMap.keySet())
			replaceById(ref,id);
		return this;
	}
	
	
	public PlaceholderReplacer replaceById(ColumnReference ref, String placeholderId) throws MalformedExpressionException{
		if(placeholderMap.containsKey(placeholderId)){
			for(PlaceholderContainer container:placeholderMap.get(placeholderId))
				container.setReference(ref);			
		}
		return this;
	}
	
	public Expression getExpression(){
		return expression;
	}
	
	private void scanForReferences(Expression toScan){
		if(!(toScan instanceof LeafExpression)){
			//Composite
			if(toScan instanceof MultipleArgumentsExpression){
				List<Expression> args=((MultipleArgumentsExpression) toScan).getArguments();
				for(int i=0;i<args.size();i++){
					Expression expr=args.get(i);
					if(expr instanceof ColumnReferencePlaceholder) putInMap(((MultipleArgumentsExpression) toScan), (ColumnReferencePlaceholder) expr, i);
					else scanForReferences(expr);					
				}
			}else if(toScan instanceof BinaryExpression){
				BinaryExpression binary=(BinaryExpression)toScan;
				
				if(binary.getLeftArgument() instanceof ColumnReferencePlaceholder) putInMap(binary,(ColumnReferencePlaceholder) binary.getLeftArgument(),0);
				else scanForReferences(binary.getLeftArgument());
				
				if(binary.getRightArgument() instanceof ColumnReferencePlaceholder) putInMap(binary,(ColumnReferencePlaceholder) binary.getRightArgument(),1);
				else scanForReferences(binary.getRightArgument());
				
			}else if(toScan instanceof UnaryExpression){
				Expression child=((UnaryExpression)toScan).getArgument();
				if(child instanceof ColumnReferencePlaceholder) putInMap((UnaryExpression)toScan, (ColumnReferencePlaceholder) child, 0);
				else scanForReferences(child);
				
			}else if(toScan instanceof ExternalReferenceExpression){
				ExternalReferenceExpression expr=(ExternalReferenceExpression) toScan;
				if(expr.getSelectArgument() instanceof ColumnReferencePlaceholder) putInMap(expr,(ColumnReferencePlaceholder)expr.getSelectArgument(),0);
				else scanForReferences(expr.getSelectArgument());
				if(expr.getExternalCondition() instanceof ColumnReferencePlaceholder) putInMap(expr,(ColumnReferencePlaceholder)expr.getExternalCondition(),1);
				else scanForReferences(expr.getExternalCondition());
			}else if(toScan instanceof TextReplaceMatchingRegex){
				TextReplaceMatchingRegex expr=(TextReplaceMatchingRegex) toScan;
				if(expr.getToCheckText() instanceof ColumnReferencePlaceholder) putInMap(expr, (ColumnReferencePlaceholder) expr.getToCheckText(), 0);
				else scanForReferences(expr.getToCheckText());				
			}else if(toScan instanceof SubstringByIndex){
				SubstringByIndex expr=(SubstringByIndex) toScan;
				if(expr.getSourceString() instanceof ColumnReferencePlaceholder) putInMap(expr, (ColumnReferencePlaceholder) expr.getSourceString(), 0);
				else scanForReferences(expr.getSourceString());
				if(expr.getFromIndex() instanceof ColumnReferencePlaceholder) putInMap(expr, (ColumnReferencePlaceholder) expr.getFromIndex(), 1);
				else scanForReferences(expr.getFromIndex());
				if(expr.getToIndex() instanceof ColumnReferencePlaceholder) putInMap(expr, (ColumnReferencePlaceholder) expr.getToIndex(), 2);
				else scanForReferences(expr.getToIndex());
			}else if(toScan instanceof Case){
				Case expr=(Case) toScan;
				int index=0;
				for (WhenConstruct construct: ((Case) toScan).getWhenConstructs()){
					if(construct.getWhen() instanceof ColumnReferencePlaceholder) putInMap(expr, (ColumnReferencePlaceholder) construct.getWhen(), index);
					else scanForReferences(construct.getWhen());
					if(construct.getThen() instanceof ColumnReferencePlaceholder) putInMap(expr, (ColumnReferencePlaceholder) construct.getThen(), index+1);
					else scanForReferences(construct.getThen());
					index++;
				}
				
			}
		}
	}
	
	private void putInMap(CompositeExpression holder,ColumnReferencePlaceholder placeholder,int index){
		if(!placeholderMap.containsKey(placeholder.getId()))
			placeholderMap.put(placeholder.getId(), new ArrayList<PlaceholderContainer>());
		placeholderMap.get(placeholder.getId()).add(new PlaceholderContainer(index, holder));
	}
	
	
	//****************************** INNER LOGIC
	
	
	private class PlaceholderContainer{
		
		private int index;
		private CompositeExpression expression;
		public PlaceholderContainer(int index, CompositeExpression expression) {
			super();
			this.index = index;
			this.expression = expression;
		}
		
		public void setReference(ColumnReference reference){
			if(expression instanceof UnaryExpression) ((UnaryExpression)expression).setArgument(reference);
			else if(expression instanceof BinaryExpression){
				BinaryExpression bin=(BinaryExpression) expression;
				if(index==0) bin.setLeftArgument(reference);
				else bin.setRightArgument(reference);
			}else if(expression instanceof ExternalReferenceExpression){
				ExternalReferenceExpression expr=(ExternalReferenceExpression) expression;
				if(index==0) expr.setSelectArgument(reference);
				else expr.setExternalCondition(reference);				
			}else if(expression instanceof MultipleArgumentsExpression){
				MultipleArgumentsExpression multi=(MultipleArgumentsExpression) expression;
				multi.getArguments().set(index, reference);
			}else if(expression instanceof TextReplaceMatchingRegex){
				TextReplaceMatchingRegex reg=(TextReplaceMatchingRegex) expression;
				reg.setToCheckText(reference);
			}else if(expression instanceof SubstringByIndex){
				SubstringByIndex sub=(SubstringByIndex) expression;
				if(index==0) sub.setSourceString(reference);
				else if(index==1) sub.setFromIndex(reference);
				else sub.setToIndex(reference);
			}else if(expression instanceof Case){
				Case caseExp=(Case) expression;
				int ind = index/2;
				int mod = index%2;
				WhenConstruct whenConstruct = caseExp.getWhenConstructs().get(ind);
				if (mod==0) whenConstruct.setWhen(reference);
				else whenConstruct.setThen(reference);
			}
		}
		
	}
}
