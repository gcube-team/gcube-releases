
/**
 *
 */
/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 29, 2016
 */
public class GeonetworkMefServiceTest {

	/*
	public static void main(String[] args) throws ServletException {

		String path = "/geonetwork/gcube_devsec_devVRE/srv/en/mef.export";
		String queryString = "p1=1&p2=2";
		String pathWithoutGN = path.substring(SERVLET_GEONETWORK.length()+1, path.length());
		logger.debug("servlet path without "+SERVLET_GEONETWORK + " is:" +pathWithoutGN);
		String[] params = pathWithoutGN.split("/");

		System.out.println(Arrays.asList(params));

		if(params[0]==null || params[0].isEmpty()){
			logger.error("Scope is null or empty, you must set a valid scope /geonetwork/root_vo_vre");
			throw new ServletException("Scope is null or empty, you must set a valid scope /geonetwork/root_vo_vre");
		}

		String scopeValue = getScope(params[0]);
		logger.debug("scope value is: "+scopeValue);
		String newURI = SERVLET_GEONETWORK + "?" + GeonetworkResolver.SCOPE + "=" + scopeValue;

		if(params.length>1){
			String remainPath = "";
//			newURI +="&remainPath=";
			for (int i = 1; i < params.length; i++) {
				String httpGetParam = params[i];
				if(httpGetParam!=null && !httpGetParam.isEmpty())
					remainPath+="/"+httpGetParam;
			}

			newURI +="&"+GeonetworkResolver.REMAIN_PATH+"="+remainPath;
		}


		if(queryString!=null && !queryString.isEmpty())
			newURI+="&"+queryString;

		logger.debug("forward "+newURI);
	}*/

}
