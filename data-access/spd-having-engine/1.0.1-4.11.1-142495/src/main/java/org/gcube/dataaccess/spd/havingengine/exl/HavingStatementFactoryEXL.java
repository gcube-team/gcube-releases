/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine.exl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.gcube.dataaccess.spd.havingengine.HavingStatement;
import org.gcube.dataaccess.spd.havingengine.HavingStatementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class HavingStatementFactoryEXL implements HavingStatementFactory {
	
	protected Logger logger = LoggerFactory.getLogger(HavingStatementFactoryEXL.class);
	
	protected JexlEngine engine;
	
	public HavingStatementFactoryEXL()
	{
		engine = new JexlEngine();
		Map<String, Object> functions = new HashMap<String, Object>();
		functions.put(null, HavingFunctions.class);
		engine.setFunctions(functions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> HavingStatement<T> compile(String expression) throws Exception {
		logger.debug("compile {}",expression);
		Expression expr = engine.createExpression(expression);
		return new HavingStatementJEXL<T>(engine, expr);
	}
	
	protected class HavingStatementJEXL<T> implements HavingStatement<T> {
		
		protected Logger logger = LoggerFactory.getLogger(HavingStatementJEXL.class);

		protected JexlEngine engine;
		protected Expression expression;

		/**
		 * @param engine
		 * @param expression
		 */
		private HavingStatementJEXL(JexlEngine engine, Expression expression) {
			this.engine = engine;
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(T element) {
			try {
				JexlContext context = new HavingContext<T>(engine, element);
				return (Boolean)expression.evaluate(context);
			} catch (Exception e)
			{
				logger.debug("Error evaluating expression", e);
				return false;
			}
		}
		
	}

}
