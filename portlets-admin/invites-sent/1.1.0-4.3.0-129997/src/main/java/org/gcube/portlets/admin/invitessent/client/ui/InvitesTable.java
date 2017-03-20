package org.gcube.portlets.admin.invitessent.client.ui;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.gcube.portal.databook.shared.Invite;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.constants.Device;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
/**
*
* @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
*/
public class InvitesTable extends Composite {
	/**
	 * Get a cell value from a record.
	 * 
	 * @param <C> the cell type
	 */
	private static interface GetValue<C> {
		C getValue(Invite invite);
	}

	private CellTable<Invite> table = new CellTable<Invite>();


	protected ListDataProvider<Invite> dataProvider = new ListDataProvider<Invite>();

	public InvitesTable(List<Invite> invites) {

		dataProvider.setList(invites);
		table.setVisibleRange(new Range(0, invites.size()));
		// Connect the table to the data provider.
		dataProvider.addDataDisplay(table);
		table.setWidth("95%", false);
		table.setStriped(true);
		table.setBordered(true);
		table.setRowCount(invites.size(), true);
		table.addStyleName("invitesTable");
		Column<Invite, String> emailCol = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(Invite invite) {
				return invite.getInvitedEmail();
			}
		});

		Column<Invite, String> status = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(Invite invite) {
				return invite.getStatus().toString();
			}
		});

		// DateCell.
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
		Column<Invite, Date> invitedDate = getColumn(new DateCell(dateFormat), new GetValue<Date>() {
			@Override
			public Date getValue(Invite invite) {
				return invite.getTime();
			}
		});

		Column<Invite, String> invitedBy = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(Invite invite) {
				return invite.getSenderFullName() + " ("+ invite.getSenderUserId()+")";
			}
		});

		table.addColumn(emailCol, "Invited Email");
		emailCol.setSortable(true);		
		ListHandler<Invite> emailColHandler = new ListHandler<Invite>(dataProvider.getList());
		emailColHandler.setComparator(emailCol, new Comparator<Invite>() {
			@Override
			public int compare(Invite o1, Invite o2) {
				return o1.getInvitedEmail().compareTo(o2.getInvitedEmail());

			}
		});
		table.addColumnSortHandler(emailColHandler);

		table.addColumn(status, "Status");
		status.setSortable(true);		
		ListHandler<Invite> statusColHandler = new ListHandler<Invite>(dataProvider.getList());
		statusColHandler.setComparator(status, new Comparator<Invite>() {
			@Override
			public int compare(Invite o1, Invite o2) {
				return o1.getStatus().toString().compareTo(o2.getStatus().toString());

			}
		});		
		table.addColumnSortHandler(statusColHandler);


		table.addColumn(invitedDate, "Invite sent day");
		invitedDate.setSortable(true);	
		invitedDate.setDefaultSortAscending(true);
		ListHandler<Invite> dateColHandler = new ListHandler<Invite>(dataProvider.getList());
		statusColHandler.setComparator(invitedDate, new Comparator<Invite>() {
			@Override
			public int compare(Invite o1, Invite o2) {
				Date date1 = o1.getTime();
				Date date2 = o2.getTime();

				if(date1.before(date2))
					return -1;
				else if (date1.after(date2))
					return 1;
				else
					return 0;

			}
		});		
		table.addColumnSortHandler(dateColHandler);
	    // We know that the data is sorted alphabetically by default.
	    table.getColumnSortList().push(invitedDate);
	    
		table.addColumn(invitedBy, "Invited by");
		

		initWidget(table);
	}

	/**
	 * get a column 
	 * 
	 * @param <C> the cell type
	 * @param cell the cell used to render the column
	 * @param getter the value getter for the cell
	 */
	private <C> Column<Invite, C> getColumn(Cell<C> cell, final GetValue<C> getter) {
		Column<Invite, C> column = new Column<Invite, C>(cell) {
			@Override
			public C getValue(Invite object) {
				return getter.getValue(object);
			}
		};		
		return column;
	}
}
