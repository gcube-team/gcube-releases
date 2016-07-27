package org.gcube.datatransformation.datatransformationlibrary.imanagers.queries;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * A simple test for the querying model functionality.
 * </p>
 */
public class QueryTest {

	/**
	 * The main method of the test.
	 * @param args The arguments of the main.
	 * @throws Exception If a error occurred during the test.
	 */
	public static void main(String[] args) throws Exception {
//		String query = "SELECT TRANSFORMATION " +
//				"WHERE SFORMAT.1.MIMETYPE=image " +
//				"AND SFORMAT.2.MIMESUBTYPE=BLA " +
//				"AND SFORMAT.1.FPARAM.width=100";
		
		String query = "SELECT SFORMAT " +
			"WHERE TRANSFORMATIONID=BLA " +
			"AND TRANSFORMATIONPROGRAMID=100";
		
		QueryObject object = QueryParser.parse(query);
		
		System.out.println("RESULTTYPE: "+object.getResultType());
		System.out.println("LOGIC: "+object.getLogicOperation());
		
		if(TransformationUnitQueryObject.class.isInstance(object)){
			TransformationUnitQueryObject tobject = (TransformationUnitQueryObject)object;
			
			System.out.println("TPID: "+tobject.transformationProgramID);
			
			Iterator<Entry<Integer, ContentTypeCondition>> it = tobject.sourceContentTypeConditions.entrySet().iterator();
			while(it.hasNext()){
				Entry<Integer, ContentTypeCondition> entry = it.next();
				System.out.println("CNT: "+entry.getKey());
				ContentTypeCondition cond = entry.getValue();
				System.out.println("MT: "+cond.getMimetype());
				System.out.println("MST: "+cond.getMimesubtype());
				for(int i = 0;i<cond.sizeOfContentTypeParameterConditions();i++){
					ContentTypeParameterCondition fcond = cond.getContentTypeParameterCondition(i);
					System.out.println(fcond.getName()+"=\""+fcond.getValue()+"\"");
				}
			}
		}else if(ContentTypeQueryObject.class.isInstance(object)){
			ContentTypeQueryObject fobject = (ContentTypeQueryObject)object;
			System.out.println("transformationUnitID: "+fobject.transformationUnitID);
			System.out.println("transformationProgramID: "+fobject.transformationProgramID);
			System.out.println("MT: "+fobject.contentTypeCondition.getMimetype());
			System.out.println("MST: "+fobject.contentTypeCondition.getMimesubtype());
			for(int i = 0;i<fobject.contentTypeCondition.sizeOfContentTypeParameterConditions();i++){
				ContentTypeParameterCondition fcond = fobject.contentTypeCondition.getContentTypeParameterCondition(i);
				System.out.println(fcond.getName()+"=\""+fcond.getValue()+"\"");
			}
		}
		
	}
}
