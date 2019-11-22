/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine.exl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class HavingFunctions {
	
	protected static Logger logger = LoggerFactory.getLogger(HavingFunctions.class);
	
	protected static Map<String, XPathExpression> expression_cache;
	
	static {
		expression_cache = new HashMap<String, XPathExpression>();
	}
	
	protected HavingContext<?> context;

	/**
	 * @param context
	 * @throws ParserConfigurationException 
	 */
	public HavingFunctions(JexlContext context)  {
		this.context = (HavingContext<?>) context;
	}


	/**
	 * Xpath function.
	 * @param xpath the xpath expression to evaluate.
	 * @return <code>true</code> if the expression is evaluated <code>true</code>, <code>false</code> otherwise.
	 * @throws SAXException if an error occurs.
	 * @throws IOException if an error occurs.
	 * @throws XPathExpressionException if an error occurs.
	 */
	public boolean xpath(String xpath) throws SAXException, IOException, XPathExpressionException
	{
		logger.debug("xpath {} ", xpath);
		Document doc = context.getDocument();
		XPathExpression expression = getExpression(xpath);
		boolean eval = (Boolean) expression.evaluate(doc, XPathConstants.BOOLEAN);
		logger.trace("xpath eval: {}",eval);
		return eval;
	}
	
	/**
	 * Retrieved the compiled expression.
	 * @param xpathExpression
	 * @return
	 * @throws XPathExpressionException
	 */
	protected XPathExpression getExpression(String xpathExpression) throws XPathExpressionException
	{
		logger.trace("getExpression xpathExpression {}", xpathExpression);
		XPathExpression expression = expression_cache.get(xpathExpression);
		if (expression == null) {
			logger.trace("Building xpath expression");
			expression = buildExpression(xpathExpression);
			expression_cache.put(xpathExpression, expression);
		} else logger.trace("XPath expression already cached");
		return expression;
	}
	
	/**
	 * Compiles the passed expression
	 * @param xpathExpression
	 * @return
	 * @throws XPathExpressionException
	 */
	protected XPathExpression buildExpression(String xpathExpression) throws XPathExpressionException
	{
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(xpathExpression);
		return expr;
	}
	
	/**
	 * EXL function.
	 * @param exlExpression the exl expression to evaluate.
	 * @return
	 */
	public boolean exl(String exlExpression) {
		logger.debug("exl exlExpression: {}", exlExpression);
		
		JexlEngine engine = context.getEngine();
		Expression expression = engine.createExpression(exlExpression);
		boolean eval = (Boolean) expression.evaluate(context);		
		logger.trace("xpath eval: {}",eval);
		return eval;
	}
	
	public boolean lucio(String expression) {
		logger.debug("lucio expression: {}", expression);
		boolean eval = ("oh".equalsIgnoreCase(expression)) || ("fro".equalsIgnoreCase(expression));
		logger.trace("xpath eval: {}",eval);
		return eval;
	}
 

}
