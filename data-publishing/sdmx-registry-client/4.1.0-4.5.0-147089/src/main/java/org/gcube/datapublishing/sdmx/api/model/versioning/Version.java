package org.gcube.datapublishing.sdmx.api.model.versioning;

public class Version implements Comparable<Version>
{
	private int [] numericElements;
	private String version;
	private String stringElement;

	
	
	public Version (String versionString)
	{
		if (versionString == null || versionString.trim().length()==0)
		{
			this.numericElements = new int []{0};
			this.stringElement = "";
			this.version = "0";
		}
		else
		{
			this.version = versionString;
			String [] versionElements = versionString.split("-");
			
			if (versionElements.length >1) this.stringElement = versionElements [1];
			else this.stringElement = "";
			
			String [] versionNumbers = versionElements [0].split("\\.");
			this.numericElements = new int [versionNumbers.length];
			
			for (int i = 0; i< versionNumbers.length; i++)
			{
				this.numericElements[i] = Integer.parseInt(versionNumbers [i]);
			}
			
		}

	}
	
	
	
	
	public String getVersion() {
		return version;
	}




	@Override
	public int compareTo(Version version) 
	{
		int lenght1 = this.numericElements.length;
		int lenght2 = version.numericElements.length;
		
		int comparisonLenght = lenght1<= lenght2 ? lenght1 : lenght2;
		
		for (int i =0; i< comparisonLenght;i++)
		{
			if (this.numericElements [i] < version.numericElements [i]) return -1;
			else if (this.numericElements [i] > version.numericElements [i]) return 1;
		}
		
		// If I am still here means that all the numeric elements are equal
		
		// I will check if the longest version number contains non zero elements
		
		for (int y = comparisonLenght; y<lenght1; y++)
		{
			if (this.numericElements [y] > 0) return 1;
		}

		for (int y = comparisonLenght; y<lenght2; y++)
		{
			if (version.numericElements [y] > 0) return -1;
		}
		
		// If I am still here I am going to check the string part
		
		if (this.stringElement.trim().length()>0 && version.stringElement.trim().length() == 0) return 1;
		else if (this.stringElement.trim().length() ==0 && version.stringElement.trim().length() > 0) return -1;

		// if I am still here the versions are considered equal
		
		return 0;
	}
	
	@Override
	public String toString() {
		return this.version;
	}

//	public static void main(String[] args) {
//		
//		Version version1 = new Version("2.1.3");
//		Version version2 = new Version("2.1.3");
//		
//		System.out.println(version1.compareTo(version2));
//
//	}

}
