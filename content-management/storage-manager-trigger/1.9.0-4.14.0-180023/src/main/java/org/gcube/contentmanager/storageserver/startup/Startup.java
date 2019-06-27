package org.gcube.contentmanager.storageserver.startup;

import java.util.Arrays;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.contentmanager.storageserver.consumer.FolderAccountingConsumer;
import org.gcube.contentmanager.storageserver.consumer.UserAccountingConsumer;
import org.gcube.contentmanager.storageserver.data.CubbyHole;
import org.gcube.contentmanager.storageserver.data.ReadingMongoOplog;

public class Startup {
    
	private static String user;
	private static String password;
	private static String accountingUser;
	private static String accountingPassword;
	private static String scope;
	private static List<ServiceEndpoint> se;
	
	public static void main(String[] args) {
		
		for (int i=0; i<args.length;i++) 
			System.out.println("param N." +i + ": " + args[i]);
		
		if(args.length != 2 && args.length != 4){
			System.out.println("Usage:");
			System.out.println("\tjava  Startup scope ip user password\n\n");
			System.out.println("Example:");
			System.out.println("\tjava  Startup /gcube/devsec localhost pippo pluT0\n");
			System.out.println("or ");
			System.out.println("Usage:");
			System.out.println("\tjava  Startup scope ip\n\n");
			System.out.println("Example:");
			System.out.println("\tjava  Startup /gcube  localhost\n\n");

			return;
		}
		scope=args[0];
		String oplogServer= args[1];
//		user=args[2];
//		password=args[3];
		Configuration cfg=new Configuration(scope, false);
		se=cfg.getStorageServiceEndpoint();
		accountingUser=cfg.getAccountingUser(se);
		accountingPassword=cfg.getAccountingPassword(se);
		String[] server=retrieveServerConfiguration(cfg);
		
		List<String> dtsHosts=null;//retrieveDTSConfiguration(cfg);
        CubbyHole c1 = new CubbyHole();
        CubbyHole c2 = null;//new CubbyHole();
        startProducer(args, oplogServer, server, c1, c2);
        startUserAccountingConsumer(args, server, dtsHosts, c1);
//        startFolderAccountingConsumer(args, server, c2);
    }

	private static void startFolderAccountingConsumer(String[] args,
			String[] server, CubbyHole c2) {
		FolderAccountingConsumer fsConsumer=null;
        if(user!=null && password != null)
        	fsConsumer=new FolderAccountingConsumer(server, user, password, c2, 1);
        else
        	fsConsumer=new FolderAccountingConsumer(server, c2, 1);
        fsConsumer.start();
	}

	private static void startUserAccountingConsumer(String[] args,
			String[] server, List<String> dtsHosts, CubbyHole c1) {
		UserAccountingConsumer ssConsumer=null;
        if(user!=null && password != null)
        	ssConsumer=new UserAccountingConsumer(server, user, password, c1, 1, dtsHosts);
        	
        else //if(args.length == 4)
        	ssConsumer=new UserAccountingConsumer(server, c1, 1, dtsHosts);
//        else{
//        	throw new IllegalArgumentException("input parameter are incorrect");
//        }
        ssConsumer.start();
	}

	private static void startProducer(String[] args, String oplogServer,
			String[] server, CubbyHole c1, CubbyHole c2) {
		ReadingMongoOplog producer=null;
        if((user !=null) && (password!= null))
        	producer=new ReadingMongoOplog( Arrays.asList(oplogServer),  accountingUser, accountingPassword, c1, c2, 1 );
        else //if(args.length == 2)
        	producer=new ReadingMongoOplog( Arrays.asList(server), c1, c2, 1 );
//        else{
//        	throw new IllegalArgumentException("input parameter are incorrect");
//        }
        producer.start();
	}

	private static String[] retrieveServerConfiguration(Configuration c) {
		String[] server= c.getServerAccess(se);
		if(user == null)
			user=c.getUsername();
		if(password == null)
			password=c.getPassword();
		return server;
	}
	
	private static List<String> retrieveDTSConfiguration(Configuration c){
		return c.retrieveDTSHosts();
	}
	
	

}
