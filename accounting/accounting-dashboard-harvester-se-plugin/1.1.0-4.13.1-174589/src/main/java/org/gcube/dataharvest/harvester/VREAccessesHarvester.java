package org.gcube.dataharvest.harvester;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.AnalyticsReportCredentials;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.datamodel.VREAccessesReportRow;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.Builder;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.PemReader;
import com.google.api.client.util.PemReader.Section;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;

public class VREAccessesHarvester extends BasicHarvester {
	
	private static Logger logger = LoggerFactory.getLogger(VREAccessesHarvester.class);
	
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	
	private static final String SERVICE_ENDPOINT_CATEGORY = "OnlineService";
	private static final String SERVICE_ENDPOINT_NAME = "BigGAnalyticsReportService";
	private static final String AP_VIEWS_PROPERTY = "views";
	private static final String AP_CLIENT_PROPERTY = "clientId";
	private static final String AP_PRIVATEKEY_PROPERTY = "privateKeyId";
	private static final String APPLICATION_NAME = "Analytics Reporting";
	
	private List<VREAccessesReportRow> vreAccesses;
	
	public VREAccessesHarvester(Date start, Date end) throws Exception {
		super(start, end);
		vreAccesses = getAllAccesses(start, end);
	}
	
	@Override
	public List<AccountingRecord> getAccountingRecords() throws Exception {
		try {
			String context = org.gcube.dataharvest.utils.Utils.getCurrentContext();
			
			ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
			
			int measure = 0;
			
			ScopeBean scopeBean = new ScopeBean(context);
			String lowerCasedContext = scopeBean.name().toLowerCase();
			String case1 = lowerCasedContext + "/";
			String case2 = lowerCasedContext + "?";
			for(VREAccessesReportRow row : vreAccesses) {
				String pagePath = row.getPagePath();
				if (!pagePath.contains("_redirect=/group") && !pagePath.contains("workspace")) {
					if(pagePath.endsWith(lowerCasedContext)) {
						logger.trace("Matched endsWith({}) : {}", lowerCasedContext, pagePath);
						measure += row.getVisitNumber();
					} else if(pagePath.contains(case1) || pagePath.contains(case2)) {
						logger.trace("Matched contains({}) || contains({}) : {}", case1, case2, pagePath);
						measure += row.getVisitNumber();
					}
				}
			}
			
			ScopeDescriptor scopeDescriptor = AccountingDataHarvesterPlugin.getScopeDescriptor();
			
			AccountingRecord ar = new AccountingRecord(scopeDescriptor, instant, getDimension(HarvestedDataKey.ACCESSES), (long) measure);
			logger.debug("{} : {}", ar.getDimension().getId(), ar.getMeasure());
			accountingRecords.add(ar);
			
			return accountingRecords;
			
		} catch(Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @return a list of {@link VREAccessesReportRow} objects containing the pagePath and the visit number e.g.
	 * VREAccessesReportRow [pagePath=/group/agroclimaticmodelling/add-new-users, visitNumber=1]
	 * VREAccessesReportRow [pagePath=/group/agroclimaticmodelling/administration, visitNumber=2]
	 * VREAccessesReportRow [pagePath=/group/agroclimaticmodelling/agroclimaticmodelling, visitNumber=39]	
	 */
	private static List<VREAccessesReportRow> getAllAccesses(Date start, Date end) throws Exception {
		DateRange dateRange = getDateRangeForAnalytics(start, end);
		logger.trace("Getting accesses in this time range {}", dateRange.toPrettyString());
		
		AnalyticsReportCredentials credentialsFromD4S = getAuthorisedApplicationInfoFromIs();
		AnalyticsReporting service = initializeAnalyticsReporting(credentialsFromD4S);
		HashMap<String,GetReportsResponse> responses = getReportResponses(service, credentialsFromD4S.getViewIds(),
				dateRange);
		List<VREAccessesReportRow> totalAccesses = new ArrayList<>();
		
		for(String view : responses.keySet()) {
			List<VREAccessesReportRow> viewReport = parseResponse(view, responses.get(view));
			logger.trace("Got {} entries from view id={}", viewReport.size(), view);
			totalAccesses.addAll(viewReport);
		}
		logger.trace("Merged in {} total entries from all views", totalAccesses.size());
		return totalAccesses;
	}
	
	/**
	 * Initializes an Analytics Reporting API V4 service object.
	 *
	 * @return An authorized Analytics Reporting API V4 service object.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private static AnalyticsReporting initializeAnalyticsReporting(AnalyticsReportCredentials cred)
			throws GeneralSecurityException, IOException {
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		GoogleCredential credential = fromD4SServiceEndpoint(cred).createScoped(AnalyticsReportingScopes.all());
		
		// Construct the Analytics Reporting service object.
		return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}
	
	/**
	 * Queries the Analytics Reporting API V4.
	 *
	 * @param service An authorized Analytics Reporting API V4 service object.
	 * @return GetReportResponse The Analytics Reporting API V4 response.
	 * @throws IOException
	 */
	private static HashMap<String,GetReportsResponse> getReportResponses(AnalyticsReporting service,
			List<String> viewIDs, DateRange dateRange) throws IOException {
		
		HashMap<String,GetReportsResponse> reports = new HashMap<>();
		
		// Create the Metrics object.
		Metric sessions = new Metric().setExpression("ga:pageviews").setAlias("pages");
		com.google.api.services.analyticsreporting.v4.model.Dimension pageTitle = new com.google.api.services.analyticsreporting.v4.model.Dimension().setName("ga:pagePath");
		
		for(String view : viewIDs) {
			logger.info("Getting data from Google Analytics for viewid: " + view);
			// Create the ReportRequest object.
			ReportRequest request = new ReportRequest().setViewId(view).setDateRanges(Arrays.asList(dateRange))
					.setMetrics(Arrays.asList(sessions)).setDimensions(Arrays.asList(pageTitle));
			
			ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
			requests.add(request);
			
			// Create the GetReportsRequest object.
			GetReportsRequest getReport = new GetReportsRequest().setReportRequests(requests);
			
			// Call the batchGet method.
			GetReportsResponse response = service.reports().batchGet(getReport).execute();
			reports.put(view, response);
		}
		// Return the response.
		return reports;
	}
	
	/**
	 * Parses and prints the Analytics Reporting API V4 response.
	 *
	 * @param response An Analytics Reporting API V4 response.
	 */
	/**
	 * Parses and prints the Analytics Reporting API V4 response.
	 *
	 * @param response An Analytics Reporting API V4 response.
	 */
	private static List<VREAccessesReportRow> parseResponse(String viewId, GetReportsResponse response) {
		logger.debug("parsing Response for " + viewId);

		List<VREAccessesReportRow> toReturn = new ArrayList<>();

		for (Report report: response.getReports()) {
			List<ReportRow> rows = report.getData().getRows();
			if (rows == null) {
				logger.warn("No data found for " + viewId);
			}
			else {
				for (ReportRow row: rows) {
					String dimension = row.getDimensions().get(0);
					DateRangeValues metric = row.getMetrics().get(0);
					VREAccessesReportRow var = new VREAccessesReportRow();
					boolean validEntry = false;
					String pagePath = dimension;
					if (pagePath.startsWith("/group") || pagePath.startsWith("/web")) {
						var.setPagePath(dimension);
						validEntry = true;
					}
					if (validEntry) {
						var.setVisitNumber(Integer.parseInt(metric.getValues().get(0)));
						toReturn.add(var);
					}
				}
			}
		}
		return toReturn;
	}
	
	private static GoogleCredential fromD4SServiceEndpoint(AnalyticsReportCredentials cred) throws IOException {
		
		String clientId = cred.getClientId();
		String clientEmail = cred.getClientEmail();
		String privateKeyPem = cred.getPrivateKeyPem();
		String privateKeyId = cred.getPrivateKeyId();
		String tokenUri = cred.getTokenUri();
		String projectId = cred.getProjectId();
		
		if(clientId == null || clientEmail == null || privateKeyPem == null || privateKeyId == null) {
			throw new IOException("Error reading service account credential from stream, "
					+ "expecting  'client_id', 'client_email', 'private_key' and 'private_key_id'.");
		}
		
		PrivateKey privateKey = privateKeyFromPkcs8(privateKeyPem);
		
		Collection<String> emptyScopes = Collections.emptyList();
		
		Builder credentialBuilder = new GoogleCredential.Builder().setTransport(Utils.getDefaultTransport())
				.setJsonFactory(Utils.getDefaultJsonFactory()).setServiceAccountId(clientEmail)
				.setServiceAccountScopes(emptyScopes).setServiceAccountPrivateKey(privateKey)
				.setServiceAccountPrivateKeyId(privateKeyId);
		
		if(tokenUri != null) {
			credentialBuilder.setTokenServerEncodedUrl(tokenUri);
		}
		
		if(projectId != null) {
			credentialBuilder.setServiceAccountProjectId(projectId);
		}
		
		// Don't do a refresh at this point, as it will always fail before the scopes are added.
		return credentialBuilder.build();
	}
	
	private static PrivateKey privateKeyFromPkcs8(String privateKeyPem) throws IOException {
		Reader reader = new StringReader(privateKeyPem);
		Section section = PemReader.readFirstSectionAndClose(reader, "PRIVATE KEY");
		if(section == null) {
			throw new IOException("Invalid PKCS8 data.");
		}
		byte[] bytes = section.getBase64DecodedBytes();
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
		Exception unexpectedException = null;
		try {
			KeyFactory keyFactory = SecurityUtils.getRsaKeyFactory();
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			return privateKey;
		} catch(NoSuchAlgorithmException exception) {
			unexpectedException = exception;
		} catch(InvalidKeySpecException exception) {
			unexpectedException = exception;
		}
		throw new IOException("Unexpected exception reading PKCS data", unexpectedException);
	}
	
	private static List<ServiceEndpoint> getAnalyticsReportingConfigurationFromIS(String infrastructureScope)
			throws Exception {
		String scope = infrastructureScope;
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '" + SERVICE_ENDPOINT_CATEGORY + "'");
		query.addCondition("$resource/Profile/Name/text() eq '" + SERVICE_ENDPOINT_NAME + "'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}
	
	/**
	 * l
	 * @throws Exception 
	 */
	private static AnalyticsReportCredentials getAuthorisedApplicationInfoFromIs() throws Exception {
		AnalyticsReportCredentials reportCredentials = new AnalyticsReportCredentials();
		
		String context = org.gcube.dataharvest.utils.Utils.getCurrentContext();
		try {
			List<ServiceEndpoint> list = getAnalyticsReportingConfigurationFromIS(context);
			if(list.size() > 1) {
				logger.error("Too many Service Endpoints having name " + SERVICE_ENDPOINT_NAME
						+ " in this scope having Category " + SERVICE_ENDPOINT_CATEGORY);
			} else if(list.size() == 0) {
				logger.warn("There is no Service Endpoint having name " + SERVICE_ENDPOINT_NAME + " and Category "
						+ SERVICE_ENDPOINT_CATEGORY + " in this context: " + context);
			} else {
				
				for(ServiceEndpoint res : list) {
					reportCredentials.setTokenUri(res.profile().runtime().hostedOn());
					Group<AccessPoint> apGroup = res.profile().accessPoints();
					AccessPoint[] accessPoints = (AccessPoint[]) apGroup.toArray(new AccessPoint[apGroup.size()]);
					AccessPoint found = accessPoints[0];
					reportCredentials.setClientEmail(found.address());
					reportCredentials.setProjectId(found.username());
					reportCredentials.setPrivateKeyPem(StringEncrypter.getEncrypter().decrypt(found.password()));
					for(Property prop : found.properties()) {
						if(prop.name().compareTo(AP_VIEWS_PROPERTY) == 0) {
							String decryptedValue = StringEncrypter.getEncrypter().decrypt(prop.value());
							String[] views = decryptedValue.split(";");
							reportCredentials.setViewIds(Arrays.asList(views));
						}
						if(prop.name().compareTo(AP_CLIENT_PROPERTY) == 0) {
							String decryptedValue = StringEncrypter.getEncrypter().decrypt(prop.value());
							reportCredentials.setClientId(decryptedValue);
						}
						if(prop.name().compareTo(AP_PRIVATEKEY_PROPERTY) == 0) {
							String decryptedValue = StringEncrypter.getEncrypter().decrypt(prop.value());
							reportCredentials.setPrivateKeyId(decryptedValue);
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return reportCredentials;
	}
	
	private static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	private static DateRange getDateRangeForAnalytics(Date start, Date end) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //required by Analytics
		String startDate = asLocalDate(start).format(formatter);
		String endDate = asLocalDate(end).format(formatter);
		DateRange dateRange = new DateRange();// date format `yyyy-MM-dd`
		dateRange.setStartDate(startDate);
		dateRange.setEndDate(endDate);
		return dateRange;
	}
	
}
