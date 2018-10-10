import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class TestMain {
	
	private static final String USERNAME_SEPARATOR = ";";
	private static boolean isShared;
	private static String sharedWith;
	
	public static void main(String[] args) {
		
		try {
			List<String> listUsername = new ArrayList<String>();
			listUsername.add("francesco.mangiacrapa");
			listUsername.add("costantino.perciante");
			listUsername.add("leoanrdo.candela");
			addUsernameToShareWith(listUsername);
			
			System.out.println(sharedWith);
			
			listUsername.add("pasquale.pagano");
			addUsernameToShareWith(listUsername);
			
			System.out.println(sharedWith);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	/**
	 * Gets the shared with.
	 *
	 * @return the shared with
	 */
	public static List<String> getSharedWith() {
		String[] out = StringUtils.splitByWholeSeparator(TestMain.sharedWith, USERNAME_SEPARATOR);
		if(out!=null) {
			return Arrays.asList(out);
		}
		return null;
			
	}

	/**
	 * Sets the shared with.
	 *
	 * @param sharedWith the new shared with
	 */
	public static void setSharedWith(List<String> sharedWith) {
		if(sharedWith!=null && sharedWith.size()>0) {
			TestMain.sharedWith = StringUtils.join(sharedWith, USERNAME_SEPARATOR);
			isShared = true;
		}else
			isShared = false;
			
		
	}
	
	/**
	 * Adds the users to share with.
	 *
	 * @param listUsername the add shared with
	 */
	public static void addUsernameToShareWith(List<String> listUsername) {
		
		if(listUsername==null || listUsername.size()==0)
			return;
		
		List<String> shared = getSharedWith();
		
		if(shared==null)
			shared = new ArrayList<String>();
		
		List<String> newShared = new ArrayList<String>();
		for (String login : listUsername) {
			if(!shared.contains(login)) {
				newShared.add(login);
			}
		}
		
		newShared.addAll(shared);
		setSharedWith(newShared);
	}


}
