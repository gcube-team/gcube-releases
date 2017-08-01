package it.eng.edison.usersurvey_portlet.server;


import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.communitymanager.impl.GCubeSiteManagerImpl;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GatewayRolesNames;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

import it.eng.edison.usersurvey_portlet.client.GreetingService;
import it.eng.edison.usersurvey_portlet.client.UserDTO;
import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyUserAnswerModel;
import it.eng.edison.usersurvey_portlet.server.dao.ChoiceanswerJpaController;
import it.eng.edison.usersurvey_portlet.server.dao.ChoicequestionJpaController;
import it.eng.edison.usersurvey_portlet.server.dao.GridanswerJpaController;
import it.eng.edison.usersurvey_portlet.server.dao.GridquestionJpaController;
import it.eng.edison.usersurvey_portlet.server.dao.InvitationtokenJpaController;
import it.eng.edison.usersurvey_portlet.server.dao.SurveyJpaController;
import it.eng.edison.usersurvey_portlet.server.dao.SurveyquestionJpaController;
import it.eng.edison.usersurvey_portlet.server.dao.SurveyuseranswerJpaController;
import it.eng.edison.usersurvey_portlet.server.dao.exceptions.IllegalOrphanException;
import it.eng.edison.usersurvey_portlet.server.dao.exceptions.NonexistentEntityException;
import it.eng.edison.usersurvey_portlet.server.entity.Gridanswer;
import it.eng.edison.usersurvey_portlet.server.entity.Survey;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyquestion;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyuseranswer;
import it.eng.edison.usersurvey_portlet.server.util.CSVUtils;
import it.eng.edison.usersurvey_portlet.server.util.CreateDB;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
GreetingService {
    
	/** The Constant HOST_PROPERTY. 
	/* properties to read
	 */
	private static final String HOST_PROPERTY = "host"; 
	
	/** The Constant MIN_NUM_RANDOM. */
	public final static Integer MIN_NUM_RANDOM = 1000;
    
    /** The Constant MAX_NUM_RANDOM. */
    public final static Integer MAX_NUM_RANDOM = 10000;		
	
	/** The Constant VRE_MANAGER_LABEL. */
    public final static String VRE_MANAGER_LABEL = "VRE-Manager";
    
    /** The Constant VRE_DESIGNER_LABEL. */
    public final static String VRE_DESIGNER_LABEL = "VRE-Designer";
    
    /** The Constant VO_ADMIN_LABEL. */
    public final static String VO_ADMIN_LABEL = "VRE-Designer";
    
    /** The Constant INFRA_MANAGER_LABEL. */
    public final static String INFRA_MANAGER_LABEL = "Infrastructure-Manager";
    
    /** The Constant DATA_MANAGER_LABEL. */
    public final static String DATA_MANAGER_LABEL = "Data-Manager";
    
    /** The runtime resource name. */
    private static String RUNTIME_RESOURCE_NAME = "SurveyDB";
	
    /** The category name. */
    private static String CATEGORY_NAME = "Database";
    
    /** The row label. */
    private static String ROW_LABEL = "Row";
    
    /** The column label. */
    private static String COLUMN_LABEL = "Column";
    
	/** The folder ID. */
	private long folderID = 0;
	
	/** The dl folder. */
	private Folder dlFolder = null;
	
	/** The path image. */
	private String pathImage;
	
	/** The jdbc URL. */
	private  String jdbcURL = null;
	
	/** The local date. */
	private Date localDate = null;
	
	/** The user liferay. */
	private User userLiferay = null;
	
	/** The survey model. */
	private SurveyModel surveyModel = null;
	
	/** The survey model list. */
	private List<SurveyModel> surveyModelList = null;
	
	/** The survey question model. */
	private SurveyQuestionModel surveyQuestionModel = null;
	
	/** The survey question model temp. */
	private SurveyQuestionModel surveyQuestionModelTemp = null;
	
	/** The survey question model list. */
	private List<SurveyQuestionModel> surveyQuestionModelList = null;
	
	/** The choice question list. */
	List<String> choiceQuestionList = null;

	/** The row grid list. */
	List<String> rowGridList = null;
	
	/** The column grid list. */
	List<String> columnGridList = null;
	
	/** The choice answer list. */
	List<String> choiceAnswerList = null;

	/** The grid answer list. */
	List<String> gridAnswerList = null;
	
	/** The gm. */
	private GroupManager gm = null;
	
	/** The service context. */
	private ServiceContext serviceContext = null;
	
	/** The http session. */
	private HttpSession httpSession = null;
	
	/** The username. */
	private String username = null;
	
	/** The asl session. */
	private ASLSession aslSession = null;
	
	/** The scope. */
	private String scope = null;
    
    /** The company id. */
    private int companyId = 0;
    
    /** The group id. */
    private long groupId = 0;
	
    /** The persistence map. */
    private Map<String, String> persistenceMap = null;
    
    /** The manager factory. */
    private EntityManagerFactory managerFactory = null;
	
    /** The dburl. */
    private String DBURL = null;
   
    /** The DB name. */
    private String DBName  = null;
    
    /** The u name. */
    private String uName = null;
    
    /** The pwd. */
    private String pwd = null; 
   
    
     
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() {
    	setDBPropertyConnection ();
    }
    
    
    
    
	/**
	 *  save survey to db
	 *  idUser  user ID
	 *  surveyModel view survey in the client
	 *  idTempFolder image folder
	 *  surveyQuestionModelList List of questions.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param idTempFolder the id temp folder
	 * @param surveyQuestionModelList the survey question model list
	 */
	public void saveAllSurvey(long idUser, SurveyModel surveyModel, long idTempFolder, List<SurveyQuestionModel> surveyQuestionModelList){
        
		GroupManager gm = new LiferayGroupManager();
        String currentUsername = getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
        String scope = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getScope();
        long currGroupId = 0;
        
		try {
			currGroupId = gm.getGroupIdFromInfrastructureScope(scope);
		} catch (IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e1) {
			e1.printStackTrace();
		}
		
		Survey survey = new Survey();
		survey.setTitlesurvey(surveyModel.getTitlesurvey());
		survey.setIdusercreator((int)idUser);
		survey.setDatesurvay(surveyModel.getDateSurvay());
		survey.setExpiredDatesurvay(surveyModel.getExpiredDateSurvay());
		survey.setIsanonymous(surveyModel.getIsAnonymous());
		survey.setGroupid(currGroupId);

		if(surveyModel.getExpiredDateSurvay() != null){
			localDate = setDate2LocalTimezone(surveyModel.getExpiredDateSurvay());
			survey.setExpiredDatesurvay(localDate);
		}
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyJpaController con = new SurveyJpaController(emf);
		try {
			con.create(survey);
		} catch (Exception e) {
			e.printStackTrace();
			emf.close();
		} finally {
			con.getEntityManager().close();
			con = null;
		}

		SurveyquestionJpaController SurveyquestionJpaController = new SurveyquestionJpaController(emf);
		ChoicequestionJpaController conChoice = new ChoicequestionJpaController(emf);
		GridquestionJpaController gridquestionJpaController = new GridquestionJpaController(emf);
		Surveyquestion surveyQuestion = null;
		
		try {
			for(int i=0; i<surveyQuestionModelList.size(); i++){
				   
				    surveyQuestion = new Surveyquestion();
					surveyQuestion.setIdSurvey(survey);
					surveyQuestion.setNumberquestion(surveyQuestionModelList.get(i).getNumberquestion());
					surveyQuestion.setIsmandatory(surveyQuestionModelList.get(i).getIsmandatory());
					surveyQuestion.setQuestiontype(surveyQuestionModelList.get(i).getQuestiontype());
					surveyQuestion.setQuestion(surveyQuestionModelList.get(i).getQuestion());
					surveyQuestion.setImagefilename(surveyQuestionModelList.get(i).getImageFileName());
					surveyQuestion.setAnswer1(surveyQuestionModelList.get(i).getAnswer1());
					surveyQuestion.setAnswer2(surveyQuestionModelList.get(i).getAnswer2());
					surveyQuestion.setAnswer3(surveyQuestionModelList.get(i).getAnswer3());
					surveyQuestion.setAnswer4(surveyQuestionModelList.get(i).getAnswer4());
					surveyQuestion.setAnswer5(surveyQuestionModelList.get(i).getAnswer5());
					surveyQuestion.setAnswer6(surveyQuestionModelList.get(i).getAnswer6());
					surveyQuestion.setAnswer7(surveyQuestionModelList.get(i).getAnswer7());
					surveyQuestion.setAnswer8(surveyQuestionModelList.get(i).getAnswer8());
					surveyQuestion.setAnswer9(surveyQuestionModelList.get(i).getAnswer9());
					surveyQuestion.setAnswer10(surveyQuestionModelList.get(i).getAnswer10());
					
					surveyQuestion.setFolderidimage(surveyQuestionModelList.get(i).getFolderIdImage());
					
					surveyQuestion.setSectiontitle(surveyQuestionModelList.get(i).getSectionTitle());
					surveyQuestion.setSectiondescription(surveyQuestionModelList.get(i).getSectionDescription());
					
					if(surveyQuestionModelList.get(i).getDateAnswer() != null){
						localDate = setDate2LocalTimezone(surveyQuestionModelList.get(i).getDateAnswer());
						surveyQuestion.setDateanswer(localDate);
					}
					SurveyquestionJpaController.create(surveyQuestion);

					if((surveyQuestionModelList.get(i).getQuestiontype().contains("Multiple Choice")) 
							|| (surveyQuestionModelList.get(i).getQuestiontype().contains("CheckBoxes")) 
							|| (surveyQuestionModelList.get(i).getQuestiontype().contains("Drop-Down"))){
						conChoice.insertMultipleChoice(survey, surveyQuestionModelList.get(i));
					}
					
					if(surveyQuestionModelList.get(i).getQuestiontype().contains("Grid")) {
						gridquestionJpaController.insertRowColumn(survey, surveyQuestionModelList.get(i));
					}
			}
			
			    long surveyId = 0;
				SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
				try {
					surveyId = surveyJpaController.findSurveyIdByTitle(surveyModel.getTitlesurvey());
				} catch (Exception e) {
					e.printStackTrace();
					emf.close();
				} finally {
					surveyJpaController.getEntityManager().close();
					surveyJpaController = null;
				}
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SurveyquestionJpaController.getEntityManager().close();
			conChoice.getEntityManager().close();
			gridquestionJpaController.getEntityManager().close();
			emf.close();
			SurveyquestionJpaController = null;
			conChoice = null;
			gridquestionJpaController = null;
			emf = null;
		}
	}
   
   /**
    *  Create a folder in order to insert images.
    *
    * @return the long
    */
	public Long createFolder() {
	        ServiceContext serviceContext = null;
	        ThemeDisplay themeDisplay = (ThemeDisplay) getThreadLocalRequest().getAttribute(WebKeys.THEME_DISPLAY);
	        
		    Random random = new Random();
			int randomNum = random.nextInt(MAX_NUM_RANDOM - MIN_NUM_RANDOM + 1) + MIN_NUM_RANDOM;
		    
			try {
				GroupManager gm = new LiferayGroupManager();
		    	serviceContext = ServiceContextFactory.getInstance(this.getThreadLocalRequest());
				HttpSession httpSession = this.getThreadLocalRequest().getSession();
				String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
				ASLSession aslSession = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
			    String scope = aslSession.getScope();
				companyId = (int) GCubeSiteManagerImpl.getCompany().getCompanyId();
			    long groupId = gm.getGroupIdFromInfrastructureScope(scope);
				userLiferay = UserLocalServiceUtil.getUserByScreenName(companyId, username);
			    long userID = userLiferay.getUserId();
			    dlFolder = DLAppLocalServiceUtil.addFolder( userID,groupId, 0L, "surveyImages_"+String.valueOf(randomNum), "survey folder", serviceContext);
			    dlFolder.getParentFolderId();
		    } catch(Exception e){
			     e.printStackTrace();
			}
		return dlFolder.getFolderId();
		 
	}
	
	/**
	 *  delete an image from repository
	 *  idTmpFolder  id folder
	 *  image2Delete.
	 *
	 * @param idSurvey the id survey
	 * @param idTmpFolder the id tmp folder
	 * @param image2Delete the image 2 delete
	 */
	public void deleteOldImage(int idSurvey, long idTmpFolder, String image2Delete) {
		 
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyquestionJpaController con = new SurveyquestionJpaController(emf);
		
		if (image2Delete != null && !image2Delete.equals("")){
			try {
				
				/* Delete image filename from DB */
				con.deleteImageFilenameFromDB(idSurvey);
				
				FileEntry fileEntry = null;
				GroupManager gm = new LiferayGroupManager();
				ServiceContext serviceContext = ServiceContextFactory.getInstance(this.getThreadLocalRequest());
				HttpSession httpSession = this.getThreadLocalRequest().getSession();
				String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
				ASLSession aslSession = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
				String scope = aslSession.getScope();
				companyId = (int) GCubeSiteManagerImpl.getCompany().getCompanyId();
				long groupId = gm.getGroupIdFromInfrastructureScope(scope);
				fileEntry = DLAppLocalServiceUtil.getFileEntry(
						groupId, idTmpFolder, image2Delete.substring(0,image2Delete.lastIndexOf(".")));
				DLAppLocalServiceUtil.deleteFileEntry(fileEntry.getFileEntryId());



			} catch (SystemException | PortalException | IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e) {
				e.printStackTrace();
			} finally {
				con.getEntityManager().close();
				emf.close();
				con = null;
				emf = null;
			}
		}
	}
	

	/**
	 * Insert survey question.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param idSurvey the id survey
	 */   
	public void insertSurveyQuestion(SurveyQuestionModel surveyQuestionModel, int idSurvey){
		Surveyquestion surveyQuestion = new Surveyquestion();
		Survey survey = new Survey();
		survey.setId(idSurvey);
		surveyQuestion.setIdSurvey(survey);

		surveyQuestion.setNumberquestion(surveyQuestionModel.getNumberquestion());
		surveyQuestion.setIsmandatory(surveyQuestionModel.getIsmandatory());
		surveyQuestion.setQuestiontype(surveyQuestionModel.getQuestiontype());
		surveyQuestion.setQuestion(surveyQuestionModel.getQuestion());
		surveyQuestion.setAnswer1(surveyQuestionModel.getAnswer1());
		surveyQuestion.setAnswer2(surveyQuestionModel.getAnswer2());
		surveyQuestion.setAnswer3(surveyQuestionModel.getAnswer3());
		surveyQuestion.setAnswer4(surveyQuestionModel.getAnswer4());
		surveyQuestion.setAnswer5(surveyQuestionModel.getAnswer5());
		surveyQuestion.setAnswer6(surveyQuestionModel.getAnswer6());
		surveyQuestion.setAnswer7(surveyQuestionModel.getAnswer7());
		surveyQuestion.setAnswer8(surveyQuestionModel.getAnswer8());
		surveyQuestion.setAnswer9(surveyQuestionModel.getAnswer9());
		surveyQuestion.setAnswer10(surveyQuestionModel.getAnswer10());
		surveyQuestion.setDateanswer(surveyQuestionModel.getDateAnswer());
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyquestionJpaController con = new SurveyquestionJpaController(emf);
		try {
			con.create(surveyQuestion);
			con.getEntityManager().getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.getEntityManager().close();
			emf.close();
			con = null;
			emf = null;
		}
	}


	/**
	 * Insert survey.
	 *
	 * @param surveyModel the survey model
	 */
	public void insertSurvey(SurveyModel surveyModel){

		Survey survey = new Survey();
		survey.setTitlesurvey(surveyModel.getTitlesurvey());
		survey.setIdusercreator(surveyModel.getIdUserCreator());
		survey.setDatesurvay(surveyModel.getDateSurvay());
		survey.setExpiredDatesurvay(surveyModel.getExpiredDateSurvay());
		survey.setIsanonymous(surveyModel.getIsAnonymous());
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyJpaController con = new SurveyJpaController(emf);
		try {
			con.create(survey);
			con.getEntityManager().getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.getEntityManager().close();
			emf.close();
			con = null;
			emf = null;
		}

	}


	/**
	 * Gets the user answered survey.
	 *
	 * @param idSurveySelected the id survey selected
	 * @return the user answered survey
	 */
	public List<UserDTO> getUserAnsweredSurvey(int idSurveySelected){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyJpaController surveyJpaController = new SurveyJpaController(emf); 
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		GroupManager gm = new LiferayGroupManager();
        String currentUsername = getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
        String scope = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getScope();
        long currGroupId = 0;
			
		boolean isAnonymousSurvey = false;
		isAnonymousSurvey = surveyJpaController.checkSurveyIsAnonymous(idSurveySelected);
		
		InvitationtokenJpaController con = new InvitationtokenJpaController(emf); 

		List<Integer> idUserAnsweredList = new ArrayList<>();
		idUserAnsweredList = con.getListUserAnsweredSurvey(idSurveySelected);
		User currentUser = null; 
		UserDTO userDTO = null;
		List<UserDTO> userDTOList = new ArrayList<>();
		for(int i=0; i<idUserAnsweredList.size(); i++){
			try {
				currGroupId = gm.getGroupIdFromInfrastructureScope(scope);
				userDTO = new UserDTO();
				
				if(isAnonymousSurvey){
					userDTO.setUserId(idUserAnsweredList.get(i));
					userDTO.setFullName("Guest");
				} else {
					currentUser = UserLocalServiceUtil.getUserById(idUserAnsweredList.get(i));
					userDTO.setUserId(currentUser.getUserId());
					userDTO.setFullName(currentUser.getFullName());
				}

				userDTO.setContactId((int) userLiferay.getContactId());
				userDTO.setGroupId(currGroupId);
				userDTO.setEmailAddress(userLiferay.getEmailAddress());
				userDTO.setScreenName(userLiferay.getScreenName());
				userDTO.setRolesId(userLiferay.getRoleIds());
				

				userDTOList.add(userDTO);
			} catch (PortalException | SystemException | IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e) {
				e.printStackTrace();
				con.getEntityManager().close();
				emf.close();
				con = null;
				emf = null;
			}

		}
		con.getEntityManager().close();
		emf.close();
		con = null;
		emf = null;  
		
		return userDTOList;
	}
	

	/**
	 * Users answered survey.
	 *
	 * @return the list
	 */
	public List<Integer> usersAnsweredSurvey(){
		InvitationtokenJpaController con = null;
		List<Integer> idUserAnsweredList = null;
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		try {
		  con = new InvitationtokenJpaController(emf); 
		  idUserAnsweredList = new ArrayList<>();
		  idUserAnsweredList = con.getUserAnsweredSurveyCount();
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.getEntityManager().close();
			emf.close();
			con = null;
			emf = null;
		}
		  return idUserAnsweredList;
 	}


	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public UserDTO getUser(){
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		UserDTO userDTO = new UserDTO();
		
		GroupManager gm = new LiferayGroupManager();
        String currentUsername = getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
        String scope = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getScope();
        long currGroupId = 0;
			
		try {
			currGroupId = gm.getGroupIdFromInfrastructureScope(scope);
			String username = null;
			companyId = (int) GCubeSiteManagerImpl.getCompany().getCompanyId();
			if ( (httpSession != null) && httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE) == null){ 
				userDTO.setUserId(0);
				userDTO.setContactId(0);
				userDTO.setGroupId(currGroupId);
				userDTO.setEmailAddress(null);
				userDTO.setFullName("Anonymous");
				userDTO.setScreenName("Anonymous");
			} else {
				username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
				userLiferay = UserLocalServiceUtil.getUserByScreenName(companyId, username);
				userDTO.setUserId((int) userLiferay.getUserId());
				userDTO.setContactId((int) userLiferay.getContactId());
				userDTO.setGroupId(currGroupId);
				userDTO.setEmailAddress(userLiferay.getEmailAddress());
				userDTO.setFullName(userLiferay.getFullName());
				userDTO.setScreenName(userLiferay.getScreenName());
				userDTO.setRolesId(userLiferay.getRoleIds());
			}
			
			RoleManager roleManager = new LiferayRoleManager();
			List<GCubeRole> roles = new ArrayList<>();
			for(int i=0; i<userLiferay.getRoleIds().length; i++){
				try {
					roles.add(roleManager.getRole(userLiferay.getRoleIds()[i]));
				} catch (UserManagementSystemException | RoleRetrievalFault e) {
					e.printStackTrace();
				}
			}
			
			if(roleManager.isAdmin(userDTO.getUserId())){
				userDTO.setAdminUser(Boolean.TRUE);
			}
			
			if(isCurrentUserVREManager()){
				userDTO.setVreManager(Boolean.TRUE);
			}
			
			for(int i=0; i<roles.size(); i++){
				if(roles.get(i).getRoleName() == VRE_MANAGER_LABEL ||
						roles.get(i).getRoleName() == INFRA_MANAGER_LABEL ||
						roles.get(i).getRoleName() == DATA_MANAGER_LABEL || 
						roles.get(i).getRoleName() == VRE_DESIGNER_LABEL ||
						roles.get(i).getRoleName() == VO_ADMIN_LABEL){
					
					userDTO.setManageSurveyUser(Boolean.TRUE);
					
				}
			}
		} catch (PortalException | SystemException | UserRetrievalFault | IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return userDTO;
	}
	
	/**
	 * Checks if is current user VRE manager.
	 *
	 * @return true, if is current user VRE manager
	 */
	private boolean isCurrentUserVREManager() {
		GroupManager gm = new LiferayGroupManager();
        String currentUsername = getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
        String scope = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getScope();
        long currGroupId = 0;
		try {
			currGroupId = gm.getGroupIdFromInfrastructureScope(scope);
		} catch (IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}

        try {
            Map<GCubeUser, List<GCubeRole>> usersAndRolesInVRE = new LiferayUserManager().listUsersAndRolesByGroup(currGroupId);        
            for (GCubeUser gCubeUser : usersAndRolesInVRE.keySet()) {
                if (gCubeUser.getUsername().compareTo(currentUsername) == 0) {
                    for (GCubeRole gCubeRole : usersAndRolesInVRE.get(gCubeUser)) {
                        if (gCubeRole.getRoleName().compareTo(GatewayRolesNames.VRE_MANAGER.getRoleName()) == 0)
                            return true;
                    }
                }
            }
        } catch (GroupRetrievalFault | UserManagementSystemException | UserRetrievalFault e1) {
            e1.printStackTrace();
        }
        return false;
    }



	/**
	 * Gets the survey.
	 *
	 * @param idSurvey the id survey
	 * @return the survey
	 */
	public SurveyModel getSurvey(int idSurvey){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
		Survey survey = surveyJpaController.findSurvey(idSurvey);

		surveyModel = new SurveyModel();
		surveyModel.setIdsurvey(survey.getId());
		surveyModel.setTitlesurvey(survey.getTitlesurvey());
		surveyModel.setIdUserCreator(survey.getIdusercreator());
		surveyModel.setGroupId(survey.getGroupid());
		surveyModel.setDateSurvay(survey.getDatesurvay());
		surveyModel.setExpiredDateSurvay(survey.getExpireddatesurvay());
		surveyModel.setIsAnonymous(survey.getIsanonymous());

		surveyJpaController.getEntityManager().close();
		emf.close();
		surveyJpaController = null;
		emf = null;
		return surveyModel;
	}


	/**
	 * Gets the survey list.
	 *
	 * @param userDTO the user DTO
	 * @return the survey list
	 */
	public List<SurveyModel> getSurveyList(UserDTO userDTO){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
		List<Survey> surveyList = new ArrayList<>();
		
		if(userDTO.isAdminUser() || userDTO.isVreManager()){
			surveyList = surveyJpaController.findAllSurveys(userDTO.getGroupId());
		} else {
			surveyList = surveyJpaController.findSurveysByUser((int)userDTO.getUserId(), userDTO.getGroupId());
		}

		surveyModel = new SurveyModel();
		surveyModelList = new ArrayList<SurveyModel>();

		
		for(int i = 0; i < surveyList.size(); i++){
			surveyModel.setIdsurvey(surveyList.get(i).getId());
			surveyModel.setTitlesurvey(surveyList.get(i).getTitlesurvey());
			surveyModel.setIdUserCreator(surveyList.get(i).getIdusercreator());
			surveyModel.setGroupId(surveyList.get(i).getGroupid());
			surveyModel.setDateSurvay(surveyList.get(i).getDatesurvay());
			surveyModel.setExpiredDateSurvay(surveyList.get(i).getExpireddatesurvay());
			surveyModel.setIsAnonymous(surveyList.get(i).getIsanonymous());
			
			surveyModel.setCreatorFullname(getCreatorSurveyName(surveyList.get(i).getIdusercreator()));
			
			surveyModelList.add(surveyModel);
			surveyModel = new SurveyModel();
		}
		surveyJpaController.getEntityManager().close();
		emf.close();
		surveyJpaController = null;
		emf = null;
		return surveyModelList;
	}

	/**
	 * Gets the creator survey name.
	 *
	 * @param idusercreator the idusercreator
	 * @return the creator survey name
	 */
	private String getCreatorSurveyName(int idusercreator) {
		String fullnameSurveyCreator = null;
		
		try {
			userLiferay = UserLocalServiceUtil.getUserById(idusercreator);
			fullnameSurveyCreator = userLiferay.getFullName();
		} catch (PortalException | SystemException e) {
			e.printStackTrace();
		}

		return fullnameSurveyCreator;
	}
    


	/**
	 * Delete survey.
	 *
	 * @param surveyModel the survey model
	 */
	public void deleteSurvey(SurveyModel surveyModel) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyquestionJpaController surveyquestionJpaController = new SurveyquestionJpaController(emf);
		SurveyuseranswerJpaController surveyuseranswerJpaController = new SurveyuseranswerJpaController(emf);
		ChoicequestionJpaController choicequestionJpaController = new ChoicequestionJpaController(emf);
		ChoiceanswerJpaController choiceanswerJpaController = new ChoiceanswerJpaController(emf);
		SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
		InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
		GridquestionJpaController gridquestionJpaController = new GridquestionJpaController(emf);
		GridanswerJpaController gridanswerJpaController = new GridanswerJpaController(emf);
		
		long folderId = surveyquestionJpaController.findFolderIdByIdSurvey(surveyModel.getIdsurvey());

		
		try {
			
			DLAppLocalServiceUtil.deleteFolder(folderId);
			
			choicequestionJpaController.deleteQuestions(surveyModel.getIdsurvey());
			surveyquestionJpaController.deleteQuestions(surveyModel.getIdsurvey());
			choiceanswerJpaController.deleteQuestions(surveyModel.getIdsurvey());
			surveyuseranswerJpaController.deleteQuestions(surveyModel.getIdsurvey());
			surveyJpaController.destroy(surveyModel.getIdsurvey());
			invitationtokenJpaController.destroy(surveyModel.getIdsurvey());
			gridquestionJpaController.deleteQuestions(surveyModel.getIdsurvey());
			gridanswerJpaController.deleteQuestions(surveyModel.getIdsurvey());
			
		} catch (IllegalOrphanException | NonexistentEntityException | PortalException | SystemException e) {
			e.printStackTrace();
		} finally {
			choicequestionJpaController.getEntityManager().close();
			surveyquestionJpaController.getEntityManager().close();
			choiceanswerJpaController.getEntityManager().close();
			surveyuseranswerJpaController.getEntityManager().close();
			surveyJpaController.getEntityManager().close();
			invitationtokenJpaController.getEntityManager().close();
			gridquestionJpaController.getEntityManager().close();
			gridanswerJpaController.getEntityManager().close();
			emf.close();
			
			choicequestionJpaController = null;
			surveyquestionJpaController = null;
			choiceanswerJpaController = null;
			surveyuseranswerJpaController = null;
			surveyJpaController = null;
			invitationtokenJpaController = null;
			gridquestionJpaController = null;
			gridanswerJpaController = null;
			emf = null;
		}
	}

	/**
	 * Update survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param surveyQuestionModelList the survey question model list
	 */
	public void updateSurvey(long idUser, SurveyModel surveyModel, List<SurveyQuestionModel> surveyQuestionModelList) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		try{
			/* Delete Survey Questions*/
			SurveyquestionJpaController surveyquestionJpaController = new SurveyquestionJpaController(emf);
			surveyquestionJpaController.deleteQuestions(surveyModel.getIdsurvey());
			surveyquestionJpaController.getEntityManager().close();

			if(surveyModel.getExpiredDateSurvay() != null){
				localDate = setDate2LocalTimezone(surveyModel.getExpiredDateSurvay());
				surveyModel.setExpiredDateSurvay(localDate);
			}
			
			/* Delete Survey*/
			SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
			try {
				surveyJpaController.updateSurvey(surveyModel);
			} catch (Exception e) {
				e.printStackTrace();
				emf.close();
			} finally {
				surveyJpaController.getEntityManager().close();
			}	

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		SurveyquestionJpaController con1 = new SurveyquestionJpaController(emf);
		ChoicequestionJpaController conChoice = new ChoicequestionJpaController(emf);
		conChoice.deleteQuestions(surveyModel.getIdsurvey());
		
		GridquestionJpaController gridquestionJpaController = new GridquestionJpaController(emf);
		gridquestionJpaController.deleteQuestions(surveyModel.getIdsurvey());
		
		
		Surveyquestion surveyQuestion = null;
		Survey survey = new Survey(surveyModel.getIdsurvey());
		try {
			for(int i=0; i<surveyQuestionModelList.size(); i++){
				surveyQuestion = new Surveyquestion();
				surveyQuestion.setIdSurvey(survey);
				surveyQuestion.setNumberquestion(surveyQuestionModelList.get(i).getNumberquestion());
				surveyQuestion.setIsmandatory(surveyQuestionModelList.get(i).getIsmandatory());
				surveyQuestion.setQuestiontype(surveyQuestionModelList.get(i).getQuestiontype());
				surveyQuestion.setQuestion(surveyQuestionModelList.get(i).getQuestion());
				surveyQuestion.setImagefilename(surveyQuestionModelList.get(i).getImageFileName());
				surveyQuestion.setFolderidimage(surveyQuestionModelList.get(i).getFolderIdImage());
				surveyQuestion.setAnswer1(surveyQuestionModelList.get(i).getAnswer1());
				surveyQuestion.setAnswer2(surveyQuestionModelList.get(i).getAnswer2());
				surveyQuestion.setAnswer3(surveyQuestionModelList.get(i).getAnswer3());
				surveyQuestion.setAnswer4(surveyQuestionModelList.get(i).getAnswer4());
				surveyQuestion.setAnswer5(surveyQuestionModelList.get(i).getAnswer5());
				surveyQuestion.setAnswer6(surveyQuestionModelList.get(i).getAnswer6());
				surveyQuestion.setAnswer7(surveyQuestionModelList.get(i).getAnswer7());
				surveyQuestion.setAnswer8(surveyQuestionModelList.get(i).getAnswer8());
				surveyQuestion.setAnswer9(surveyQuestionModelList.get(i).getAnswer9());
				surveyQuestion.setAnswer10(surveyQuestionModelList.get(i).getAnswer10());
				
				surveyQuestion.setSectiontitle(surveyQuestionModelList.get(i).getSectionTitle());
				surveyQuestion.setSectiondescription(surveyQuestionModelList.get(i).getSectionDescription());

				if(surveyQuestionModelList.get(i).getDateAnswer() != null){
					localDate = setDate2LocalTimezone(surveyQuestionModelList.get(i).getDateAnswer());
					surveyQuestion.setDateanswer(localDate);
				}
				con1.create(surveyQuestion);
				
				if((surveyQuestionModelList.get(i).getQuestiontype().contains("Multiple Choice")) 
						|| (surveyQuestionModelList.get(i).getQuestiontype().contains("CheckBoxes")) 
						|| (surveyQuestionModelList.get(i).getQuestiontype().contains("Drop-Down"))){
					conChoice.insertMultipleChoice(survey, surveyQuestionModelList.get(i));
				}
				
				if(surveyQuestionModelList.get(i).getQuestiontype().contains("Grid")) {
					gridquestionJpaController.insertRowColumn(survey, surveyQuestionModelList.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con1.getEntityManager().close();
			conChoice.getEntityManager().close();
			gridquestionJpaController.getEntityManager().close();
			emf.close();
			
			con1 = null;
			conChoice = null;
			gridquestionJpaController = null;
			emf = null;
		}
	}



	/**
	 * Gets the questions survey.
	 *
	 * @param idSurvey the id survey
	 * @return the questions survey
	 */
	public List<SurveyQuestionModel> getQuestionsSurvey(int idSurvey){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyquestionJpaController surveyQuestionJpaController = new SurveyquestionJpaController(emf);
		List<Surveyquestion> surveyQuestionList = surveyQuestionJpaController.findSurveyQuestionByIdSurvey(idSurvey);
		
		ChoicequestionJpaController choicequestionJpaController = new ChoicequestionJpaController(emf);
		GridquestionJpaController gridquestionJpaController = new GridquestionJpaController(emf);
				
		surveyQuestionModelList = new ArrayList<SurveyQuestionModel>();
		for(int i = 0; i < surveyQuestionList.size(); i++){
				surveyQuestionModel = new SurveyQuestionModel();
				surveyQuestionModel.setIdsurvey(idSurvey);
				surveyQuestionModel.setNumberquestion(surveyQuestionList.get(i).getNumberquestion());
				surveyQuestionModel.setIsmandatory(surveyQuestionList.get(i).getIsmandatory());
				surveyQuestionModel.setQuestiontype(surveyQuestionList.get(i).getQuestiontype());
				surveyQuestionModel.setQuestion(surveyQuestionList.get(i).getQuestion());
				surveyQuestionModel.setImageFileName(surveyQuestionList.get(i).getImagefilename());
				surveyQuestionModel.setFolderIdImage(surveyQuestionList.get(i).getFolderidimage());
				surveyQuestionModel.setAnswer1(surveyQuestionList.get(i).getAnswer1());
				surveyQuestionModel.setAnswer2(surveyQuestionList.get(i).getAnswer2());
				surveyQuestionModel.setAnswer3(surveyQuestionList.get(i).getAnswer3());
				surveyQuestionModel.setAnswer4(surveyQuestionList.get(i).getAnswer4());
				surveyQuestionModel.setAnswer5(surveyQuestionList.get(i).getAnswer5());
				surveyQuestionModel.setAnswer6(surveyQuestionList.get(i).getAnswer6());
				surveyQuestionModel.setAnswer7(surveyQuestionList.get(i).getAnswer7());
				surveyQuestionModel.setAnswer8(surveyQuestionList.get(i).getAnswer8());
				surveyQuestionModel.setAnswer9(surveyQuestionList.get(i).getAnswer9());
				surveyQuestionModel.setAnswer10(surveyQuestionList.get(i).getAnswer10());
				surveyQuestionModel.setDateAnswer(surveyQuestionList.get(i).getDateanswer());
				
				surveyQuestionModel.setSectionTitle(surveyQuestionList.get(i).getSectiontitle());
				surveyQuestionModel.setSectionDescription(surveyQuestionList.get(i).getSectiondescription());
				
				if(((surveyQuestionList.get(i).getQuestiontype().contains("Multiple Choice")) 
						|| (surveyQuestionList.get(i).getQuestiontype().contains("CheckBoxes")) 
						|| (surveyQuestionList.get(i).getQuestiontype().contains("Drop-Down")))){
					choiceQuestionList = choicequestionJpaController.findSurveyQuestionChoiceByIdSurveyAndNumberQuestion(idSurvey, surveyQuestionList.get(i).getNumberquestion());
					surveyQuestionModel.setMultipleChoiceList(choiceQuestionList);
				}
				
				if(surveyQuestionList.get(i).getQuestiontype().contains("Grid")) {
					rowGridList = gridquestionJpaController.findSurveyQuestionRowColumnGridByIdSurveyAndNumberQuestion(idSurvey, surveyQuestionList.get(i).getNumberquestion(), ROW_LABEL);
					columnGridList = gridquestionJpaController.findSurveyQuestionRowColumnGridByIdSurveyAndNumberQuestion(idSurvey, surveyQuestionList.get(i).getNumberquestion(), COLUMN_LABEL);
					if(rowGridList != null
							&& !rowGridList.isEmpty()
							&& columnGridList != null
							&& !columnGridList.isEmpty()){
						surveyQuestionModel.setRowGridList(rowGridList);
						surveyQuestionModel.setColumnGridList(columnGridList);
					}
				}
				
				surveyQuestionModelList.add(surveyQuestionModel);
		}
		
		surveyQuestionJpaController.getEntityManager().close();
		choicequestionJpaController.getEntityManager().close();
		gridquestionJpaController.getEntityManager().close();
		emf.close();
		
		surveyQuestionJpaController = null;
		choicequestionJpaController = null;
		gridquestionJpaController = null;
		emf = null;
		
		return surveyQuestionModelList;
	}
	
	


	/**
	 * Gets the answers survey.
	 *
	 * @param idSurvey the id survey
	 * @return the answers survey
	 */
	public List<SurveyUserAnswerModel> getAnswersSurvey(int idSurvey){

		Survey survey = new Survey();
		survey.setId(idSurvey);

		SurveyUserAnswerModel surveyUserAnswerModel;
		List<SurveyUserAnswerModel> surveyUserAnswerModelList;
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibManager", persistenceMap);
		SurveyuseranswerJpaController surveyUserAnswerJpaController = new SurveyuseranswerJpaController(emf);
		List<Surveyuseranswer> surveyUserAnswerList = surveyUserAnswerJpaController.findAnswersSurveyById(idSurvey);

		SurveyquestionJpaController surveyQuestionJpaController = new SurveyquestionJpaController(emf);
		ChoiceanswerJpaController conChoiceAns = new ChoiceanswerJpaController(emf);
		GridanswerJpaController gridanswerJpaController = new GridanswerJpaController(emf);
		List<Surveyquestion> surveyQuestionList = surveyQuestionJpaController.findSurveyQuestionByIdSurvey(idSurvey);

		surveyUserAnswerModel = new SurveyUserAnswerModel();
		surveyUserAnswerModelList = new ArrayList<>();

		for(int i=0; i<surveyUserAnswerList.size(); i++){

			surveyUserAnswerModel.setIduseranswer(surveyUserAnswerList.get(i).getIduseranswer());
			surveyUserAnswerModel.setIdsurvey(idSurvey);
			surveyUserAnswerModel.setQuestiontype(surveyUserAnswerList.get(i).getQuestiontype());
			surveyUserAnswerModel.setNumberquestion(surveyUserAnswerList.get(i).getNumberquestion());
			surveyUserAnswerModel.setAnswer1(surveyUserAnswerList.get(i).getAnswer1());
			surveyUserAnswerModel.setAnswer2(surveyUserAnswerList.get(i).getAnswer2());
			surveyUserAnswerModel.setAnswer3(surveyUserAnswerList.get(i).getAnswer3());
			surveyUserAnswerModel.setAnswer4(surveyUserAnswerList.get(i).getAnswer4());
			surveyUserAnswerModel.setAnswer5(surveyUserAnswerList.get(i).getAnswer5());
			surveyUserAnswerModel.setAnswer6(surveyUserAnswerList.get(i).getAnswer6());
			surveyUserAnswerModel.setAnswer7(surveyUserAnswerList.get(i).getAnswer7());
			surveyUserAnswerModel.setAnswer8(surveyUserAnswerList.get(i).getAnswer8());
			surveyUserAnswerModel.setAnswer9(surveyUserAnswerList.get(i).getAnswer9());
			surveyUserAnswerModel.setAnswer10(surveyUserAnswerList.get(i).getAnswer10());
			surveyUserAnswerModel.setDateAnswer(surveyUserAnswerList.get(i).getDateanswer());

			for(int j = 0; j < surveyQuestionList.size(); j++){
				if(surveyUserAnswerList.get(i).getNumberquestion() == surveyQuestionList.get(j).getNumberquestion()){
					surveyUserAnswerModel.setQuestion(surveyQuestionList.get(j).getQuestion());
				}
			}
			
			if((surveyUserAnswerList.get(i).getQuestiontype() != null)){
				if((surveyUserAnswerList.get(i).getQuestiontype().contains("CheckBoxes"))){
					choiceAnswerList = conChoiceAns.findSurveyChoiceAnswerByIdSurveyAndNumberQuestion(idSurvey, surveyUserAnswerList.get(i).getIduseranswer(), surveyUserAnswerList.get(i).getNumberquestion());
					surveyUserAnswerModel.setMultipleChoiceList(choiceAnswerList);
				}
				
				if((surveyUserAnswerList.get(i).getQuestiontype().contains("Grid"))){
					gridAnswerList = gridanswerJpaController.findGridAnswersByIdSurveyAndNumberQuestion(idSurvey, surveyUserAnswerList.get(i).getIduseranswer(), surveyUserAnswerList.get(i).getNumberquestion());
					surveyUserAnswerModel.setGridAnswerList(gridAnswerList);
				}
			}

			surveyUserAnswerModelList.add(surveyUserAnswerModel);
			surveyUserAnswerModel = new SurveyUserAnswerModel();
		}

		conChoiceAns.getEntityManager().close();
		gridanswerJpaController.getEntityManager().close();
		surveyQuestionJpaController.getEntityManager().close();
		surveyUserAnswerJpaController.getEntityManager().close();
		emf.close();
		
		conChoiceAns = null;
		gridanswerJpaController = null;
		surveyQuestionJpaController = null;
		surveyUserAnswerJpaController = null;
		emf = null;
		
		return surveyUserAnswerModelList;
	}
	

	/**
	 * Check Database is created.
	 */
	public void checkDBisCreated(){
		
	}

	/**
	 * String to date.
	 *
	 * @param answer10 the answer 10
	 * @return the date
	 */
	private Date StringToDate(String answer10) {
		Date dateConverted = null;
		DateFormat dateFormat;

		if(answer10 != null){
			try {
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				dateConverted = dateFormat.parse(answer10);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateConverted;
	}
	
	/**
	 * Date to string.
	 *
	 * @param surveyDate the survey date
	 * @return the string
	 */
	private String dateToString(Date surveyDate){
		String dateConverted = null;
		DateFormat dateFormat;
		if(surveyDate != null){
			surveyDate = setDate2LocalTimezone(surveyDate);
			dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			dateConverted = dateFormat.format(surveyDate);
		}
		return dateConverted;
	}

	/**
	 * Sets the date 2 local timezone.
	 *
	 * @param surveyDate the survey date
	 * @return the date
	 */
	private Date setDate2LocalTimezone(Date surveyDate) {
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
		now.setTime(surveyDate);
		now.set(Calendar.HOUR_OF_DAY, 6);
		return now.getTime();
	}
	
	/**
	 * Export to CSV file.
	 *
	 * @param titleSurvey the title survey
	 * @param isAnonymous the is anonymous
	 * @param userDTOList the user DTO list
	 * @param surveyUserAnswerModelList the survey user answer model list
	 * @param surveyQuestionModelList the survey question model list
	 */
	public void exportToCSVFile(String titleSurvey, boolean isAnonymous,  List<UserDTO> userDTOList, List<SurveyUserAnswerModel> surveyUserAnswerModelList, List<SurveyQuestionModel> surveyQuestionModelList){
		
		String realPath = getThreadLocalRequest().getSession().getServletContext().getRealPath("/");
		File theDir = new File(realPath + "/SurveyStatistics");
		
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    boolean result = false;

		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		    	se.printStackTrace();
		    }        
		}
		
		String csvFileName = "/"+titleSurvey+"_Statistics.csv";
		String csvFile = theDir.getPath() + csvFileName;
		String user = null;
		FileWriter writer = null;
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFileFormat = null;
		try {
			
			File csvFileToExport = new File(csvFile);
			csvFileToExport.createNewFile();
			
			  writer = new FileWriter(csvFileToExport);
			//CSVUtils.writeLine(writer, Arrays.asList("Title", "User", "Question", "Answers"));
			final Object [] FILE_HEADER = {"Title", "User", "Question", "Answers"};
			csvFilePrinter = null;
			csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
		
			csvFilePrinter = new CSVPrinter(writer, csvFileFormat);
			csvFilePrinter.printRecord(FILE_HEADER);

			int gridrowIndex = 0;
			
			for(int i=0; i<surveyUserAnswerModelList.size(); i++){
				for(int j=0; j<userDTOList.size(); j++){
					if(userDTOList.get(j).getUserId() == surveyUserAnswerModelList.get(i).getIduseranswer()){
						if(isAnonymous){
							user = "Guest";
						} else {
							user = userDTOList.get(j).getFullName();
						}
					}
				}
				
				List<String> list = new ArrayList<>();
				list.add(titleSurvey);
				list.add(user);
				
				if(surveyUserAnswerModelList.get(i).getQuestion() == null){
					return;
				} else {
					list.add(surveyUserAnswerModelList.get(i).getQuestion());
				}
				
				if(surveyUserAnswerModelList.get(i).getQuestiontype().equalsIgnoreCase("Time")){
					list.add(surveyUserAnswerModelList.get(i).getAnswer1() + ":" + surveyUserAnswerModelList.get(i).getAnswer2());
				} else if(surveyUserAnswerModelList.get(i).getQuestiontype().equalsIgnoreCase("Date")){
					list.add(dateToString(surveyUserAnswerModelList.get(i).getDateAnswer()));
				} else if(surveyUserAnswerModelList.get(i).getQuestiontype().equalsIgnoreCase("Multiple Choice")){
					if(surveyUserAnswerModelList.get(i).getAnswer1() != null && surveyUserAnswerModelList.get(i).getAnswer1().contains("Other...")){
						list.add(surveyUserAnswerModelList.get(i).getAnswer1() + ": " + surveyUserAnswerModelList.get(i).getAnswer2());
					} else if (surveyUserAnswerModelList.get(i).getAnswer1() != null){
						list.add(surveyUserAnswerModelList.get(i).getAnswer1());
					}
				} else if(surveyUserAnswerModelList.get(i).getQuestiontype().equalsIgnoreCase("CheckBoxes")){
					for(int j = 0; j < surveyUserAnswerModelList.get(i).getMultipleChoiceList().size(); j++){
						if(surveyUserAnswerModelList.get(i).getMultipleChoiceList().get(j).equalsIgnoreCase("Other...")){
							list.add(surveyUserAnswerModelList.get(i).getMultipleChoiceList().get(j) + ": " + surveyUserAnswerModelList.get(i).getAnswer1());
						} else if(surveyUserAnswerModelList.get(i).getMultipleChoiceList().get(j) != null){
							list.add(surveyUserAnswerModelList.get(i).getMultipleChoiceList().get(j));
						}
					}
				} else if(surveyUserAnswerModelList.get(i).getQuestiontype().equalsIgnoreCase("Grid")){
					for(int j = 0; j < surveyQuestionModelList.size(); j++){
						if((surveyQuestionModelList.get(j).getRowGridList() != null)
								&& (surveyUserAnswerModelList.get(i).getGridAnswerList() != null)
								&& (surveyQuestionModelList.get(j).getNumberquestion() == surveyUserAnswerModelList.get(i).getNumberquestion())){
							for(int k = 0; k < surveyQuestionModelList.get(j).getRowGridList().size(); k++){
								list.clear();
								list.add(titleSurvey);
								list.add(user);
								list.add(surveyUserAnswerModelList.get(i).getQuestion());
								list.add(surveyQuestionModelList.get(j).getRowGridList().get(k));
								list.add(surveyUserAnswerModelList.get(i).getGridAnswerList().get(k));
								//CSVUtils.writeLine(writer, list);
								csvFilePrinter.printRecord(list);
								list.clear();
							}
							gridrowIndex++;
						}
					}
				}
				
				else if(surveyUserAnswerModelList.get(i).getAnswer1() != null){
					list.add(surveyUserAnswerModelList.get(i).getAnswer1());
				}
				
				if( list != null && !list.isEmpty()){
//					CSVUtils.writeLine(writer, list);
					csvFilePrinter.printRecord(list);
				}
			}
	        writer.flush();
	        writer.close();
	        csvFilePrinter.close();
	        writer = null;
		} catch (IOException e) {
			e.printStackTrace();
		 }
	}
	
	

	
	/**
	 * Sets the DB property connection.
	 */
	public void setDBPropertyConnection (){
		try {
			 
				AccessPoint ac = getSurveyDBAccessPoint();
				System.out.println("Got AccessPoint:" + ac.toString());
				String dbAddress = ac.address();
				this.DBURL = dbAddress;
				System.out.println("DB address: "+ dbAddress);
				String dbName = ac.name();
				this.DBName = dbName;
				System.out.println("DB name: "+ dbName);
				String dbUser = ac.username();
				this.uName = dbUser;
				System.out.println("DB user: " + dbUser);
		
				jdbcURL = new StringBuffer("jdbc:postgresql://").append(dbAddress).append("/").append(dbName).toString();
				System.out.println("jdbc.url: "+jdbcURL);
		
				//save the context for this resource
				String currContext = ScopeProvider.instance.get();
				//set the context for this resource
				ScopeProvider.instance.set("/"+PortalContext.getConfiguration().getInfrastructureName());
		
				String pwd = StringEncrypter.getEncrypter().decrypt(ac.password());
				this.pwd = pwd;
				System.out.println("Decrypted Password: *******");
		
				//reset the context
				ScopeProvider.instance.set(currContext);	
				
				persistenceMap = new HashMap<String, String>();
				persistenceMap.put("javax.persistence.jdbc.url", jdbcURL);
				persistenceMap.put("javax.persistence.jdbc.user", dbUser);
				persistenceMap.put("javax.persistence.jdbc.password", pwd);
				persistenceMap.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
				persistenceMap.put("hibernate.show_sql", "false");
				persistenceMap.put("hibernate.format_sql", "true");
				persistenceMap.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
				persistenceMap.put("hibernate.hbm2ddl.auto", "validate");
				
				CreateDB createDB = null;
				try {
				  createDB = new CreateDB(jdbcURL,DBName,uName,pwd);
				} catch (Exception e) {
					e.printStackTrace();
				}
	} catch (Exception e){
			
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Gets the survey DB access point.
	 *
	 * @return the survey DB access point
	 */
	private AccessPoint getSurveyDBAccessPoint() {

		//save the context for this resource
		String currContext = ScopeProvider.instance.get();
		//set the context for this resource
		ScopeProvider.instance.set("/"+PortalContext.getConfiguration().getInfrastructureName());

		//construct the xquery
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY_NAME +"'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> conf = client.submit(query);
		ServiceEndpoint res = conf.get(0);

		//reset the context
		ScopeProvider.instance.set(currContext);	

		return res.profile().accessPoints().iterator().next();
	}
	

	/**
	 * Gets the survey question model temp.
	 *
	 * @return the survey question model temp
	 */
	public SurveyQuestionModel getSurveyQuestionModelTemp() {
		return surveyQuestionModelTemp;
	}

	/**
	 * Sets the survey question model temp.
	 *
	 * @param surveyQuestionModelTemp the new survey question model temp
	 */
	public void setSurveyQuestionModelTemp(SurveyQuestionModel surveyQuestionModelTemp) {
		this.surveyQuestionModelTemp = surveyQuestionModelTemp;
	}

	/**
	 * Gets the folder ID.
	 *
	 * @return the folder ID
	 */
	public long getFolderID() {
		return folderID;
	}

	/**
	 * Sets the folder ID.
	 *
	 * @param folderID the new folder ID
	 */
	public void setFolderID(long folderID) {
		this.folderID = folderID;
	}

	/**
	 * Gets the dl folder.
	 *
	 * @return the dl folder
	 */
	public Folder getDlFolder() {
		return dlFolder;
	}

	/**
	 * Sets the dl folder.
	 *
	 * @param dlFolder the new dl folder
	 */
	public void setDlFolder(Folder dlFolder) {
		this.dlFolder = dlFolder;
	}


	/**
	 * Gets the gm.
	 *
	 * @return the gm
	 */
	public GroupManager getGm() {
		return gm;
	}


	/**
	 * Sets the gm.
	 *
	 * @param gm the new gm
	 */
	public void setGm(GroupManager gm) {
		this.gm = gm;
	}


	/**
	 * Gets the service context.
	 *
	 * @return the service context
	 */
	public ServiceContext getServiceContext() {
		return serviceContext;
	}


	/**
	 * Sets the service context.
	 *
	 * @param serviceContext the new service context
	 */
	public void setServiceContext(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}


	/**
	 * Gets the http session.
	 *
	 * @return the http session
	 */
	public HttpSession getHttpSession() {
		return httpSession;
	}


	/**
	 * Sets the http session.
	 *
	 * @param httpSession the new http session
	 */
	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}


	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	/**
	 * Gets the asl session.
	 *
	 * @return the asl session
	 */
	public ASLSession getAslSession() {
		return aslSession;
	}


	/**
	 * Sets the asl session.
	 *
	 * @param aslSession the new asl session
	 */
	public void setAslSession(ASLSession aslSession) {
		this.aslSession = aslSession;
	}


	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}


	/**
	 * Sets the scope.
	 *
	 * @param scope the new scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}


	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	public int getCompanyId() {
		return companyId;
	}


	/**
	 * Sets the company id.
	 *
	 * @param companyId the new company id
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}


	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public long getGroupId() {
		return groupId;
	}


	/**
	 * Sets the group id.
	 *
	 * @param groupId the new group id
	 */
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	/**
	 * Gets the persistence map.
	 *
	 * @return the persistence map
	 */
	public Map<String, String> getPersistenceMap() {
		return persistenceMap;
	}




	/**
	 * Sets the persistence map.
	 *
	 * @param persistenceMap the persistence map
	 */
	public void setPersistenceMap(Map<String, String> persistenceMap) {
		this.persistenceMap = persistenceMap;
	}




	/**
	 * Gets the manager factory.
	 *
	 * @return the manager factory
	 */
	public EntityManagerFactory getManagerFactory() {
		return managerFactory;
	}




	/**
	 * Sets the manager factory.
	 *
	 * @param managerFactory the new manager factory
	 */
	public void setManagerFactory(EntityManagerFactory managerFactory) {
		this.managerFactory = managerFactory;
	}




	/**
	 * Gets the dburl.
	 *
	 * @return the dburl
	 */
	public String getDBURL() {
		return DBURL;
	}




	/**
	 * Sets the dburl.
	 *
	 * @param dBURL the new dburl
	 */
	public void setDBURL(String dBURL) {
		DBURL = dBURL;
	}




	/**
	 * Gets the DB name.
	 *
	 * @return the DB name
	 */
	public String getDBName() {
		return DBName;
	}




	/**
	 * Sets the DB name.
	 *
	 * @param dBName the new DB name
	 */
	public void setDBName(String dBName) {
		DBName = dBName;
	}




	/**
	 * Gets the u name.
	 *
	 * @return the u name
	 */
	public String getuName() {
		return uName;
	}




	/**
	 * Sets the u name.
	 *
	 * @param uName the new u name
	 */
	public void setuName(String uName) {
		this.uName = uName;
	}




	/**
	 * Gets the pwd.
	 *
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}




	/**
	 * Sets the pwd.
	 *
	 * @param pwd the new pwd
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}




	/**
	 * Gets the category name.
	 *
	 * @return the category name
	 */
	public static String getCATEGORY_NAME() {
		return CATEGORY_NAME;
	}




	/**
	 * Sets the category name.
	 *
	 * @param cATEGORY_NAME the new category name
	 */
	public static void setCATEGORY_NAME(String cATEGORY_NAME) {
		CATEGORY_NAME = cATEGORY_NAME;
	}

	 
 
	
	
}
