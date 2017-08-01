/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.homelibrary.util.Extensions;
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class WorkspaceUploadServlet.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 21, 2014
 */
public class WorkspaceUploadServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -7861878364437065019L;

	protected static final String UTF_8 = "UTF-8";

	public static final String UPLOAD_TYPE = ConstantsWorkspaceUploader.UPLOAD_TYPE;

	public static final String ID_FOLDER = ConstantsWorkspaceUploader.ID_FOLDER;

	public static final String UPLOAD_FORM_ELEMENT = ConstantsWorkspaceUploader.UPLOAD_FORM_ELEMENT;

	public static final String CLIENT_UPLOAD_KEY = ConstantsWorkspaceUploader.CLIENT_UPLOAD_KEYS;

	public static final String IS_OVERWRITE = ConstantsWorkspaceUploader.IS_OVERWRITE;

	public static final String FILE = "File";

	public static final String D4ST = Extensions.REPORT_TEMPLATE.getName(); //extension of Report Template type
	public static final String D4SR = Extensions.REPORT.getName(); //extension of Report type

	public static Logger logger = LoggerFactory.getLogger(WorkspaceUploadServlet.class);


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
