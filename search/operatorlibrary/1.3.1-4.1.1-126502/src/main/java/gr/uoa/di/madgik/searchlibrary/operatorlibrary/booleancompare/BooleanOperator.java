package gr.uoa.di.madgik.searchlibrary.operatorlibrary.booleancompare;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator.CompareTokens;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author UoA
 * 
 */
public class BooleanOperator {
	/**
	 * Logger used by this class
	 */
	private Logger logger = LoggerFactory.getLogger(BooleanOperator.class.getName());

	/**
	 * Name of the record element in the result set.
	 */
	protected static String RSRecordName = "RSRecord"; 
	/**
	 * Statistics
	 */
	private StatsContainer stats = null;
	/**
	 * Local RS reader
	 */
	private IRecordReader<Record>[] readers;

	/**
	 * Document object
	 */
	private Document doc;

	/**
	 * The default timeout used by the {@link IRecordWriter} and the {@link IRecordReader}. Currently set to 60.
	 */
	private static final long TimeoutDef = 60;
	/**
	 * The default timeout unit used by the {@link IRecordWriter} and the {@link IRecordReader}. The current default unit is seconds.
	 */
	private static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	
	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;
	
	/**
	 * Main service method. Given a hypothesis expression and a set of RS eprs
	 * this service decides whether this hypothesis is true or not. It returns a
	 * new epr of an RS which only contains &lt;BooleanResult&gt; with boolean
	 * value. This version of the method uses the default timeout
	 * 
	 * @param eprs
	 *            An array of eprs of the services that participate in the
	 *            hypothesis
	 * @param expression
	 *            hypothesis expression
	 * @param stats used to populate statistics
	 * @param timeout The timeout which will be used both by the reader and the writer
	 * @param timeUnit The time unit of the timeout used
	 * 
	 * @return RS epr
	 * @throws Exception
	 *             in case of error
	 */
	public URI compareMe(URI[] locators, String expression, StatsContainer stats) throws Exception {
		return compareMe(locators, expression, stats, TimeoutDef, TimeUnitDef);
	}
	
	/**
	 * Main service method. Given a hypothesis expression and a set of RS eprs
	 * this service decides whether this hypothesis is true or not. It returns a
	 * new epr of an RS which only contains &lt;BooleanResult&gt; with boolean
	 * value. This version of the method allows timeout to be configured to the desired value
	 * 
	 * @param eprs
	 *            An array of eprs of the services that participate in the
	 *            hypothesis
	 * @param expression hypothesis expression
	 * @param stats used to populate statistics
	 * @param timeout The timeout which will be used both by the reader and the writer
	 * @param timeUnit The time unit of the timeout used
	 * 
	 * @return RS epr
	 * @throws Exception in case of error
	 */
	public URI compareMe(URI[] locators, String expression, StatsContainer stats, long timeout, TimeUnit timeUnit)
			throws Exception {
		long start=Calendar.getInstance().getTimeInMillis();
		System.out.println("Expression: " + expression);
		for (int i = 0; i < locators.length; i++)
			System.out.println("locator(" + i + "): " + locators[i]);

		this.timeout = timeout;
		this.timeUnit = timeUnit;
		// Create readers
		readers = new IRecordReader[locators.length];

		long startInit=Calendar.getInstance().getTimeInMillis();
		for (int i = 0; i < locators.length; i++) {
			try {
				readers[i] = new ForwardReader<Record>(locators[i]);
				
			} catch (Exception e) {
				logger.error("could not complete comparison. throwing Exception", e);
				throw new Exception(e.getMessage());
			}
		}

//		// localize result set and create an iterator out of the new reader
//		RSXMLIterator[] iter = new RSXMLIterator[eprs.length];
//		localReader = new RSXMLReader[eprs.length];
//		for (int i = 0; i < eprs.length; i++) {
//			try {
//				localReader[i] = reader[i].makeLocal(new RSResourceLocalType());
//				iter[i] = localReader[i].getRSIterator();
//			} catch (Exception e) {
//				logger.error("could not complete comparison. throwing Exception", e);
//				throw new Exception(e.getMessage());
//			}
//		}
		stats.timeToInitialize(Calendar.getInstance().getTimeInMillis()-startInit);
		stats.timeToFirstInput(Calendar.getInstance().getTimeInMillis()-startInit);
		boolean compResult = false;

		// parse the XML expression
		IRecordWriter<Record> writer = null;
		try {
			getDocFromString(expression);
			compResult = evaluateDocument(this.doc.getDocumentElement());
//			System.out.println("OVERALL RESULT: " + compResult);
			
			writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[]{new GenericRecordDefinition(new FieldDefinition[]{new StringFieldDefinition()})}, 100,
					RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
			
			
			GenericRecord rec = new GenericRecord();
			StringField field = new StringField(compResult == true ? "true" : "false");
			rec.setFields(new Field[]{field});
			
			if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose)
				logger.info("Consumer side stopped consumption. Stopping.");
			if(!writer.put(rec, timeout, timeUnit)) {
				if(writer.getStatus() == IBuffer.Status.Open)
					logger.warn("Consumer has timed out");
			}
			stats.timeToFirst(Calendar.getInstance().getTimeInMillis()-start);
			stats.timeToComplete(Calendar.getInstance().getTimeInMillis()-start);
			stats.producedResults(1);
			stats.productionRate(0);
			return writer.getLocator();

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}finally {
			try { writer.close();} catch(Exception e) { }
		}
	}

	/**
	 * parse document, calling the appropriate method
	 * 
	 * @param curNode current XML node
	 * @return boolean
	 * @throws Exception in case of error
	 */
	private boolean evaluateDocument(Node curNode) throws Exception {

		System.out.println("Evaluate Document.");

		// First check if there is a greater than, lower than, equal, lower
		// equal, greater equal operation
		// If there is not any, then throw Exception
		if (isGreaterEqual(curNode.getNodeName()) == false
				&& isGreaterThan(curNode.getNodeName()) == false
				&& isLowerEqual(curNode.getNodeName()) == false
				&& isLowerThan(curNode.getNodeName()) == false
				&& isEqual(curNode.getNodeName()) == false
				&& isNotEqual(curNode.getNodeName()) == false
				&& isLike(curNode.getNodeName()) == false)
			throw new Exception(
					"Root Operation should be one of the following:\n"
							+ OperationSet.GREATER_EQUAL + " "
							+ OperationSet.GREATER_THAN + " "
							+ OperationSet.LOWER_EQUAL + " "
							+ OperationSet.LOWER_THAN + " "
							+ OperationSet.EQUAL + " " + OperationSet.NOT_EQUAL
							+ " " + OperationSet.LIKE);

		NodeList elemChildren = curNode.getChildNodes();

		int elemCount = 0;

		for (int i = 0; i < elemChildren.getLength(); i++)
			if (elemChildren.item(i).getNodeType() == Node.ELEMENT_NODE)
				elemCount++;
		if (elemCount != 2)
			throw new Exception(
					"Equal operation should have two compare expressions");

		// Equal
		if (isEqual(curNode.getNodeName()) == true) {
			System.out.println("EQUAL");
			String[] tok = new String[2];
			int id = 0;
			for (int i = 0; i < elemChildren.getLength(); i++) {
				if(elemChildren.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				System.out.println("I: " + id);
				tok[id] = evaluateExpression(elemChildren.item(i)).toString();
				System.out.println("Finished I: " + id);
				id++;
			}

			System.out.println("Checking equality between " + tok[0] + " and "
					+ tok[1]);

			// check equality
			if (CompareTokens.compare(tok[0], tok[1]) == 0)
				return true;
			return false;
		}

		// Not Equal
		else if (isNotEqual(curNode.getNodeName()) == true) {
			System.out.println("NOT EQUAL");
			String[] tok = new String[2];
			int id = 0;
			for (int i = 0; i < elemChildren.getLength(); i++) {
				if(elemChildren.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				System.out.println("I: " + id);
				tok[id] = evaluateExpression(elemChildren.item(i));
				id++;
			}

			System.out.println("Checking inequality between " + tok[0]
					+ " and " + tok[1]);

			// check inequality
			if (CompareTokens.compare(tok[0], tok[1]) == 0)
				return false;
			return true;
		}

		// Greater Than
		else if (isGreaterThan(curNode.getNodeName()) == true) {
			System.out.println("GREATER THAN");
			String[] tok = new String[2];
			int id = 0;
			for (int i = 0; i < elemChildren.getLength(); i++) {
				if(elemChildren.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				tok[id] = evaluateExpression(elemChildren.item(i));
				id++;
			}

			System.out.println("Checking inequality between " + tok[0]
					+ " and " + tok[1]);

			// check inequality
			if (CompareTokens.compare(tok[0], tok[1]) > 0)
				return true;
			return false;
		}

		// Greater Equal
		else if (isGreaterEqual(curNode.getNodeName()) == true) {
			System.out.println("GREATER EQUAL");
			String[] tok = new String[2];
			int id = 0;
			for (int i = 0; i < elemChildren.getLength(); i++) {
				if(elemChildren.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				tok[id] = evaluateExpression(elemChildren.item(i));
				id++;
			}

			System.out.println("Checking inequality between " + tok[0]
					+ " and " + tok[1]);

			// check equality
			if (CompareTokens.compare(tok[0], tok[1]) >= 0)
				return true;
			return false;
		}

		// Lower Than
		else if (isLowerThan(curNode.getNodeName()) == true) {
			System.out.println("LOWER THAN");
			String[] tok = new String[2];
			int id = 0;
			for (int i = 0; i < elemChildren.getLength(); i++) {
				if(elemChildren.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				tok[id] = evaluateExpression(elemChildren.item(i));
				id++;
			}

			System.out.println("Checking inequality between " + tok[0]
					+ " and " + tok[1]);

			// check equality
			if (CompareTokens.compare(tok[0], tok[1]) < 0)
				return true;
			return false;
		}

		// Lower Equal
		else if (isLowerEqual(curNode.getNodeName()) == true) {
			System.out.println("LOWER EQUAL");
			String[] tok = new String[2];
			int id = 0;
			for (int i = 0; i < elemChildren.getLength(); i++) {
				if(elemChildren.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				tok[id] = evaluateExpression(elemChildren.item(i));
				id++;
			}

			System.out.println("Checking inequality between " + tok[0]
					+ " and " + tok[1]);

			// check equality
			if (CompareTokens.compare(tok[0], tok[1]) <= 0)
				return true;
			return false;
		}

		// Like
		else if (isLike(curNode.getNodeName()) == true) {
			System.out.println("LIKE");
			String[] tok = new String[2];
			int id = 0;
			for (int i = 0; i < elemChildren.getLength(); i++) {
				if(elemChildren.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				tok[id] = evaluateExpression(elemChildren.item(i));
				id++;
			}

			System.out.println("Checking pattern matching between " + tok[0]
					+ " and " + tok[1]);

			// check pattern matching
			Pattern myPattern = Pattern.compile(tok[1]);
			Matcher myMatcher = myPattern.matcher(tok[0]);
			if (myMatcher.find())
				return true;
			return false;
		}

		throw new Exception("This Exception should NEVER be thrown.");
	}

	/**
	 * Computes and returns the expression evaluation in string format
	 * 
	 * @param curNode current node
	 * @return The expression evaluation in string format
	 * @throws Exception in case of error
	 */
	private String evaluateExpression(Node curNode) throws Exception {
		if (isMax(curNode.getNodeName()) == false
				&& isMin(curNode.getNodeName()) == false
				&& isSum(curNode.getNodeName()) == false
				&& isAverage(curNode.getNodeName()) == false
				&& isSize(curNode.getNodeName()) == false
				&& isToken(curNode.getNodeName()) == false)
			throw new Exception(
					"Non-root operation should be one of the following:\n"
							+ OperationSet.MAX + " " + OperationSet.MIN + " "
							+ OperationSet.AVERAGE + " " + OperationSet.SUM
							+ " " + OperationSet.SIZE + " "
							+ OperationSet.TOKEN);

		System.out.println("Inside evaluateExpression method.");
		StringWriter sw = new StringWriter();
		
		//TODO replace with
		//XMLUtils.ElementToWriter((Element) curNode, sw);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer tr = tf.newTransformer();
		tr.transform(new DOMSource((Element)curNode), new StreamResult(sw));
		
		System.out.println("Current node is: " + sw.toString());

		NodeList children = null;

		try {
			// Max
			if (isMax(curNode.getNodeName()) == true) {
				System.out.println("MAX");
				int id = -1;
				String xpath = null;
				String fieldName = null;

				// check is there is an id and xpath element children nodes
				children = curNode.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
						if(isFieldName(children.item(i).getNodeName()))
							fieldName = children.item(i).getFirstChild().getNodeValue();
						if (isID(children.item(i).getNodeName())) {
							if (id >= this.readers.length)
								throw new Exception(
										"Given id is greater than the reader array size.");
							id = new Integer(children.item(i).getFirstChild().getNodeValue()).intValue();
						}
						if (isXPath(children.item(i).getNodeName()))
							xpath = children.item(i).getFirstChild().getNodeValue();
					}
				}

				System.out.println("CALLING MAX");
				System.out.println("xpath: " + xpath);
				ComputeMax comp = new ComputeMax(xpath, fieldName, this.readers[id], this.timeout, this.timeUnit);
				String res = (String) comp.compute();
				System.out.println("RES: " + res);
				return res;
			}
			// Min
			else if (isMin(curNode.getNodeName()) == true) {
				System.out.println("MIN");
				int id = -1;
				String xpath = null;
				String fieldName = null;

				// check is there is an id and xpath element children nodes
				children = curNode.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
						if(isFieldName(children.item(i).getNodeName()))
							fieldName = children.item(i).getFirstChild().getNodeValue();
						if (isID(children.item(i).getNodeName())) {
							if (id >= this.readers.length)
								throw new Exception(
										"Given id is greater than the reader array size.");
							id = new Integer(children.item(i).getFirstChild().getNodeValue()).intValue();
						}
						if (isXPath(children.item(i).getNodeName()))
							xpath = children.item(i).getFirstChild().getNodeValue();
					}
				}

				System.out.println("CALLING MIN");
				ComputeMin comp = new ComputeMin(xpath, fieldName, this.readers[id], this.timeout, this.timeUnit);
				String res = (String) comp.compute();
				System.out.println("RES: " + res);
				return res;
			}
			// sum
			else if (isSum(curNode.getNodeName()) == true) {
				System.out.println("SUM");
				int id = -1;
				String xpath = null;
				String fieldName = null;

				// check is there is an id and xpath element children nodes
				children = curNode.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
						if(isFieldName(children.item(i).getNodeName()))
							fieldName = children.item(i).getFirstChild().getNodeValue();
						if (isID(children.item(i).getNodeName())) {
							if (id >= this.readers.length)
								throw new Exception(
										"Given id is greater than the reader array size.");
							id = new Integer(children.item(i).getFirstChild().getNodeValue()).intValue();
						}
						if (isXPath(children.item(i).getNodeName()))
							xpath = children.item(i).getFirstChild().getNodeValue();
					}
				}

				System.out.println("CALLING SUM");
				ComputeSum comp = new ComputeSum(xpath, fieldName, this.readers[id], this.timeout, this.timeUnit);
				String res = (String) comp.compute();
				System.out.println("RES: " + res);
				return res;
			}
			// Size
			else if (isSize(curNode.getNodeName()) == true) {
				System.out.println("Size");
				int id = -1;
				String xpath = null;
				String fieldName = null;

				// check is there is an id and xpath element children nodes
				children = curNode.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
						if(isFieldName(children.item(i).getNodeName()))
							fieldName = children.item(i).getFirstChild().getNodeValue();
						if (isID(children.item(i).getNodeName())) {
							if (id >= this.readers.length)
								throw new Exception(
										"Given id is greater than the reader array size.");
							id = new Integer(children.item(i).getFirstChild()
									.getNodeValue()).intValue();
						}
						if (isXPath(children.item(i).getNodeName()))
							xpath = children.item(i).getFirstChild().getNodeValue();
					}
				}

				System.out.println("CALLING SIZE");
				ComputeSize comp = new ComputeSize(xpath, fieldName, this.readers[id], this.timeout, this.timeUnit);
				String res = (String) comp.compute();
				System.out.println("RES: " + res);
				return res;
			}
			// Average
			else if (isAverage(curNode.getNodeName()) == true) {
				System.out.println("AVERAGE");
				int id = -1;
				String xpath = null;
				String fieldName = null;

				// check is there is an id and xpath element children nodes
				children = curNode.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
						if(isFieldName(children.item(i).getNodeName()))
							fieldName = children.item(i).getFirstChild().getNodeValue();
						if (isID(children.item(i).getNodeName())) {
							if (id >= this.readers.length)
								throw new Exception(
										"Given id is greater than the reader array size.");
							id = new Integer(children.item(i).getFirstChild().getNodeValue()).intValue();
						}
						if (isXPath(children.item(i).getNodeName()))
							xpath = children.item(i).getFirstChild().getNodeValue();
					}
				}

				System.out.println("id = " + id);
				System.out.println("CALLING AVERAGE with xpath = " + xpath + " and reader = " + this.readers[id]);
				ComputeAverage comp = new ComputeAverage(xpath, fieldName, this.readers[id], this.timeout, this.timeUnit);
				String res = (String) comp.compute();
				System.out.println("RES: " + res);

				return res;
			}
			// Token
			else if (isToken(curNode.getNodeName()) == true) {
				System.out.println("TOKEN");
//				int id = -1;
//				String xpath = null;
//
//				// check is there is an id and xpath element children nodes
//				children = curNode.getChildNodes();
//				for (int i = 0; i < children.getLength(); i++) {
//					if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
//						if (isID(children.item(i).getNodeName())) {
//							if (id >= this.readers.length)
//								throw new Exception(
//										"Given id is greater than the reader array size.");
//							id = new Integer(children.item(i).getFirstChild()
//									.getNodeValue()).intValue();
//						}
//						if (isXPath(children.item(i).getNodeName()))
//							xpath = children.item(i).getFirstChild()
//									.getNodeValue();
//					}
//				}

				// check is there is an id and xpath element children nodes
				String res = (String) curNode.getFirstChild().getNodeValue();
				System.out.println("RES: " + res);
				return res;
			}

		} catch (Exception e) {
			logger.error("could not evaluate expression. throwing Exception", e);
			throw new Exception(e.getMessage());
		}

		throw new Exception("This Exception should NEVER be thrown.");
	}

	/**
	 * Get Document object from the XML string
	 * 
	 * @param XMLString  XML document in string format
	 * @throws Exception in case of error
	 */
	private void getDocFromString(String XMLString) throws Exception {
		// create the document builder factory object
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();

		// set it to namespace-aware and in validating mode
		dbfactory.setIgnoringElementContentWhitespace(true);
		// dbfactory.setValidating(true);

		// create the document builder - parser and set the current class as its
		// error handler
		DocumentBuilder db = null;
		try {
			db = dbfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ParserConfigurationException(
					"ProfileLoaderDOMImpl constructor caught ParserConfigurationException:\n"
							+ e.getMessage());
		}

		try {
			this.doc = db.parse(new InputSource(new StringReader(XMLString)));
		} catch (SAXException e) {
			throw new SAXException(
					"ProfileLoaderDOMImpl constructor caught SAXException:\n"
							+ e.getMessage());
		} catch (IOException e) {
			throw new IOException(
					"ProfileLoaderDOMImpl constructor caught IOException:\n"
							+ e.getMessage());
		}
	}

	/**
	 * Determines if token is '>'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isGreaterThan(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.GREATER_THAN) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '<'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isLowerThan(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.LOWER_THAN) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '='
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isGreaterEqual(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.GREATER_EQUAL) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '<='
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isLowerEqual(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.LOWER_EQUAL) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '>='
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isEqual(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.EQUAL) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '!='
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isNotEqual(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.NOT_EQUAL) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is max'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isMax(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.MAX) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is 'min'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isMin(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.MIN) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is 'size'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isSize(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.SIZE) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is 'av'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isAverage(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.AVERAGE) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is 'sum'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isSum(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.SUM) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is 'ID'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isID(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.ID) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is 'FIELD_NAME'
	 * @param tok token
	 * @return boolean
	 */
	private boolean isFieldName(String tok) {
		if(tok == null)
			return false;
		if(tok.compareTo(OperationSet.FIELD_NAME) == 0)
			return true;
		return false;
	}
	/**
	 * Determines if token is 'XPATH'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isXPath(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.XPATH) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '+'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isPlus(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.PLUS) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '-'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isMinus(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.MINUS) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '*'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isTimes(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.TIMES) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is '/'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isDiv(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.DIVIDED_BY) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is 'like'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isLike(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.LIKE) == 0)
			return true;
		return false;
	}

	/**
	 * Determines if token is 'token'
	 * 
	 * @param tok token
	 * @return boolean
	 */
	private boolean isToken(String tok) {
		if (tok == null)
			return false;
		if (tok.compareTo(OperationSet.TOKEN) == 0)
			return true;
		return false;
	}

}
