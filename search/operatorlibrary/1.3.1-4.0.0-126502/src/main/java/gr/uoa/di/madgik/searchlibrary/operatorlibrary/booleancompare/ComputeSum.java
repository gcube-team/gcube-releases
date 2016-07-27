package gr.uoa.di.madgik.searchlibrary.operatorlibrary.booleancompare;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author UoA
 */
public class ComputeSum implements Compute {
	/**
	 * Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ComputeSum.class.getName());

	/**
	 * XPATH that refers to the field being computed
	 */
	private String xpath;
	/**
	 * The name of the {@link Field} that contains the data to be processed
	 */
	private String fieldName;
	/**
	 * RS reader
	 */
	private IRecordReader<Record> reader;
	/**
	 * The timeout used by the {@link IRecordWriter} and the {@link IRecordReader}.
	 */
	private long timeout;
	/**
	 * The timeout unit used by the {@link IRecordWriter} and the {@link IRecordReader}.
	 */
	private TimeUnit timeUnit;
	
	/**
	 * Constructor
	 * 
	 * @param xpath refers to the field being processed, withing the {@link Field}
	 * @param fieldName The name of the {@field Field} that contains the data to be processed
	 * @param reader The reader used to read input
	 * @param timeout The timeout of the reader
	 * @param timeUnit The time unit of the timeout
	 */
	public ComputeSum(String xpath, String fieldName, IRecordReader<Record> reader, long timeout, TimeUnit timeUnit) {
		this.xpath = xpath;
		this.fieldName = fieldName;
		this.reader = reader;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	/**
	 * Implemented method
	 * 
	 * @see Compute#compute()
	 * @return result of computation
	 * @throws Exception
	 *             in case of error
	 */
	public Object compute() throws Exception {
		double SUM = 0;
		XPathExpression xpe = null;
		if(xpath != null) {
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xp = xpf.newXPath();
			xpe = xp.compile("//" + this.xpath + " | //@"+ this.xpath);
		}
		
		try {
			while(true) {
				
				Record rec = reader.get(timeout, timeUnit);
				if(rec == null) {
					if(reader.getStatus() == Status.Open) 
						logger.warn("Producer has timed out");
					break;
				}
				
				StringField field = (StringField)rec.getField(fieldName);
				
				if(xpath != null) {
				//	String XMLdoc = this.reader.executeQueryOnDocument("//"
				//			+ BooleanOperator.RSRecordName);
	
					// System.out.println("Raw Document: "+XMLdoc);
	
					// obtain a new DomunetBuilder factory
					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();
					factory.setNamespaceAware(false);
					factory.setIgnoringElementContentWhitespace(true);
	
					// create a new document builder object
					DocumentBuilder builder = factory.newDocumentBuilder();
	
					// parse the XML document found in the 'XML' string and create a
					// Document object
					Document document = builder.parse(new InputSource(
							new StringReader(field.getPayload())));
					
					// get the nodelist produced by the xpath selection
					NodeList myNodeList = (NodeList)xpe.evaluate(document.getDocumentElement(), XPathConstants.NODESET);
	
					// check if there is actually the Node
					if (myNodeList.getLength() > 0) {
						// check if the value of the node is a numeric
						if (isNumeric(myNodeList.item(0).getFirstChild()
								.getNodeValue()) == false)
							throw new Exception(
									"Node value is NOT numeric. Unable to calculate sum of non-numerics");
	
						// traverse through the nodes of the node list, get their
						// value
						// and select the maximum value
						//System.out.println("Size: " + myNodeList.getLength());
						for (int i = 0; i < myNodeList.getLength(); i++) {
							if (myNodeList.item(i).getChildNodes() == null
									|| myNodeList.item(i).getChildNodes()
											.getLength() == 0)
								continue;
							/*System.out.println("NodeType: "
									+ myNodeList.item(i).getFirstChild()
											.getNodeType());
							System.out.println("NodeValue: "
									+ myNodeList.item(i).getFirstChild()
											.getNodeValue());*/
	
							SUM += Double.parseDouble(myNodeList.item(i).getFirstChild().getNodeValue());
						}
					}
				}else {
					SUM += Double.parseDouble(field.getPayload());
				}
			}

		} catch (Exception e) {
			logger.error("could not complete compute", e);
			return null;
		}finally {
			try { reader.close(); } catch(Exception e) { }
		}

		// System.out.println("SUM: " + new Double(SUM).toString());
		return new Double(SUM).toString();
	}

	/**
	 * The isNumeric method checks if its string paramater can be translated
	 * into a number. If the string is consisted of digits and a single dot,
	 * then it is numeric; else it is not. algorithm complexity: 0(n), n: string
	 * length
	 * 
	 * @param alphanum
	 *            The string being checked
	 * @return True if the string parameter can be translated into a number;
	 *         otherwise False
	 */
	private static boolean isNumeric(String alphanum) {
		int i = 0;
		boolean dotFound = false;
		boolean isNumeric = true;

		for (i = 0; i < alphanum.length(); i++) {
			if (Character.isDigit(alphanum.charAt(i)))
				continue;
			if ((new Character(alphanum.charAt(i)))
					.compareTo(new Character('.')) == 0
					&& dotFound == false) {
				dotFound = true;
				continue;
			}
			isNumeric = false;
			break;
		}

		return isNumeric;
	}

}
