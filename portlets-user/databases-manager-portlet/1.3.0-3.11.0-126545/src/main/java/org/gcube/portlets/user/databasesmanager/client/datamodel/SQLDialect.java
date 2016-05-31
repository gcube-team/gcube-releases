package org.gcube.portlets.user.databasesmanager.client.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.databasesmanager.shared.ConstantsPortlet;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SQLDialect extends BaseModelData {

	private static final long serialVersionUID = 1L;

	// private String name;

	public SQLDialect() {
	}

	public SQLDialect(String name) {
		setName(name);
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getName() {
		return get("name");
	}

	public static List<SQLDialect> loadData() {
		List<SQLDialect> dialects = new ArrayList<SQLDialect>();

		SQLDialect element1 = new SQLDialect(ConstantsPortlet.NONE);
		SQLDialect element2 = new SQLDialect(ConstantsPortlet.POSTGRES);
		SQLDialect element3 = new SQLDialect(ConstantsPortlet.MYSQL);

		dialects.add(element1);
		dialects.add(element2);
		dialects.add(element3);

		return dialects;
	}
}
