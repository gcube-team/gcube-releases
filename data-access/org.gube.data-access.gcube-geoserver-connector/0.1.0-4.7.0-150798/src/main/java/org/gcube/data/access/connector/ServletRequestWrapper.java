package org.gcube.data.access.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ServletRequestWrapper extends HttpServletRequestWrapper {

	private Map<String, String> headerMap;
	private Map<String, String> paramsMap;

	public ServletRequestWrapper(HttpServletRequest request) {
		super(request);
		headerMap = new HashMap<String, String>();
		paramsMap = new HashMap<String, String>();
	}

	public void addHeader(String name, String value) {
		headerMap.put(name, new String(value));
	}

	public Enumeration<String> getHeaderNames() {
		HttpServletRequest request = (HttpServletRequest) getRequest();
		List<String> list = new ArrayList<String>();
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
			String header = e.nextElement().toString();
			list.add(header);
		}

		for (Iterator<String> i = headerMap.keySet().iterator(); i.hasNext();) {
			list.add(i.next());
		}
		return Collections.enumeration(list);
	}

	public String getHeader(String name) {
		Object value;
		if ((value = headerMap.get("" + name)) != null) {
			return value.toString();
		} else {
			return ((HttpServletRequest) getRequest()).getHeader(name);
		}
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		Enumeration<String> e = super.getHeaders(name);
		if (e != null && e.hasMoreElements()) {
			return e;
		} else {
			List<String> l = new ArrayList<String>();
			if (headerMap.get(name) != null) {
				l.add(headerMap.get(name));
			}
			return Collections.enumeration(l);
		}
	}


	public void addParameter(String name, String value) {
		paramsMap.put(name, value);
	}

	public String getParameter(String name) {
		// if we added one, return that one
		if (paramsMap.get(name) != null) {
			return paramsMap.get(name);
		}
		// otherwise return what's in the original request
		HttpServletRequest req = (HttpServletRequest) super.getRequest();
		return req.getParameter(name);
	}
}