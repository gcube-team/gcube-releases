package org.gcube.vomanagement.vomsapi.test;

import java.io.File;
import java.io.FileOutputStream;

import org.gcube.vomanagement.vomsAdmin.impl.VOMSAdminImpl;
import org.gcube.vomanagement.vomsClient.impl.CredentialsManagerImpl;
import org.gridforum.jgss.ExtendedGSSCredential;

/**
 * 
 * @author Andrea Turli
 * 
 */
public class CredTest {

    /**
         * 
         * UserName = arg[0], like "testUser" Pwd = arg[1], like "test" mail =
         * arg[2], like "testUser@mail.it" groupName = arg[3], like
         * "/gcbe/devsec/test parentName = arg[4], like "/gcbe/devsec/" roleName =
         * arg[5], like "testNewRole" dlName = arg[6], like "testGroup"
         * proxyFileOut = arg[7], like "$HOME/proxyExported"
         * 
         * @param args args
         * @throws Exception exception
         */
    public static void main(String[] args) throws Exception {

	/*
         * RootCategory.getRoot().setLevel(Level.DEBUG); if(args.length != 8) {
         * throw new Exception("Please check parameters.\n * UserName = arg[0],
         * like testUser; Pwd = arg[1], like test;" + "mail = arg[2], like
         * testUser@mail.it;" + "groupName = arg[3], like /gcbe/devsec/test;" +
         * "parentName = arg[4], like /gcbe/devsec/; +" + "roleName = arg[5],
         * like testNewRole;" + "dlName = arg[6], like testGroup;" +
         * "proxyFileOut = arg[7], like $HOME/proxyExported"); } String userName =
         * args[0]; String pwd = args[1]; String mail = args[2]; String
         * groupName = args[3]; String parentName =args[4]; String roleName =
         * args[5]; String dlName = args[6]; String proxyFile = args[7];
         */

	String userName = "test";
	String pwd = "password";
	String mail = "test@eng.it";
	String groupName = "/grifin/testMio";
	String parentName = "grifin";
	String roleName = "testNewRole";
	String proxyFile = "/home/turli/proxies/vomsAPI2009";

	/*
	 * This is to test robustness of this lib also when x509_USER_*
	 * 
	 * System.setProperty("X509_USER_CERT", "/home/turli/certs/andreaTurli_INFN_2009_cert.pem");
	 * System.setProperty("X509_USER_KEY", "/home/turli/certs/andreaTurli_INFN_2009_key.pem");
	 * System.setProperty("X509_USER_PROXY", "/tmp/proxy");
	 * System.out.println("X509_USER_CERT " + System.getProperty("X509_USER_CERT"));
	 * System.out.println("X509_USER_KEY " + System.getProperty("X509_USER_KEY"));
	 * System.out.println("X509_USER_PROXY " + System.getProperty("X509_USER_PROXY"));
	*/

	try {
	    VOMSAdminImpl vomsAdmin = new VOMSAdminImpl();
	    CredentialsManagerImpl manager = new CredentialsManagerImpl(vomsAdmin);
	    

	    System.out.print("Create user: " + userName + " mail " + mail);
	    vomsAdmin.getExtendedPortType().createOnlineCAUser(userName, mail);
	    System.out.println(" DONE!");
	    System.out.println();

	    System.out.print("Create group: " + groupName
		    + " with parent name " + parentName);
	    vomsAdmin.getPortType().createGroup(parentName, groupName);
	    System.out.println(" DONE!");
	    System.out.println();

	    System.out.print("Create role: " + roleName);
	    vomsAdmin.getPortType().createRole(roleName);
	    System.out.println(" DONE!");
	    System.out.println();

	    System.out
		    .print("Add Membership: " + userName + " to " + groupName);
	    vomsAdmin.getExtendedPortType().addOnlineCAMember(groupName, userName);
	    System.out.println("");

	    System.out.println("Assign Role: " + roleName + " to " + userName
		    + " in " + groupName);
	    vomsAdmin.getExtendedPortType().assignOnlineCARole(groupName, roleName, userName);
	    System.out.println(" DONE!");
	    System.out.println();

	    System.out.println("Assign Role: vo to " + userName + " in "
		    + groupName);
	    vomsAdmin.getExtendedPortType().assignOnlineCARole(groupName, "vo", userName);
	    System.out.println(" DONE!");
	    System.out.println();

	    System.out.print("Retrieve Credentials:");
	    long start = System.currentTimeMillis();
	    ExtendedGSSCredential cred;
	    try {
		cred = manager.getCredentials(userName, pwd, groupName);
		if (cred != null) {
		    byte[] data = cred
			    .export(ExtendedGSSCredential.IMPEXP_OPAQUE);
		    File file = new File(proxyFile);
		    file.createNewFile();
		    FileOutputStream out = new FileOutputStream(proxyFile,
			    false);
		    out.write(data);
		    out.close();
		    System.out.println();
		    long end = System.currentTimeMillis();
		    System.out.println(" DONE!");
		    System.out.println();

		    System.out.println("Time needed to retrieve credentials "
			    + (end - start) + " milliseconds.");
		    System.out.println();

		    System.out.println("Credentials for " + userName
			    + " are correctly retrieved and stored in ");
		    System.out.println("\t" + proxyFile + ".");
		    System.out.println();
		    System.out.println("Please run: ");
		    System.out.println("\tvoms-proxy-info -file " + proxyFile
			    + " -all");
		    System.out
			    .println("to check correctness of the credentials.");
		    System.out.println();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    System.out.println("Dismiss Role: " + roleName + " to " + userName
		    + " in " + groupName);
	    vomsAdmin.getExtendedPortType().dismissOnlineCARole(groupName, roleName, userName);
	    System.out.println("DONE!");

	    System.out.println("Remove OnLineCA member " + userName
		    + " from groupName: " + groupName);
	    vomsAdmin.getExtendedPortType().removeOnlineCAMember(groupName, userName);
	    System.out.println("");

	    System.out.println("Delete user: " + userName);
	    vomsAdmin.getExtendedPortType().deleteOnlineCAUser(userName);
	    System.out.println("DONE!");

	    System.out.println("Delete role: " + roleName);
	    vomsAdmin.getPortType().deleteRole(roleName);
	    System.out.println("DONE!");

	    System.out.println("Delete group: " + groupName);
	    vomsAdmin.getPortType().deleteGroup(groupName);
	    System.out.println("DONE!");

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
