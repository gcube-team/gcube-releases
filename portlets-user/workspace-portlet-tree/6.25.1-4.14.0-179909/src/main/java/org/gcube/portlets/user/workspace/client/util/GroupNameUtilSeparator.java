package org.gcube.portlets.user.workspace.client.util;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *         May 8, 2014
 *
 */
public class GroupNameUtilSeparator {

	private String stringSeparator = "/";

	public GroupNameUtilSeparator(String separator) {

		if (separator != null && !separator.isEmpty())
			this.stringSeparator = separator;
	}

	/**
	 * Return Either subsequence of name from separatorStartIndex (including
	 * separator) to end, or name passed in input if split.length is less of
	 * separatorStartIndex
	 *
	 * @param name
	 *            name
	 * @param separatorStartIndex
	 *            start
	 * @return sub sequence
	 * @throws Exception
	 *             error
	 */
	public String getSubsequenceName(String name, int separatorStartIndex) throws Exception {

		if (separatorStartIndex < 1)
			throw new Exception("Invalid start separator index: " + separatorStartIndex);

		if (name == null)
			throw new Exception("Invalid name: " + name);

		String[] split = name.split(stringSeparator);

		/*
		 * for (String sp : split) { System.out.println("split: "+sp); }
		 */

		if (split == null || split.length == 0)
			return "";

		String displayName = "";

		if (split.length > separatorStartIndex) {

			for (int i = separatorStartIndex; i < split.length; i++) {
				displayName += stringSeparator + split[i];
			}

		} else
			displayName = name;

		return displayName;
	}

	public static void main(String[] args) {

		GroupNameUtilSeparator filter = new GroupNameUtilSeparator("/");

		String name = "/gcube/devsec/devVRE";

		try {
			String displayedName = filter.getSubsequenceName(name, 2);
			System.out.println("display name: " + displayedName);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
