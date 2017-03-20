package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRTimeSeries;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeSeriesTest {


	static JCRWorkspace ws = null;
	public static final String REMOTE_STORAGE_PATH 		=  	"hl:remotePath";
	public static final String DATA 	  				= 	"jcr:data";


	static Logger logger = LoggerFactory.getLogger(TimeSeriesTest.class);

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {




		ScopeProvider.instance.set("/gcube/devsec");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();

		String name = "test"+ UUID.randomUUID();
		String description = "test";
		List<String> users = new ArrayList<String>();
		users.add("roberto.cirillo");
		WorkspaceSharedFolder folder = ws.createSharedFolder(name, description, users, ws.getRoot().getId());
		System.out.println(folder.getPath());
		folder.setACL(users, ACLType.WRITE_ALL);

		//		List<WorkspaceItem> children = ws.getRoot().getChildren();
		//		for(WorkspaceItem child: children){
		////			JCRWorkspaceItem item = (JCRWorkspaceItem) child;
		//			System.out.println(child.getName() + " - " + child.getType());
		//			try{
		//				JCRTimeSeries item = (JCRTimeSeries) child;
		//				System.out.println(item.getAbsolutePath());
		//				System.out.println(item.getLength());
		//				
		////				ItemDelegate(id=0d657c9b-8c1d-404b-89d3-e4e6ca3aca54, 
		////						name=LargeTimeseries, 
		////						title=LargeTimeseries, 
		////						description=Saved Time Series, 
		////						lastModifiedBy=giancarlo.panichi, 
		////						parentId=88ceb93f-afae-479a-a083-9ee3b9932d87, 
		////						parentPath=/Home/giancarlo.panichi/Workspace/LargeTimeseries, 
		////						lastModificationTime=java.util.GregorianCalendar[time=1371119897386,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="GMT+02:00",offset=7200000,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=2013,MONTH=5,WEEK_OF_YEAR=24,WEEK_OF_MONTH=3,DAY_OF_MONTH=13,DAY_OF_YEAR=164,DAY_OF_WEEK=5,DAY_OF_WEEK_IN_MONTH=2,AM_PM=1,HOUR=0,HOUR_OF_DAY=12,MINUTE=38,SECOND=17,MILLISECOND=386,ZONE_OFFSET=7200000,DST_OFFSET=0], creationTime=java.util.GregorianCalendar[time=1371119897386,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="GMT+02:00",offset=7200000,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=2013,MONTH=5,WEEK_OF_YEAR=24,WEEK_OF_MONTH=3,DAY_OF_MONTH=13,DAY_OF_YEAR=164,DAY_OF_WEEK=5,DAY_OF_WEEK_IN_MONTH=2,AM_PM=1,HOUR=0,HOUR_OF_DAY=12,MINUTE=38,SECOND=17,MILLISECOND=386,ZONE_OFFSET=7200000,DST_OFFSET=0], 
		////						properties={}, 
		////						path=/Home/giancarlo.panichi/Workspace/LargeTimeseries, 
		////						owner=82b12ed3-85c9-48fd-b9cd-ea8d7a16f2e6, 
		////						primaryType=nthl:timeSeriesItem, 
		////						lastAction=CREATED, 
		////						shared=false, locked=false, accounting=null, 
		////						metadata={},
		////						content={hl:sourceName=LargeTimeseries, 
		////						jcr:mimeType=application/zip, 
		////						hl:created=13-May-13 16.54.40, 
		////						hl:size=<long>3346736</long>, 
		////						jcr:content=<org.gcube.common.homelibary.model.items.type.ContentType>GENERAL</org.gcube.common.homelibary.model.items.type.ContentType>, hl:sourceId=24bcf820-7c78-11e2-ae70-fa57338d3ce0, hl:dimension=<long>645300</long>, hl:description=Saved Time Series, hl:creator=pasquale.pagano, hl:publisher=D4Science consortium, hl:headerLabels=<list/>, hl:rights=This timeseries is for sampling the capabilities of the environment. Any other use is not allowed. , hl:title=LargeTimeseries, hl:id=09ca0fe0-bbdd-11e2-aa99-ceb7e45f6184, hl:remotePath=/Home/giancarlo.panichi/Workspace25152f8a-d42d-4ca5-95d6-7b42c8960a7c})
		//
		//				
		//			} catch (Exception e) {
		////				e.printStackTrace();
		//			}
		//		}

	}

}
