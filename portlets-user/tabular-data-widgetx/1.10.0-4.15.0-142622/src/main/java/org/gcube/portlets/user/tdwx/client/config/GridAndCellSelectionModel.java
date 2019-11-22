package org.gcube.portlets.user.tdwx.client.config;

import java.util.Collections;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.widget.core.client.event.RowClickEvent;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.selection.CellSelection;

/**
 * Extends the GridSelectionModel to retrieve the selected cell.
 * CellSelectionModel is not suitable.
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 * @param <M>
 */
public class GridAndCellSelectionModel<M> extends GridSelectionModel<M> {

	private CellSelection<M> cellSelected;
	private boolean focusCellCalled;
	private int indexOnSelectNoShift;

	// private List<M> totalSelected = new ArrayList<M>();

	public GridAndCellSelectionModel() {
		super();
	}

	/**
	 * Handles a row click event. The row click event is responsible for adding
	 * to a selection in multiple selection mode.
	 * 
	 * @param event
	 *            the row click event
	 */
	@Override
	protected void onRowClick(RowClickEvent event) {
		if (Element.is(event.getEvent().getEventTarget())
				&& !grid.getView().isSelectableTarget(
						Element.as(event.getEvent().getEventTarget()))) {
			return;
		}

		if (isLocked()) {
			return;
		}

		if (fireSelectionChangeOnClick) {
			fireSelectionChange();
			fireSelectionChangeOnClick = false;
		}

		XEvent xe = event.getEvent().<XEvent> cast();

		int rowIndex = event.getRowIndex();
		int colIndex = event.getColumnIndex();
		if (rowIndex == -1) {
			deselectAll();
			return;
		}

		M sel = listStore.get(rowIndex);

		boolean isSelected = isSelected(sel);
		boolean isControl = xe.getCtrlOrMetaKey();
		boolean isShift = xe.getShiftKey();

		// we only handle multi select with control key here
		if (selectionMode == SelectionMode.MULTI) {
			cellSelected = new CellSelection<M>(sel, rowIndex, colIndex);
			if (isSelected && isControl) {
				grid.getView().focusCell(rowIndex, colIndex, false);
				focusCellCalled = true;
				// reset the starting location of the click
				indexOnSelectNoShift = rowIndex;
				doDeselect(Collections.singletonList(sel), false);
			} else if (isControl) {
				grid.getView().focusCell(rowIndex, colIndex, false);
				focusCellCalled = true;
				// reset the starting location of the click
				indexOnSelectNoShift = rowIndex;
				doSelect(Collections.singletonList(sel), true, false);
			} else if (isSelected && !isControl && !isShift
					&& selected.size() > 1) {
				doSelect(Collections.singletonList(sel), false, false);
			}

			if (!focusCellCalled) {
				grid.getView().focusCell(rowIndex, colIndex, false);
			}
		}

	}

	/**
	 * Handles a row mouse down event. The row mouse down event is responsible
	 * for initiating a selection.
	 * 
	 * @param event
	 *            the row mouse down event
	 */
	@Override
	protected void onRowMouseDown(RowMouseDownEvent event) {
		if (Element.is(event.getEvent().getEventTarget())
				&& !grid.getView().isSelectableTarget(
						Element.as(event.getEvent().getEventTarget()))) {
			return;
		}

		if (isLocked()) {
			return;
		}

		int rowIndex = event.getRowIndex();
		int colIndex = event.getColumnIndex();
		if (rowIndex == -1) {
			return;
		}

		focusCellCalled = false;
		mouseDown = true;

		XEvent e = event.getEvent().<XEvent> cast();

		// it is important the focusCell be called once, and only once in
		// onRowMouseDown and onRowMouseClick
		// everything but multi select with the control key pressed is handled
		// in mouse down

		if (event.getEvent().getButton() == Event.BUTTON_RIGHT) {
			if (selectionMode != SelectionMode.SINGLE
					&& isSelected(listStore.get(rowIndex))) {
				return;
			}
			M sel = listStore.get(rowIndex);
			cellSelected = new CellSelection<M>(sel, rowIndex, colIndex);
			grid.getView().focusCell(rowIndex, colIndex, false);
			select(rowIndex, false);
			focusCellCalled = true;
		} else {
			M sel = listStore.get(rowIndex);
			if (sel == null) {
				return;
			}

			boolean isSelected = isSelected(sel);
			boolean isMeta = e.getCtrlOrMetaKey();
			boolean isShift = event.getEvent().getShiftKey();

			switch (selectionMode) {
			case SIMPLE:
				grid.getView().focusCell(rowIndex, colIndex, false);
				focusCellCalled = true;
				if (!isSelected) {
					cellSelected = new CellSelection<M>(sel, rowIndex, colIndex);
					select(sel, true);
				} else if (isSelected && deselectOnSimpleClick) {
					deselect(sel);
				}
				break;

			case SINGLE:
				grid.getView().focusCell(rowIndex, colIndex, false);
				focusCellCalled = true;
				if (isSelected && isMeta) {
					deselect(sel);
				} else if (!isSelected) {
					cellSelected = new CellSelection<M>(sel, rowIndex, colIndex);
					select(sel, false);
				}
				break;

			case MULTI:
				if (isMeta) {
					break;
				}

				if (isShift && lastSelected != null) {
					int last = listStore.indexOf(lastSelected);
					grid.getView().focusCell(last, colIndex, false);

					int start;
					int end;
					// This deals with flipping directions
					if (indexOnSelectNoShift < rowIndex) {
						start = indexOnSelectNoShift;
						end = rowIndex;
					} else {
						start = rowIndex;
						end = indexOnSelectNoShift;
					}

					focusCellCalled = true;
					select(start, end, false);
				} else if (!isSelected) {
					cellSelected = new CellSelection<M>(sel, rowIndex, colIndex);
					// reset the starting location of multi select
					indexOnSelectNoShift = rowIndex;

					grid.getView().focusCell(rowIndex, colIndex, false);
					focusCellCalled = true;
					doSelect(Collections.singletonList(sel), false, false);
				}
				break;
			}
		}

		mouseDown = false;

	}

	public CellSelection<M> getCellSelected() {
		return cellSelected;
	}

	public void setCellSelected(CellSelection<M> cellSelected) {
		this.cellSelected = cellSelected;
	}

	/*************************/

	@Override
	public void refresh() {
		//Log.debug("Selection Model Called Refresh()");
	}

	@Override
	protected void onClear(StoreClearEvent<M> event) {
		//Log.debug("Selection Model Called OnClear()");

	}

	@Override
	protected void onRemove(M model) {
		//Log.debug("Selection Model Called OnRemove(): ");
	}

	@Override
	protected void onAdd(List<? extends M> models) {
		super.onAdd(models);

		ModelKeyProvider<? super M> mod = grid.getStore().getKeyProvider();
		for (M item : selected) {
			Log.debug("Selected: " + mod.getKey(item));
		}

		ExtendedLiveGridView<M> gridView = (ExtendedLiveGridView<M>) grid
				.getView();
		boolean notChanged;
		for (M model : gridView.getCacheStore().getAll()) {
			notChanged = true;
			for (M item : selected) {
				if (mod.getKey(model).compareTo(mod.getKey(item)) == 0) {
					onSelectChange(model, true);
					notChanged = false;
					break;
				}

			}
			if (notChanged) {
				onSelectChange(model, false);
			}
		}

	}

	public void onChangeNumberOfRows() {
		Log.debug("Selection Model Called OnChangeNumberOfRows()");
		if (selected != null) {
			Log.debug("Rows Selected: " + selected.size());
		}

		int oldSize = selected.size();
		selected.clear();
		lastSelected = null;
		setLastFocused(null);
		if (oldSize > 0)
			fireSelectionChange();

		//deselectAll();
		//fireSelectionChange();

		if (selected != null) {
			Log.debug("After deselect Rows Selected: " + selected.size());
		} else {
			Log.debug("After deselect No Rows Selected");
		}

	}

}
