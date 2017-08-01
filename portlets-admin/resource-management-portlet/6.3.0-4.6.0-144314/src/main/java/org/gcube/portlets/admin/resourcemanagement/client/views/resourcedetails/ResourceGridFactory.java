/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ResourceGridFactory.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails;

import java.util.List;

import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDetailModel;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.XmlLoadResultReader;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ResourceGridFactory {
	public static final synchronized Grid<ModelData> createGrid(
			final String resourceType,
			final List<String> result,
			final String groupingColumn) {
		return createGrid(resourceType, result, groupingColumn, true);
	}

	public static final synchronized Grid<ModelData> createGrid(
			final String resourceType,
			final List<String> result,
			final String groupingColumn,
			final boolean autoExpand) {
		try {

			if (result == null || result.size() == 0) {
				Commands.showPopup(
						"Failure",
						"the retrieved result set for required resource is null or empty",
						6000);
				return null;
			}

			// The the XML reader
			ModelType type = ResourceDetailModel.getXMLMapping(resourceType);
			// For the grid representation
			final ColumnModel cm = ResourceDetailModel.getRecordDefinition(resourceType);

			if (type == null || cm == null) {
				MessageBox.alert("Failure", "the model representation has not been found", null);
				return null;
			}

			// need a loader, proxy, and reader
			XmlLoadResultReader<ListLoadResult<ModelData>> reader =
				new XmlLoadResultReader<ListLoadResult<ModelData>>(type);

			StringBuilder toParse = new StringBuilder().append("<Resources>\n");
			for (String elem : result) {
				toParse.append(elem.toString());
			}
			toParse.append("\n</Resources>");

			MemoryProxy<String> proxy = new MemoryProxy<String>(toParse.toString());

			final BaseListLoader<ListLoadResult<ModelData>> loader =
				new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);

			ListStore<ModelData> store = null;

			if (groupingColumn != null) {
				store = new GroupingStore<ModelData>(loader);
			} else {
				store = new ListStore<ModelData>(loader);
			}
			//apply the custom sorter	
			store.setStoreSorter(new CustomSorter());
			
			final Grid<ModelData> grid = new Grid<ModelData>(store, cm);

			if (groupingColumn != null) {
				((GroupingStore<ModelData>) store).groupBy(groupingColumn);

				// GROUPING
				// Builds the grouping structure to collapse elements
				// having the same type (log severity).
				GroupingView groupingView = new GroupingView();
				groupingView.setGroupRenderer(new GridGroupRenderer() {
					public String render(final GroupColumnData data) {
						int s = data.models.size();
						String f = cm.getColumnById(data.field).getHeader();
						String l = s == 1 ? "Item" : "Items";
						return f + ": " + ((data.group != null && data.group.trim().length() > 0) ? data.group : "<i>Undefined</i>")  + " (" + s + " " + l + ")";
					}
				});

				groupingView.setAutoFill(true);
				groupingView.setForceFit(false);
				grid.setView(groupingView);
			}

			if (autoExpand) {
				// The first visible column will be considered auto-expand
				grid.setAutoExpandColumn(cm.getColumn(0).getId());
			}

			loader.setSortField("ID");
			loader.setSortDir(SortDir.ASC);
			loader.load();

			return grid;
		} catch (RuntimeException e) {
			ConsoleMessageBroker.error(ResourceGridFactory.class, "During resourceDetailGrid creation");
			return null;
		}
	}
}
