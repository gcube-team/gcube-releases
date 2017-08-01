/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.service;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.ReferenceResolver;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.NamesRetriever;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 24, 2014
 *
 */
public class ExpressionEvaluatorInstance {
	
	private DescriptionExpressionEvaluatorFactory evaluatorFactory;
	@SuppressWarnings("unused")
	private TemplateService service;
	
	public static Logger logger = LoggerFactory.getLogger(ExpressionEvaluatorInstance.class);
	
	/**
	 * 
	 */
	public ExpressionEvaluatorInstance(TemplateService service) {
		logger.info("Instancing..");
		this.service = service;

		ReferenceResolverInstance referenceResolver = new ReferenceResolverInstance();
		NamesRetriever namesRetriever = new NamesRetriever(referenceResolver, new CubeManagerInstanceTemplate(service));
		evaluatorFactory = new DescriptionExpressionEvaluatorFactory(namesRetriever);
	}
	
	public String evaluate(Expression expression){
		logger.info("evaluating: "+expression);
		String value = evaluatorFactory.getEvaluator(expression).evaluate();
		logger.info("evaluate is: "+value);
		return value;
	}
	
	public class ReferenceResolverInstance implements ReferenceResolver{

		/* (non-Javadoc)
		 * @see org.gcube.data.analysis.tabulardata.expression.evaluator.ReferenceResolver#getColumn(org.gcube.data.analysis.tabulardata.model.column.ColumnReference)
		 */
		@Override
		public Column getColumn(ColumnReference columnRef) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.gcube.data.analysis.tabulardata.expression.evaluator.ReferenceResolver#getTable(org.gcube.data.analysis.tabulardata.model.column.ColumnReference)
		 */
		@Override
		public Table getTable(ColumnReference columnRef) {
			return null;
		}
		
	}
	
	

}
