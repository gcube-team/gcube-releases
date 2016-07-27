package org.gcube.portlets.user.messages.shared;

import java.util.Date;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class FileGridModel extends FileModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected FileGridModel(){
	}
	
	public FileGridModel(String identifier, String name, String path, Date creationDate, FileModel parent, long size, boolean isDirectory) {
		super(identifier, name, parent, isDirectory);
		
		setCreationDate(creationDate);
		setSize(size);
	}
	
	
	public FileGridModel(String identifier, String name, Date creationDate, FileModel parent, long size, boolean isDirectory) {
		super(identifier, name, parent, isDirectory);
		
		setCreationDate(creationDate);
		setSize(size);
	}
	
	private void setSize(long size) {
		set(ConstantsPortletMessages.SIZE, size);	
	}
	
	public long getSize() {
		return (Long) get(ConstantsPortletMessages.SIZE);	
	}

	private void setCreationDate(Date creationDate) {
		set(ConstantsPortletMessages.GRIDCOLUMNCREATIONDATE, creationDate);	
		
	}
	
	public Date getCreationDate(){
		return (Date) get(ConstantsPortletMessages.GRIDCOLUMNCREATIONDATE);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileGridModel) {
			FileGridModel mobj = (FileGridModel) obj;
			return getIdentifier().equals(mobj.getIdentifier());
		}
		return super.equals(obj);
	}
}
