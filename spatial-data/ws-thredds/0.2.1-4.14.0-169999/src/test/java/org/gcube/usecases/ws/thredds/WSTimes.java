package org.gcube.usecases.ws.thredds;

import java.io.IOException;
import java.net.MalformedURLException;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;

public class WSTimes {

	public static void main(String[] args) throws WorkspaceFolderNotFoundException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, MalformedURLException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException {
		TestCommons.setScope();
		WorkspaceFolder folder=TestCommons.getTestFolder();
		for(WorkspaceItem item : folder.getChildren()) {
			printDates(item);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			
			}
			item.getProperties().addProperty("Fake prop", "fake value");
			printDates(item);
		}
	}

	
	public static final void printDates(WorkspaceItem item) throws InternalErrorException {
		System.out.println("ITEM : "+item.getName());
		System.out.println("Creation Date : "+Constants.DATE_FORMAT.format(item.getCreationTime().getTime()));
		System.out.println("Creation Date : "+Constants.DATE_FORMAT.format(item.getLastModificationTime().getTime()));
	}
	
}
