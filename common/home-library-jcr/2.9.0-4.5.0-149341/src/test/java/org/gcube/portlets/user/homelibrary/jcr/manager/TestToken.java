package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.jcr.PathNotFoundException;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class TestToken {

	static int count = 0;

	static List<String> vres;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException, ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

		vres = new ArrayList<String>();
		vres.add("/d4science.research-infrastructures.eu/gCubeApps/SoBigData.eu");
		vres.add("/d4science.research-infrastructures.eu/gCubeApps/SoBigData.it");
		vres.add("/d4science.research-infrastructures.eu/SoBigData/SoBigDataLab");
		vres.add("/d4science.research-infrastructures.eu/SoBigData/ResourceCatalogue");
		vres.add("/d4science.research-infrastructures.eu/SoBigData/CityOfCitizens");
		vres.add("/d4science.research-infrastructures.eu/SoBigData/SocietalDebates");
		vres.add("/d4science.research-infrastructures.eu/SoBigData/WellBeingAndEconomy");		
		vres.add("/d4science.research-infrastructures.eu/SoBigData/SMAPH");
		vres.add("/d4science.research-infrastructures.eu/SoBigData/TagMe");


		String startDate = "2017-02-01";
		String endDate = "2017-03-01";
		Date start = format.parse(startDate);
		Date end = format.parse(endDate);

		for (String vre: vres){
			ScopeProvider.instance.set(vre);

			try {

				HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();

				String user = getVREName(vre)  + "-Manager";
				Home home = manager.getHome(user);

				JCRWorkspace ws = (JCRWorkspace) home.getWorkspace();

				JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/MySpecialFolders/"+getVREName(vre));

				System.out.println("Analyzing "+ vre + " from " +  startDate + " to " + endDate);
				
				getStatsByMonth(item, start, end);

				System.out.println(count + " for vre " + vre);
				count = 0;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}




	private static void getStatsByMonth(WorkspaceItem root, Date start, Date end) throws InternalErrorException {

		List<? extends WorkspaceItem> children;
		if (root.isFolder()){
			children = root.getChildren();
			for (WorkspaceItem child: children)
				getStatsByMonth(child, start, end);
		}else {

			try{

				List<AccountingEntry> accounting = root.getAccounting();
				for (AccountingEntry entry: accounting){

					switch (entry.getEntryType()) {
					case CREATE: case UPDATE: case READ:

						Calendar calendar = entry.getDate();
						if (calendar.after(dateToCalendar(start)) && calendar.before(dateToCalendar(end))){
							count++;
							//							System.out.println("** " + count + ") "+ calendar.getTime());
						}

						break;

					default:
						break;
					}

				}
			}catch (Exception e){
				System.out.println( " ERROR  " +  root.getPath());
			}
		}

	}



	private static String getVREName(String vre) {
		Validate.notNull(vre, "scope must be not null");

		String newName;
		if (vre.startsWith(JCRRepository.PATH_SEPARATOR))			
			newName = vre.replace(JCRRepository.PATH_SEPARATOR, "-").substring(1);
		else
			newName = vre.replace(JCRRepository.PATH_SEPARATOR, "-");
		return newName;
	}


	public static Calendar dateToCalendar(Date date ) 
	{ 
		Calendar cal = null;
		try {   
			DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
			date = (Date)formatter.parse(date.toString()); 
			cal=Calendar.getInstance();
			cal.setTime(date);
		}
		catch (ParseException e)
		{
			System.out.println("Exception :"+e);  
		}  
		return cal;
	}

}

