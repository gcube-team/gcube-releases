package org.gcube.portlets.user.tdwx.client.style;

import java.util.ArrayList;

import org.gcube.portlets.user.tdwx.client.config.Row;
import org.gcube.portlets.user.tdwx.client.config.RowStyleProvider;
import org.gcube.portlets.user.tdwx.client.resources.ResourceBundle;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class DefaultRowStyle implements RowStyleProvider {

	protected ResourceBundle res;
	protected boolean errorNotColored;

	/**
	 * 
	 */
	public DefaultRowStyle() {
		errorNotColored = false;
		res = ResourceBundle.INSTANCE;
		res.tdGridCSS().ensureInjected();

	}

	/**
	 * 
	 * @param errorRed
	 *            true if you want error rows white
	 */
	public DefaultRowStyle(boolean errorNotColored) {
		this.errorNotColored = errorNotColored;
		res = ResourceBundle.INSTANCE;
		res.tdGridCSS().ensureInjected();

	}

	@Override
	public String getRowStyle(Row row,
			ArrayList<ColumnDefinition> validationColumns) {
		String style = "";

		for (ColumnDefinition c : validationColumns) {
			String valid = row.getFieldAsText(c.getColumnLocalId());
			Boolean b = new Boolean(valid);
			if (!b) {
				if (errorNotColored) {
					style = res.tdGridCSS().getGridRowNoColor();
				} else {
					style = res.tdGridCSS().getGridRowRed();
				}
				break;
			}

		}

		return style;
	}

	@Override
	public String getColStyle(Row row,
			ArrayList<ColumnDefinition> validationColumns,
			ValueProvider<? super DataRow, ?> valueProvider, int rowIndex,
			int colIndex) {
		String style = "";
		for (ColumnDefinition c : validationColumns) {
			String valid = row.getFieldAsText(c.getColumnLocalId());
			Boolean b = new Boolean(valid);
			if (!b) {
				if (errorNotColored) {
					style = res.tdGridCSS().getGridRowNoColor();
				} else {
					style = res.tdGridCSS().getGridRowRed();
				}
				break;
			}

		}
		return style;
	}

}
