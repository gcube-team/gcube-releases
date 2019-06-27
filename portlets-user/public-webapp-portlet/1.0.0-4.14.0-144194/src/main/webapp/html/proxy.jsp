<%
/**
 * The MIT License
 *
 * Copyright (c) 2011 James Sumners
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * $Date$
 * $Revision$
 *
 * This script is used to fetch remote content on behalf of a local script.
 * This allows us to get around the Same Origin Policy limitation. Additionally,
 * this script can be used to fetch non-SSL content as SSL content, thereby
 * eliminating "mixed content" warnings.
 *
 * There are several parameters that can be passed to the script:
 *  "p_url" (required): This parameter specifies the resource the proxy should fetch.
 *  "p_mimetype" (optional [Default: "text/html"]): This specifies the mimetype the
 *    proxy should respond with.
 *  "p_act_as_client" (optional): If present, and not null, then the proxy will pretend
 *    to be client. It will pass along the client's user agent and cookies.
 *  "p_mangle" (optional): If present, and not null, then any `src` attributes in the
 *    fetched content will be re-written to pass through the proxy.
 *
 * You can pass `p_url=example.com` or `p_url=http%3A%2F%2Fexample.com` (URI Component Encoded)
 * to fetch data from `example.com`. If you do not specify the scheme ("http" or "https"), then
 * the proxy will use the scheme it was requested through. That is,
 * `http://localhost/proxy.jsp?p_url=example.com` will result in a fetch URL of
 * `http://example.com`. This will affect mangled data. As a consequence, the remote
 * server should support SSL. Otherwise, you will get an IOException.
 *
 * If the resource you are fetching requires parameters of its own, you can append them
 * as parameters to the proxy request. Any parameter that the proxy does not recognize
 * as its own will be passed along to the remote request. So if you do
 * `http://localhost/proxy.jsp?p_url=google.com&q=bricks`, you will get back the Google
 * search results for the query "bricks".
 */
%>

<%@page language="java" %>
<%@page import="org.apache.http.*" %>
<%@page import="org.apache.http.message.*" %>
<%@page import="org.apache.http.client.*" %>
<%@page import="org.apache.http.impl.client.*" %>
<%@page import="org.apache.http.client.methods.*" %>
<%@page import="org.apache.http.client.entity.*" %>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%@page import="java.net.*" %>
<%@page import="java.util.regex.*" %>

<%
// Define a list of parameters that we expect to use
// in this proxy script. They will _not_ be passed
// along in the request.
//
// Add any parameters to this list that you want this
// script to recognize as local. It is recommended that
// you prefix them with a namespace. The default is
// "p_".
ArrayList privateParameters = new ArrayList();
privateParameters.add("p_url"); // The URL to fetch.
privateParameters.add("p_mimetype"); // The mimetype for our output.
privateParameters.add("p_act_as_client"); // Pass cookies and user agent if not null.
privateParameters.add("p_mangle"); // Mangle embedded resources in the response to use the proxy.

// We always need a URL to fetch. If the one we are given
// does not include a scheme, we will append a default one.
String defaultScheme = request.getScheme().toLowerCase();
String url = request.getParameter("p_url");
if (url == null) {
  response.setStatus(400);
  out.print("MissingRequiredQueryParameter: p_url");
  return;
} else if (url.indexOf("http") == -1) {
  // The URL did not start with an appropriate scheme definition.
  // Therefore, we must add one.
  url = defaultScheme + "://" + url;
}

// Set our reponse header to the appropriate mimetype.
// The default is "text/html", but we do try to detect some types automatically.
String responseMimetype = request.getParameter("p_mimetype");
if (responseMimetype == null) {
  if ( (url.toLowerCase().indexOf(".jpg") != -1) ||
      (url.toLowerCase().indexOf(".jpeg") != -1) ) {
    responseMimetype = "image/jpeg";
  } else if ( url.toLowerCase().indexOf(".gif") != -1 ) {
    responseMimetype = "image/gif";
  } else if ( url.toLowerCase().indexOf(".png") != -1 ) {
    responseMimetype = "image/png";
  } else {
    responseMimetype = "text/html";
  }
}
response.setHeader("content-type", responseMimetype + "; charset=utf-8");

// Populate the request parameters we will use when requesting
// the remote URL.
Enumeration requestParameterNames = request.getParameterNames();
List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
while ( requestParameterNames.hasMoreElements() ) {
  String parameterName = requestParameterNames.nextElement().toString();
  String parameterValue = request.getParameter(parameterName);

  if ( !privateParameters.contains(parameterName) ) {
    requestParams.add(new BasicNameValuePair(parameterName, parameterValue));
  }
}


Boolean isPost = request.getMethod().equalsIgnoreCase("POST");
HttpRequestBase httpMethod = null;
DefaultHttpClient httpClient = new DefaultHttpClient();

// Add our parameters to the request method object.
// Thanks for making this difficult HttpClient 4.x.
if (requestParams.size() > 0) {
  UrlEncodedFormEntity formData = new UrlEncodedFormEntity(requestParams, "UTF-8");
  if (isPost) {
    HttpPost postMethod = new HttpPost(url);
    postMethod.setEntity(formData);
    httpMethod = postMethod;
  } else {
    StringBuilder queryString = new StringBuilder();
    for (NameValuePair pair : requestParams) {
      queryString.append(pair.getName() + "="  + pair.getValue());
    }

    url = url + "?" + queryString.toString();
    httpMethod = new HttpGet(url);
  }
}

if (request.getParameter("p_act_as_client") != null) {
  httpMethod.setHeader("user-agent", request.getHeader("user-agent"));
  httpMethod.setHeader("cookie", request.getHeader("cookie"));
}

// Now that all of our preparations are complete, let's
// do this thing.
try {
  HttpResponse httpResponse = httpClient.execute(httpMethod);
  int statusCode = httpResponse.getStatusLine().getStatusCode();

  if (statusCode != HttpStatus.SC_OK) {
    out.print("Method failed: " + httpResponse.getStatusLine());
  }

  // Read in the remote response. It was easier with HttpClient 3.x *sigh*.
  HttpEntity responseMessage = httpResponse.getEntity();
  String responseBody = "";
  if (responseMessage != null) {
    InputStream instream = responseMessage.getContent();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
      String line = reader.readLine();
      while (line != null) {
        responseBody += line;
        line = reader.readLine();
      }
    } catch (IOException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      httpMethod.abort();
      throw ex;
    } finally {
      instream.close();
    }
  }

  if (request.getParameter("p_mangle") != null) {
    // TODO: match href attributes on link elements (i.e. for stylesheets)
    // Re-write any src attributes in the responseBody to use the proxy.
    Pattern pattern = Pattern.compile ("(src=(?:'|\"){0,1})(.+?)((?:'|\"|\\s|>|\\)))");
    Matcher matcher = pattern.matcher(responseBody);

    while (matcher.find()) {
      String match = matcher.group(2);

      if (match.toLowerCase().indexOf("http") == -1) {
        // Looks like a relative URL.
        match = url + match;
      }
      match = encodeURI(match);

      String substitute = request.getRequestURL() + "?p_url=" + match;
      responseBody = responseBody.replace(matcher.group(2), substitute);
    }
  }

  // Send our new output to the script that requested it.
  out.print(responseBody);
} catch (HttpResponseException e) {
  // Something bad happened during the request.
  response.setStatus(500);
  out.print("InternalError: HttpException retrieving " + url);
} catch (IOException e) {
  // Something bad happened during the request.
  response.setStatus(500);
  out.print("InternalError: IOException retreiving " + url);
} finally {
  httpMethod.releaseConnection();
}
%>

<%!
/**
 * Encode an URI to be used as a query string parameter.
 * From -- http://www.dmurph.com/2011/01/java-uri-encoder/
 */
private String encodeURI(String uri) {
  String mark = "-_.!~*'()\"";
  StringBuffer encodedURI = new StringBuffer();

  char[] chars = uri.toCharArray();
  for (int i = 0; i < chars.length; i++) {
    char c = chars[i];
    if ( (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') ||
       (c >= 'A' && c <= 'Z') || (mark.indexOf(c) != -1) ) {
      encodedURI.append(c);
    } else {
      encodedURI.append("%");
      encodedURI.append(Integer.toHexString((int)c));
    }
  }

  return encodedURI.toString();
}
%>