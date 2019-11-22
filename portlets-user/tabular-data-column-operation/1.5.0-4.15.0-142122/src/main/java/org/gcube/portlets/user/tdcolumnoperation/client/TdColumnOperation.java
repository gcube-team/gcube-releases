package org.gcube.portlets.user.tdcolumnoperation.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TdColumnOperation implements EntryPoint {

	public EventBus bus = new SimpleEventBus();
	public TRId fakeTrId = new TRId("244");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		fakeTrId.setTableTypeName("Generic");
		fakeTrId.setTableId("4250");
		// testerSplit();
		// testerMerge();

		testerGroupByTime();

	}

	/**
 * 
 */
	private void testerGroupByTime() {
		AggregateByTimeColumnDialog group = new AggregateByTimeColumnDialog(fakeTrId,bus);
		group.show();

	}

	@SuppressWarnings("unused")
	private void testerSplit() {

		SplitColumnDialog dialog;

		try {
			dialog = new SplitColumnDialog(fakeTrId, bus);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private void testerMerge() {

		try {
			MergeColumnDialog dialog = new MergeColumnDialog(fakeTrId, bus);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void testerGroupBy() {
		GroupByColumnDialog group = new GroupByColumnDialog(fakeTrId, bus);
		group.show();
	}

	/************************ TODO TEST CODE REMOVE THIS ************************/
	public static void addFakeColumn(
			AsyncCallback<ListLoadResult<ColumnData>> callback) {

		callback.onSuccess(new ListLoadResultBean<ColumnData>(fakeColumns()));
	}

	/************************ TODO TEST CODE REMOVE THIS ************************/
	public static ArrayList<ColumnData> fakeColumns() {

		ArrayList<ColumnData> result = new ArrayList<ColumnData>();

		for (int i = 0; i < 5; i++) {
			ColumnData cd = new ColumnData();
			cd.setColumnId("id" + "");
			cd.setId("id" + i);
			cd.setLabel("label" + i);
			cd.setTrId(new TRId("trid" + i, TabResourceType.STANDARD, "tableId"
					+ i));
			result.add(cd);

		}

		return result;
	}
}
