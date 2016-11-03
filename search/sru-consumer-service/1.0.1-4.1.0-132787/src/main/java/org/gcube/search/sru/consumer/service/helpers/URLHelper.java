package org.gcube.search.sru.consumer.service.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource.DescriptionDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;

public class URLHelper {

	static final Logger logger = LoggerFactory.getLogger(URLHelper.class);
	
	public static String urlToString(String urlString) throws IOException, URISyntaxException{
//		URL url = Resources.getResource(urlString);
//		String text = Resources.toString(url, Charsets.UTF_8);
		
		String text = IOUtils.toString(new URI(urlString), "UTF-8");
		
//		String text = xml;
		
		return text;
	}
	
	
	public static String constructExplainURLString(DescriptionDocument descriptionDocument) throws URISyntaxException {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("operation=explain");
		if (descriptionDocument.getVersion() != null)
			strBuf.append("&version=" + descriptionDocument.getVersion());

		String url = strBuf.toString();
		
		URI uri = new URI(
				descriptionDocument.getSchema(),
				null,
				descriptionDocument.getHost(),
				descriptionDocument.getPort(),
				"/" + descriptionDocument.getServlet(),
				//URLEncoder.encode(strBuf.toString()),
				url,
				null
				);
		return uri.toString();

	}
	
	public static void main(String[] args) {
		DescriptionDocument descriptionDocument = new DescriptionDocument();
		descriptionDocument.setHost("localhost");
		descriptionDocument.setSchema("http");
		descriptionDocument.setServlet("servlet");
		descriptionDocument.setPort(8080);
		
		String url = new SearchRetrieveRequest()
			.query("query1")
			.version("1.1")
			.recordPacking("xml")
			.recordSchema("xml")
			.descriptionDocument(descriptionDocument)
			.build();
		logger.debug(url);
		
	}
	
	
	public static class SearchRetrieveRequest {
		private String version;
		private String query;
		private String recordPacking;
		private Long maximumRecords;
		private String recordSchema;
		private DescriptionDocument descriptionDocument = new DescriptionDocument();
		private String operation = "searchRetrieve";
		
		public SearchRetrieveRequest(){
			
		}
		
		public SearchRetrieveRequest version(String version){
			this.version = version;
			return this;
		}
		
		public SearchRetrieveRequest recordPacking(String recordPacking){
			this.recordPacking = recordPacking;
			return this;
		}
		
		public SearchRetrieveRequest query(String query){
			this.query = query;
			return this;
		}
		
		public SearchRetrieveRequest maximumRecords(Long maximumRecords){
			this.maximumRecords = maximumRecords;
			return this;
		}
		
		public SearchRetrieveRequest recordSchema(String recordSchema){
			this.recordSchema = recordSchema;
			return this;
		}
		
		public SearchRetrieveRequest descriptionDocument(DescriptionDocument descriptionDocument){
			this.descriptionDocument = descriptionDocument;
			return this;
		}
		
		public String build(){
			return this.constructSearchURLString();
		}
		
		
		
		String constructSearchURLString(){
			Map<String, Object> params = Maps.newHashMap();
			
			params.put("operation", this.operation);
			
			putIfPresent(params, "version", this.version, descriptionDocument.getVersion());
			putIfPresent(params, "maximumRecords", this.maximumRecords, descriptionDocument.getMaxRecords());
			putIfPresent(params, "recordSchema", this.recordSchema, descriptionDocument.getDefaultRecordSchema());
			
			try {
				if (this.query != null){
					String queryEnc = URLEncoder
							.encode(this.query, "UTF-8")
							.replaceAll("=", "%3D");
					params.put("query", queryEnc);
				}
			} catch (Exception e) {
				logger.debug("error while converting query");
			}
			
			if (this.recordPacking != null)
				params.put("recordPacking", this.recordPacking);
			
			logger.debug("params have : " + params);
			
			String urlPath = descriptionDocument.getSchema() + "://" + descriptionDocument.getHost() + ":" + descriptionDocument.getPort() + "/" + descriptionDocument.getServlet();
			
			String paramsString = Joiner.on("&").withKeyValueSeparator("=").join(params);
			
			String url = urlPath + "?" + paramsString;
			
			logger.debug(paramsString);
			
			return url;
		}
		
		static void putIfPresent(Map<String, Object> map, String key, Object first, Object second){
			try {
				Object value = Objects.firstNonNull(first, second);
				map.put(key, value);
			} catch (Exception e) {
				
			}
		}
	}
	
	
	public static class ExplainRequest {
		private String version;
		private String recordPacking;
		private DescriptionDocument descriptionDocument = new DescriptionDocument();
		private String operation = "explain";
		
		public ExplainRequest(){
			
		}
		
		public ExplainRequest version(String version){
			this.version = version;
			return this;
		}
		
		public ExplainRequest recordPacking(String recordPacking){
			this.recordPacking = recordPacking;
			return this;
		}
		
		public ExplainRequest descriptionDocument(DescriptionDocument descriptionDocument){
			this.descriptionDocument = descriptionDocument;
			return this;
		}
		
		public String build(){
			return this.constructExplainURLString();
		}
		
		
		
		String constructExplainURLString(){
			Map<String, Object> params = Maps.newHashMap();
			
			params.put("operation", this.operation);
			
			putIfPresent(params, "version", this.version, descriptionDocument.getVersion());
			if (this.recordPacking != null)
				params.put("recordPacking", this.recordPacking);
			
			logger.info("params have : " + params);
			
			String urlPath = descriptionDocument.getSchema() + "://" + descriptionDocument.getHost() + ":" + descriptionDocument.getPort() + "/" + descriptionDocument.getServlet();
			
			String paramsString = Joiner.on("&").withKeyValueSeparator("=").join(params);
			
			String url = urlPath + "?" + paramsString;
			
			logger.info("paramstring : " + paramsString);
			
			return url;
		}
		
		static void putIfPresent(Map<String, Object> map, String key, Object first, Object second){
			try {
				Object value = Objects.firstNonNull(first, second);
				map.put(key, value);
			} catch (Exception e) {
				
			}
		}
	}
	
	
	
	
	public static String constructSearchURLString(DescriptionDocument descriptionDocument, String version, String recordPacking, String query, Long maximumRecords, String recordSchema) throws URISyntaxException, UnsupportedEncodingException {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("operation=searchRetrieve");
		
		if (version != null)
			strBuf.append("&version=" + version);
		else if (descriptionDocument.getVersion() != null)
			strBuf.append("&version=" + descriptionDocument.getVersion());

		if (query != null){
			query = URLEncoder.encode(query, "UTF-8");
			query = query.replaceAll("=", "%3D");
			
			strBuf.append("&query=" + query);
		}
		
		if (maximumRecords != null)
			strBuf.append("&maximumRecords=" + maximumRecords);
		else if (descriptionDocument.getMaxRecords() != null)
			strBuf.append("&maximumRecords=" + descriptionDocument.getMaxRecords());
		
		
		if (recordPacking != null)
			strBuf.append("&recordPacking=" + recordPacking);
		

		if (recordSchema != null)
			strBuf.append("&recordSchema=" + recordSchema);
		else if (descriptionDocument.getDefaultRecordSchema() != null)
		strBuf.append("&recordSchema=" + descriptionDocument.getDefaultRecordSchema());

		logger.info("uri : " + strBuf);
		
		
		URI uri = new URI(
				descriptionDocument.getSchema(),
				null,
				descriptionDocument.getHost(),
				descriptionDocument.getPort(),
				"/" + descriptionDocument.getServlet(),
				//URLEncoder.encode(strBuf.toString()),
				strBuf.toString(),
				null
				);
		return uri.toString();

	}
	
	static String xml = 
			//"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			" <srw:searchRetrieveResponse xmlns:dc=\"http://pulr.org/dc/elements/1.1/\" xmlns:dtl-xsl=\"http://schema.highwire.org/DTL/XSLT/Extern\" xmlns:explain=\"http://explain.z3950.org/dtd/2.0/\" xmlns:sites=\"http://schema.highwire.org/Site/Metadata\" xmlns:prism=\"http://prismstandard.org/namespaces/1.2/basic/\" xmlns:srw=\"http://www.loc.gov/zing/srw/\" xmlns:dtl=\"http://schema.highwire.org/DTL/Data\" xmlns:diag=\"http://www.loc.gov/zing/srw/diagnostic/\">" + 
					"   <srw:version>1.1</srw:version>" + 
					"   <srw:numberOfRecords>355910</srw:numberOfRecords>" + 
					"   <srw:records>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>C-M. Chen</dc:contributor>" + 
					"               <dc:contributor>V. Morgenstern</dc:contributor>" + 
					"               <dc:contributor>W. Bischof</dc:contributor>" + 
					"               <dc:contributor>O. Herbarth</dc:contributor>" + 
					"               <dc:contributor>M. Borte</dc:contributor>" + 
					"               <dc:contributor>H. Behrendt</dc:contributor>" + 
					"               <dc:contributor>U. Kramer</dc:contributor>" + 
					"               <dc:contributor>A. von Berg</dc:contributor>" + 
					"               <dc:contributor>D. Berdel</dc:contributor>" + 
					"               <dc:contributor>C. P. Bauer</dc:contributor>" + 
					"               <dc:contributor>S. Koletzko</dc:contributor>" + 
					"               <dc:contributor>H-E. Wichmann</dc:contributor>" + 
					"               <dc:contributor>J. Heinrich</dc:contributor>" + 
					"               <dc:contributor>  the Influences of Lifestyle Related Factors on the Human Immune System and Development of Allergies in Children (LISA) Study Group and the German Infant Nutrition Intervention Programme (GINI) Study Group</dc:contributor>" + 
					"               <dc:date>2008-05-01</dc:date>" + 
					"               <dc:identifier>10.1183/09031936.00092807</dc:identifier>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/31/5/963</dc:description>" + 
					"               <dc:title>Dog ownership and contact during childhood and later allergy development</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>963</prism:startingPage>" + 
					"            <prism:endingPage>973</prism:endingPage>" + 
					"            <prism:number>5</prism:number>" + 
					"            <prism:volume>31</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>1</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>C. Carlsten</dc:contributor>" + 
					"               <dc:contributor>M. Brauer</dc:contributor>" + 
					"               <dc:contributor>H. Dimich-Ward</dc:contributor>" + 
					"               <dc:contributor>A. Dybuncio</dc:contributor>" + 
					"               <dc:contributor>A.B. Becker</dc:contributor>" + 
					"               <dc:contributor>M. Chan-Yeung</dc:contributor>" + 
					"               <dc:date>2011-02-01</dc:date>" + 
					"               <dc:identifier>10.1183/09031936.00187609</dc:identifier>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/37/2/324</dc:description>" + 
					"               <dc:title>Combined exposure to dog and indoor pollution: incident asthma in a high-risk birth cohort</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>324</prism:startingPage>" + 
					"            <prism:endingPage>330</prism:endingPage>" + 
					"            <prism:number>2</prism:number>" + 
					"            <prism:volume>37</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>2</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>Dan Norback</dc:contributor>" + 
					"               <dc:contributor>Nina Fabjan</dc:contributor>" + 
					"               <dc:contributor>Guihong Cai</dc:contributor>" + 
					"               <dc:contributor>Ivan Kreft</dc:contributor>" + 
					"               <dc:contributor>Erik Lampa</dc:contributor>" + 
					"               <dc:contributor>Gunilla Wieslander</dc:contributor>" + 
					"               <dc:date>2011-09-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/38/Suppl_55/1727</dc:description>" + 
					"               <dc:title>Cat, dog and horse allergens in day care centres in Uppsala, Sweden, associations with FeNO and dyspnea</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>1727</prism:startingPage>" + 
					"            <prism:number>Suppl_55</prism:number>" + 
					"            <prism:volume>38</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>3</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>N.K. Nakagawa</dc:contributor>" + 
					"               <dc:contributor>F. Donato-Junior</dc:contributor>" + 
					"               <dc:contributor>C.S. Kondo</dc:contributor>" + 
					"               <dc:contributor>M. King</dc:contributor>" + 
					"               <dc:contributor>J.O.C. Auler-Junior</dc:contributor>" + 
					"               <dc:contributor>P.H.N. Saldiva</dc:contributor>" + 
					"               <dc:contributor>G. Lorenzi-Filho</dc:contributor>" + 
					"               <dc:date>2004-11-01</dc:date>" + 
					"               <dc:identifier>10.1183/09031936.04.10021704</dc:identifier>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/24/5/805</dc:description>" + 
					"               <dc:title>Effects of acute hypovolaemia by furosemide on tracheal transepithelial potential difference and mucus in dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>805</prism:startingPage>" + 
					"            <prism:endingPage>810</prism:endingPage>" + 
					"            <prism:number>5</prism:number>" + 
					"            <prism:volume>24</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>4</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>F. Chabot</dc:contributor>" + 
					"               <dc:contributor>F. Schrijen</dc:contributor>" + 
					"               <dc:contributor>C. Saunier</dc:contributor>" + 
					"               <dc:date>2001-01-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/17/1/20</dc:description>" + 
					"               <dc:title>Role of NO pathway, calcium and potassium channels in the peripheral pulmonary vascular tone in dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>20</prism:startingPage>" + 
					"            <prism:endingPage>26</prism:endingPage>" + 
					"            <prism:number>1</prism:number>" + 
					"            <prism:volume>17</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>5</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>Rainer Ehmann</dc:contributor>" + 
					"               <dc:contributor>Enole Boedeker</dc:contributor>" + 
					"               <dc:contributor>Uwe Friedrich</dc:contributor>" + 
					"               <dc:contributor>Jutta Sagert</dc:contributor>" + 
					"               <dc:contributor>Thorsten Walles</dc:contributor>" + 
					"               <dc:contributor>Godehard Friedel</dc:contributor>" + 
					"               <dc:date>2011-09-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/38/Suppl_55/p2787</dc:description>" + 
					"               <dc:title>Detection of patients with lung cancer out of a risk group by breath sample presentation to sniffer dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>2787</prism:startingPage>" + 
					"            <prism:number>Suppl_55</prism:number>" + 
					"            <prism:volume>38</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>6</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>GT De Sanctis</dc:contributor>" + 
					"               <dc:contributor>RP Tomkiewicz</dc:contributor>" + 
					"               <dc:contributor>BK Rubin</dc:contributor>" + 
					"               <dc:contributor>S Schurch</dc:contributor>" + 
					"               <dc:contributor>M King</dc:contributor>" + 
					"               <dc:date>1994-09-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/7/9/1616</dc:description>" + 
					"               <dc:title>Exogenous surfactant enhances mucociliary clearance in the anaesthetized dog</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>1616</prism:startingPage>" + 
					"            <prism:endingPage>1621</prism:endingPage>" + 
					"            <prism:number>9</prism:number>" + 
					"            <prism:volume>7</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>7</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>O Ueno</dc:contributor>" + 
					"               <dc:contributor>LN Lee</dc:contributor>" + 
					"               <dc:contributor>PD Wagner</dc:contributor>" + 
					"               <dc:date>1989-03-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/2/3/238</dc:description>" + 
					"               <dc:title>Effect of N-acetylcysteine on gas exchange after methacholine challenge and isoprenaline inhalation in the dog</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>238</prism:startingPage>" + 
					"            <prism:endingPage>246</prism:endingPage>" + 
					"            <prism:number>3</prism:number>" + 
					"            <prism:volume>2</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>8</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>H Bishara</dc:contributor>" + 
					"               <dc:contributor>M Odeh</dc:contributor>" + 
					"               <dc:contributor>RP Schnall</dc:contributor>" + 
					"               <dc:contributor>N Gavriely</dc:contributor>" + 
					"               <dc:contributor>A Oliven</dc:contributor>" + 
					"               <dc:date>1995-09-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/8/9/1537</dc:description>" + 
					"               <dc:title>Electrically-activated dilator muscles reduce pharyngeal resistance in anaesthetized dogs with upper airway obstruction</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>1537</prism:startingPage>" + 
					"            <prism:endingPage>1542</prism:endingPage>" + 
					"            <prism:number>9</prism:number>" + 
					"            <prism:volume>8</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>9</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>K Cao</dc:contributor>" + 
					"               <dc:contributor>RR Grunstein</dc:contributor>" + 
					"               <dc:contributor>KY Ho</dc:contributor>" + 
					"               <dc:contributor>CE Sullivan</dc:contributor>" + 
					"               <dc:date>1998-06-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/11/6/1376</dc:description>" + 
					"               <dc:title>The effect of octreotide on breathing and the ventilatory response to CO2 in conscious dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>1376</prism:startingPage>" + 
					"            <prism:endingPage>1381</prism:endingPage>" + 
					"            <prism:number>6</prism:number>" + 
					"            <prism:volume>11</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>10</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>M Corboz</dc:contributor>" + 
					"               <dc:contributor>S Sanou</dc:contributor>" + 
					"               <dc:contributor>FA Grimbert</dc:contributor>" + 
					"               <dc:date>1995-07-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/8/7/1122</dc:description>" + 
					"               <dc:title>Capillary pressure estimates from arterial and venous occlusion in intact dog lung</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>1122</prism:startingPage>" + 
					"            <prism:endingPage>1129</prism:endingPage>" + 
					"            <prism:number>7</prism:number>" + 
					"            <prism:volume>8</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>11</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>ME Ward</dc:contributor>" + 
					"               <dc:contributor>M Paiva</dc:contributor>" + 
					"               <dc:contributor>PT Macklem</dc:contributor>" + 
					"               <dc:date>1992-02-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/5/2/219</dc:description>" + 
					"               <dc:title>Vector analysis in partitioning of inspiratory muscle action in dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>219</prism:startingPage>" + 
					"            <prism:endingPage>227</prism:endingPage>" + 
					"            <prism:number>2</prism:number>" + 
					"            <prism:volume>5</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>12</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>EL De Vito</dc:contributor>" + 
					"               <dc:contributor>AJ Roncoroni</dc:contributor>" + 
					"               <dc:date>1990-04-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/3/4/456</dc:description>" + 
					"               <dc:title>Effect of aminophylline and verapamil upon diaphragmatic force generation in dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>456</prism:startingPage>" + 
					"            <prism:endingPage>462</prism:endingPage>" + 
					"            <prism:number>4</prism:number>" + 
					"            <prism:volume>3</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>13</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>G Sant'Ambrogio</dc:contributor>" + 
					"               <dc:contributor>FB Sant'Ambrogio</dc:contributor>" + 
					"               <dc:date>1998-02-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/11/2/339</dc:description>" + 
					"               <dc:title>Action of moguisteine on the activity of tracheobronchial rapidly adapting receptors in the dog</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>339</prism:startingPage>" + 
					"            <prism:endingPage>344</prism:endingPage>" + 
					"            <prism:number>2</prism:number>" + 
					"            <prism:volume>11</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>14</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>M Suzuki</dc:contributor>" + 
					"               <dc:contributor>S Suzuki</dc:contributor>" + 
					"               <dc:contributor>T Akahori</dc:contributor>" + 
					"               <dc:contributor>A Miyashita</dc:contributor>" + 
					"               <dc:contributor>T Yoshioka</dc:contributor>" + 
					"               <dc:contributor>M Sato</dc:contributor>" + 
					"               <dc:contributor>T Okubo</dc:contributor>" + 
					"               <dc:date>1997-02-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/10/2/430</dc:description>" + 
					"               <dc:title>Patterns of inspiratory muscle shortening during hypoxia and hypercapnia in dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>430</prism:startingPage>" + 
					"            <prism:endingPage>436</prism:endingPage>" + 
					"            <prism:number>2</prism:number>" + 
					"            <prism:volume>10</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>15</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>PG Jorens</dc:contributor>" + 
					"               <dc:contributor>JB Richman-Eisenstat</dc:contributor>" + 
					"               <dc:contributor>BP Housset</dc:contributor>" + 
					"               <dc:contributor>PP Massion</dc:contributor>" + 
					"               <dc:contributor>I Ueki</dc:contributor>" + 
					"               <dc:contributor>JA Nadel</dc:contributor>" + 
					"               <dc:date>1994-11-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/7/11/1925</dc:description>" + 
					"               <dc:title>Pseudomonas-induced neutrophil recruitment in the dog airway in vivo is mediated in part by IL-8 and inhibited by a leumedin</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>1925</prism:startingPage>" + 
					"            <prism:endingPage>1931</prism:endingPage>" + 
					"            <prism:number>11</prism:number>" + 
					"            <prism:volume>7</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>16</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>JG Widdicombe</dc:contributor>" + 
					"               <dc:contributor>A Davies</dc:contributor>" + 
					"               <dc:date>1988-10-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/1/9/779</dc:description>" + 
					"               <dc:title>Upper airways resistance and snoring in anaesthetized dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>779</prism:startingPage>" + 
					"            <prism:endingPage>784</prism:endingPage>" + 
					"            <prism:number>9</prism:number>" + 
					"            <prism:volume>1</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>17</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>RP Tomkiewicz</dc:contributor>" + 
					"               <dc:contributor>EM App</dc:contributor>" + 
					"               <dc:contributor>M Coffiner</dc:contributor>" + 
					"               <dc:contributor>J Fossion</dc:contributor>" + 
					"               <dc:contributor>P Maes</dc:contributor>" + 
					"               <dc:contributor>M King</dc:contributor>" + 
					"               <dc:date>1994-01-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/7/1/81</dc:description>" + 
					"               <dc:title>Mucolytic treatment with N-acetylcysteine L-lysinate metered dose inhaler in dogs: airway epithelial function changes</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>81</prism:startingPage>" + 
					"            <prism:endingPage>87</prism:endingPage>" + 
					"            <prism:number>1</prism:number>" + 
					"            <prism:volume>7</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>18</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>P Lejeune</dc:contributor>" + 
					"               <dc:contributor>M Leeman</dc:contributor>" + 
					"               <dc:contributor>C Melot</dc:contributor>" + 
					"               <dc:contributor>R Naeije</dc:contributor>" + 
					"               <dc:date>1989-04-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/2/4/370</dc:description>" + 
					"               <dc:title>Effects of theophylline and S 9795 on hyperoxic and hypoxic pulmonary vascular tone in intact dogs</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>370</prism:startingPage>" + 
					"            <prism:endingPage>376</prism:endingPage>" + 
					"            <prism:number>4</prism:number>" + 
					"            <prism:volume>2</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>19</srw:recordPosition>" + 
					"      </srw:record>" + 
					"      <srw:record>" + 
					"         <srw:recordSchema>info:srw/schema/1/dc-v1.1</srw:recordSchema>" + 
					"         <srw:recordPacking>XML</srw:recordPacking>" + 
					"         <srw:recordData>" + 
					"            <dc:dc>" + 
					"               <dc:contributor>M Leeman</dc:contributor>" + 
					"               <dc:contributor>P Lejeune</dc:contributor>" + 
					"               <dc:contributor>C Melot</dc:contributor>" + 
					"               <dc:contributor>R Naeije</dc:contributor>" + 
					"               <dc:date>1988-08-01</dc:date>" + 
					"               <dc:description>http://erj.ersjournals.com/cgi/content/abstract/1/8/711</dc:description>" + 
					"               <dc:title>Pulmonary artery pressure--flow plots in hyperoxic and in hypoxic dogs: effects of prostaglandin E1</dc:title>" + 
					"            </dc:dc>" + 
					"            <prism:startingPage>711</prism:startingPage>" + 
					"            <prism:endingPage>715</prism:endingPage>" + 
					"            <prism:number>8</prism:number>" + 
					"            <prism:volume>1</prism:volume>" + 
					"            <prism:publicationName>European Respiratory Journal</prism:publicationName>" + 
					"         </srw:recordData>" + 
					"         <srw:recordPosition>20</srw:recordPosition>" + 
					"      </srw:record>" + 
					"   </srw:records>" + 
					"</srw:searchRetrieveResponse>";
}
