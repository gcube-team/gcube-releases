package org.gcube.portlets.user.workspace.server.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;


/**
 * The Class DifferenceBetweenInfoContactModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Nov 19, 2015
 */
public class DifferenceBetweenInfoContactModel {
	
	
	private List<InfoContactModel> listOne;
	private List<InfoContactModel> listTwo;
	
	/**
	 * Get difference between listA and listB .
	 *
	 * @param listA the list a
	 * @param listB the list b
	 */
	public DifferenceBetweenInfoContactModel(List<InfoContactModel> listA, List<InfoContactModel> listB){
		this.listOne = listA;
		this.listTwo = listB;
	}
	
	/**
	 * Gets the differents contacts.
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
				if(InfoContactModel.COMPARATORLOGINS.compare(o1, o2)==0){
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
	 * test.
	 *
	 * @param args the arguments
	 */
	/*public static void main(String[] args) {
		
		List<InfoContactModel> listA = new ArrayList<InfoContactModel>();
		listA.add(new InfoContactModel("federico.defaveri", "federico.defaveri", "Federico de Faveri", false));
		listA.add(new InfoContactModel("antonio.gioia", "antonio.gioia", "Antonio Gioia",false));
//		listA.add(new InfoContactModel("fabio.sinibaldi", "fabio.sinibaldi", "Fabio Sinibaldi", false));
//		listA.add(new InfoContactModel("pasquale.pagano", "pasquale.pagano", "Pasquale Pagano",false));
		listA.add(new InfoContactModel("francesco.mangiacrapa", "francesco.mangiacrapa", "Francesco Mangiacrapa",false));
		listA.add(new InfoContactModel("massimiliano.assante", "massimiliano.assante", "Massimiliano Assante",false));
		
		List<InfoContactModel> listB = new ArrayList<InfoContactModel>();
		
//		listB.add(new InfoContactModel("federico.defaveri", "federico.defaveri", "Federico de Faveri",false));
//		listB.add(new InfoContactModel("fabio.sinibaldi", "fabio.sinibaldi", "Fabio Sinibaldi",false));
//		listB.add(new InfoContactModel("antonio.gioia", "antonio.gioia", "Antonio Gioia",false));
//		listB.add(new InfoContactModel("pasquale.pagano", "pasquale.pagano", "Pasquale Pagano",false));
		listB.add(new InfoContactModel("francesco.mangiacrapa", "francesco.mangiacrapa", "Francesco Mangiacrapa",false));
		listB.add(new InfoContactModel("massimiliano.assante", "massimiliano.assante", "Massimiliano Assante",false));
		
		DiffereceBeetweenInfoContactModel diff = new DiffereceBeetweenInfoContactModel(listA, listB);
		
		System.out.println("# differences: "+diff.getDifferentsContacts().size());

		for (InfoContactModel infoContactModel : diff.getDifferentsContacts()) {
			System.out.println("the differce is: "+infoContactModel.getLogin());
		}
	}	*/

}
