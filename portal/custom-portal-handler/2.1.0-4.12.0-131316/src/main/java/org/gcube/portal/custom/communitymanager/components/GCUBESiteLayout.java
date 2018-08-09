package org.gcube.portal.custom.communitymanager.components;

import java.util.LinkedList;
import java.util.List;

import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
/**
 * 
 * @author Massimiliano Assante, massimiliano.assante@isti.cnr.it
 * @version 0.1
 *
 */
public class GCUBESiteLayout {
	/**
	 * layout name
	 */
	private String name;
	/**
	 * the creator username
	 */
	long userid;
	/**
	 * list of portlets to place in the layout
	 */
	private List<GCUBELayoutTab> tabs;

	/**
	 * @param name -
	 * @param email
	 */
	public GCUBESiteLayout(Company company, String name, String email) {
		User creator = null;

		try {
			creator = UserLocalServiceUtil.getUserByEmailAddress(company.getCompanyId(), email);			
		} catch (com.liferay.portal.kernel.exception.PortalException e) {
			e.printStackTrace();
		} catch (com.liferay.portal.kernel.exception.SystemException e) {
			e.printStackTrace();
		}
		this.name = name;
		this.userid = creator.getUserId();
		this.tabs = new LinkedList<GCUBELayoutTab>();
	}
	/**
	 * 
	 * @return the layout tab objects
	 */
	public List<GCUBELayoutTab> getTabs() {
		return tabs;
	}
	/**
	 * 
	 * @param layoutTab -
	 */
	public void addTab(GCUBELayoutTab layoutTab) {
		if (layoutTab == null)
			throw new NullPointerException();
		tabs.add(layoutTab);
	}
	/**
	 * 
	 * @return layout name
	 */
	public String getName() {
		return name;
	}


}
