/**
 * 
 */
package org.gcube.portlets.user.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.workspace.server.util.AclTypeComparator;
import org.gcube.portlets.user.workspace.shared.ReportAssignmentACL;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 29, 2014
 *
 */
public class EditPermissionsTest {
	
	
//	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps"; //PRODUCTION
	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
	public static String TEST_USER = "francesco.mangiacrapa";
//	public static String ITEMID = "63832213-098d-42d1-8774-89b6349764c0"; //Activity T3.4 working drafts/T2-EC-IMAR-HO-14-015  iMarine Sustainability WP - Business Model tools.pdf
//	public static String ITEMID = "42fa2601-39d0-4951-aabf-27d2a2f1dca7";
	
//	protected static Logger logger = Logger.getLogger(EditPermissionsTest.class);
	
	
	public static void main(String[] args) {
		
		ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
		ScopeProvider.instance.set(scope.toString());
		
		System.out.println("init HL");
		try {
			
			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(TEST_USER)
					.getWorkspace();
			
			WorkspaceFolder sharedFolder = (WorkspaceFolder) ws.getItem("bd5fa899-225d-4547-a3c5-79b5333cde20");
			
			System.out.println(sharedFolder.getACLOwner());
			
			ArrayList<String> list = new ArrayList<String>();
			list.add("francesco.mangiacrapa");
			list.add("massimiliano.assante");
			list.add("pasquale.pagano");
			list.add("valentina.marioli");
			
			validateACLToUser(sharedFolder, list, ACLType.READ_ONLY.toString());
			
		} catch (WorkspaceFolderNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HomeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ItemNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	private static ReportAssignmentACL validateACLToUser(WorkspaceFolder folder, List<String> listLogins, String aclType) throws Exception {
		
		ReportAssignmentACL reportValidation = new ReportAssignmentACL();
		
		try {
			
			Map<ACLType, List<String>> mapACL = folder.getACLOwner();
			
			ACLType settingACL = ACLType.valueOf(aclType);
			
			System.out.println("Tentative setting: "+settingACL);
			System.out.println("For Logins: "+listLogins);
			
			AclTypeComparator comparator = new AclTypeComparator();
			
			List<String> admins = mapACL.get(ACLType.ADMINISTRATOR);
			
			for (String admin : admins) {
				listLogins.remove(admin);
				System.out.println("Reject username: "+admin +" as "+ACLType.ADMINISTRATOR);
			}
			
			List<String> validLogins = new ArrayList<String>(listLogins);
			List<String> errors = new ArrayList<String>();
			
			
			for (String username : listLogins) {
				System.out.println("\nChecking username: "+username);
				for (ACLType aclHL : mapACL.keySet()) {

					if(!aclHL.equals(ACLType.ADMINISTRATOR)){
						List<String> loginsHL = mapACL.get(aclHL);
						System.out.println("to ACLType: "+aclHL +", logins found: "+loginsHL);
		
						if(loginsHL.contains(username)){
							int cmp = comparator.compare(settingACL, aclHL);
							System.out.println("Compare result between "+aclHL + " and "+settingACL +": "+cmp);
							if(cmp==-1){
								//CHANGE ACL IS NOT VALID
								System.out.println("Reject ACL: "+settingACL+ " to "+username);
								validLogins.remove(username);
								//TODO FULL NAME
								errors.add("Unable for "+username+ " the grant of the privilege '"+settingACL+", it's lower than (already assigned) "+ aclHL);
								break;
							}else if(cmp==0){
								//SAME ACL
								System.out.println("Skipping ACL: "+settingACL+ " to "+username);
								//TODO FULL NAME
								errors.add("Ignoring for "+username+ " the grant of the privilege '"+settingACL+", it's already assigned");
								validLogins.remove(username);
								break;
							}else if(cmp==1){
								//CHANGE ACL IS VALID
								System.out.println("Valid ACL: "+settingACL+ " to "+username);
							}
						}else{
							//CHANGE ACL IS VALID
							System.out.println("[Login not found], Set ACL: "+settingACL+ " to "+username);
						}
					}
				}	
			}
			
			System.out.println("\n");
			for (String username : validLogins) {
				System.out.println("Set ACL: "+settingACL+ " to "+username);
			}
			
			
			System.out.println("\n");
			for (String error : errors) {
				System.out.println(error);
			}
			reportValidation.setAclType(aclType);
			reportValidation.setErrors(errors);
			reportValidation.setValidLogins(validLogins);
			return reportValidation;
			
		} catch (InternalErrorException e) {
			throw new Exception("Sorry, an error occurred when validating ACL assignment, try again later");
		}
	}

}
