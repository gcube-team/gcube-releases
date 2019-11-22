/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.client.event.SelectedKPIEvent;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * The model that defines the nodes in the tree.
 */
public class CustomTreeModel implements TreeViewModel {

	// private final List<PopulationType> kpis = new
	// ArrayList<PopulationType>();
	/**
	 * This selection model is shared across all leaf nodes. A selection model
	 * can also be shared across all nodes in the tree, or each set of child
	 * nodes can have its own instance. This gives you flexibility to determine
	 * how nodes are selected.
	 */
	private final SingleSelectionModel<KPI> multiSelectionModel = new SingleSelectionModel<KPI>();
	private ListDataProvider<KPI> populationTypeProvider = new ListDataProvider<KPI>();
	private Cell<KPI> kpiCell;
	private HandlerManager eventBus;

	// private ListDataProvider<KPI> dataProvider = new ListDataProvider<KPI>();
	public CustomTreeModel(HandlerManager eventBus) {
		this.eventBus = eventBus;
		init();
		multiSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			public void onSelectionChange(SelectionChangeEvent event) {

				GWT.log("Selected: " + multiSelectionModel.getSelectedSet());
			}
		});
	}

	public void init() {

		// Construct a composite cell for contacts that includes a checkbox.
		List<HasCell<KPI, ?>> hasCells = new ArrayList<HasCell<KPI, ?>>();
		hasCells.add(new HasCell<KPI, Boolean>() {

			private CheckboxCell cell = new CheckboxCell(true, true);

			public Cell<Boolean> getCell() {

				return cell;
			}

			public FieldUpdater<KPI, Boolean> getFieldUpdater() {

				return new FieldUpdater<KPI, Boolean>() {

			        @Override
			        public void update(int index, KPI object, Boolean value) {
			            // TODO Auto-generated method stub
			        	GWT.log("Checked: "+object.getName() + " value: "+value);
			        	eventBus.fireEvent(new SelectedKPIEvent(null, object, value));
//			        	cell.set(value);
//			            dataGrid.redraw();
			        }
			    };
			}

			public Boolean getValue(KPI object) {

				return multiSelectionModel.isSelected(object);
			}

		});
		hasCells.add(new HasCell<KPI, KPI>() {

			private KPICell cell = new KPICell(null);

			public Cell<KPI> getCell() {

				return cell;
			}

			public FieldUpdater<KPI, KPI> getFieldUpdater() {

				return null;
			}

			public KPI getValue(KPI object) {

				return object;
			}
		});

		kpiCell = new CompositeCell<KPI>(hasCells) {

			@Override
			public void render(Context context, KPI value, SafeHtmlBuilder sb) {

				if(value.isLeaf()){
					sb.appendHtmlConstant("<table style=\"color:#0066cc;\"><tbody><tr>");
					super.render(context, value, sb);
					sb.appendHtmlConstant("</tr></tbody></table>");
				}else{
					sb.appendHtmlConstant("<table><tbody><tr>");
					sb.appendHtmlConstant(value.getName());
					sb.appendHtmlConstant("</tr></tbody></table>");
				}
			}

			@Override
			protected Element getContainerElement(Element parent) {

				// Return the first TR element in the table.
				return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
			}

			@Override
			protected <X> void render(
				Context context, KPI value, SafeHtmlBuilder sb,
				HasCell<KPI, X> hasCell) {

				Cell<X> cell = hasCell.getCell();
				sb.appendHtmlConstant("<td>");
				cell.render(context, hasCell.getValue(value), sb);
				sb.appendHtmlConstant("</td>");

			}
		};
	}

	public void setNewBatchType(PopulationType populationType) {

		//GWT.log("Displayng KPI for: " + populationType.toString());
		if (this.populationTypeProvider != null) {
			//removing all data from base data provider
			this.populationTypeProvider.getList().clear();
			this.populationTypeProvider.getList().addAll(populationType.getListKPI());
		}
	}

	/**
	 * Get the {@link NodeInfo} that provides the children of the specified
	 * value.
	 */
	public <T> NodeInfo<?> getNodeInfo(T value) {

		GWT.log("Get Node Info fired: " + value);
		if (value == null) {
			// LEVEL 0.
			// We passed null as the root value. Create a fake root KPIs
			PopulationType rootKPI = new PopulationType("", "KPIs", "", "", null);
			rootKPI.setListKPI(rootKPI.getListKPI());
			ListDataProvider<PopulationType> rootProvider = new ListDataProvider<PopulationType>();
			rootProvider.getList().add(rootKPI);
			Cell<PopulationType> cell = new AbstractCell<PopulationType>() {

				@Override
				public void render(
					com.google.gwt.cell.client.Cell.Context context,
					PopulationType value, SafeHtmlBuilder sb) {

					GWT.log("Rendering Root: " + value + " KPIs: " +
						value.getListKPI());
					// sb.appendHtmlConstant("  ROOT  ");
					sb.appendEscaped(value.getName());
				}
			};
			// Return a node info that pairs the data provider and the cell.
			return new DefaultNodeInfo<PopulationType>(rootProvider, cell);
		}
		else if (value instanceof PopulationType) {
			// LEVEL 1.
			// We want the children of PopulationType. Getting its list of KPI.
			// List<KPI> listOfKpi = ((PopulationType) value).getListKPI();
			//GWT.log("PopulationType listOfKpi: " + populationTypeProvider.getList());
			//return new DefaultNodeInfo<KPI>(populationTypeProvider, kpiCell, multiSelectionModel, null);
			return new DefaultNodeInfo<KPI>(populationTypeProvider, kpiCell);

		}
		else if (value instanceof KPI) {
			// LEVEL 2 - LEAF.
			// We want the children of the KPI. Return them.
			List<KPI> listOfKpi = ((KPI) value).getListKPI();
			//GWT.log("KPI type listOfKpi: " + listOfKpi.toString());
			ListDataProvider<KPI> kpiProvider = new ListDataProvider<KPI>(listOfKpi);
			return new DefaultNodeInfo<KPI>(kpiProvider, kpiCell);
		}

		return null;
	}

	/**
	 * Check if the specified value represents a leaf node. Leaf nodes cannot be
	 * opened.
	 */
	public boolean isLeaf(Object value) {

		// The leaf nodes are the songs, which are Strings.
		if (value instanceof KPI) {
			KPI toKPI = (KPI) value;
			return toKPI.isLeaf();
		}
		return false;
	}


}
