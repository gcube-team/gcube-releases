package org.gcube.common.informationsystem.client.eximpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISTemplateQuery;
import org.gcube.common.core.informationsystem.client.QueryParameter;



public  abstract class ISTemplateQueryImpl<RESULT> extends ExistQuery<RESULT> implements ISTemplateQuery<RESULT> {
		
		public static final String resultVar ="$result"; 
		private static final String template = NS+" for "+resultVar+" in collection(\"/db<COLLECTION/>\")//Document<ROOT/> <FILTER/> return <RESULT/>";
		
		protected StringBuilder filter=new StringBuilder();
		protected StringBuilder scopeFilter=new StringBuilder();
		
		protected ISTemplateQueryImpl() {//add static parameters
			this.setExpression(template);
			this.addParameters(new QueryParameter("RESULT", resultVar),
							   new QueryParameter("COLLECTION",this.getCollection().length()==0?this.getCollection():"/"+this.getCollection()),
							   new QueryParameter("ROOT",this.getRoot().length()==0?this.getRoot():"/"+this.getRoot()));
		}
		
		/** {@inheritDoc} */
		protected boolean isWellFormed() {return super.isWellFormed() && this.getCollection()!=null && this.getRoot()!=null;}
		
		public void addAtomicConditions(AtomicCondition ... properties) {
			if (properties==null) return;
			for (AtomicCondition prop : properties) {
				Matcher m = Pattern.compile("(.*)/@(.*)$").matcher(prop.name);
				String condition=m.find()?resultVar+m.group(1)+"[string(@"+m.group(2)+") eq \""+prop.value+"\"]":
					resultVar+prop.name+"/string() eq \""+prop.value+"\"";
				this.addGenericCondition(condition);
			}
		}
		
				
		public void addGenericCondition(String property){
			if (filter.length()==0) filter.append(" where (").append(property).append(")"); 
			else filter.append(" and (").append(property).append(")");
		}
		
		public void clearConditions(){filter.setLength(0);}
		
		protected abstract String getCollection();	
		protected abstract String getRoot();
		

		/**{@inheritDoc}*/
		public String getExpression() {
			if (filter!=null) this.addParameters(new QueryParameter("FILTER",this.filter.toString()));
			return super.getExpression();
		}
		
}

