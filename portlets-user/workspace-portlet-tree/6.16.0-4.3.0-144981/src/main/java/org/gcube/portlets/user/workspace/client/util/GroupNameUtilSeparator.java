package org.gcube.portlets.user.workspace.client.util;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 8, 2014
 *
 */
public class GroupNameUtilSeparator {
	
	private String stringSeparator = "/";


	/**
	 * 
	 * @param separator - the string separator
	 */
	public GroupNameUtilSeparator(String separator) {
		
		if(separator!=null && !separator.isEmpty())
			this.stringSeparator = separator;
	}
	
	/**
	 * Return Either subsequence of name from separatorStartIndex (including separator) to end,
	 * or name passed in input if split.length < separatorStartIndex
	 * @see main
	 * 
	 * 
	 * @param name
	 * @param separatorStartIndex - start index must be 1
	 * @return
	 * @throws Exception 
	 */
	public String getSubsequenceName(String name, int separatorStartIndex) throws Exception{

		if(separatorStartIndex<1)
			throw new Exception("Invalid start separator index: "+separatorStartIndex);
		
		if(name==null)
			throw new Exception("Invalid name: "+name);
		
		String[] split = name.split(stringSeparator);
		
		
		/*for (String sp : split) {
			System.out.println("split: "+sp);
		}*/
		
		
		if(split==null || split.length==0)
			return "";
		

		String displayName ="";

		if(split.length>separatorStartIndex){
			
			for (int i = separatorStartIndex; i < split.length; i++) {
				displayName+= stringSeparator+split[i];
			}
			
		}else
			displayName = name;
		
		return displayName;
	}
	

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		GroupNameUtilSeparator filter = new GroupNameUtilSeparator("/");
		
		String name = "/gcube/devsec/devVRE";
		
		try {
			String displayedName = filter.getSubsequenceName(name, 2);
			System.out.println("display name: "+displayedName);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
