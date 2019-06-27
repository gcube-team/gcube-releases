package org.gcube.portlets.user.tdwx.client.filter;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.Loader;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.grid.filters.AbstractGridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;

/**
 * 
 * @author "Giancarlo Panichi"
 *
 * @param <M>
 */
public class ExtendedGridFilters<M> extends GridFilters<M> {
	private Menu filterMenu;
	private SeparatorMenuItem separatorItem;
	private CheckMenuItem checkFilterItem;
	private ExtendedGridFiltersMessages msgs = GWT.create(ExtendedGridFiltersMessages.class);
	
	

	/**
	 * Creates grid filters to be applied remotely. See
	 * {@link AbstractGridFilters#AbstractGridFilters(Loader)} for more
	 * information.
	 * 
	 * @param loader
	 *            the remote loader
	 */
	public ExtendedGridFilters(Loader<FilterPagingLoadConfig, ?> loader) {
		super(loader);
	}

	@Override
	protected void onContextMenu(HeaderContextMenuEvent event) {
		
		int column = event.getColumnIndex();

		if (separatorItem == null) {
			separatorItem = new SeparatorMenuItem();
		}
		separatorItem.removeFromParent();

		if (checkFilterItem == null) {
			checkFilterItem = new CheckMenuItem(msgs.inlineFilter());
			checkFilterItem
					.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

						@Override
						public void onCheckChange(
								CheckChangeEvent<CheckMenuItem> event) {
							onCheckChangeExt(event);
							
							
						}
					});
		}

		checkFilterItem.setData("index", column);

		Filter<M, ?> f = getFilter(grid.getColumnModel().getColumn(column)
				.getValueProvider().getPath());
		if (f != null) {
			filterMenu = f.getMenu();
			checkFilterItem.setChecked(f.isActive(), true);
			checkFilterItem.setSubMenu(filterMenu);

			Menu menu = event.getMenu();
			menu.add(separatorItem);
			menu.add(checkFilterItem);
		}
	}
	
	
	protected void onCheckChangeExt(CheckChangeEvent<CheckMenuItem> event) {
	    getMenuFilter(event).setActive(event.getItem().isChecked(), false);
	 }
}
