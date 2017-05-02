package org.gcube.common.searchservice.searchlibrary.rsreader;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementType;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.InitReaderThread;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.MakeLocalThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocationWrapper;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.ReaderInitParams;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.InitReaderThread.RSReaderEnum;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSFullWriter;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSWriterCreationParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Generic XML Reader class used to retrieve payload of a previously created
 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * hidding its location
 * 
 * @author UoA
 */
public class RSXMLReader extends RSReader {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSXMLReader.class);
	/**
	 * The underlying location independent element
	 */
	RSLocationWrapper rs = null;

	/**
	 * Creates a new {@link RSXMLReader} pointing to the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that the provided {@link RSLocator} identifies
	 * 
	 * @param locator
	 *            The {@link RSLocator} that identifies the
	 *            {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * @return The {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public static RSXMLReader getRSXMLReader(RSLocator locator)
			throws Exception {
		try {
			return new RSXMLReader(new RSLocationWrapper(locator));
		} catch (Exception e) {
			log.error("Could not create RSXMLReader Throwing Exception", e);
			throw new Exception("Could not create RSXMLReader");
		}
	}

	/**
	 * Instantiates and localizes or not the readers that point to the specified
	 * locator
	 * 
	 * @param params
	 *            the initialization parameters
	 * @return the created readers in the same order as their input. If some
	 *         reader could not be initialized null is placed
	 */
	public static RSXMLReader[] getRSXMLReader(ReaderInitParams[] params) {
		InitReaderThread[] ts = new InitReaderThread[params.length];
		RSXMLReader[] rs = new RSXMLReader[params.length];
		for (int i = 0; i < params.length; i += 1) {
			ts[i] = new InitReaderThread(params[i], RSReaderEnum.XMLReader);
			ts[i].start();
		}
		for (int i = 0; i < params.length; i += 1) {
			try {
				ts[i].join();
				rs[i] = (RSXMLReader) ts[i].getReader();
			} catch (Exception e) {
				log
						.error("interrupted whil waiting for reader. setting to null");
				rs[i] = null;
			}
		}
		return rs;
	}

	/**
	 * Creates a new {@link RSXMLReader} that operates on the prodiced wrapper
	 * hidding the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * location
	 * 
	 * @param rs
	 *            The {@link RSLocationWrapper} that hides the
	 *            {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 *            location
	 */
	protected RSXMLReader(RSLocationWrapper rs) {
		super(rs);
		this.rs = rs;
	}

	/**
	 * Creates a new {@link RSXMLIterator} over the underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * this {@link RSXMLReader} is initialized to point to
	 * 
	 * @see RSIterator
	 * 
	 * @return The {@link RSXMLIterator}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLIterator getRSIterator() throws Exception {
		try {
			return new RSXMLIterator(RSXMLReader.getRSXMLReader(this
					.getRSLocator()));
		} catch (Exception e) {
			log.error("could not create iterator. Throwing Exception", e);
			throw new Exception("could not create iterator");
		}
	}

	/**
	 * Creates a new {@link RSXMLIterator} over the underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * this {@link RSXMLReader} is initialized to point to
	 * 
	 * @see RSIterator
	 * 
	 * @param waittime
	 *            the time to wait at each has next
	 * @return The {@link RSXMLIterator}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLIterator getRSIterator(int waittime) throws Exception {
		try {
			return new RSXMLIterator(RSXMLReader.getRSXMLReader(this
					.getRSLocator()), waittime);
		} catch (Exception e) {
			log.error("could not create iterator. Throwing Exception", e);
			throw new Exception("could not create iterator");
		}
	}

	/**
	 * Retrieves the number of results available in the current payload part
	 * 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getNumberOfResults()
	 * 
	 * @return The number of results
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public int getNumberOfResults() throws Exception {
		try {
			return rs.getNumberOfResults(PropertyElementType.XML);
		} catch (Exception e) {
			log.error("Could not get number of results. Throwing Exception", e);
			throw new Exception("Could not get number of results");
		}
	}

	/**
	 * Retrieves the result with the specified index of the current payload part
	 * 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResult(int)
	 * 
	 * @param template
	 *            The {@link ResultElementBase} extending class type that should
	 *            be used to instantiate the returned result element
	 * @param index
	 *            The index of the result that must be retrieved
	 * @return The result element
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public ResultElementBase getResults(Class template, int index)
			throws Exception {
		try {
			ResultElementBase tmp = (ResultElementBase) template.newInstance();
			tmp.RS_fromXML(rs.getResults(index));
			return tmp;
		} catch (Exception e) {
			log.error("Could not get result. Throwing Exception", e);
			throw new Exception("Could not get result");
		}
	}

	/**
	 * Retrieves all the results of the current payload part
	 * 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResults()
	 * 
	 * @param template
	 *            The {@link ResultElementBase} extending class type that should
	 *            be used to instantiate the returned result elements
	 * @return The retrieved result elements
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public ResultElementBase[] getResults(Class template) throws Exception {
		try {
			String[] res = rs.getResults();
			if (res == null || res.length == 0) {
				return new ResultElementBase[0];
			}
			ResultElementBase[] ret = new ResultElementBase[res.length];
			for (int i = 0; i < res.length; i += 1) {
				try {
					ResultElementBase tmp = (ResultElementBase) template
							.newInstance();
					tmp.RS_fromXML(res[i]);
					ret[i] = tmp;
				} catch (Exception e) {
					log
							.error(
									"Error while creating result element. Putting null and continuing",
									e);
					ret[i] = null;
				}
			}
			return ret;
		} catch (Exception e) {
			log.error("Could not get results. Throwing Exception", e);
			throw new Exception("Could not get results");
		}
	}

	/**
	 * Retrieves all the results of the current payload part whose index falls
	 * in the specified area
	 * 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResults(int,
	 *      int)
	 * 
	 * @param template
	 *            The {@link ResultElementBase} extending class type that should
	 *            be used to instantiate the returned result elements
	 * @param from
	 *            The starting index
	 * @param to
	 *            The end index
	 * @return The retrieved result elements
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public ResultElementBase[] getResults(Class template, int from, int to)
			throws Exception {
		try {
			String[] res = rs.getResults(from, to);
			if (res == null || res.length == 0) {
				return new ResultElementBase[0];
			}
			ResultElementBase[] ret = new ResultElementBase[res.length];
			for (int i = 0; i < res.length; i += 1) {
				try {
					ResultElementBase tmp = (ResultElementBase) template
							.newInstance();
					tmp.RS_fromXML(res[i]);
					ret[i] = tmp;
				} catch (Exception e) {
					log
							.error(
									"Error while creating result element. Putting null and continuing",
									e);
					ret[i] = null;
				}
			}
			return ret;
		} catch (Exception e) {
			log.error("Could not get results. Throwing Exception", e);
			throw new Exception("Could not get results");
		}
	}

	/**
	 * Performs the provided xPath expression on the document of the current
	 * part. The output of the ervaluation is returned in a string serialization
	 * 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnDocument(java.lang.String)
	 * 
	 * @param xPath
	 *            The xPath to be evaluated
	 * @return The serialization of the evaluation output
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public String executeQueryOnDocument(String xPath) throws Exception {
		try {
			return rs.executeQueryOnDocument(xPath);
		} catch (Exception e) {
			log.error("Could not execute query. Throwing Exception", e);
			throw new Exception("Could not execute query");
		}
	}

	/**
	 * Performs the provided xPath expression on the results of the current
	 * payload part. if the evaluation returns output then this result is
	 * incuded in the returned value
	 * 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnResults(java.lang.String)
	 * 
	 * @param template
	 *            The {@link ResultElementBase} extending class type that should
	 *            be used to instantiate the returned result elements
	 * @param xPath
	 *            The xPath that should be evaluated. The expression must start
	 *            from the current node
	 * @return The matching result elements
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public ResultElementBase[] filter(Class template, String xPath)
			throws Exception {
		try {
			String[] res = rs.executeQueryOnResults(xPath);
			if (res == null || res.length == 0) {
				return new ResultElementBase[0];
			}
			ResultElementBase[] ret = new ResultElementBase[res.length];
			for (int i = 0; i < res.length; i += 1) {
				try {
					ResultElementBase tmp = (ResultElementBase) template
							.newInstance();
					tmp.RS_fromXML(res[i]);
					ret[i] = tmp;
				} catch (Exception e) {
					log
							.error(
									"Error while creating result element. Putting null and continuing",
									e);
					ret[i] = null;
				}
			}
			return ret;
		} catch (Exception e) {
			log.error("Could not execute query. Throwing Exception", e);
			throw new Exception("Could not execute query");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} whose content is the result of a
	 * filtering operation on the results the current
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * not preserving nessecarily the records per part the current
	 * {@link ResultSet} has. The current {@link RSXMLReader} is not altered.
	 * The operation is non blocking. The {@link RSXMLReader} will be created
	 * and returned and on the background the top operation will continue. The
	 * computation will take part on the service side that holds the referenced
	 * by this {@link RSXMLReader} {@link ResultSet}. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that will be created and pointed to by the created
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will be located to the same host as the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * the current {@link RSXMLReader} point to and will have the same access
	 * type. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will have the same properties as the existing one.
	 * 
	 * @see ResultSet#filterRS(java.lang.String)
	 * @see RSReader#wrap(RSResourceType)
	 * 
	 * @param xPath
	 *            the xpath to base teh filtering on
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader filter(String xPath) throws Exception {
		try {
			String headName = this.rs.filterRS(xPath);
			RSLocator newLocator = null;
			if (this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator = new RSLocator(new RSResourceLocalType(), headName);
			} else {
				newLocator = this.rs.wrap(new RSResourceWSRFType(), headName,
						this.getRSLocator().getURI().toString());
			}
			return RSXMLReader.getRSXMLReader(newLocator);
		} catch (Exception e) {
			log.error("Could not filter. Throwing Exception", e);
			throw new Exception("Could not filter");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} whose content is the result of a
	 * filtering operation on the results the current
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * not preserving nessecarily the records per part the current
	 * {@link ResultSet} has. The current {@link RSXMLReader} is not altered.
	 * The operation is non blocking. The {@link RSXMLReader} will be created
	 * and returned and on the background the top operation will continue. The
	 * computation will take part on the service side that holds the referenced
	 * by this {@link RSXMLReader} {@link ResultSet}. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that will be created and pointed to by the created
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will be located to the same host as the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * the current {@link RSXMLReader} point to and will have the same access
	 * type. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will have will have the properties that are provided.
	 * 
	 * @see ResultSet#filterRS(java.lang.String)
	 * @see RSReader#wrap(RSResourceType)
	 * 
	 * @param xPath
	 *            the xpath to base teh filtering on
	 * @param properties
	 *            The properties that should be added to the new
	 *            {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 *            head part
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader filter(String xPath, PropertyElementBase[] properties)
			throws Exception {
		try {
			if (properties == null || properties.length == 0) {
				log
						.error("Cannot initialize Result Set with empty property list. Throwing Exception");
				throw new Exception(
						"Cannot initialize Result Set with empty property list");
			}
			String[] props = new String[properties.length];
			for (int i = 0; i < properties.length; i += 1)
				props[i] = properties[i].RS_toXML();
			String headName = this.rs.filterRS(xPath, props);
			RSLocator newLocator = null;
			if (this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator = new RSLocator(new RSResourceLocalType(), headName);
			} else {
				newLocator = this.rs.wrap(new RSResourceWSRFType(), headName,
						this.getRSLocator().getURI().toString());
			}
			return RSXMLReader.getRSXMLReader(newLocator);
		} catch (Exception e) {
			log.error("Could not filter. Throwing Exception", e);
			throw new Exception("Could not filter");
		}
	}

	/**
	 * Performs the provided xslt transformation on the current part and
	 * retrieves the output of this transformation
	 * 
	 * @see ResultSet#transformByXSLT(java.lang.String)
	 * 
	 * @param transformation
	 *            The xslt transformation to be performed
	 * @return The output of the transformation
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public String project(String transformation) throws Exception {
		try {
			return this.rs.transformByXSLT(transformation);
		} catch (Exception e) {
			log
					.error(
							"Could not perform transform and retrieve. Throwing Exception",
							e);
			throw new Exception(
					"Could not perform transform and retrieve. Throwing Exception");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} whose content is the output of the
	 * provided transformation on every content part of the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * this {@link RSXMLReader} is pointing to. The current {@link RSXMLReader}
	 * is not altered. The operation is non blocking. The {@link RSXMLReader}
	 * will be created and returned and on the background the transformation
	 * will continue. The computation will take part on the service side that
	 * holds the referenced by this {@link RSXMLReader} {@link ResultSet}. The
	 * new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that will be created and pointed to by the created
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will be located to the same host as the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * the current {@link RSXMLReader} point to and will have the same access
	 * type. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will have the same properties as the existing one.
	 * 
	 * @see ResultSet#transformRS(java.lang.String)
	 * @see ResultSet#wrapFile(java.lang.String)
	 * 
	 * @param transformation
	 *            The transformation to be performed
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader transform(String transformation) throws Exception {
		try {
			String headName = this.rs.transformRS(transformation);
			RSLocator newLocator = null;
			if (this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator = new RSLocator(new RSResourceLocalType(), headName);
			} else {
				newLocator = this.rs.wrap(new RSResourceWSRFType(), headName,
						this.getRSLocator().getURI().toString());
			}
			return RSXMLReader.getRSXMLReader(newLocator);
		} catch (Exception e) {
			log.error("Could not trasnform. Throwing Exception", e);
			throw new Exception("Could not trasnform");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} whose content is the output of the
	 * provided transformation on every content part of the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * this {@link RSXMLReader} is pointing to. The current {@link RSXMLReader}
	 * is not altered. The operation is non blocking. The {@link RSXMLReader}
	 * will be created and returned and on the background the transformation
	 * will continue. The computation will take part on the service side that
	 * holds the referenced by this {@link RSXMLReader} {@link ResultSet}. The
	 * new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that will be created and pointed to by the created
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will be located to the same host as the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * the current {@link RSXMLReader} point to and will have the same access
	 * type. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will have the provided properties
	 * 
	 * @see ResultSet#transformRS(java.lang.String)
	 * @see ResultSet#wrapFile(java.lang.String)
	 * 
	 * @param transformation
	 *            The transformation to be performed
	 * @param properties
	 *            The properties that should be added to the new
	 *            {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 *            head part
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader transformByXSLT(String transformation,
			PropertyElementBase[] properties) throws Exception {
		try {
			if (properties == null || properties.length == 0) {
				log
						.error("Cannot initialize Result Set with empty property list. Throwing Exception");
				throw new Exception(
						"Cannot initialize Result Set with empty property list");
			}
			String[] props = new String[properties.length];
			for (int i = 0; i < properties.length; i += 1)
				props[i] = properties[i].RS_toXML();
			String headName = this.rs.transformRS(transformation, props);
			RSLocator newLocator = null;
			if (this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator = new RSLocator(new RSResourceLocalType(), headName);
			} else {
				newLocator = this.rs.wrap(new RSResourceWSRFType(), headName,
						this.getRSLocator().getURI().toString());
			}
			return RSXMLReader.getRSXMLReader(newLocator);
		} catch (Exception e) {
			log.error("Could not trasnform. Throwing Exception", e);
			throw new Exception("Could not trasnform");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} that points to a
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSReader} and which is an exact
	 * mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * element. Depending on the provided {@link RSResourceType} the created
	 * {@link RSXMLReader} will point to the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * either directly as a java element or through a web service front end.
	 * Thius operation is non blocking. The created {@link RSXMLReader} is
	 * returned while on the background the localization is still on going if
	 * nessecary
	 * 
	 * @see RSReader#isLocal()
	 * @see RSReader#wrap(RSResourceType)
	 * 
	 * @param type
	 *            The type of the Resource to be created
	 * @param waittime
	 *            time in milliseconds to use while waiting to localize a remote
	 *            RS. This will not be used if the localized RS is local
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader makeLocalPatiently(RSResourceType type, int waittime)
			throws Exception {
		try {
			if (isLocal() && !(rs.isForward()) && !(rs.hasAccessLeasing())
					&& !(rs.hasTimeLeasing()) && !(rs.isSecure())
					&& !(rs.isScoped())) {
				if (rs.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
					return RSXMLReader.getRSXMLReader(rs.getRSLocator());
				} else if (rs.getRSLocator().getRSResourceType() instanceof RSResourceWSRFType) {
					return RSXMLReader.getRSXMLReader(new RSLocator(
							new RSResourceLocalType(), rs.getHeadFileName()));
				} else {
					log
							.error("RSResource type not recognized. Throwing Exception");
					throw new Exception("RSResource type not recognized");
				}
			} else {
				RSReader thisReader = RSReader.getRSReader(getRSLocator());
				String properties = thisReader.retrieveCustomProperties();
				RSFullWriter writer = RSFullWriter.getRSFullWriter(properties);
				if (!(type instanceof RSResourceLocalType)
						&& !(type instanceof RSResourceWSRFType)) {
					log
							.error("not regognized resource type.Throwing Exception");
					throw new Exception(
							"not regognized resource type.Throwing Exception");
				}
				MakeLocalThreadGeneric worker = new MakeLocalThreadGeneric(
						writer, thisReader, MakeLocalThreadGeneric.CLEAR,
						waittime, rs.getStaticPort(), rs.getSSLsupport());
				worker.start();
				return RSXMLReader.getRSXMLReader(writer.getRSLocator(type));
			}
		} catch (Exception e) {
			log.error("Could not make local. Throwing Exception", e);
			throw new Exception("Could not make local");
		}
	}

	/**
	 * Return an XML document in a String
	 * 
	 * @param doc
	 *           the DOM document
	 * @return the XML doc in a String
	 */
	public String getStringFromDocument(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty("omit-xml-declaration", "yes");
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} that points to a
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSReader} and which is an exact
	 * mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * element. Depending on the provided {@link RSResourceType} the created
	 * {@link RSXMLReader} will point to the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * either directly as a java element or through a web service front end.
	 * Thius operation is non blocking. The created {@link RSXMLReader} is
	 * returned while on the background the localization is still on going if
	 * nessecary
	 * 
	 * @see RSReader#isLocal()
	 * @see RSReader#wrap(RSResourceType)
	 * 
	 * @param type
	 *            The type of the Resource to be created
	 * @param waittime
	 *            time in milliseconds to use while waiting to localize a remote
	 *            RS. This will not be used if the localized RS is local
	 * @param properties
	 *            array of extra properties
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader makeLocalPatiently(RSResourceType type, int waittime,
			PropertyElementBase[] properties) throws Exception {
		try {
			if (isLocal() && !(rs.isForward()) && !(rs.hasAccessLeasing())
					&& !(rs.hasTimeLeasing()) && !(rs.isSecure())
					&& !(rs.isScoped())) {
				if (rs.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
					return RSXMLReader.getRSXMLReader(rs.getRSLocator());
				} else if (rs.getRSLocator().getRSResourceType() instanceof RSResourceWSRFType) {
					return RSXMLReader.getRSXMLReader(new RSLocator(
							new RSResourceLocalType(), rs.getHeadFileName()));
				} else {
					log
							.error("RSResource type not recognized. Throwing Exception");
					throw new Exception("RSResource type not recognized");
				}
			} else {
				RSReader thisReader = RSReader.getRSReader(getRSLocator());
				String customProperties = thisReader.retrieveCustomProperties();
				String allProperties = null;
				try {
					DocumentBuilderFactory datadbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder datadb = datadbf.newDocumentBuilder();
					Document datadom = datadb.parse(new InputSource(
							new StringReader(customProperties)));

					Element imp = datadom.getDocumentElement();
					for (int i = 0; i < properties.length; i += 1) {
						log.trace("Extra property: " + properties[i].RS_toXML());
						try {
							Document datadom2 = datadb
									.parse(new InputSource(new StringReader(
											properties[i].RS_toXML())));
							Element imp2 = datadom2.getDocumentElement();
							Node dup = datadom.importNode(imp2, true);
							imp.appendChild(dup);							
						} catch (Exception e) {
							log.error("could not create custom property element",e);
						}
					}
					
					log.info("Final Dom object " + getStringFromDocument(datadom));
					allProperties = getStringFromDocument(datadom);
				} catch (Exception e) {
					log.error("Could not parse properties. Thorwing Exception",	e);
					throw new Exception("Could not parse properties");
				}
				
				log.info("Custom properties: " + customProperties);

				RSFullWriter writer = RSFullWriter.getRSFullWriter(allProperties);
				if (!(type instanceof RSResourceLocalType)
						&& !(type instanceof RSResourceWSRFType)) {
					log
							.error("not regognized resource type.Throwing Exception");
					throw new Exception(
							"not regognized resource type.Throwing Exception");
				}
				MakeLocalThreadGeneric worker = new MakeLocalThreadGeneric(
						writer, thisReader, MakeLocalThreadGeneric.CLEAR,
						waittime, rs.getStaticPort(), rs.getSSLsupport());
				worker.start();
				return RSXMLReader.getRSXMLReader(writer.getRSLocator(type));
			}
		} catch (Exception e) {
			log.error("Could not make local. Throwing Exception", e);
			throw new Exception("Could not make local");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} that points to a
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSReader} and which is an exact
	 * mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * element. Depending on the provided {@link RSResourceType} the created
	 * {@link RSXMLReader} will point to the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * either directly as a java element or through a web service front end.
	 * Thius operation is non blocking. The created {@link RSXMLReader} is
	 * returned while on the background the localization is still on going if
	 * nessecary
	 * 
	 * @see RSReader#isLocal()
	 * @see RSReader#wrap(RSResourceType)
	 * 
	 * @param type
	 *            The type of the Resource to be created
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader makeLocal(RSResourceType type) throws Exception {
		try {
			if (isLocal() && !(rs.isForward()) && !(rs.hasAccessLeasing())
					&& !(rs.hasTimeLeasing()) && !(rs.isSecure())
					&& !(rs.isScoped())) {
				if (rs.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
					return RSXMLReader.getRSXMLReader(rs.getRSLocator());
				} else if (rs.getRSLocator().getRSResourceType() instanceof RSResourceWSRFType) {
					RSLocator l = new RSLocator(new RSResourceLocalType(), rs
							.getHeadFileName());
					return RSXMLReader.getRSXMLReader(l);
				} else {
					log
							.error("RSResource type not recognized. Throwing Exception");
					throw new Exception("RSResource type not recognized");
				}
			} else {
				RSReader thisReader = RSReader.getRSReader(getRSLocator());
				String properties = thisReader.retrieveCustomProperties();
				RSFullWriter writer = RSFullWriter.getRSFullWriter(properties);
				if (!(type instanceof RSResourceLocalType)
						&& !(type instanceof RSResourceWSRFType)) {
					log
							.error("not regognized resource type.Throwing Exception");
					throw new Exception(
							"not regognized resource type.Throwing Exception");
				}
				MakeLocalThreadGeneric worker = new MakeLocalThreadGeneric(
						writer, thisReader, MakeLocalThreadGeneric.CLEAR, rs
								.getStaticPort(), rs.getSSLsupport());
				worker.start();
				return RSXMLReader.getRSXMLReader(writer.getRSLocator(type));
			}
		} catch (Exception e) {
			log.error("Could not make local. Throwing Exception", e);
			throw new Exception("Could not make local");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} that points to a
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSReader} and which is an exact
	 * mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * element. Depending on the provided {@link RSResourceType} the created
	 * {@link RSXMLReader} will point to the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * either directly as a java element or through a web service front end.
	 * Thius operation is non blocking. The created {@link RSXMLReader} is
	 * returned while on the background the localization is still on going if
	 * nessecary
	 * 
	 * @see RSReader#isLocal()
	 * @see RSReader#wrap(RSResourceType)
	 * 
	 * @param type
	 *            The type of the Resource to be created
	 * @param params
	 *            RS creation parameters
	 * @param scope
	 *            The scope of the RS
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader makeLocal(RSResourceType type,
			RSWriterCreationParams params, GCUBEScope scope) throws Exception {
		try {
			RSReader thisReader = RSReader.getRSReader(getRSLocator());
			// String properties=thisReader.retrieveCustomProperties();
			RSFullWriter writer = RSFullWriter.getRSFullWriter(params);
			if (!(type instanceof RSResourceLocalType)
					&& !(type instanceof RSResourceWSRFType)) {
				log.error("not regognized resource type.Throwing Exception");
				throw new Exception(
						"not regognized resource type.Throwing Exception");
			}
			MakeLocalThreadGeneric worker = new MakeLocalThreadGeneric(writer,
					thisReader, MakeLocalThreadGeneric.CLEAR, rs
							.getStaticPort(), rs.getSSLsupport());
			worker.start();
			RSLocator newlocator = writer.getRSLocator(type, scope, params
					.getPrivKey());
			newlocator.setPrivKey(params.getPrivKey());
			newlocator.setScope(scope);
			return RSXMLReader.getRSXMLReader(newlocator);
		} catch (Exception e) {
			log.error("Could not make local. Throwing Exception", e);
			throw new Exception("Could not make local");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} that points to a
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSXMLReader} and which is an exact
	 * mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * element up until the provided number of results is mirrored. Depending on
	 * the provided {@link RSResourceType} the created {@link RSXMLReader} will
	 * point to the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * either directly as a java element or through a web service front end.
	 * Thius operation is non blocking. The created {@link RSXMLReader} is
	 * returned while on the background the localization is still on going if
	 * nessecary
	 * 
	 * @see RSReader#isLocal()
	 * @see RSReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERRECORD
	 * 
	 * @param type
	 *            The type of the Resource to be created
	 * @param count
	 *            The number of results to localize
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader makeLocal(RSResourceType type, int count)
			throws Exception {
		if (count < 0) {
			log.error("invalid topCount argument. Throwing Exception");
			throw new Exception("invalid topCount argument");
		}
		try {
			return keepTop(count).makeLocal(type);
		} catch (Exception e) {
			log
					.error(
							"could not perform partial localization. Throwing Exception",
							e);
			throw new Exception("could not perform partial localization");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} whose content is the top count of the
	 * results the current
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * holds preserving the records per part the current
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * has. The current {@link RSXMLReader} is not altered. The operation is non
	 * blocking. The {@link RSXMLReader} will be created and returned and on the
	 * background the top operation will continue. The computation will take
	 * part on the service side that holds the referenced by this
	 * {@link RSXMLReader} {@link ResultSet}. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that will be created and pointed to by the created
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will be located to the same host as the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * the current {@link RSXMLReader} point to and will have the same access
	 * type. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will have will have the properties that are provided.
	 * 
	 * @see RSReader#keepTop(int, PropertyElementBase[])
	 * @see KeepTopThreadGeneric#PERRECORD
	 * 
	 * @param count
	 *            The top count that should be kept
	 * @param properties
	 *            The properties that should be added to the new
	 *            {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 *            head part
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader keepTop(int count, PropertyElementBase[] properties)
			throws Exception {
		try {
			if (properties == null || properties.length == 0) {
				log
						.error("Cannot initialize Result Set with empty property list. Throwing Exception");
				throw new Exception(
						"Cannot initialize Result Set with empty property list");
			}
			String[] props = new String[properties.length];
			for (int i = 0; i < properties.length; i += 1)
				props[i] = properties[i].RS_toXML();
			String headName = this.rs.keepTop(count, props,
					KeepTopThreadGeneric.PERRECORD);
			RSLocator newLocator = null;
			if (this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator = new RSLocator(new RSResourceLocalType(), headName);
			} else {
				newLocator = this.rs.wrap(new RSResourceWSRFType(), headName,
						this.getRSLocator().getURI().toString());
			}
			return RSXMLReader.getRSXMLReader(newLocator);
		} catch (Exception e) {
			log.error("Could not keep top. Throwing Exception", e);
			throw new Exception("Could not keep top");
		}
	}

	/**
	 * Creates a new {@link RSXMLReader} whose content is the top count of the
	 * results the current
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * holds preserving the records per part the current
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * has. The current {@link RSXMLReader} is not altered. The operation is non
	 * blocking. The {@link RSXMLReader} will be created and returned and on the
	 * background the top operation will continue. The computation will take
	 * part on the service side that holds the referenced by this
	 * {@link RSXMLReader} {@link ResultSet}. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that will be created and pointed to by the created
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will be located to the same host as the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * the current {@link RSXMLReader} point to and will have the same access
	 * type. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will have will have the properties that are provided.
	 * 
	 * @see RSReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERRECORD
	 * 
	 * @param count
	 *            The top count that should be kept
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader keepTop(int count) throws Exception {
		try {
			String headName = this.rs.keepTop(count,
					KeepTopThreadGeneric.PERRECORD);
			RSLocator newLocator = null;
			if (this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator = new RSLocator(new RSResourceLocalType(), headName);
			} else {
				newLocator = this.rs.wrap(new RSResourceWSRFType(), headName,
						this.getRSLocator().getURI().toString());
			}
			return RSXMLReader.getRSXMLReader(newLocator);
		} catch (Exception e) {
			log.error("Could not keep top. Throwing Exception", e);
			throw new Exception("Could not keep top");
		}
	}

	/**
	 * Creates a new {@link ResultSet} that is a exect copy of the current one.
	 * The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that will be created and pointed to by the created {@link RSXMLReader}
	 * will be located to the same host as the
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * the current {@link RSXMLReader} point to and will have the same access
	 * type. The new
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will have will have the properties that the current one has
	 * 
	 * @return The created {@link RSXMLReader}
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public RSXMLReader cloneRS() throws Exception {
		try {
			return RSXMLReader.getRSXMLReader(super.cloneRS().getRSLocator());
		} catch (Exception e) {
			log.error("Could not clone rs. Throwing Exception", e);
			throw new Exception("Could not clone rs");
		}
	}

	/**
	 * Retrieves the full payload of the current content part
	 * 
	 * @return The full payload of the current content part
	 * @throws Exception
	 *             An unrecoverable for the operation error occured
	 */
	public String getFullPayload() throws Exception {
		try {
			return this.rs.getCurrentContentPartPayload();
		} catch (Exception e) {
			log.error("Could not get payload. Throwing Exception", e);
			throw new Exception("Could not get payload");
		}
	}
}
