/**
 * 
 */
package org.gcube.common.homelibrary.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;


import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeFolderVisitor {
	
	protected static final String tab = "  ";
	protected static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy hh:mm:ss");
	protected String indentation = "";
	
	protected PrintStream output;
	
	/**
	 * 
	 */
	public HomeFolderVisitor()
	{
		output = System.out;
	}
	
	/**
	 * @param output the stream output.
	 */
	public HomeFolderVisitor(PrintStream output) {
		this.output = output;
	}

	protected void indent()
	{
		indentation += tab;
	}
	
	protected void outdent()
	{
		indentation = indentation.substring(tab.length());
	}
	
	/**
	 * @param folder teh folder to visit.
	 * @throws InternalErrorException if an internal error occurs.
	 */
/*	public void visit(DataFolder folder) throws InternalErrorException
	{
		visitFolder(folder);
	}
	
	protected void visit(DataAreaItem item) throws InternalErrorException
	{
		if (item.isFolder()) visitFolder((DataFolder) item);
		else visitFile((DataFile) item);
	}
	
	protected void visitFolder(DataFolder folder) throws InternalErrorException
	{
		println("/"+folder.getName()+"/");
		indent();
		for (DataAreaItem item:folder.listFiles()){
			visit(item);
		}
		outdent();
		
	}
	
	protected void visitFile(DataFile file){
		println(file.getName()+" "+((file.getCreationTime()!=null)?sdf.format(file.getCreationTime()):""));
	}
	
	protected void println(String line)
	{
		output.println(indentation+line);
	}*/

}
