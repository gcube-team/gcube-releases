package org.gcube.datatransformation.datatransformationlibrary.imanagers.queries;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Parses a query.
 * </p>
 */
public class QueryParser {

	protected static final String GET="GET";
	protected static final String WHERE="WHERE";
	
	protected static final String LOGICAND = "AND";
	protected static final String LOGICOR = "OR";
	
	/**
	 * 
	 */
	public static final String QUERYSOURCECONTENTTYPE="SCONTENTTYPE";
	protected static final String QUERYTARGETCONTENTTYPE="TCONTENTTYPE";
	protected static final String QUERYTRANSFORMATIONUNIT="TRANSFORMATIONUNIT";
	protected static final String QUERYDESCRIPTION="DESCRIPTION";
	protected static final String QUERYPROGRAMPARAMETERS="PROGRAMPARAMETERS";
	
	protected static final String TRANSFORMATIONPROGRAMID="TRANSFORMATIONPROGRAMID";
	protected static final String TRANSFORMATIONUNITID="TRANSFORMATIONUNITID";
	
	protected static final String MIMETYPE="MIMETYPE";
	protected static final String MIMESUBTYPE="MIMESUBTYPE";
	protected static final String CONTENTTYPEPARAMETER="CTPARAM";
	protected static final String CONTENTTYPEPARAMETERNAME="NAME";
	protected static final String CONTENTTYPEPARAMETERVALUE="VALUE";
	
	/**
	 * Parses a query.
	 * 
	 * @param query The query.
	 * @return The query object.
	 * @throws Exception If the query is not valid.
	 */
	public static QueryObject parse(String query) throws Exception {
		
		int cnt=0;
		StringTokenizer st = new StringTokenizer(query);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			//GET
			if(cnt==0){
				if(!token.equals(GET)){
					System.out.println(token);
					throw new Exception("Invalid query: GET must be the first token");
				}
			}else if(cnt==1){//RESULT TYPE
				if(token.equals(QUERYSOURCECONTENTTYPE) || token.equals(QUERYTARGETCONTENTTYPE)){
					return parseFormatQueryObject(st, token);
				}else if (token.equals(QUERYTRANSFORMATIONUNIT)){
					return parseTransformationQueryObject(st);
				}else if (token.equals(QUERYDESCRIPTION)){
					return parseDescriptionQueryObject(st);
				}else if (token.equals(QUERYPROGRAMPARAMETERS)){
					return parseProgramParametersQueryObject(st);
				}else{
					throw new Exception("Invalid query: Can only GET "+QUERYSOURCECONTENTTYPE+"/"+QUERYTARGETCONTENTTYPE+"/"+QUERYTRANSFORMATIONUNIT);
				}
			}
			cnt++;
		}
		throw new Exception("Invalid query: Undefined Error"); 
	}
	
	private static ContentTypeQueryObject parseFormatQueryObject(StringTokenizer st, String resultType) throws Exception {
		ContentTypeQueryObject object = new ContentTypeQueryObject();
		object.setResultType(resultType);
		
		int cnt=0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			//WHERE
			if(cnt==0){
				if(!token.equals(WHERE)){
					System.out.println(token);
					throw new Exception("Invalid query: Third token must be "+WHERE);
				}
				object.setHasWhereClause(true);
			} else {//CONDITIONS...
				if(cnt%2==1){
					parseConditionForFormatQuery(token, object);
				}else{
					//AND - OR
					if(!(token.equals(LOGICAND) || token.equals(LOGICOR))){
						throw new Exception("Invalid query: Logic operator expected");
					}
					//TODO: Currently only one logic operation is permitted(The last one:-))
					object.setLogicOperation(token);
				}
			}
			cnt++;
		}
		return object;
	}
	
	private static TransformationUnitQueryObject parseTransformationQueryObject(StringTokenizer st) throws Exception{
		TransformationUnitQueryObject object = new TransformationUnitQueryObject();
		object.setResultType(QUERYTRANSFORMATIONUNIT);
		
		int cnt=0;
		while (st.hasMoreTokens()){
			String token = st.nextToken();
			//WHERE
			if(cnt==0){
				if(!token.equals(WHERE)){
					throw new Exception("Invalid query: Third token must be "+WHERE);
				}
				object.setHasWhereClause(true);
			} else {//CONDITIONS...
				if(cnt%2==1){
					parseConditionForTransformationQuery(token, object);
				}else{
					//AND - OR
					if(!(token.equals(LOGICAND) || token.equals(LOGICOR))){
						throw new Exception("Invalid query: Logic operator expected");
					}
					//TODO: Currently only one logic operation is permitted(The last one:-))
					object.setLogicOperation(token);
				}
			}
			cnt++;
		}
		return object;
	}
	
	private static DescriptionQueryObject parseDescriptionQueryObject(StringTokenizer st) throws Exception{
		DescriptionQueryObject object = new DescriptionQueryObject();
		object.setResultType(QUERYDESCRIPTION);
		
		int cnt=0;
		while (st.hasMoreTokens()){
			String token = st.nextToken();
			//WHERE
			if(cnt==0){
				if(!token.equals(WHERE)){
					throw new Exception("Invalid query: Third token must be "+WHERE);
				}
				object.setHasWhereClause(true);
			} else {//CONDITIONS...
				if(cnt%2==1){
					parseConditionForDescriptionQuery(token, object);
				}else{
					//AND - OR --> invalid
					if(!(token.equals(LOGICAND))){
						throw new Exception("Invalid query: AND Logic operator expected");
					}
					//TODO: Currently only one logic operation is permitted(The last one:-))
					object.setLogicOperation(token);
				}
			}
			cnt++;
		}
		return object;
	}
	
	private static ProgramParametersQueryObject parseProgramParametersQueryObject(StringTokenizer st) throws Exception{
		ProgramParametersQueryObject object = new ProgramParametersQueryObject();
		object.setResultType(QUERYPROGRAMPARAMETERS);
		
		int cnt=0;
		while (st.hasMoreTokens()){
			String token = st.nextToken();
			//WHERE
			if(cnt==0){
				if(!token.equals(WHERE)){
					throw new Exception("Invalid query: Third token must be "+WHERE);
				}
				object.setHasWhereClause(true);
			} else {//CONDITIONS...
				if(cnt%2==1){
					parseConditionForProgramParametersQuery(token, object);
				}else{
					//AND - OR --> invalid
					if(!(token.equals(LOGICAND))){
						throw new Exception("Invalid query: AND Logic operator expected");
					}
					//TODO: Currently only one logic operation is permitted(The last one:-))
					object.setLogicOperation(token);
				}
			}
			cnt++;
		}
		return object;
	}
	
	private static void parseConditionForProgramParametersQuery(String condition, ProgramParametersQueryObject object) throws Exception {
		StringTokenizer eqTokenizer = new StringTokenizer(condition, "=");
		if(eqTokenizer.countTokens()!=2){
			throw new Exception("Invalid query: Condition must be in the format name=value");
		}
		String name = eqTokenizer.nextToken();
		String value = eqTokenizer.nextToken();
		
		if(name.equals(TRANSFORMATIONPROGRAMID)){
			object.transformationProgramID=value;
		}
		
		if(name.equals(TRANSFORMATIONUNITID)){
			object.transformationUnitID=value;
		}
		
	}
	
	private static void parseConditionForTransformationQuery(String condition, TransformationUnitQueryObject object) throws Exception {
		String name;
		String value;
		
		StringTokenizer eqTokenizer = new StringTokenizer(condition, "=");
		if(eqTokenizer.countTokens()!=2){
			throw new Exception("Invalid query: Condition must be in the format name=value");
		}
		name=eqTokenizer.nextToken();
		value=eqTokenizer.nextToken();
		
		StringTokenizer dotTokenizer = new StringTokenizer(name, ".");
		HashMap<Integer, ContentTypeCondition> formatConditions = null;
		ContentTypeCondition formatCondition = null;
		int cnt=0;
		while(dotTokenizer.hasMoreElements()){
			String dotToken = dotTokenizer.nextToken();
			if(cnt==0){
				if(dotToken.equals(TRANSFORMATIONPROGRAMID)){
					if(object.transformationProgramID!=null){
						throw new Exception("Invalid query: Only one condition for TransformationProgramID is currently supported");
					}
					object.transformationProgramID=value;
					if(dotTokenizer.hasMoreElements()){
						throw new Exception("Invalid query: Cannot have dot after "+TRANSFORMATIONPROGRAMID+" condition.");
					}
					return;
				} else if(dotToken.equals(QUERYSOURCECONTENTTYPE)){
					formatConditions=object.sourceContentTypeConditions;
				} else if(dotToken.equals(QUERYTARGETCONTENTTYPE)){
					formatConditions=object.targetContentTypeConditions;
				} else {
					throw new Exception("Invalid query: Cannot have condition starting with "+dotToken);
				}
			}
			if(cnt==1){
				Integer formatCounter;
				try {
					formatCounter = Integer.parseInt(dotToken);
				} catch (NumberFormatException e) {
					throw new Exception("Invalid query: Expected Integer after "+QUERYSOURCECONTENTTYPE+"/"+QUERYTARGETCONTENTTYPE);
				}
				formatCondition = formatConditions.get(formatCounter);
				if(formatCondition==null){
					formatCondition=new ContentTypeCondition();
					formatConditions.put(formatCounter, formatCondition);
				}
			}
			
			if(cnt==2){
				if(dotToken.equals(MIMETYPE)){
					formatCondition.setMimetype(value);
					if(dotTokenizer.hasMoreElements()){
						throw new Exception("Invalid query: Cannot have dot after "+MIMETYPE+" condition.");
					}
					return;
				}else if(dotToken.equals(MIMESUBTYPE)){
					formatCondition.setMimesubtype(value);
					if(dotTokenizer.hasMoreElements()){
						throw new Exception("Invalid query: Cannot have dot after "+MIMESUBTYPE+" condition.");
					}
					return;
				}else if(dotToken.equals(CONTENTTYPEPARAMETER)){
					if(!dotTokenizer.hasMoreElements())
						throw new Exception("Invalid query: Format Parameter Name expected after "+CONTENTTYPEPARAMETER);
					String formatName = dotTokenizer.nextToken();
					formatCondition.addContentTypeParameterCondition(new ContentTypeParameterCondition(formatName, value));
					if(dotTokenizer.hasMoreElements())
						throw new Exception("Invalid query: Cannot have dot after Content Type Parameter Name");
					return;
				}
			}
			cnt++;
		}
		
	}
	
	
	private static void parseConditionForDescriptionQuery(String condition, DescriptionQueryObject object) throws Exception {
		StringTokenizer eqTokenizer = new StringTokenizer(condition, "=");
		if(eqTokenizer.countTokens()!=2){
			throw new Exception("Invalid query: Condition must be in the format name=value");
		}
		String name = eqTokenizer.nextToken();
		String value = eqTokenizer.nextToken();
		
		if(name.equals(TRANSFORMATIONPROGRAMID)){
			object.transformationProgramID=value;
		}
		if(name.equals(TRANSFORMATIONUNITID)){
			object.transformationUnitID=value;
		}
		
	}
	
	private static void parseConditionForFormatQuery(String condition, ContentTypeQueryObject object) throws Exception {
		String name;
		String value;
		
		StringTokenizer eqTokenizer = new StringTokenizer(condition, "=");
		if(eqTokenizer.countTokens()!=2){
			throw new Exception("Invalid query: Condition must be in the format name=value");
		}
		name=eqTokenizer.nextToken();
		value=eqTokenizer.nextToken();
		
		StringTokenizer dotTokenizer = new StringTokenizer(name, ".");
		while(dotTokenizer.hasMoreElements()){
			String dotToken = dotTokenizer.nextToken();
			if(dotToken.equals(TRANSFORMATIONPROGRAMID)){
				if(object.transformationProgramID!=null){
					throw new Exception("Invalid query: Only one condition for TransformationProgramID is currently supported");
				}
				object.transformationProgramID=value;
				if(dotTokenizer.hasMoreElements()){
					throw new Exception("Invalid query: Cannot have dot after "+TRANSFORMATIONPROGRAMID+" condition.");
				}
				return;
			} else if(dotToken.equals(TRANSFORMATIONUNITID)){
				if(object.transformationUnitID!=null){
					throw new Exception("Invalid query: Only one condition for TransformationID is currently supported");
				}
				object.transformationUnitID=value;
				if(dotTokenizer.hasMoreElements()){
					throw new Exception("Invalid query: Cannot have dot after "+TRANSFORMATIONUNITID+" condition.");
				}
				return;
			} else if(dotToken.equals(MIMETYPE)){
				object.contentTypeCondition.setMimetype(value);
				if(dotTokenizer.hasMoreElements()){
					throw new Exception("Invalid query: Cannot have dot after "+MIMETYPE+" condition.");
				}
				return;
			}else if(dotToken.equals(MIMESUBTYPE)){
				object.contentTypeCondition.setMimesubtype(value);
				if(dotTokenizer.hasMoreElements()){
					throw new Exception("Invalid query: Cannot have dot after "+MIMESUBTYPE+" condition.");
				}
				return;
			}else if(dotToken.equals(CONTENTTYPEPARAMETER)){
				if(!dotTokenizer.hasMoreElements())
					throw new Exception("Invalid query: Format Parameter Name expected after "+CONTENTTYPEPARAMETER);
				String formatName = dotTokenizer.nextToken();
				object.contentTypeCondition.addContentTypeParameterCondition(new ContentTypeParameterCondition(formatName, value));
				if(dotTokenizer.hasMoreElements())
					throw new Exception("Invalid query: Cannot have dot after Content Type Parameter Name");
				return;
			} else {
				throw new Exception("Invalid query: Cannot have condition starting with "+dotToken);
			}
		}
	}
	
}
