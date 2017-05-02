package org.gcube.portlets.user.tdwx.client.config;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.event.LiveGridViewUpdateEvent;
import com.sencha.gxt.widget.core.client.event.LiveGridViewUpdateEvent.LiveGridViewUpdateHandler;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * Extend LiveToolItem adding selected rows
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TDXLiveToolItem extends LiveToolItem {

	/**
	 * TDXLiveToolItem messages.
	 */
	public interface TDXLiveToolItemMessages extends LiveToolItemMessages {

		String displayMessage(int totalSelected);
		String displayNoSelectedMessage();

	}

	protected static class DefaultTDXLiveToolItemMessages implements
			TDXLiveToolItemMessages {

		private int end;
		private int start;
		private int total;
		private int totalSelected;

		public String displayMessage(int totalSelected) {
			this.totalSelected = totalSelected;
			return buildMessage();
		}

		@Override
		public String displayMessage(int start, int end, int total) {
			this.start = start;
			this.end = end;
			this.total = total;

			return buildMessage();
		}

		protected String buildMessage() {
			String msg = "";

			if (totalSelected > 0) {
				if (totalSelected > 1) {
					msg = new String(
							"<span>Displaying "
									+ start + " - " + end + " of " + total
									+ "</span><span style='margin-left:10px;color:red;'>[ "
									+ totalSelected
									+ " rows selected ]</span>");
				} else {
					msg = new String(
							"<span>Displaying "
									+ start + " - " + end + " of " + total
									+ "</span><span style='margin-left:10px;color:red;'>[ "
									+ totalSelected
									+ " row selected ]</span>");
				}
			} else {
				msg = new String("<span>Displaying " + start
						+ " - " + end + " of " + total + "</span>");
			}
			return msg;
		}

		@Override
		public String displayNoSelectedMessage() {
			totalSelected=0;
			String msg = new String("<span>Displaying " + start
					+ " - " + end + " of " + total + "</span>");
			return msg;
		}

	}

	private HandlerRegistration handlerRegistration;
	private TDXLiveToolItemMessages messages;
	private List<?> lastSelected;

	public TDXLiveToolItem(Grid<?> grid) {
		super(grid);

	}

	/**
	 * Returns the tool item messages.
	 * 
	 * @return the messages
	 */
	public TDXLiveToolItemMessages getMessages() {
		if (messages == null) {
			messages = new DefaultTDXLiveToolItemMessages();
		}
		return messages;
	}

	/**
	 * Sets the tool item messages.
	 * 
	 * @param messages
	 *            the messages
	 */
	public void setMessages(TDXLiveToolItemMessages messages) {
		this.messages = messages;
	}

	@Override
	protected void onUpdate(LiveGridViewUpdateEvent be) {
		int pageSize = be.getRowCount();
		int viewIndex = be.getViewIndex();
		int totalCount = be.getTotalCount();
		int i = pageSize + viewIndex;
		if (i > totalCount) {
			i = totalCount;
		}
		setLabel(getMessages().displayMessage(
				totalCount == 0 ? 0 : viewIndex + 1, i, (int) totalCount));
	}
	
	
	public void setNoSelected(){
		setLabel(getMessages().displayNoSelectedMessage());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void bindGrid(Grid<?> grid) {
		if (handlerRegistration != null) {
			handlerRegistration.removeHandler();
			handlerRegistration = null;
		}
		if (grid != null) {
			ExtendedLiveGridView<?> view = (ExtendedLiveGridView<?>) grid
					.getView();

			view.addLiveGridViewUpdateHandler(new LiveGridViewUpdateHandler() {
				@Override
				public void onUpdate(LiveGridViewUpdateEvent event) {
					ExtendedLiveGridView<?> source = (ExtendedLiveGridView<?>) event
							.getSource();
					Log.debug("CacheStore List Items: " + source.getCacheSize());

					TDXLiveToolItem.this.onUpdate(event);
				}
			});

			// final DataRowModelKeyProvider modelKeyProvider =
			// (DataRowModelKeyProvider)grid.getStore().getKeyProvider();

			GridSelectionModel<?> selectionModel = (GridSelectionModel<?>) grid
					.getSelectionModel();

			selectionModel
					.addSelectionChangedHandler(new SelectionChangedHandler() {

						@Override
						public void onSelectionChanged(
								SelectionChangedEvent event) {
							if (event.getSelection() != null) {
								Log.debug("Selected List Items: "
										+ event.getSelection().size());
								lastSelected = event.getSelection();
								setLabel(getMessages().displayMessage(
										lastSelected.size()));
							} else {
								lastSelected = null;
								setLabel(getMessages().displayMessage(0));
							}

						}
					});

		}
	}

	

}
