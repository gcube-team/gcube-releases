package org.gcube.common.eolusclient;

import java.rmi.RemoteException;

public class startContainer extends Thread {
	Eolus eolus;
	String VMname;
	public startContainer(Eolus eolus, String VMname) {
		this.eolus = eolus;
		this.VMname = VMname;
	}
	public void run(){
		String cmdtorun = "startGHN.sh";
		String[] res;

		try {	
			res = eolus.execCMD(cmdtorun, VMname).getItem();
			if (res.length > 1)
				System.out.println("Stdout: "+res[0]);				
			if (res.length > 2)
				System.out.println("Stderr: "+res[1]);				
		} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}

	}
	
}
