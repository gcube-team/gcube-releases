package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FileArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FileType;

public class File extends DataModel{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((uuri == null) ? 0 : uuri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		File other = (File) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (uuri == null) {
			if (other.uuri != null)
				return false;
		} else if (!uuri.equals(other.uuri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "File [type=" + type + ", uuri=" + uuri + ", name=" + name + "]";
	}

	private FileType type=FileType.InternalProfile;
	private String uuri;
	private String name;

	public File(FileType type,String uuri,String name) {
		this.type=type;
		this.uuri=uuri;
		this.name=name;
	}
	
	public FileType getType() {
		return type;
	}
	public void setType(FileType type) {
		this.type = type;
	}
	public String getUuri() {
		return uuri;
	}
	public void setUuri(String uuri) {
		this.uuri = uuri;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public File (org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.File toLoad){
		super();
		this.setName(toLoad.name());
		this.setType(FileType.valueOf(toLoad.type()));
		this.setUuri(toLoad.url());
	}

	public static ArrayList<File> load(FileArray toLoad){
		ArrayList<File> toReturn= new ArrayList<File>();
		if((toLoad!=null)&&(toLoad.theList()!=null))
			for(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.File f: toLoad.theList())
				toReturn.add(new File(f));
		return toReturn;
	}

	public static FileArray toStubsVersion(List<File> toConvert){
		List<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.File> list=new ArrayList<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.File>();
		if(toConvert!=null)
			for(File obj:toConvert)
				list.add(obj.toStubsVersion());
		return new FileArray(list);
	}

	public org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.File toStubsVersion(){
		org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.File toReturn= new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.File();
		toReturn.name(this.name);
		toReturn.type(this.type.toString());
		toReturn.url(this.uuri);
		return toReturn;
	}
}
