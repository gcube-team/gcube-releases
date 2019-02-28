package org.gcube.portlets.user.td.metadatawidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRLocalizedText;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabNamesMetadata;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class TabNamesMetadataGrid {
	private static final TRLocalizedTextProperties props = GWT
			.create(TRLocalizedTextProperties.class);

	protected final Grid<TRLocalizedText> grid;

	TabNamesMetadataGrid(TabNamesMetadata tabMetadata) {

		ColumnConfig<TRLocalizedText, String> valueCol = new ColumnConfig<TRLocalizedText, String>(
				props.value());
		ColumnConfig<TRLocalizedText, String> localeCodeCol = new ColumnConfig<TRLocalizedText, String>(
				props.localeCode());

		List<ColumnConfig<TRLocalizedText, ?>> l = new ArrayList<ColumnConfig<TRLocalizedText, ?>>();
		l.add(valueCol);
		l.add(localeCodeCol);
		ColumnModel<TRLocalizedText> cm = new ColumnModel<TRLocalizedText>(l);

		ListStore<TRLocalizedText> store = new ListStore<TRLocalizedText>(
				props.id());
		store.addAll(tabMetadata.getListTRLocalizedText());

		grid = new Grid<TRLocalizedText>(store, cm);
		grid.getView().setAutoExpandColumn(valueCol);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);
		

	}

	public Grid<TRLocalizedText> getGrid() {
		return grid;
	}
}
