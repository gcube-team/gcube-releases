package org.gcube.usecases.ws.thredds;

import java.io.IOException;
import java.util.Date;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.usecases.ws.thredds.engine.impl.WorkspaceUtils;

public class WorkspaceAccounting {

	public static void main(String[] args) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException, WorkspaceFolderNotFoundException, HomeNotFoundException, UserNotFoundException, NumberFormatException, ItemNotFoundException {
		TestCommons.setScope();
		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		
		
//		WorkspaceFolder folder=ws.getRoot().createFolder("Accounting", "test purposes");
//		
//		
//		WorkspaceFolder subFolder=folder.createFolder("SubFolder", "Will be removed");
//
//		
//		ExternalFile toBeRemovedFile=folder.createExternalFileItem("The file", "file to be removed", "application/xml", File.createTempFile("tmp", ".tmp"));
//		
//		subFolder.remove();
//		toBeRemovedFile.remove();
//		
//		
//		for(AccountingEntry entry:folder.getAccounting()) {
//			try {
//				
//				
//				Date eventTime=entry.getDate().getTime();
//				String toDeleteRemote=null;
//				switch(entry.getEntryType()) {
//				case REMOVAL:{					
//					AccountingEntryRemoval removalEntry=(AccountingEntryRemoval) entry;
//					System.out.println(removalEntry.getItemName() +"REMOVED. FolderItemType "+removalEntry.getFolderItemType()+" ItemType "+removalEntry.getItemType());
//					
//					
//					break;
//				}
//				case RENAMING:{					
//					AccountingEntryRenaming renamingEntry=(AccountingEntryRenaming) entry;					
//					toDeleteRemote=renamingEntry.getOldItemName();				
//					
//				}
//				case CUT:{					
//					AccountingEntryCut cut = (AccountingEntryCut) entry;
//					toDeleteRemote=cut.getItemName();
//					break;
//				}
//				}
//				
//			}catch(Throwable t) {
//				t.printStackTrace();
//			}
//		}
//
//	}

		
		
		System.out.println(WorkspaceUtils.isModifiedAfter(ws.getItemByPath(TestCommons.getTestFolder().getPath()+"/mySub/dissolved_oxygen_annual_5deg_ENVIRONMENT_BIOTA_.nc"), new Date(0l)));
		
		
	}
}
