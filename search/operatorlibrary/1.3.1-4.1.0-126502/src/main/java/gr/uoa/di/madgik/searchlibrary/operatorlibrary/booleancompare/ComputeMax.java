package gr.uoa.di.madgik.searchlibrary.operatorlibrary.booleancompare;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator.CompareTokens;

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
public class ComputeMax implements Compute {
	/**
	 * Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ComputeMax.class.getName());

	/**
	 * XPATH refering to the field being processed
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
	public ComputeMax(String xpath, String fieldName, IRecordReader<Record> reader, long timeout, TimeUnit timeUnit) {
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
	 */
	public Object compute() throws Exception {
		String MAX = Integer.toString(Integer.MIN_VALUE);
		XPathExpression xpe = null;
		if(xpath != null) {
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xp = xpf.newXPath();
			xpe = xp.compile("//" + this.xpath + " | //@"+ this.xpath);
		}
		
		try {
			boolean firstRecord = true;
			while(true) {
				
				Record rec = reader.get(timeout, timeUnit);
				if(rec == null) {
					if(reader.getStatus() == Status.Open) 
						logger.warn("Producer has timed out");
					break;
				}
				
			//	String XMLdoc = this.reader.executeQueryOnDocument("//"
			//			+ BooleanOperator.RSRecordName);

				// XPathResolver xxx = new XPathResolver(XMLdoc, "//" + xpath);
				// String res = xxx.doXPath();
			//	System.out.println("XML: " + XMLdoc);

				// parse the XML document found in the 'XML' string and create a
				// Document object
				StringField field = (StringField)rec.getField(fieldName);
	
				if(xpath != null) {
					// obtain a new DocumentBuilder factory
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setNamespaceAware(false);
					factory.setIgnoringElementContentWhitespace(true);

					// create a new document builder object
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(
							new StringReader(field.getPayload())));
					
					// get the nodelist produced by the xpath selection
					NodeList myNodeList = (NodeList)xpe.evaluate(document.getDocumentElement(), XPathConstants.NODESET);
	
					if (myNodeList.getLength() > 0) {
						// traverse through the nodes of the node list, get their
						// value
						// and select the maximum value
						System.out.println("Size: " + myNodeList.getLength());
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
	
							if (firstRecord) {
								MAX = myNodeList.item(i).getFirstChild().getNodeValue();
								firstRecord = false;
							} else {
								String cand = myNodeList.item(i).getFirstChild().getNodeValue();
							/*	System.out.println("MAX: " + MAX + "\ncand: "
										+ cand);*/
	
								int res = CompareTokens.compare(MAX, cand);
								if (res < 0)
									MAX = cand;
							}
						}
					}
				} else {
					if(firstRecord) {
						MAX = field.getPayload();
						firstRecord = false;
					}else {
						if(CompareTokens.compare(MAX, field.getPayload()) < 0)
							MAX = field.getPayload();
					}
				}
			}

		} catch (Exception e) {
			logger.error("could not complete compute",e);
			return null;
		}finally {
			try { reader.close(); } catch(Exception e) { }
		}
		return MAX;
	}
}
