package org.gcube.datatransfer.portlets.user.server.workers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ConnectionHTTP {
	public int autoId;
	public String usedURL;
	public String username;
	public String password;
	public String specificPath;
	public HttpURLConnection connection = null;
	public BufferedReader rd  = null;
	public StringBuilder sb = null;
	List<String> errors;


	public ConnectionHTTP(String url, String specificFolder, String user, String pass){
		if(!url.endsWith("/"))this.usedURL=url+"/";
		else this.usedURL=url;
		errors=new ArrayList<String>();

		this.username=user;
		this.password=pass;
		this.specificPath=specificFolder;
		if(!specificPath.endsWith("/"))specificPath=specificPath+"/";
		this.autoId=0;
	}

	public FolderDto process() {
		if(usedURL==null){
			errors.add("ConnectionHTTP(process) - usedURL is null");
			return null;
		}

		FolderDto empty= makeFolder("");
		FolderDto folder = makeFolder(specificPath);

		String line = null;

		URL serverAddress = null;
		try {
			serverAddress = new URL(usedURL+specificPath);
			//set up out communications stuff
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection)serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(15000);

			// authorization
			if(username!=null && password !=null){
				if(username.compareTo("")!=0 && password.compareTo("")!=0){
					String userPassword = username + ":" + password;
					String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
					connection.setRequestProperty("Authorization", "Basic " + encoding);
				}
			}
			//connection
			connection.connect();

			//read the result from the server
			rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null)sb.append(line + '\n');
			// optional printing of web page
			//System.out.println("ConnectionHTTP - printing the WebPage:\n\n"+sb.toString()+"\n\n");

			List<String> elements = retrieveListing(sb.toString());

			if(elements==null){
				System.out.println("ConnectionHTTP - list is empty - returned value is null");
				folder.addChild(empty);
			}
			else{
				for(String element: elements){
					if(element.endsWith("/")){
						FolderDto subFolder = makeFolder(specificPath+element);
						subFolder.addChild(empty);
						folder.addChild(subFolder);
					}
					else{
						FolderDto child = makeFolder(specificPath+element);
						folder.addChild(child);
					}
					//System.out.println("- "+element);
				}
				if(folder.getChildren().size()==0)folder.addChild(empty);
			}

		} catch (MalformedURLException e) {
			errors.add("ConnectionHTTP(process) - MalformedURLException\n"+e.getMessage());
			e.printStackTrace();
			return null;
		} catch (ProtocolException e) {
			errors.add("ConnectionHTTP(process) - ProtocolException\n"+e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			errors.add("ConnectionHTTP(process) - IOException\n"+e.getMessage());
			e.printStackTrace();
			return null;
		}

		return folder;
	}

	public void disconnect(){
		//close the connection, set all objects to null
		connection.disconnect();
		rd = null;
		sb = null;
		connection = null;
	}

	/*
	 * retrieveListing
	 * input: String with HtmlText
	 * returns: List of Strings with the elements
	 * Details : It has two methods
	 * The first one takes the elements by tag matching 'a' and then retrieves
	 * the value of 'href' .. 
	 * The second one takes the elements by tag matching 'file' or 'dir' or both and 
	 * then retrieves the value of 'href'
	 * In both cases we check with the 'handleHrefLink' function if the href value is valid
	 */
	public List<String> retrieveListing(String HtmlText){
		List<String> list = new ArrayList<String>();
		Document doc = Jsoup.parse(HtmlText);

		//method 1 : getting the elements by tag 'a' .. 
		Elements linksMethod1 = doc.getElementsByTag("a");
		if(linksMethod1!=null){
			for(Element link: linksMethod1){
				String linkHref = link.attr("href");

				String handledHrefLink = handleHrefLink(linkHref,link.text());
				if(handledHrefLink==null)continue;
				else list.add(handledHrefLink);
			}

			list = removeDuplicates(list);
			if(list!=null)if(list.size()>0){
		//		System.out.println("ConnectionHTTP - method 1 worked ..");
				return list;  // method 1 worked .. 
			}
		}
	//	System.out.println("ConnectionHTTP - method 1 did not work");
		//if we reach this point, means that method 1 did not work ... 
		//method 2: getting the elements by tag 'file' or 'dir' (common in some repositories)
		Elements linksMethod2 = new Elements();
		Elements links1 = doc.getElementsByTag("file");
		Elements links2 = doc.getElementsByTag("dir");
		if(links1!=null)linksMethod2.addAll(links1);
		if(links2!=null)linksMethod2.addAll(linksMethod2.size(),links2);

		if(linksMethod2.size()>0){
			for(Element link: linksMethod2){
				String linkHref = link.attr("href");

				String handledHrefLink = handleHrefLink(linkHref,link.text());
				if(handledHrefLink==null)continue;
				else list.add(handledHrefLink);
			}

			list = removeDuplicates(list);
			if(list!=null)if(list.size()>0){
		//		System.out.println("ConnectionHTTP - method 2 worked ..");
				return list;       //method 2 worked .. 
			}
		}
		//if we reach this point, means that also method 2 did not work ... 
	//	System.out.println("ConnectionHTTP - method 2 did not work");

		return null;

	}

	public List<String> removeDuplicates(List<String> list){
		if(list==null)return null;
		List<String> tmpList = new ArrayList<String>();
		//set does not allow duplicate 
		HashSet<String> set = new HashSet<String>(list);
		tmpList.addAll(set);
		//we sort the list because after removing duplicates 'set' has changed the order a lot .. 
		Collections.sort(tmpList);
		return tmpList;
	}

	/*
	 * handleHrefLink
	 * input: String with the href value
	 * input: String with the text value if exist
	 * returns: The valid href value
	 */
	public String handleHrefLink(String href, String text){
		String linkHref = href;
		String linkText = text;
		// handle complete URL listings
		if (linkHref.startsWith("http:") || linkHref.startsWith("https:")) {
			try {
				linkHref = new URL(linkHref).getPath();
				String url = new URL(usedURL+specificPath).getPath();

				if (!linkHref.startsWith(url)) {
					// ignore URLs which aren't children of the base URL
					return null;
				}
				linkHref = linkHref.substring(url.length());
			} catch (Exception ignore) {
				// incorrect URL , ignore
				return null;
			}
		}
		// we are only interested in sub-URLs, not parent URLs, so skip this one
		if (linkHref.startsWith("../")) return null;

		// we are not interested in email links, so skip this one
		if (linkHref.startsWith("mailto:")) return null;

		//if there is no text, we return the linkHref
		if(linkText==null)return linkHref;
		if(linkText.compareTo("")==0)return linkHref;

		//if there is text, we exclude those where they do not match .. 
		// href will never be truncated, text may be truncated by apache
		
		//we modify the linkHref so that we can compare it with the text .. 
		// absolute href: convert to relative one
		String tmpLinkHref=linkHref;
		if (tmpLinkHref.startsWith("/")) {
			int slashIndex = tmpLinkHref.substring(0, tmpLinkHref.length() - 1).lastIndexOf('/');
			tmpLinkHref = tmpLinkHref.substring(slashIndex + 1);
		}
		// relative to current href: convert to simple relative one
		if (tmpLinkHref.startsWith("./")) {
			tmpLinkHref = tmpLinkHref.substring("./".length());
		}
		
		if (linkText.endsWith("..>")) {
			// text is probably truncated, we can only check if the href starts with text
			if (!tmpLinkHref.startsWith(linkText.substring(0, linkText.length() - 3))) {
				return null;
			}
		} else if (linkText.endsWith("..&gt;")) {
			// text is probably truncated, we can only check if the href starts with text
			if (!tmpLinkHref.startsWith(linkText.substring(0, linkText.length() - 6))) {
				return null;
			}
		} else {
			// text is not truncated, so it must match the url after stripping optional
			// trailing slashes
			String strippedHref = tmpLinkHref.endsWith("/") ? tmpLinkHref.substring(0, tmpLinkHref.length() - 1) : tmpLinkHref;
			String strippedText = linkText.endsWith("/") ? linkText.substring(0, linkText.length() - 1) : linkText;
			if (!strippedHref.equalsIgnoreCase(strippedText)) {
				return null;
			}
		}

		return linkHref;
	}

	public void printFolder(FolderDto folder, int indent){
		for(int i = 0; i < indent; i++) System.out.print("\t");
		System.out.println("fold : name="+folder.getName() +" - id="+folder.getId());

		List<FolderDto> tmpListOfChildren = folder.getChildren();
		if(tmpListOfChildren!=null){
			for(FolderDto tmp : tmpListOfChildren){ //first the files
				if(tmp.getChildren().size() <= 0){
					if((tmp.getName().compareTo("")==0))continue;
					for(int i = 0; i < indent; i++) System.out.print("\t");
					String type= "";
					if((tmp.getName().substring(tmp.getName().length()-1,tmp.getName().length())).compareTo("/")==0)type="fold";
					else type="file";
					System.out.println(type+" : name="+tmp.getName()+" - id="+tmp.getId());
				}
			}		    	
			for(FolderDto tmp : tmpListOfChildren){ //then the folders
				if(tmp.getChildren().size() > 0){
					printFolder(tmp,indent+1);
				}
			}
		}		    
	}

	public FolderDto makeFolder(String name) {
		FolderDto theReturn = new FolderDto(++autoId, name);
		theReturn.setChildren((List<FolderDto>) new ArrayList<FolderDto>());
		return theReturn;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}


}
