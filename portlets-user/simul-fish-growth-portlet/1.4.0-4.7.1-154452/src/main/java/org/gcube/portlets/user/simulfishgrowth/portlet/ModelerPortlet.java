package org.gcube.portlets.user.simulfishgrowth.portlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.TextUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.simulfishgrowthdata.model.verify.ModelerVerify;
import org.gcube.data.simulfishgrowthdata.util.AccessPointer;
import org.gcube.data.simulfishgrowthdata.util.CalcKPIAlgorithmExecutor;
import org.gcube.data.simulfishgrowthdata.util.PostMessageExecutor;
import org.gcube.data.simulfishgrowthdata.util.SendMessageExecutor;
import org.gcube.data.simulfishgrowthdata.util.UserFriendlyException;
import org.gcube.portlets.user.simulfishgrowth.model.util.BroodstockQualityUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.FeedQualityUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.ModelerFullUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.ModelerUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.SiteFullUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.SpeciesUtil;
import org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders;
import org.gcube.portlets.user.simulfishgrowth.util.ConnectionUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortalUtil;

import gr.i2s.fishgrowth.model.BroodstockQuality;
import gr.i2s.fishgrowth.model.FeedQuality;
import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.Species;
import gr.i2s.fishgrowth.model.Usage;

/**
 * Portlet implementation class ModelerPortlet
 */
public class ModelerPortlet extends BasePortlet {
	private static Log logger = LogFactoryUtil.getLog(ModelerPortlet.class);

	public static final String CTX_PARAM_SHARED_FOLDER = "SharedFolderName";
	static public final String DF_TIMESTAMP_PATTERN = "yyyyMMdd_hhmmss";

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		String path = getPath(request);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("render path [%s]", path));
		}

		HttpSession httpSession = PortalUtil.getHttpServletRequest(request).getSession();

		try {
			Exception prev = (Exception) (request.getAttribute("errorReason"));
			if (prev != null) {
				throw prev;
			}

			prepareSession(httpSession, request);

			String scope = getScope(httpSession);

			try {
				if (!ConnectionUtils.hasEndpoint()) {
					ConnectionUtils.setEndPoint(getDataAccessEndpoint(scope));
				}
			} catch (Exception e) {
				throw new UserFriendlyException(
						"Could not setup communication. If the problem persists, please contact support.", e);
			}

			AddGCubeHeaders addGCubeHeaders = new AddGCubeHeadersCreator(request).create();

			if (path == null) {
				path = "/html/modeler/view.jsp";
			}
			if (path.endsWith("view.jsp")) {
				if (logger.isTraceEnabled()) {
					logger.trace("Preparing view");
				}

				String ownerId = null; // invalid
				try {
					ServiceContext context = ServiceContextFactory.getInstance(request);
					ownerId = scopeAsOwnerId(scope);
				} catch (SystemException | PortalException e) {
					logger.error(e);
				}
				List<Usage> ownerUsage = new ModelerUtil(addGCubeHeaders).getUsage(ownerId);
				Map<Long, Integer> usages = new HashMap<>();
				for (Usage usage : ownerUsage) {
					usages.put(usage.getId(), usage.getUsage());
				}
				request.setAttribute("usages", usages);
				request.setAttribute("ownerId", ownerId);
				request.setAttribute("addGCubeHeaders", addGCubeHeaders);

			} else if (path.endsWith("edit.jsp")) {
				if (logger.isTraceEnabled()) {
					logger.trace("Preparing edit");
				}

				String ownerId = scopeAsOwnerId(scope);

				List<Species> specieList = null;
				List<Site> siteList = null;
				List<BroodstockQuality> broodstockQualityList = null;
				List<FeedQuality> feedQualityList = null;

				try {
					specieList = new ArrayList<Species>(new SpeciesUtil(addGCubeHeaders).getSpecieses());
					siteList = new ArrayList<Site>(new SiteFullUtil(addGCubeHeaders).getSiteFulls(ownerId));
					broodstockQualityList = new ArrayList<BroodstockQuality>(
							new BroodstockQualityUtil(addGCubeHeaders).getBroodstockQualities());
					feedQualityList = new ArrayList<FeedQuality>(
							new FeedQualityUtil(addGCubeHeaders).getFeedQualities());
				} catch (Exception e) {
					logger.error(e);
				}

				if (specieList == null) {
					specieList = new ArrayList<>();
				}
				request.setAttribute("specieList", specieList);

				if (siteList == null) {
					siteList = new ArrayList<>();
				}
				request.setAttribute("siteList", siteList);

				if (broodstockQualityList == null) {
					broodstockQualityList = new ArrayList<>();
				}
				request.setAttribute("broodstockQualityList", broodstockQualityList);

				if (feedQualityList == null) {
					feedQualityList = new ArrayList<>();
				}
				request.setAttribute("feedQualityList", feedQualityList);
				request.setAttribute("addGCubeHeaders", addGCubeHeaders);

			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Preparing nothing!");
				}

			}

		} catch (Exception e) {
			logger.error("Could not render properly", e);
			request.setAttribute("errorReason", e);
			SessionMessages.clear(request);
			path = "/html/error.jsp";
		}

		PortletContext portletContext = getPortletContext();
		PortletRequestDispatcher portletRequestDispatcher = portletContext.getRequestDispatcher(path);
		if (portletRequestDispatcher == null) {
			logger.error(String.format("[%s] is not a valid path", path));
		} else {
			portletRequestDispatcher.include(request, response);
		}

	}

	public void save(ActionRequest request, ActionResponse response) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("save action");

		if (logger.isTraceEnabled()) {
			logger.trace(String.format("request parameters %s", request.getParameterMap()));
		}

		HttpSession httpSession = PortalUtil.getHttpServletRequest(request).getSession();

		try {

			final String scope = getScope(httpSession);
			final String token = getToken(httpSession);

			final ServiceContext context = ServiceContextFactory.getInstance(request);

			final AddGCubeHeaders addGCubeHeaders = new AddGCubeHeadersCreator(request).create();

			final Long id = ParamUtil.getLong(request, "id");
			final Modeler modeler = (id > 0) ? new ModelerUtil(addGCubeHeaders).getModeler(id) : new Modeler();
			if (id < 0) {
				modeler.setId(id);
				modeler.setStatusId(org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil.STATUS_READY);
			}

			String username = Strings.isNullOrEmpty(debugHomeApiUserName) ? getUsername(httpSession)
					: debugHomeApiUserName;
			// https://support.d4science.org/issues/4869#note-5
			String designation = ParamUtil.getString(request, "designation");
			String comments = ParamUtil.getString(request, "comments");
			Long speciesId = ParamUtil.getLong(request, "speciesId");
			Long siteId = ParamUtil.getLong(request, "siteId");
			Long broodstockQualityId = ParamUtil.getLong(request, "broodstockQualityId");
			Boolean broodstockGeneticImprovement = ParamUtil.get(request, "broodstockGeneticImprovement", false);
			Long feedQualityId = ParamUtil.getLong(request, "feedQualityId");

			// setup model
			if ((modeler.getSiteId() != siteId) || (modeler.getSpeciesId() != speciesId)) {
				modeler.setStatusId(org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil.STATUS_PENDING_KPI);
			}
			try {
				UploadFileDescription fileSaveData;
				UploadFileDescription fileSaveWeights;
				if (Boolean.valueOf(getPortletContext().getInitParameter(DEBUG_BYPASS_FILE_UPLOAD))) {
					logger.warn("Bypassing file upload due to debug");
					if (StringUtils.isEmpty(modeler.getUploadFilenameData())) {
						fileSaveData = new UploadFileDescription("foo", "foo", "foo");
						fileSaveWeights = new UploadFileDescription("bar", "bar", "bar");
					} else {
						fileSaveData = new UploadFileDescription(modeler.getUploadFilenameData(),
								modeler.getUploadFileLocationData(), modeler.getUploadFileTypeData());
						fileSaveWeights = new UploadFileDescription(modeler.getUploadFilenameWeights(),
								modeler.getUploadFileLocationWeights(), modeler.getUploadFileTypeWeights());
					}
				} else {

					WorkspaceFolder uploadFolder = null;

					// manage uploaded files
					UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
					if (uploadFolder == null) {
						String useScope = scope;
						if (debugHomeApiScope != null) {
							useScope = debugHomeApiScope;
							logger.warn(String.format("Debug homeapi scope used [%s]", useScope));
						}

						uploadFolder = getUploadFolder(useScope, getWorkspace(username).getVREFolderByScope(useScope),
								getPortletContext().getInitParameter(CTX_PARAM_SHARED_FOLDER));
					}
					fileSaveData = getAndSaveUploadFile(uploadRequest, uploadFolder, "Data");
					fileSaveWeights = getAndSaveUploadFile(uploadRequest, uploadFolder, "Weights");

					if (fileSaveData != null || fileSaveWeights != null) {
						if (logger.isDebugEnabled()) {
							logger.debug(String.format(
									"uploaded data file (%s) or weights file (%s) so we will clear the old KPIs",
									(fileSaveData != null), (fileSaveWeights != null)));
						}
						modeler.setStatusId(org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil.STATUS_PENDING_KPI);
					}
				}

				if (fileSaveData != null) {
					modeler.setUploadFilenameData(fileSaveData.filename);
					modeler.setUploadFileLocationData(fileSaveData.fileLocation);
					modeler.setUploadFileTypeData(fileSaveData.fileType);
				}
				if (fileSaveWeights != null) {
					modeler.setUploadFilenameWeights(fileSaveWeights.filename);
					modeler.setUploadFileLocationWeights(fileSaveWeights.fileLocation);
					modeler.setUploadFileTypeWeights(fileSaveWeights.fileType);
				}
			} catch (Exception e) {
				if (logger.isTraceEnabled()) {
					logger.trace("Could not save uploaded files", e);
				}

				throw new UserFriendlyException("Could not save uploaded files", e);
			}

			modeler.setDesignation(designation);
			modeler.setComments(comments);
			modeler.setSpeciesId(speciesId);
			modeler.setSiteId(siteId);
			modeler.setBroodstockQualityId(broodstockQualityId);
			modeler.setBroodstockGeneticImprovement(broodstockGeneticImprovement);
			modeler.setFeedQualityId(feedQualityId);
			modeler.setOwnerId(scopeAsOwnerId(scope));

			if (logger.isTraceEnabled()) {
				logger.trace(String.format("retrieved [%s]", modeler));
			}

			new ModelerVerify(modeler).normalise().verify();

			if (logger.isTraceEnabled()) {
				logger.trace(String.format("persisting %s", modeler));
			}

			final ModelerUtil mutil = new ModelerUtil(addGCubeHeaders);
			spawnSaveThread(scope, token, username, modeler, mutil);
		} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("Signaling exception on save", e);
			}
			request.setAttribute("errorReason", e);
		}

	}

	protected void spawnSaveThread(final String scope, final String token, final String username, final Modeler modeler,
			final ModelerUtil mutil) throws Exception, UserFriendlyException {

		// save required info so the user has a proper view on the data
		if (modeler.getId() > 0) {
			mutil.update(modeler, false);
		} else {
			mutil.add(modeler, false);
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("after persist %s", modeler));
		}

		final ExecutorService executor = Executors.newSingleThreadExecutor();

		executor.submit(() -> {

			try {
				try {
					new SendMessageExecutor(getSocialNetworkingEndpoint(scope))
							.setSubject("Calculating [" + modeler.getDesignation() + "]")
							.setBody("Starting KPIs calculations on model [" + modeler.getDesignation()
									+ "]. You will be notified as soon as they are completed")
							.addRecipient(username).setToken(token).execute();
				} catch (Exception sne) {
					// on't let the notification crash the process
					logger.warn("Could not send notification message to the user", sne);

				}

				// force it to perform the calculation
				mutil.update(modeler, true);

				if (modeler.getStatusId() != org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil.STATUS_READY) {
					mutil.cleanKPIs(modeler.getId());
					try {
						String useToken = token;
						if (debugDataMinerToken != null) {
							useToken = debugDataMinerToken;
							logger.warn(String.format("Debug dataminer token used "));
						}
						String useScope = scope;
						if (debugDataMinerScope != null) {
							useScope = debugDataMinerScope;
							logger.warn(String.format("Debug dataminer scope used [%s]", useScope));
						}
						calcKPIs(useScope, useToken, getDBAccessPoint(scope), modeler);
						try {
							String snEndpoint = getSocialNetworkingEndpoint(scope);
							new PostMessageExecutor(snEndpoint)
									.setText("Model [" + modeler.getDesignation() + "] is setup and ready to use!")
									.setEnableNotification(true).setToken(token).execute();
							new SendMessageExecutor(snEndpoint)
									.setSubject("Done calculating model [" + modeler.getDesignation() + "].")
									.setBody("KPIs calculations for model [" + modeler.getDesignation()
											+ "] completed. You may use the model for your analysis.")
									.addRecipient(username).setToken(token).execute();
						} catch (Exception sne) {
							// on't let the notification crash the process
							logger.warn("Could not send notification message to the user", sne);

						}
					} catch (Exception e) {
						logger.warn(String.format("Problem on KPI calculation for simulid [%s].", modeler.getId()), e);
						throw new UserFriendlyException(
								String.format("Problem on KPI calculation for (%s).", modeler.getDesignation()), e);

					}
				}
			} catch (Exception e) {
				logger.info("Problem saving model in separate thread", e);
				try {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					try {
						new SendMessageExecutor(getSocialNetworkingEndpoint(scope))
								.setSubject("Problem calculating model [" + modeler.getDesignation() + "].")
								.setBody("Problem on calculations on model [" + modeler.getDesignation() + "]. "
										+ Joiner.on("\n").skipNulls()
												.join(UserFriendlyException.getFriendlyTraceFrom(e))
										+ "\n\nTechnical info:\n " + errors.toString())
								.addRecipient(username).setToken(token).execute();

					} catch (Exception sne) {
						// on't let the notification crash the process
						logger.warn("Could not send notification message to the user", sne);

					}
				} catch (Exception e1) {
					logger.info("Could not notify user on error", e1);
				}
			}
		});
	}

	public void delete(ActionRequest request, ActionResponse response) throws Exception {
		long id = ParamUtil.getLong(request, "id");
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Deleting modeler [%s]", id));
		}
		try {
			AddGCubeHeaders addGCubeHeaders = new AddGCubeHeadersCreator(request).create();
			new ModelerFullUtil(addGCubeHeaders).delete(id);
		} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("Signaling exception on delete", e);
			}
			request.setAttribute("errorReason", e);
		}
	}

	private void calcKPIs(String scope, String token, AccessPoint accessPoint, Modeler modeler) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("calcKPIs"));
		}

		try {
			if (Boolean.valueOf(getPortletContext().getInitParameter(DEBUG_BYPASS_KPI_CALCULATION))) {
				logger.warn("Bypassing KPI calculation due to debug");
			} else {
				String id = getCalcKPIsIdentifier(scope);
				String endpointName = getPortletContext().getInitParameter(CTX_PARAM_CALC_KPI_ENDPOINT_NAME);

				ScopeProvider.instance.set(scope);

				// TODO this should go
				String endPointUrl = org.gcube.portlets.user.simulfishgrowth.portlet.BasePortlet.debugDataMinerAddress;
				if (endPointUrl != null) {
					logger.warn(String.format("Debug dataminer url used [%s]", endPointUrl));
				} else {
					// as advised at https://support.d4science.org/issues/5532
					String additionaCondition = "$resource/Profile/AccessPoint/Description/text() ne 'GetCapabilities'";
					endPointUrl = new AccessPointer(endpointName).addCondition(additionaCondition).getIt().address();
				}

				new CalcKPIAlgorithmExecutor(endpointName, id, token)
						.setConnectionInfo(accessPoint.name(), accessPoint.username(),
								StringEncrypter.getEncrypter().decrypt(accessPoint.password()), accessPoint.address())
						.setModelId(String.valueOf(modeler.getId())).execute(endPointUrl);
			}
		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.debug("Could not calculate KPIs", e);
			throw new UserFriendlyException("Could not calculate KPIs", e);
		}
	}

	private Workspace getWorkspace(String username) throws Exception {
		HomeManagerFactory factory;
		try {
			factory = HomeLibrary.getHomeManagerFactory();

			// Obtained the factory you can retrieve the HomeManager:
			HomeManager manager = factory.getHomeManager();

			// Then we retrieve the User home:
			User user = manager.createUser(username);
			Home home = manager.getHome(user);

			// At this point we can get the Workspace with his root:
			Workspace ws = home.getWorkspace();

			return ws;
		} catch (HomeNotFoundException | WorkspaceFolderNotFoundException | InternalErrorException e) {
			throw new Exception("Could not get workspace", e);
		}

	}

	private WorkspaceFolder getUploadFolder(String scope, WorkspaceSharedFolder ws, String name) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("getting folder [%s] in scope [%s]", name, scope));
		}
		// create ws folder
		String description = "Uploaded files from Growth Analysis Setup";
		WorkspaceFolder folder = null;

		try {
			if (logger.isTraceEnabled())
				logger.trace(String.format("folder [%s] exists? [%s]", name, ws.exists(name)));
			if (!ws.exists(name)) {
				folder = ws.createFolder(name, description);
			} else {
				folder = (WorkspaceFolder) ws.find(name);
			}
			if (logger.isTraceEnabled())
				logger.trace(String.format("final folder [%s]", folder));
		} catch (Exception e) {
			throw new Exception(String.format("Could not get or create upload folder [%s]", name), e);
		}
		if (logger.isTraceEnabled())
			logger.trace(String.format("final folder [%s]", folder));
		return folder;

	}

	protected String[] splitFilename(String fileName) {
		return fileName.split("\\.(?=[^\\.]+$)");
	}

	private FolderItem doStoreFile(WorkspaceFolder destinationFolder, File uploadedFile, String fileName) {
		FolderItem toRet = null;

		final DateFormat dfTimestamp = new SimpleDateFormat(DF_TIMESTAMP_PATTERN);
		String description = "";
		String mimeType = null;
		try {
			InputStream is = new FileInputStream(uploadedFile);
			// http://stackoverflow.com/a/4546093/874502
			String[] tokens = splitFilename(fileName);
			synchronized (this) {
				fileName = String.format("%s_%s.%s", tokens[0], dfTimestamp.format(new Date()), tokens[1]);
			}
			toRet = WorkspaceUtil.createExternalFile(destinationFolder, fileName, description, mimeType, is);
		} catch (Exception e) {
			logger.error(String.format("Could not upload [%s] to [%s]", fileName, destinationFolder),
					new Exception("Undeclared exception catched", e));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("copied [%s] at [%s]", fileName, destinationFolder));
		}

		return toRet;
	}

	private File doStoreFile(File uploadedFile, String originalFileName) throws IOException {

		String sourceFileName = uploadedFile.getName();

		// Allocate temp space in order to store
		File targetFile = File.createTempFile("gr_i2s_simul_", "_" + originalFileName);

		if (logger.isTraceEnabled()) {
			logger.trace(String.format("I will try to save as [%s]", targetFile.getAbsolutePath()));
		}

		// Move the existing temporary file to new location.
		Files.copy(uploadedFile, targetFile);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("copied [%s] as [%s]", sourceFileName, targetFile));
		}

		return targetFile;

	}

	protected UploadFileDescription getAndSaveUploadFile(UploadPortletRequest uploadRequest,
			WorkspaceFolder uploadFolder, String kind) throws UserFriendlyException, InternalErrorException {

		String action = ParamUtil.getString(uploadRequest.getPortletRequest(),
				String.format("fileUploadAction%s", kind));

		{
			String uploadedFileName = String.format("fileUpload%s", kind);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("uploaded filename [%s]", uploadedFileName));
			}
			String originalFileName = uploadRequest.getFileName(uploadedFileName);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("original filename [%s]", originalFileName));
			}
			File uploadedFile = uploadRequest.getFile(uploadedFileName);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("uploaded file [%s]", uploadedFile));
			}

		}

		if (action.equals("keep")) {
			// keep it that way
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("for upload [%s] the action is [keep] so I do nothing", kind));
			}
			return null;
		}

		// action is "replace"; either add new or replace existing
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("for upload [%s] the action is [replace] so I will try to upload a file", kind));
		}

		// TODO actually delete the existing file, if there is one

		String uploadedFileName = String.format("fileUpload%s", kind);
		String originalFileName = uploadRequest.getFileName(uploadedFileName);
		if (TextUtils.isEmpty(originalFileName)) {
			// keep it that way
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("for upload [%s] there is no file; keeping previous", kind));
			}
			return null;
		}

		// Get the uploaded file as a file.
		File uploadedFile = uploadRequest.getFile(uploadedFileName);

		if (uploadRequest.getSize(uploadedFileName) == 0) {
			logger.warn(String.format("Bypassing 0 bytes file: [%s]", uploadedFile.getName()));
			throw new UserFriendlyException(
					String.format("The file [%s] is empty! This is not valid input.", originalFileName));
		}

		FolderItem savedFile = doStoreFile(uploadFolder, uploadedFile, originalFileName);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("uploaded file with id [%s]", savedFile.getId()));
		}

		return new UploadFileDescription(savedFile.getName(), savedFile.getPublicLink(true),
				splitFilename(savedFile.getName())[1]);
	}

	static class UploadFileDescription {
		public String filename;
		public String fileLocation;
		public String fileType;

		public UploadFileDescription(String filename, String fileLocation, String fileType) {
			this.filename = filename;
			this.fileLocation = fileLocation;
			this.fileType = fileType;
		}

		public void clear() {
			this.filename = "";
			this.fileLocation = "";
			this.fileType = "";
		}

		public boolean isClear() {
			return Strings.isNullOrEmpty(this.filename);
		}
	}

}
