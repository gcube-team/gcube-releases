package org.gcube.portlets.widgets.workspacesharingwidget.server.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 25, 2014
 *
 */
public class DiffereceBeetweenInfoContactModel {
	
	
	private List<InfoContactModel> listOne;
	private List<InfoContactModel> listTwo;
	
	/**
	 * Get difference between listA and listB 
	 * @param listA
	 * @param listB
	 */
	public DiffereceBeetweenInfoContactModel(List<InfoContactModel> listA, List<InfoContactModel> listB){
		
		this.listOne = listA;
		this.listTwo = listB;
		
	}
	
	/**
	 * 
	 * @return what is in listA that is not in listB.
	 */
	public List<InfoContactModel> getDifferentsContacts(){
		
		if(this.listOne==null)
			return new ArrayList<InfoContactModel>();
		
		if(this.listTwo==null || this.listTwo.size()==0)
			return this.listOne;
		
		List<InfoContactModel> difference = new ArrayList<InfoContactModel>();
		
		boolean found;
		
		for (InfoContactModel o1 : listOne) {
			found = false;
			for (InfoContactModel o2 : listTwo) {
				if(compare(o1,o2)==0){
					found = true;
					break;
				}
			}
			
			if(!found)
				difference.add(o1);
		}
		
		return difference;
		
	}
	
	/**
	 * 
	 * @param o1
	 * @param o2
	 * @return 0 if and only if o1.getName().compareTo(o2.getName())==0 && (o1.getLogin().compareTo(o2.getLogin())==0) is true
	 */
	public int compare(InfoContactModel o1, InfoContactModel o2) {
		
		if (o1 == null) {
	        return -1;
	    } else if (o2 == null) {
	        return 1;
	    }
		
	    if (o1.getName().compareTo(o2.getName())==0 && (o1.getLogin().compareTo(o2.getLogin())==0))
	    	return 0;
	    else
	    	return -2;
	}
	
	
	/**
	 * test
	 * @param args
	 */
	/*public static void main(String[] args) {
		
		List<InfoContactModel> listA = new ArrayList<InfoContactModel>();
		listA.add(new InfoContactModel("federico.defaveri", "federico.defaveri", "Federico de Faveri", false));
		listA.add(new InfoContactModel("antonio.gioia", "antonio.gioia", "Antonio Gioia",false));
		listA.add(new InfoContactModel("fabio.sinibaldi", "fabio.sinibaldi", "Fabio Sinibaldi",false));
		listA.add(new InfoContactModel("pasquale.pagano", "pasquale.pagano", "Pasquale Pagano",false));
		listA.add(new InfoContactModel("francesco.mangiacrapa", "francesco.mangiacrapa", "Francesco Mangiacrapa",false));
		listA.add(new InfoContactModel("massimiliano.assante", "massimiliano.assante", "Massimiliano Assante",false));
		
		List<InfoContactModel> listB = new ArrayList<InfoContactModel>();
		
		listB.add(new InfoContactModel("federico.defaveri", "federico.defaveri", "Federico de Faveri",false));
		listB.add(new InfoContactModel("fabio.sinibaldi", "fabio.sinibaldi", "Fabio Sinibaldi",false));
		listB.add(new InfoContactModel("antonio.gioia", "antonio.gioia", "Antonio Gioia",false));
		listB.add(new InfoContactModel("pasquale.pagano", "pasquale.pagano", "Pasquale Pagano",false));
		
		DiffereceBeetweenInfoContactModel diff = new DiffereceBeetweenInfoContactModel(listA, listB);
		
		System.out.println("the differce is: "+diff.getDifferentsContacts());

	}*/
}
