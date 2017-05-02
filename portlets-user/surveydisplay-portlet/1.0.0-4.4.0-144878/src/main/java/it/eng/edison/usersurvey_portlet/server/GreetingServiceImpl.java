package it.eng.edison.usersurvey_portlet.server ;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

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
import org.gcube.vomanagement.usermanagement.UserManager;
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
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

import it.eng.edison.usersurvey_portlet.client.GreetingService;
import it.eng.edison.usersurvey_portlet.client.UserDTO;
import it.eng.edison.usersurvey_portlet.client.model.SurveyAnswerModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.TokenModel;
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
import it.eng.edison.usersurvey_portlet.server.entity.Invitationtoken;
import it.eng.edison.usersurvey_portlet.server.entity.Survey;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyquestion;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyuseranswer;
import it.eng.edison.usersurvey_portlet.server.util.SendEmailToSurveyCreator;
import it.eng.edison.usersurvey_portlet.server.util.SendEmailToUsers;


/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
GreetingService {

	/** The Constant HOST_PROPERTY. */
	/* properties to read
	 */
	private static final String HOST_PROPERTY = "host"; 
	
	/** The Constant HOST_PORT_PROPERTY. */
	private static final String HOST_PORT_PROPERTY = "port";
	
	/** The Constant CLUSTER_NAME_PROPERTY. */
	private static final String CLUSTER_NAME_PROPERTY = "cluster"; 
	
	/** The Constant KEY_SPACE_NAME_PROPERTY. */
	private static final String KEY_SPACE_NAME_PROPERTY = "keyspace";
	
	  /** The runtime resource name. */
    private static String RUNTIME_RESOURCE_NAME = "SurveyDB";
	
    /** The category name. */
    private static String CATEGORY_NAME = "Database";
    
	/** The Constant PLATFORM_NAME. */
	private final static String PLATFORM_NAME = "Cassandra";

	/** The Constant DEFAULT_CONFIGURATION. */
	private static final String DEFAULT_CONFIGURATION = "/org/gcube/portal/databook/server/resources/databook.properties";
	
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
    
    /** The Constant MIN_ID_USER_RANDOM. */
    public final static Integer MIN_ID_USER_RANDOM = 100000;
    
    /** The Constant MAX_ID_USER_RANDOM. */
    public final static Integer MAX_ID_USER_RANDOM = 200000;		
    
    /** The row label. */
    private static String ROW_LABEL = "Row";
    
    /** The column label. */
    private static String COLUMN_LABEL = "Column";
    
	/** The company id. */
	private int companyId = 0;
	
	/** The jdbc URL. */
	private  String jdbcURL = null;
	
    /** The dburl. */
    private String DBURL = null;
   
    /** The DB name. */
    private String DBName  = null;
    
    /** The u name. */
    private String uName = null;
    
    /** The pwd. */
    private String pwd = null; 
	
	
	/** The user liferay. */
	private User userLiferay = null;
	
	/** The survey model. */
	private SurveyModel surveyModel = null;
	
	/** The survey model list. */
	private List<SurveyModel> surveyModelList = null;
	
	/** The survey question model. */
	private SurveyQuestionModel surveyQuestionModel = null;
	
	/** The survey question model list. */
	private List<SurveyQuestionModel> surveyQuestionModelList = null;
	
	/** The user list. */
	private List<User> userList = null;
	
	/** The list user map. */
	private HashMap listUserMap = new HashMap();
	
	/** The token model list. */
	private List<TokenModel> tokenModelList;
	
	/** The token model. */
	private TokenModel tokenModel;
	
	/** The current URL. */
	private String currentURL;
	
	/** The choice question list. */
	List<String> choiceQuestionList = null;
	
	/** The local date. */
	private Date localDate = null;
	
	/** The emf. */
	private EntityManagerFactory emf;

    /** The persistence map. */
    private Map<String, String> persistenceMap = null;
    
	/** The row grid list. */
	private List<String> rowGridList = null;
	
	/** The column grid list. */
	private List<String> columnGridList = null;
	
    public void init() {
    	setDBPropertyConnection ();
    }
	
	/**
	 * Save all survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param surveyQuestionModelList the survey question model list
	 */
	public void saveAllSurvey(long idUser, SurveyModel surveyModel, List<SurveyQuestionModel> surveyQuestionModelList){

		Survey survey = new Survey();
		survey.setTitlesurvey(surveyModel.getTitlesurvey());
		survey.setIdusercreator((int)idUser);
		survey.setDatesurvay(surveyModel.getDateSurvay());
		survey.setExpiredDatesurvay(surveyModel.getExpiredDateSurvay());
		survey.setIsanonymous(surveyModel.getIsAnonymous());
		survey.setGroupid(surveyModel.getGroupId());
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		SurveyJpaController con = new SurveyJpaController(emf);
		try {
			con.create(survey);
		} catch (Exception e) {
			e.printStackTrace();
			emf.close();
			con = null;
			emf = null;
		} finally {
			con.getEntityManager().close();
		}

		SurveyquestionJpaController con1 = new SurveyquestionJpaController(emf);
		Surveyquestion surveyQuestion = null;
		try {
			for(int i=0; i<surveyQuestionModelList.size(); i++){
				surveyQuestion = new Surveyquestion();
				surveyQuestion.setIdSurvey(survey);
				surveyQuestion.setNumberquestion(surveyQuestionModelList.get(i).getNumberquestion());
				surveyQuestion.setIsmandatory(surveyQuestionModelList.get(i).getIsmandatory());
				surveyQuestion.setQuestiontype(surveyQuestionModelList.get(i).getQuestiontype());
				surveyQuestion.setQuestion(surveyQuestionModelList.get(i).getQuestion());
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
				Date localDate = setDate2LocalTimezone(surveyQuestionModelList.get(i).getDateAnswer());
				surveyQuestion.setDateanswer(localDate);
				con1.create(surveyQuestion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con1.getEntityManager().close();
			emf.close();
			con1 = null;
			emf = null;
		}
	}


	/**
	 * Save all answer.
	 *
	 * @param surveyModel the survey model
	 * @param surveyAnswerModelList the survey answer model list
	 * @param tokenModel the token model
	 * @param currentURL the current URL
	 * @param fullNameUser the full name user
	 */
	public void saveAllAnswer(SurveyModel surveyModel, List<SurveyAnswerModel> surveyAnswerModelList, TokenModel tokenModel, String currentURL, String fullNameUser){
		
		String urlPorltet = getUrlWithoutParams(currentURL);
		GCubeUser user = null;
		String currentGroupName = null;
		long currGroupId = 0;
		String surveyCreatorManager = null;
		String emailCreatorManager = null;
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		UserManager um = new LiferayUserManager();
		
		Survey survey = new Survey();
		survey.setId(surveyModel.getIdsurvey());
		survey.setTitlesurvey(surveyModel.getTitlesurvey());
		survey.setIdusercreator(surveyModel.getIdUserCreator());
		survey.setDatesurvay(surveyModel.getDateSurvay());
		survey.setExpiredDatesurvay(surveyModel.getExpiredDateSurvay());
		survey.setIsanonymous(surveyModel.getIsAnonymous());

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		SurveyuseranswerJpaController con = new SurveyuseranswerJpaController(emf);
		ChoiceanswerJpaController conChoiceAns = new ChoiceanswerJpaController(emf);
		GridanswerJpaController gridanswerJpaController = new GridanswerJpaController(emf);

		Surveyuseranswer surveyUserAnswer = null;
		try {
			for(int i=0; i<surveyAnswerModelList.size(); i++){
				surveyUserAnswer = new Surveyuseranswer();
				surveyUserAnswer.setIdSurvey(survey);
				surveyUserAnswer.setIduseranswer(surveyAnswerModelList.get(i).getIdUserAnswer());
				surveyUserAnswer.setQuestiontype(surveyAnswerModelList.get(i).getQuestiontype());
				surveyUserAnswer.setNumberquestion(surveyAnswerModelList.get(i).getNumberquestion());
				surveyUserAnswer.setAnswer1(surveyAnswerModelList.get(i).getAnswer1());
				surveyUserAnswer.setAnswer2(surveyAnswerModelList.get(i).getAnswer2());
				surveyUserAnswer.setAnswer3(surveyAnswerModelList.get(i).getAnswer3());
				surveyUserAnswer.setAnswer4(surveyAnswerModelList.get(i).getAnswer4());
				surveyUserAnswer.setAnswer5(surveyAnswerModelList.get(i).getAnswer5());
				surveyUserAnswer.setAnswer6(surveyAnswerModelList.get(i).getAnswer6());
				surveyUserAnswer.setAnswer7(surveyAnswerModelList.get(i).getAnswer7());
				surveyUserAnswer.setAnswer8(surveyAnswerModelList.get(i).getAnswer8());
				surveyUserAnswer.setAnswer9(surveyAnswerModelList.get(i).getAnswer9());
				surveyUserAnswer.setAnswer10(surveyAnswerModelList.get(i).getAnswer10());

				if(surveyAnswerModelList.get(i).getDate() != null){
					localDate = setDate2LocalTimezone(surveyAnswerModelList.get(i).getDate());
					surveyUserAnswer.setDateanswer(localDate);
				}
				
				if(surveyModel.getIsAnonymous() &&
						((httpSession != null) && httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE) == null)){
					
					if(tokenModel.getIdUserAnswer() == 0){
						Random random = new Random();
						int randomIdUser = random.nextInt(MAX_ID_USER_RANDOM - MIN_ID_USER_RANDOM + 1) + MIN_ID_USER_RANDOM;
						surveyUserAnswer.setIduseranswer(randomIdUser);
					} else {
						surveyUserAnswer.setIduseranswer(tokenModel.getIdUserAnswer());
					}
				}
				
				con.create(surveyUserAnswer);
				
				if((surveyAnswerModelList.get(i).getMultipleChoiceList() != null)
						&& !(surveyAnswerModelList.get(i).getMultipleChoiceList().isEmpty())
						&& (surveyAnswerModelList.get(i).getMultipleChoiceList().size() != 0)){ 
					conChoiceAns.insertMultipleChoice(survey, surveyAnswerModelList.get(i));
				}
				
				if((surveyAnswerModelList.get(i).getGridAnswerList() != null)
						&& !(surveyAnswerModelList.get(i).getGridAnswerList().isEmpty())
						&& (surveyAnswerModelList.get(i).getGridAnswerList().size() != 0)){ 
					gridanswerJpaController.insertGridAnswer(survey, surveyAnswerModelList.get(i));
				}
				
			}
			
			/* Public page */
			if((httpSession != null) && httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE) == null){
				fullNameUser = "Guest";
				user = um.getUserById(surveyModel.getIdUserCreator());
				emailCreatorManager = user.getEmail();
				surveyCreatorManager = user.getFullname();
				GroupManager gm = new LiferayGroupManager();
				
				currentGroupName = gm.getGroup(surveyModel.getGroupId()).getGroupName();
						
			} else {
				String currentUsername = getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
				String scope = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getScope();
				GroupManager gm = new LiferayGroupManager();
				currGroupId = gm.getGroupIdFromInfrastructureScope(scope);
				currentGroupName = gm.getGroup(currGroupId).getGroupName();
				user = um.getUserById(surveyModel.getIdUserCreator());
				emailCreatorManager = user.getEmail();
				surveyCreatorManager = user.getFullname();
				if(surveyModel.getIsAnonymous() || fullNameUser == null){
					fullNameUser = "Guest";
				}
			}
			SendEmailToSurveyCreator sendEmailToSurveyCreator = new SendEmailToSurveyCreator(urlPorltet, fullNameUser, emailCreatorManager, surveyCreatorManager, currentGroupName, getThreadLocalRequest());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.getEntityManager().close();
			conChoiceAns.getEntityManager().close();
			gridanswerJpaController.getEntityManager().close();
			con = null;
			conChoiceAns = null;
			gridanswerJpaController = null;
		}

		InvitationtokenJpaController con1 = new InvitationtokenJpaController(emf);
		Date today = new Date();
		String fillOutSurveyDate = dateToString(today);
		try{
			con1.burnTokenAnonymousSurvey(fillOutSurveyDate, tokenModel.getUUID(), survey.getId());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con1.getEntityManager().close();
			emf.close();
			con1  = null;
			emf = null;
		}
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
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateConverted = dateFormat.format(surveyDate);
		}
		return dateConverted;
	}


	/**
	 * Insert survey question.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param idSurvay the id survay
	 */
	public void insertSurveyQuestion(SurveyQuestionModel surveyQuestionModel, int idSurvay){
		Surveyquestion surveyQuestion = new Surveyquestion();
		Survey survey = new Survey();
		survey.setId(idSurvay);
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

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
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
		survey.setGroupid(surveyModel.getGroupId());

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
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
	 * Gets the user.
	 *
	 * @return the user
	 */
	public UserDTO getUser(){
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		UserDTO userDTO = new UserDTO();
		
		GroupManager gm = new LiferayGroupManager();
		/* Public page */
		if((httpSession != null) && httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE) == null){
			return userDTO;
		}
		/* Public page end */
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
	 * Gets the user list current company.
	 *
	 * @param idSurvey the id survey
	 * @return the user list current company
	 */
	public UserDTO getUserListCurrentCompany(int idSurvey) {

		UserDTO userDTO = new UserDTO();
		List<GCubeUser> users = null;
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		
		String currentUsername = getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		String scope = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getScope();
		String groupName = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getGroupName();
		
		long currGroupId = 0;
		try {
			currGroupId = gm.getGroupIdFromInfrastructureScope(scope);
			String rootName = gm.getRootVOName();
		} catch (IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e1) {
			e1.printStackTrace();
		}
		
	    try {
	    	users = um.listUsersByGroup(currGroupId);   
	    } catch (UserManagementSystemException | GroupRetrievalFault | UserRetrievalFault e) {
	    	e.printStackTrace();
	    }

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
		List<String> usersJustInvitedToFillSurvey = invitationtokenJpaController.findEmailUsersByIdSurvey(idSurvey);
				
		
		Iterator it = users.iterator();
		GCubeUser currentUser = null;
		while (it.hasNext()){
			currentUser = (GCubeUser)it.next();
			userDTO.getListUserMap().put(currentUser.getFullname()+"<"+currentUser.getEmail()+">",currentUser.getEmail());
		}
		
		if(usersJustInvitedToFillSurvey!=null && !usersJustInvitedToFillSurvey.isEmpty()){
			for(int j = 0; j < usersJustInvitedToFillSurvey.size(); j++){
				if(userDTO.getListUserMap().containsValue(usersJustInvitedToFillSurvey.get(j))){
					String emailUserMapKeyValue = (String)getKeyFromValue(userDTO.getListUserMap(), usersJustInvitedToFillSurvey.get(j));
					userDTO.getListUserMap().remove(emailUserMapKeyValue);
				}
			}
		}

		invitationtokenJpaController.getEntityManager().close();
		emf.close();
		invitationtokenJpaController = null;
		emf = null;
		
		return userDTO; 
	}
	

	/**
	 * Gets the users invited to fill survey.
	 *
	 * @param idSurvey the id survey
	 * @return the users invited to fill survey
	 */
	public Map<String,String> getUsersInvitedToFillSurvey(int idSurvey){
		Map<String,String> usersInvitedToFillSurvey = new HashMap<String,String>();
		List<GCubeUser> users = null;
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		
		try {
			users = um.listUsersByGroup(gm.getRootVO().getGroupId());
		} catch (UserManagementSystemException | GroupRetrievalFault | UserRetrievalFault e) {
			e.printStackTrace();
		}

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
		List<String> emailUsersInvitedToFillSurvey = invitationtokenJpaController.findEmailUsersByIdSurvey(idSurvey);
		
		Iterator it = users.iterator();
		GCubeUser currentUser = null;
		while (it.hasNext()){
			currentUser = (GCubeUser)it.next();
			if(emailUsersInvitedToFillSurvey.contains(currentUser.getEmail())){
				usersInvitedToFillSurvey.put(currentUser.getEmail(), currentUser.getFullname());
			}
		}
		invitationtokenJpaController.getEntityManager().close();
		emf.close();
		
		invitationtokenJpaController= null;
		emf = null;
		
		return usersInvitedToFillSurvey;
	}
	
	  /**
  	 * Gets the key from value.
  	 *
  	 * @param hm the hm
  	 * @param value the value
  	 * @return the key from value
  	 */
  	private Object getKeyFromValue(Map hm, String value) {
		    for (Object o : hm.keySet()) {
		      if (hm.get(o).equals(value)) {
		        return o;
		      }
		    }
		    return null;
		  }


	/**
	 * Gets the survey.
	 *
	 * @param idSurvey the id survey
	 * @return the survey
	 */
	public SurveyModel getSurvey(int idSurvey){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
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
		surveyModel.setGroupId(survey.getGroupid());

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
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
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
			surveyModel.setGroupId(surveyList.get(i).getGroupid());

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
	 * Gets the all surveys from DB.
	 *
	 * @return the all surveys from DB
	 */
	public List<SurveyModel> getAllSurveysFromDB(){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		String currentUsername = getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		String scope = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getScope();
		String groupName = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getGroupName();
		
		long currGroupId = 0;
		try {
			currGroupId = gm.getGroupIdFromInfrastructureScope(scope);
		} catch (IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		
		SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
		List<Survey> surveyList = surveyJpaController.findAllSurveys(currGroupId);

		surveyModel = new SurveyModel();
		surveyModelList = new ArrayList<SurveyModel>();
		
		for(int i = 0; i < surveyList.size(); i++){
			surveyModel.setIdsurvey(surveyList.get(i).getId());
			surveyModel.setTitlesurvey(surveyList.get(i).getTitlesurvey());
			surveyModel.setIdUserCreator(surveyList.get(i).getIdusercreator());
			surveyModel.setDateSurvay(surveyList.get(i).getDatesurvay());
			surveyModel.setExpiredDateSurvay(surveyList.get(i).getExpireddatesurvay());
			surveyModel.setIsAnonymous(surveyList.get(i).getIsanonymous());
			surveyModel.setGroupId(surveyList.get(i).getGroupid());

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
	 * Gets the id survey by UUID.
	 *
	 * @param UUID the uuid
	 * @return the id survey by UUID
	 */
	public int getIdSurveyByUUID(String UUID){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
		int idSurveyByUUID = invitationtokenJpaController.findIdSurveyByUUID(UUID);
		invitationtokenJpaController.getEntityManager().close();
		emf.close();
		invitationtokenJpaController = null;
		emf = null;
		
		return idSurveyByUUID;
	}
	

	/**
	 * Gets the survey by UUID.
	 *
	 * @param UUID the uuid
	 * @return the survey by UUID
	 */
	public SurveyModel getSurveyByUUID(String UUID){
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
		int idSurveyByUUID = -1;
		SurveyModel surveyModel = null;
		idSurveyByUUID = invitationtokenJpaController.findIdSurveyByUUID(UUID);
		
		/* WRONG UUID PASSED: NOT EXIST */
		if(idSurveyByUUID == -2){
			surveyModel = new SurveyModel();
			surveyModel.setIdsurvey(new Integer(idSurveyByUUID));
			invitationtokenJpaController.getEntityManager().close();
			emf.close();
			invitationtokenJpaController = null;
			emf = null;
			return surveyModel;
		}
		
		if(idSurveyByUUID != -1 && 
				idSurveyByUUID != 0){
			surveyModel = getSurvey(idSurveyByUUID);
		}
		
		
		invitationtokenJpaController.getEntityManager().close();
		emf.close();
		invitationtokenJpaController = null;
		emf = null;
		
		return surveyModel;
	}
	
	/**
	 * Gets the survey by UUID and user id.
	 *
	 * @param UUID the uuid
	 * @param userId the user id
	 * @return the survey by UUID and user id
	 */
	public SurveyModel getSurveyByUUIDAndUserId(String UUID, int userId){
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
		int idSurveyByUUID = 0;
		SurveyModel surveyModel = null;
		
		idSurveyByUUID = invitationtokenJpaController.findIdSurveyByUUID(UUID);
		
		/* WRONG UUID PASSED: NOT EXIST */
		if(idSurveyByUUID == -2){
			surveyModel = new SurveyModel();
			surveyModel.setIdsurvey(new Integer(idSurveyByUUID));
			invitationtokenJpaController.getEntityManager().close();
			emf.close();
			invitationtokenJpaController = null;
			emf = null;
			return surveyModel;
		}
		
		idSurveyByUUID = invitationtokenJpaController.findIdSurveyByUUIDAndUserId(UUID, userId);
		if(idSurveyByUUID != -1 && idSurveyByUUID != 0){
			surveyModel = getSurvey(idSurveyByUUID);
		}
		
		/* WRONG UUID FOR THIS MEMBER */
		if(idSurveyByUUID == -1){
			surveyModel = new SurveyModel();
			surveyModel.setIdsurvey(new Integer(idSurveyByUUID));
		}
		
		invitationtokenJpaController.getEntityManager().close();
		emf.close();
		invitationtokenJpaController = null;
		emf = null;
		return surveyModel;
	}
	
	/**
	 * Gets the survey list by user answer id.
	 *
	 * @param idUserAnswer the id user answer
	 * @return the survey list by user answer id
	 */
	public List<TokenModel> getSurveyListByUserAnswerId(int idUserAnswer){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
		List<Invitationtoken> invitationtoken = invitationtokenJpaController.findInvitationtokenByUserAnswerId(idUserAnswer);
		tokenModel = new TokenModel();
		tokenModelList = new ArrayList<>();

		for(int i=0; i<invitationtoken.size(); i++){
			tokenModel.setIdSurvey(invitationtoken.get(i).getIdSurvey());
			tokenModel.setIdUserAnswer(invitationtoken.get(i).getIduseranswer());
			tokenModel.setToken(invitationtoken.get(i).getField3());
			tokenModel.setEmail(invitationtoken.get(i).getField1());
			tokenModel.setUUID(invitationtoken.get(i).getUuid());

			tokenModelList.add(tokenModel);
			tokenModel = new TokenModel();
		}
		
		invitationtokenJpaController.getEntityManager().close();
		emf.close();
		invitationtokenJpaController = null;
		emf = null;
		return tokenModelList;
	}
	
	/**
	 * Gets the token model by UUID.
	 *
	 * @param UUID the uuid
	 * @return the token model by UUID
	 */
	public TokenModel getTokenModelByUUID(String UUID){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
		List<Invitationtoken> invitationtoken = invitationtokenJpaController.findInvitationtokenByUUID(UUID);
		tokenModel = new TokenModel();

		if(invitationtoken.size() != 0){
			tokenModel.setIdSurvey(invitationtoken.get(0).getIdSurvey());
			tokenModel.setIdUserAnswer(invitationtoken.get(0).getIduseranswer());
			tokenModel.setToken(invitationtoken.get(0).getField3());
			tokenModel.setEmail(invitationtoken.get(0).getField1());
			tokenModel.setUUID(invitationtoken.get(0).getUuid());
		} 
		else {
			tokenModel.setToken("WRONG_UUID");
		}
		
		invitationtokenJpaController.getEntityManager().close();
		emf.close();
		invitationtokenJpaController = null;
		emf = null;
		return tokenModel;
	}

	/**
	 * Delete survey.
	 *
	 * @param surveyModel the survey model
	 */
	public void deleteSurvey(SurveyModel surveyModel) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		SurveyquestionJpaController surveyquestionJpaController = new SurveyquestionJpaController(emf);
		surveyquestionJpaController.deleteQuestions(surveyModel.getIdsurvey());

		SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
		try {
			surveyJpaController.destroy(surveyModel.getIdsurvey());
		} catch (IllegalOrphanException | NonexistentEntityException e) {
			e.printStackTrace();
		} finally {
			surveyquestionJpaController.getEntityManager().close();
			surveyJpaController.getEntityManager().close();
			emf.close();
			surveyquestionJpaController = null;
			surveyJpaController = null;
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

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
		SurveyquestionJpaController surveyquestionJpaController = new SurveyquestionJpaController(emf);
		SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
		try{
			/* Delete Survey Questions*/
			surveyquestionJpaController.deleteQuestions(surveyModel.getIdsurvey());
			surveyquestionJpaController.getEntityManager().close();

			/* Delete Survey*/
			surveyJpaController.destroy(surveyModel.getIdsurvey());

			/* Create Survey and SurveyQuestions */
			saveAllSurvey(idUser, surveyModel, surveyQuestionModelList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			surveyquestionJpaController.getEntityManager().close();
			surveyJpaController.getEntityManager().close();
			emf.close();
			surveyquestionJpaController = null;
			surveyJpaController = null;
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
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
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
		choicequestionJpaController.getEntityManager().close();
		surveyQuestionJpaController.getEntityManager().close();
		gridquestionJpaController.getEntityManager().close();
		emf.close();
		
		choicequestionJpaController = null;
		surveyQuestionJpaController = null;
		gridquestionJpaController = null;
		emf = null;
		
		return surveyQuestionModelList;

	}

	/**
	 * Send survey to users.
	 *
	 * @param currentURL the current URL
	 * @param idSurveySelected the id survey selected
	 * @param isAnonymous the is anonymous
	 * @param surveySender the survey sender
	 * @param usersInviteSurveyList the users invite survey list
	 */
	public void sendSurveyToUsers(String currentURL, int idSurveySelected, boolean isAnonymous, String surveySender, List<String> usersInviteSurveyList){
		GCubeUser user = null;
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String currentUsername = getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		String scope = SessionManager.getInstance().getASLSession(getThreadLocalRequest().getSession().getId(), currentUsername).getScope();
		
		String currentGroupName = null;
		long currGroupId;
		try {
			currGroupId = gm.getGroupIdFromInfrastructureScope(scope);
			currentGroupName = gm.getGroup(currGroupId).getGroupName();
		} catch (IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e1) {
			e1.printStackTrace();
		}

		long userId = 0;
		String uuid = null;

		String urlPorltet = getUrlWithoutParams(currentURL);

		Set<String> usersInviteSurveyListNoDuplicate = new HashSet<>();
		usersInviteSurveyListNoDuplicate.addAll(usersInviteSurveyList);
		usersInviteSurveyList.clear();
		usersInviteSurveyList.addAll(usersInviteSurveyListNoDuplicate);
		
		int leftAngleBracket; 
		int rightAngleBracket;
		if(usersInviteSurveyList != null){
			for(int i=0; i<usersInviteSurveyList.size() ; i++){
				// Extract email users from usersInviteSurveyList
				String emailUser;
				String fullNameUser = "";
				if(usersInviteSurveyList.get(i).contains("<") && usersInviteSurveyList.get(i).contains(">")){
					leftAngleBracket = usersInviteSurveyList.get(i).indexOf("<");
					rightAngleBracket = usersInviteSurveyList.get(i).indexOf(">");
					emailUser = usersInviteSurveyList.get(i).substring(leftAngleBracket+1, rightAngleBracket);
					fullNameUser = usersInviteSurveyList.get(i).substring(0, leftAngleBracket);
				} else {
					emailUser = usersInviteSurveyList.get(i);
					try {
						user = um.getUserByEmail(emailUser);
					} catch (UserManagementSystemException | UserRetrievalFault e) {
						e.printStackTrace();
					}
					fullNameUser = user.getFullname();
				}
				
				Invitationtoken invitationtoken = new Invitationtoken();
				uuid = UUID.randomUUID().toString();
					try {
						user = um.getUserByEmail(emailUser);
						userId = user.getUserId();
						invitationtoken.setIduseranswer((int) userId);
					} catch (UserManagementSystemException | UserRetrievalFault e) {
						e.printStackTrace();
					}


				invitationtoken.setUuid(uuid);
				invitationtoken.setIdSurvey(idSurveySelected);
				invitationtoken.setField1(emailUser);
				invitationtoken.setField3(uuid);

				//Send an email to invite users to survey
				EntityManagerFactory emf = Persistence.createEntityManagerFactory("SurveyLibPU", persistenceMap);
				SurveyJpaController surveyJpaController = new SurveyJpaController(emf);
				Survey survey = surveyJpaController.findSurvey(idSurveySelected);
				
				try {
					user = um.getUserById(survey.getIdusercreator());
				} catch (UserManagementSystemException | UserRetrievalFault e1) {
					e1.printStackTrace();
				}
				
				String surveyAdminFullName = user.getFullname();
				SendEmailToUsers sendEmailToUsers = new SendEmailToUsers(urlPorltet, uuid, fullNameUser, emailUser, surveySender, currentGroupName, surveyAdminFullName, isAnonymous, getThreadLocalRequest());
				InvitationtokenJpaController invitationtokenJpaController = new InvitationtokenJpaController(emf);
				try {
					invitationtokenJpaController.create(invitationtoken);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					invitationtokenJpaController.getEntityManager().close();
					emf.close();
					invitationtokenJpaController = null;
					emf = null;
				}
			}
		}
	}

	/**
	 * Gets the url without params.
	 *
	 * @param currentURL the current URL
	 * @return the url without params
	 */
	private String getUrlWithoutParams(String currentURL) {
		String UrlWithoutParams;
		int endOfLink = currentURL.indexOf("?");
		if(endOfLink != -1){
			UrlWithoutParams = currentURL.substring(0,endOfLink);
		} else {
			return currentURL;
		}
		return UrlWithoutParams;
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
		
				System.out.println("decrypting password ...");
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
	 * Gets the jdbc URL.
	 *
	 * @return the jdbc URL
	 */
	public String getJdbcURL() {
		return jdbcURL;
	}


	/**
	 * Sets the jdbc URL.
	 *
	 * @param jdbcURL the new jdbc URL
	 */
	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
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
	 * Gets the current URL.
	 *
	 * @return the current URL
	 */
	public String getCurrentURL() {
		return currentURL;
	}


	/**
	 * Sets the current URL.
	 *
	 * @param currentURL the new current URL
	 */
	public void setCurrentURL(String currentURL) {
		this.currentURL = currentURL;
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
	
	
	
	
	
	
	
	
	
}
