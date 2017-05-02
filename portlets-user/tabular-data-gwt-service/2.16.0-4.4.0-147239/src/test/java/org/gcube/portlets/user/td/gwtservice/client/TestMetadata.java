package org.gcube.portlets.user.td.gwtservice.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.AgencyMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.RightsMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRAgencyMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRDescriptionMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRNameMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRRightsMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TestMetadata {

	private static Logger logger = LoggerFactory.getLogger(TestMetadata.class);

	//private static SimpleDateFormat sdf = new SimpleDateFormat(
	//		"yyyy-MM-dd HH:mm");

	@Test
	public void listTR() throws Exception {
		logger.debug("################ List TR ####################");
		TDService tdService=new TDService();
		TabularDataService service = tdService.getService();

		List<TabularResource> trs = service.getTabularResources();
		Assert.assertTrue(trs.size() > 0);
		Table lastTable = null;
		for (TabularResource tr : trs) {
			lastTable = service.getLastTable(tr.getId());
			if (lastTable != null) {
				logger.debug("TabularResource: [" + tr.getId()
						+ ", lastTableId: " + lastTable.getId().getValue()
						+ ", lastTableType: "
						+ lastTable.getTableType().getName() + "]");
				logger.debug("--------Column:");
				List<Column> cols = lastTable.getColumns();
				for (Column c : cols) {
					logger.debug(c.toString());
				}

			} else {
				logger.debug("TabularResource: [" + tr.getId()
						+ ", lastTable: " + lastTable + "]");

			}

			logger.debug("---------Metadata:");

			ArrayList<TRMetadata> metas = showMetadata(tr);
			for (TRMetadata meta : metas) {
				logger.debug(meta.toString());
			}
			System.out
					.println("############################################################");

		}

		// TabularResource tr=service.createTabularResource();
		// Date date = Calendar.getInstance().getTime();

		// logger.debug("New TR [id="+tr.getId().getValue()+" date:"+sdf.format(tr.getCreationDate().getTime())+"]");
		// tr.setMetadata(new NameMetadata("Sinatra"));
		// tr.setMetadata(new DescriptionMetadata("Sinatra Collection"));
		// tr.setMetadata(new RightsMetadata("Sinatra free"));

		// TabResource tabResource=new TabResource();
		// updateTabResourceInformation(tabResource,metas);
		// logger.debug("------");
		// logger.debug(tabResource.toString());

		// showMoreInformation(service);

	}

	@SuppressWarnings("unused")
	private void showMoreInformation(TabularDataService service)
			throws Exception {
		List<TabularResource> trs = service.getTabularResources();
		Assert.assertTrue(trs.size() > 0);
		logger.debug("-----More Informations-----");
		Table lastTable = null;
		for (TabularResource tr : trs) {
			lastTable = service.getLastTable(tr.getId());
			if (lastTable != null) {
				logger.debug("TabularResource: [" + tr.getId()
						+ ", lastTableId: " + lastTable.getId().getValue()
						+ " lastTableType:"
						+ lastTable.getTableType().getName() + "]");
			} else {
				logger.debug("TabularResource: [" + tr.getId()
						+ ", lastTable: " + lastTable + "]");

			}
			logger.debug("-----------");
		}
	}

	private ArrayList<TRMetadata> showMetadata(TabularResource tr) {
		Collection<TabularResourceMetadata<?>> trMetas = tr.getAllMetadata();

		ArrayList<TRMetadata> listTRMetadata = new ArrayList<TRMetadata>();

		for (TabularResourceMetadata<?> trMetadata : trMetas) {
			if (trMetadata instanceof org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata) {
				TRDescriptionMetadata trDescriptionMetadata = new TRDescriptionMetadata();
				trDescriptionMetadata
						.setValue(((org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata) trMetadata)
								.getValue());
				listTRMetadata.add(trDescriptionMetadata);
			} else {
				if (trMetadata instanceof org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata) {
					TRNameMetadata trNameMetadata = new TRNameMetadata();
					trNameMetadata
							.setValue(((org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata) trMetadata)
									.getValue());
					listTRMetadata.add(trNameMetadata);
				} else {
					if (trMetadata instanceof AgencyMetadata) {
						TRAgencyMetadata trAgencyMetadata = new TRAgencyMetadata();
						trAgencyMetadata.setValue(((AgencyMetadata) trMetadata)
								.getValue());
						listTRMetadata.add(trAgencyMetadata);
					} else {
						if (trMetadata instanceof RightsMetadata) {
							TRRightsMetadata trRightsMetadata = new TRRightsMetadata();
							trRightsMetadata
									.setValue(((RightsMetadata) trMetadata)
											.getValue());
							listTRMetadata.add(trRightsMetadata);
						} else {

						}

					}
				}
			}
		}

		logger.debug("TR Metadata: " + listTRMetadata.toString());
		return listTRMetadata;
	}

	@SuppressWarnings("unused")
	private void updateTabResourceInformation(TabResource tabResource,
			ArrayList<TRMetadata> trMetadatas) {
		for (TRMetadata trMetadata : trMetadatas) {
			if (trMetadata instanceof TRDescriptionMetadata) {
				tabResource.setDescription(((TRDescriptionMetadata) trMetadata)
						.getValue());
			} else {
				if (trMetadata instanceof TRNameMetadata) {
					tabResource.setName(((TRNameMetadata) trMetadata)
							.getValue());
				} else {
					if (trMetadata instanceof TRAgencyMetadata) {
						tabResource.setAgency(((TRAgencyMetadata) trMetadata)
								.getValue());
					} else {

						if (trMetadata instanceof TRRightsMetadata) {
							tabResource
									.setRight(((TRRightsMetadata) trMetadata)
											.getValue());
						} else {

						}

					}
				}
			}
		}
	}

}
