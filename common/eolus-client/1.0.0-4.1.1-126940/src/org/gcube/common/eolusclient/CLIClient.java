package org.gcube.common.eolusclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;

import net.java.dev.jaxb.array.StringArray;


public class CLIClient {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 * @throws ServiceException 
	 */
	public static void main(String[] args) throws MalformedURLException, ServiceException {
		String epr = "http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl";
		if (args.length == 1) {
			epr = args[0];
		}

		String username;
		String password;
		
		URL url = new URL(epr);
//		EolusServiceLocator servicelocator = new EolusServiceLocator(epr, new QName("http://eolus.uoa.org/", "EolusService"));
		EolusServiceLocator servicelocator = new EolusServiceLocator();
		Eolus eolus = servicelocator.getEolusPort();
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			String str = "";
			System.out.print("Username: ");
			username = in.readLine();
			System.out.print("Password: ");
			password = in.readLine();


			((Stub) eolus)._setProperty(Call.USERNAME_PROPERTY, username);
			((Stub) eolus)._setProperty(Call.PASSWORD_PROPERTY, password);
			
			while (!str.equalsIgnoreCase("q")) {
				System.out.print("\n" +
						"Q) Quit\n"+
						"USER MANAGEMENT\n" +
						"1) Read users\t" +
						"2) Create user\t" +
						"3) Remove user\n" +
						"TEMPLATE MANAGEMENT\n" +
						"4) List my templates\t" +
						"5) List public templates\n" +
						"6) Copy my template\t" +
						"7) Move my template\n" +
						"8) Remove my template\t" +
						"9) Remove any user template (acting as admin)\n" +
						"10) Make my template public\t" +
						"11) Remove public template (acting as admin)\n" +
						"12) Sync my templates with repository \n" +
						"SCRIPT MANAGEMENT\n" +
						"13) List my scripts\t" +
						"14) Add script\t" +
						"15) Get script description\n" +
						"16) Remove script\t" +
						"17) Remove any user script (acting as admin)\t" +
						"18) Sync my scripts with repository\n" +
						"VM MANAGEMENT\n" +
						"19) Create new VM\t" +
						"20) Shutdown VM\t" +
						"21) Pause VM\t" +
						"22) Resume VM\n" +
						"23) Get VM IP\t" +
						"24) Get VM Status\t" +
						"25) Get VM Info\t" +
						"26) List my VMs\n" +
						"27) Shutdown any user VM (as admin)\t" +
						"28) Pause any user VM (as admin)\t" +
						"29) Resume any user VM (as admin)\n" +
						"30) Get any user VM IP (as admin)\t" +
						"31) Get any user VM Status (as admin)\t" +
						"32) Get any user VM Info (as admin)\n" +
						"33) List all VMs (as admin)\t" +
						"34) List stray VMs\t" +
						"35) Assign VM to user (as admin)\n" +
						"36) Make template from VM\t" +
						"37) Run cmd on VM\t" +
						"38) Run script on VM\n" +
						"HOST MANAGEMENT\n" +
						"39) List hosts\t" +
						"40) Get host info\t" +
						"41) Enable host\n" +
						"42) Disable host\t" +
						"43) Delete host\t" +
						"44) Create host\n" +
						"45) VM migrate\n" +
						"NETWORK MANAGEMENT\n" +
						"46) List all Networks (as admin)\t" +
						"47) List user Networks (as admin)\t" +
						"48) List my Networks\n" +
						"49) Create Network\t" +
						"50) Create public Network (as admin)\n" +
						"51) Assign Network to User\n" +
						"52) Remove Network\t" +
						"53) Remove Network (as admin)\n" +
						"54) Info of Network\t" +
						"55) Info of Network (as admin)\n" +
						"CONFIGURATION\n" +
						"56) Change config parameter\n" +
				"");
				str = in.readLine();
				process(eolus, in, str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void process(Eolus eolus, BufferedReader in, String cmd) throws IOException{
		if (cmd.equalsIgnoreCase("1")){ //List Users
			String[] users = eolus.getUsers().getItem();
			System.out.println("Total users: "+ users.length);
			for (int i = 0; i< users.length; i++){
				System.out.println(users[i]);
			}
		}
		if (cmd.equalsIgnoreCase("2")){ //Create user
			System.out.print("New username: ");
			String user = in.readLine();
			try {
				eolus.addUser(user);
				System.out.println("OK");
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReservedUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("3")){ //Create user
			System.out.print("Username of user to delete: ");
			String user = in.readLine();
			try {
				eolus.deleteUser(user);
				System.out.println("OK");
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReservedUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("4")){ //List user templates
			String[] templts = eolus.getTemplates().getItem();
			System.out.println("Total number of templates: "+ templts.length);
			for (int i = 0; i<templts.length; i++){
				String status = "";
				try {
					status = eolus.getTemplateStatus(templts[i]);
				} catch (MultipleTemplatesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownTemplateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(templts[i]+ "("+status+")");
			}
		}
		if (cmd.equalsIgnoreCase("5")){ //List public templates
			String[] templts = eolus.getPublicTemplates().getItem();
			System.out.println("Total number of public templates: "+ templts.length);
			for (int i = 0; i<templts.length; i++){
				System.out.println(templts[i]);
			}
		}
		if (cmd.equalsIgnoreCase("6")){ //Copy user template
			System.out.print("Template to copy: ");
			String templ = in.readLine();
			System.out.print("New template name: ");
			String newtempl = in.readLine();
			try {
				eolus.transferTemplate(templ, newtempl, false);
				System.out.println("OK");
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("7")){ //move user template
			System.out.print("Template to move: ");
			String templ = in.readLine();
			System.out.print("New template name: ");
			String newtempl = in.readLine();
			try {
				eolus.transferTemplate(templ, newtempl, true);
				System.out.println("OK");
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("8")){ //Remove user template
			System.out.print("Template to remove: ");
			String templ = in.readLine();
			try {
				eolus.removeTemplate(templ);
				System.out.println("OK");
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MultipleTemplatesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TemplateNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("9")){ //Force remove user template
			System.out.print("Username of user who owns the template: ");
			String user = in.readLine();
			System.out.print("Template to remove: ");
			String templ = in.readLine();
			try {
				eolus.adminRemoveTemplate(user, templ);
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("OK");
			} catch (MultipleTemplatesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TemplateNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("10")){ //Make template public 
			System.out.print("Template to publish: ");
			String templ = in.readLine();
			System.out.print("New template name: ");
			String newtempl = in.readLine();
			try {
				eolus.makeTemplatePublic(templ, newtempl);
				System.out.println("OK");
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MultipleTemplatesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TemplateNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownTemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("11")){ //Remove public template
			System.out.print("Template to publish: ");
			String templ = in.readLine();
			System.out.print("New template name: ");
			try {
				eolus.adminRemovePublicTemplate(templ);
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("OK");
			} catch (MultipleTemplatesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TemplateNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("12")){ //Sync user templates
			try {
				eolus.syncTemplates();
				System.out.println("OK");
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("13")){ //List user scripts
			String[] scripts = eolus.getScriptList().getItem();
			System.out.println("Total number of scripts: "+ scripts.length);
			for (int i = 0; i<scripts.length; i++){
				System.out.println(scripts[i]);
			}
		}
		if (cmd.equalsIgnoreCase("14")){ //Add user script
			System.out.print("Name of script: ");
			String scriptname = in.readLine();
			System.out.print("Script content: ");
			String content = in.readLine();
			System.out.print("Script description: ");
			String description = in.readLine();
			try {
				eolus.addScript(scriptname, content, description);
				System.out.println("OK");
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("15")){ //Get user script description
			System.out.print("Name of script: ");
			String scriptname = in.readLine();
			String description = "";
			try {
				description = eolus.getDescription(scriptname);
			} catch (UnknownScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Description: "+description);
		}
		if (cmd.equalsIgnoreCase("16")){ //Remove user script
			System.out.print("Name of script: ");
			String scriptname = in.readLine();
			try {
				eolus.removeScript(scriptname);
				System.out.println("OK");				
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("17")){ //Remove user script as admin
			System.out.print("Username of user who owns the script: ");
			String user = in.readLine();
			System.out.print("Name of script: ");
			String scriptname = in.readLine();
			try {
				eolus.adminRemoveScript(user, scriptname);
				System.out.println("OK");				
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("18")){ //Remove user script as admin
			try {
				eolus.syncScripts();
				System.out.println("OK");				
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("19")){ //Create VM
			System.out.print("Template of VM: ");
			String template = in.readLine();
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			String[] nets = {"public"};
			StringArray vnets = new StringArray();
			vnets.setItem(nets);
			try {
				eolus.createVM(template, VMname, 2, 512, vnets);
				System.out.println("OK");				
			} catch (DirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TemplateNotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownTemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VMExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("20")){ //Shutdown VM
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			System.out.print("Force shutdown (Y/N): ");
			String force = in.readLine();
			boolean res;
			if (force.equalsIgnoreCase("Y")){
				try {
					eolus.shutdownVM(VMname, true);
					System.out.println("OK");				
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownUserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					eolus.shutdownVM(VMname, false);
					System.out.println("OK");				
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownUserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (cmd.equalsIgnoreCase("21")){ //Pause VM
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			try {
				eolus.suspendVM(VMname);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("22")){ //Resume VM
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			try {
				eolus.resumeVM(VMname);
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("OK");				
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("23")){ //get VM IP
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			String res;
			try {
				res = eolus.getVMIP(VMname);
				System.out.println(res);				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("24")){ //get VM Status
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			String res;
			try {
				res = eolus.getVMStatus(VMname);
				System.out.println(res);				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("25")){ //get VM Info
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			String res;
			try {
				res = eolus.getVMInfo(VMname);
				System.out.println(res);				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("26")){ //List user VMs
			String[] res;
			try {
				res = eolus.getVMlist().getItem();
				System.out.println("Total number of VMs: "+ res.length);
				for (int i = 0; i<res.length; i++){
					System.out.println(res[i]);
				}
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("27")){ //Shutdown VM as admin
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			System.out.print("Force shutdown (Y/N): ");
			String force = in.readLine();
			boolean res;
			if (force.equalsIgnoreCase("Y")){
				try {
					eolus.adminShutdownVM(VMname, true);
					System.out.println("OK");				
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					eolus.adminShutdownVM(VMname, false);
					System.out.println("OK");				
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (cmd.equalsIgnoreCase("28")){ //Pause VM as admin
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			try {
				eolus.adminSuspendVM(VMname);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("29")){ //Resume VM as admin
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			try {
				eolus.adminResumeVM(VMname);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("30")){ //get VM IP as admin
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			String res;
			try {
				res = eolus.adminGetVMIP(VMname);
				System.out.println(res);				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("31")){ //get VM Status as admin
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			String res;
			try {
				res = eolus.adminGetVMStatus(VMname);
				System.out.println(res);				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("32")){ //get VM Info as admin
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			String res;
			try {
				res = eolus.adminGetVMInfo(VMname);
				System.out.println(res);				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("33")){ //List VMs as admin
			String[] res;
			try {
				res = eolus.adminGetVMlist().getItem();
				System.out.println("Total number of VMs: "+ res.length);
				for (int i = 0; i<res.length; i++){
					System.out.println(res[i]);
				}
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("34")){ //List stray VMs
			String[] res;
			try {
				res = eolus.adminGetStrayVMlist().getItem();
				System.out.println("Total number of VMs: "+ res.length);
				for (int i = 0; i<res.length; i++){
					System.out.println(res[i]);
				}
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("35")){ //Assign VMs to user
			System.out.print("Username of user who owns the VM: ");
			String user = in.readLine();
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			try {
				eolus.adminAssignVMtoUser(user, VMname);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("36")){ //Make template from VM
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			System.out.print("Name of template: ");
			String template = in.readLine();
			boolean res = eolus.VMtoTemplate(VMname, template);
			if (res){
				System.out.println("OK");				
			}else{
				System.out.println("VM templetize failed");								
			}
		}
		if (cmd.equalsIgnoreCase("37")){ //Run cmd on VM
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			System.out.print("Command: ");
			String cmdtorun = in.readLine();
			String[] res;
			try {
				res = eolus.execCMD(cmdtorun, VMname).getItem();
				if (res.length > 1)
					System.out.println("Stdout: "+res[0]);				
				if (res.length > 2)
					System.out.println("Stderr: "+res[1]);				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VMContactErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("38")){ //Apply script
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			System.out.print("Script name: ");
			String script = in.readLine();
			try {
				eolus.applyScript(script, VMname);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VMContactErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("39")){ //List hosts
			String[] res;
			try {
				res = eolus.getHostList().getItem();
				for (int i = 0 ; i < res.length; i++){
					System.out.println(res[i]);									
				}
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("40")){ //Get host info
			System.out.print("Name of host: ");
			String host = in.readLine();
			String res;
			try {
				res = eolus.getHostInfo(host);
				System.out.println(res);
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("41")){ //Enable host
			System.out.print("Name of host: ");
			String host = in.readLine();
			try {
				eolus.enableHost(host, true);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("42")){ //Disable host
			System.out.print("Name of host: ");
			String host = in.readLine();
			try {
				eolus.enableHost(host, false);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("43")){ //Delete host
			System.out.print("Name of host: ");
			String host = in.readLine();
			try {
				eolus.deleteHost(host);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("44")){ //Create host
			System.out.print("Name of host: ");
			String host = in.readLine();
			try {
				eolus.createHost(host);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("45")){ //VM migrate
			System.out.print("Name of VM: ");
			String vm = in.readLine();
			System.out.print("Name of host: ");
			String host = in.readLine();
			try {
				eolus.migrateVM(vm, host);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("46")){ //List all Networks (as admin)
			try {
				String[] vnets = eolus.adminGetAllVNetList().getItem();
				for (int i = 0; i < vnets.length; i++){
					System.out.println(vnets[i]);					
				}
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("47")){ //List user Networks (as admin)
			System.out.print("Name of user: ");
			String user = in.readLine();
			try {
				String[] vnets = eolus.adminGetVNetList(user).getItem();
				for (int i = 0; i < vnets.length; i++){
					System.out.println(vnets[i]);					
				}
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("48")){ //List my Networks
			try {
				String[] vnets = eolus.getVNetList().getItem();
				for (int i = 0; i < vnets.length; i++){
					System.out.println(vnets[i]);					
				}
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("49")){ //Create Network
			System.out.print("Name of VNet: ");
			String vn = in.readLine();
			System.out.print("Subnet: ");
			String subnet = in.readLine();
			try {
				eolus.createVNet(vn, Integer.parseInt(subnet));
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VNExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("50")){ //Create public Network (as admin)
			System.out.print("Name of VNet: ");
			String vn = in.readLine();
			System.out.print("Subnet: ");
			String subnet = in.readLine();
			try {
				eolus.adminCreateVNet(vn, Integer.parseInt(subnet));
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VNExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("51")){ //Assign Network to User
			System.out.print("Name of user: ");
			String user = in.readLine();
			System.out.print("Name of VNet: ");
			String vn = in.readLine();
			try {
				eolus.adminAssignVNettoUser(user, vn);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("52")){ //Remove Network
			System.out.print("Name of VNet: ");
			String vn = in.readLine();
			try {
				eolus.removeVNet(vn);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("53")){ //Remove Network (as admin)
			System.out.print("Name of VNet: ");
			String vn = in.readLine();
			try {
				eolus.adminRemoveVNet(vn);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("54")){ //Info of Network
			System.out.print("Name of VNet: ");
			String vn = in.readLine();
			try {
				String info = eolus.getVNetInfo(vn);
				System.out.println(info);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("55")){ //Info of Network (as admin)
			System.out.print("Name of VNet: ");
			String vn = in.readLine();
			try {
				String info = eolus.adminGetVNetInfo(vn);
				System.out.println(info);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cmd.equalsIgnoreCase("56")){ //Info of Network (as admin)
			System.out.print("Parameter key: ");
			String param = in.readLine();
			System.out.print("Parameter value: ");
			String val = in.readLine();
			try {
				eolus.setConfigurationParameter(param, val);
				System.out.println("OK");				
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}


