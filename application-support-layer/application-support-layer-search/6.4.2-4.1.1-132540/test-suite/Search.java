





import java.util.ArrayList;
import java.util.List;

//import org.gcube.application.framework.contentmanagement.model.CollectionInfo;
//import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.application.framework.core.session.SessionManager;
//import org.gcube.application.framework.search.exception.BrowsePortTypeCreationException;
//import org.gcube.application.framework.search.exception.ClientCreationFailedException;
//import org.gcube.application.framework.search.exception.ClientUnexpectedErrorException;
//import org.gcube.application.framework.search.exception.NoEprFoundException;
//import org.gcube.application.framework.search.exception.NoSessionFoundException;
//import org.gcube.application.framework.search.exception.SearchQueryCreationException;
//import org.gcube.application.framework.search.library.ResultSetConsumerI;
//import org.gcube.application.framework.search.library.impl.SearchHelper;
//import org.gcube.application.framework.search.library.model.Query;

public class Search {
	
	public static void main(String[] args) {

//		// Build and Submit Search Queries
//		
//		// Submit a browse query
//		
//		ASLSession aslSession = SessionManager.getInstance().getASLSession("fakeId", "rena.tsantouli");
//		aslSession.setScope("/gCube/devsec");
//		
//		List<CollectionInfo>[] collectionInfos = null;
//		try {
//			SearchHelper s_h = new SearchHelper("rena.tsantouli", "fakeId");
//			collectionInfos = s_h.getAvailableCollections();
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			return;
//		}
//		
//		Query q = new Query();
//		
//		// Create and submit a browse query
//		
//		System.out.println("Creating browse query");
//		List<String> selectedCollections = new ArrayList();
//		selectedCollections.add("e69478d0-f223-11dd-afb5-ef0e7cd26495");
//		q.selectCollections(selectedCollections, true, aslSession);
// 
//		List<String> brFields = q.getAvailableBrowseFields();
//		if (brFields == null || brFields.size() == 0) {
//			System.out.println("No browse fields available");
//		}
//		
//		// set the search criterion
//		q.setSortBy(brFields.get(0));
//		
//		
//		System.out.println("The formulated query is: " + q.getQueryDescription());
//		
//		ResultSetConsumerI rs = null;
//		try {
//			rs = q.browse(aslSession);
//		} catch (BrowsePortTypeCreationException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		
//		try {
//			List<String>results = rs.getResultsToXML(10, 1, aslSession);
//			
//			
//			for (int i = 0; i < results.size(); i++) {
//				System.out.println("Result: " + results.get(i));
//			}
//		} catch (NoEprFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientCreationFailedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientUnexpectedErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSessionFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		
//		
//		/****************************************/
//		
//		
//		// Submit a quick search query
//		
//		Query q2 = new Query();
//		ResultSetConsumerI rs2 = null;
//		
//		try {
//			rs = q.quickSearch(aslSession, "earth");
//		} catch (SearchQueryCreationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		try {
//			List<String>results2 = rs2.getResultsToXML(10, 0, aslSession);
//			
//			
//			for (int i = 0; i < results2.size(); i++) {
//				System.out.println("Result: " + results2.get(i));
//			}
//		} catch (NoEprFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientCreationFailedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientUnexpectedErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSessionFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

}
