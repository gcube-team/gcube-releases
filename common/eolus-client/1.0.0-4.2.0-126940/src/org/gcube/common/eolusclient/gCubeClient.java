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


public class gCubeClient {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 * @throws ServiceException 
	 */
	public static void main(String[] args) throws MalformedURLException, ServiceException {
		//String epr = "http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl";
		/*if (args.length == 1) {
			epr = args[0];
		}
		*/
		String username = "gcube";
		String password;

		//URL url = new URL(epr);
		//		EolusServiceLocator servicelocator = new EolusServiceLocator(epr, new QName("http://eolus.uoa.org/", "EolusService"));
		EolusServiceLocator servicelocator = new EolusServiceLocator();
		Eolus eolus = servicelocator.getEolusPort();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			String str = "gcube";
			System.out.print("Username (gcube): ");
			String ans = in.readLine();
			if (ans.length() != 0)
				username = ans;
			System.out.print("Password: ");
			password = in.readLine();


			((Stub) eolus)._setProperty(Call.USERNAME_PROPERTY, username);
			((Stub) eolus)._setProperty(Call.PASSWORD_PROPERTY, password);

			while (!str.equalsIgnoreCase("q")) {
				System.out.print("\n" +
						"Q) To Quit \n" +
						"== Templates ==\n" +
						"1) List my templates\n" +
						"== gCube Operations ==\n" +
						"2) Start container\t" +
						"3) Stop container\t" +
						"4) Configure container\n" +
						"== Virtual Machine provisioning ==\n" +
						"5) Create new VM\t" +
						"6) Shutdown VM\t" +
						"7) Get VM IP\t" +
						"8) Get VM Status\t" +
						"9) List my VMs\n" +

				"");
				str = in.readLine();
				process(eolus, in, str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void process(Eolus eolus, BufferedReader in, String cmd) throws IOException{
		if (cmd.equalsIgnoreCase("1")){ //List user templates
			String[] templts = eolus.getTemplates().getItem();
			System.out.println("Total number of templates: "+ templts.length);
			for (int i = 0; i<templts.length; i++){
				String status = "";
				try {
					status = eolus.getTemplateStatus(templts[i]);
				} catch (MultipleTemplatesException e) {
					e.printStackTrace();
				} catch (UnknownTemplateException e) {
					e.printStackTrace();
				}
				System.out.println(templts[i]+ "       Status:"+status+"");
			}
		}
		if (cmd.equalsIgnoreCase("2")){ //Start container
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			Thread startcontainerthread = new startContainer(eolus,VMname);
			startcontainerthread.start();
		}
		if (cmd.equalsIgnoreCase("3")){ //Stop container
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			String cmdtorun = "gcore-stop-container";
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
		if (cmd.equalsIgnoreCase("4")){ //Configure container
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			System.out.print("Name of GHN Infrastructure: ");
			String infra = in.readLine();
			System.out.print("Name of GHN Scope: ");
			String scope = in.readLine();
			String cmdtorun = "configureGHN.sh "+infra+" "+scope;
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
		if (cmd.equalsIgnoreCase("5")){ //Create VM
			System.out.print("Template of VM: ");
			String template = in.readLine();
			System.out.print("Name of VM: ");
			String VMname = in.readLine();
			System.out.print("VM cores: ");
			int cores = Integer.parseInt(in.readLine());
			System.out.print("VM Ram (in MB): ");
			int ram = Integer.parseInt(in.readLine());
			String[] nets = {"public"};
			StringArray vnets = new StringArray();
			vnets.setItem(nets);
			try {
				eolus.createVM(template, VMname, cores, ram, vnets);
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
		if (cmd.equalsIgnoreCase("6")){ //Shutdown VM
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
		if (cmd.equalsIgnoreCase("7")){ //get VM IP
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
		if (cmd.equalsIgnoreCase("8")){ //get VM Status
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
		if (cmd.equalsIgnoreCase("9")){ //List user VMs
			String[] res;
			try {
				res = eolus.getVMlist().getItem();
				if (res!=null){
					System.out.println("Total number of VMs: "+ res.length);
					for (int i = 0; i<res.length; i++){
						System.out.println(res[i]);
					}
				}
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}


