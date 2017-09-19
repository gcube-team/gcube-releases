/**
 *
 */

package org.gcube.datatransfer.resolver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;


/**
 * The Class MultiReadHttpServletRequest.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 26, 2016
 */
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

	private ByteArrayOutputStream cachedBytes;
	private final Map<String, String[]> modifiableParameters;
	private Map<String, String[]> allParameters = null;

	/**
	 * Instantiates a new multi read http servlet request.
	 *
	 * @param request the request
	 */
	public MultiReadHttpServletRequest(HttpServletRequest request) {
		super(request);
		modifiableParameters = new TreeMap<String, String[]>();
	}

	/**
	 * Create a new request wrapper that will merge additional parameters into
	 * the request object without prematurely reading parameters from the
	 * original request.
	 *
	 * @param request
	 *            the request
	 * @param additionalParams
	 *            the additional params
	 */
	public MultiReadHttpServletRequest(
		final HttpServletRequest request,
		final Map<String, String[]> additionalParams) {

		super(request);
		modifiableParameters = new TreeMap<String, String[]>();
		modifiableParameters.putAll(additionalParams);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getInputStream()
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {

		if (cachedBytes == null)
			cacheInputStream();
		return new CachedServletInputStream();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getReader()
	 */
	@Override
	public BufferedReader getReader() throws IOException {

		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	/**
	 * Cache input stream.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void cacheInputStream()
		throws IOException {

		/*
		 * Cache the inputstream in order to read it multiple times. For
		 * convenience, I use apache.commons IOUtils
		 */
		cachedBytes = new ByteArrayOutputStream();
		IOUtils.copy(super.getInputStream(), cachedBytes);
	}

	/* An inputstream which reads the cached request body */
	/**
	 * The Class CachedServletInputStream.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Apr 26, 2016
	 */
	public class CachedServletInputStream extends ServletInputStream {

		private ByteArrayInputStream input;

		/**
		 * Instantiates a new cached servlet input stream.
		 */
		public CachedServletInputStream() {

			/* create a new input stream from the cached request body */
			input = new ByteArrayInputStream(cachedBytes.toByteArray());
		}

		/* (non-Javadoc)
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			return input.read();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(final String name) {

		String[] strings = getParameterMap().get(name);
		if (strings != null) {
			return strings[0];
		}
		return super.getParameter(name);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getParameterMap()
	 */
	@Override
	public Map<String, String[]> getParameterMap() {

		if (allParameters == null) {
			allParameters = new TreeMap<String, String[]>();
			allParameters.putAll(super.getParameterMap());
			allParameters.putAll(modifiableParameters);
		}
		// Return an unmodifiable collection because we need to uphold the
		// interface contract.
		return Collections.unmodifiableMap(allParameters);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getParameterNames()
	 */
	@Override
	public Enumeration<String> getParameterNames() {

		return Collections.enumeration(getParameterMap().keySet());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
	 */
	@Override
	public String[] getParameterValues(final String name) {

		return getParameterMap().get(name);
	}
}
