package org.gcube.contentmanagement.blobstorage.service.directoryOperation;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;


/**
 * Is used only by terrastore client: Define a directory object.
 * Is useful for the operations on the directory tree
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class DirectoryEntity {

	private String directory;
	private String name;
	private String author;
	private String a;
	private String b;
	private String c;
	private String d;
	private String e;

	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public DirectoryEntity(){
		
	}
	
	public DirectoryEntity(String dir, String author){
		setDirectory(dir);
		setAuthor(author);
	}
	
	public DirectoryEntity(String dir, String author, MyFile file){
		setDirectory(dir);
		setAuthor(author);
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int setGenericVariable(String name, String value){
		int codeError=0;
		if(a==null){
			a=name+"%"+value;
		}else if(b==null){
			b=name+"%"+value;
		}else if(c==null){
			c=name+"%"+value;
		}else if(d==null){
			d=name+"%"+value;
		}else if(e==null){
			e=name+"%"+value;
		}else 
			codeError=-1;
		return codeError;
	}


}
