package org.gcube.portlets.user.tdtemplate.server.service;

import java.util.Collection;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class CubeManagerInstanceTemplate.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 17, 2015
 */
public class CubeManagerInstanceTemplate implements CubeManager{

	private TemplateService service;
	public static Logger logger = LoggerFactory.getLogger(CubeManagerInstanceTemplate.class);

	/**
	 * Instantiates a new cube manager instance template.
	 *
	 * @param service the service
	 */
	public CubeManagerInstanceTemplate(TemplateService service) {
		this.service = service;
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#getTable(org.gcube.data.analysis.tabulardata.model.table.TableId)
	 */
	public Table getTable(TableId tableId) throws NoSuchTableException {
		try {
			return service.getTable(tableId);
		} catch (org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException e) {
			logger.error("Error on get Table: ",e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#addValidations(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.model.column.Column[])
	 */
	@Override
	public Table addValidations(TableId arg0, Column... arg1) throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#removeValidations(org.gcube.data.analysis.tabulardata.model.table.TableId)
	 */
	@Override
	public Table removeValidations(TableId id) throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#addValidations(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata, org.gcube.data.analysis.tabulardata.model.column.Column[])
	 */
	@Override
	public Table addValidations(TableId id,
			ValidationsMetadata tableValidationMetadata,
			Column... validationColumns) throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#createTable(org.gcube.data.analysis.tabulardata.model.table.TableType)
	 */
	@Override
	public TableCreator createTable(TableType type) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#removeTable(org.gcube.data.analysis.tabulardata.model.table.TableId)
	 */
	@Override
	public void removeTable(TableId id) throws NoSuchTableException {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#modifyTableMeta(org.gcube.data.analysis.tabulardata.model.table.TableId)
	 */
	@Override
	public TableMetaCreator modifyTableMeta(TableId tableId)
			throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#getTables()
	 */
	@Override
	public Collection<Table> getTables() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#getTables(org.gcube.data.analysis.tabulardata.model.table.TableType)
	 */
	@Override
	public Collection<Table> getTables(TableType tableType) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#removeColumn(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId)
	 */
	@Override
	public Table removeColumn(TableId arg0, ColumnLocalId arg1)
			throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#getTimeTable(org.gcube.data.analysis.tabulardata.model.time.PeriodType)
	 */
	@Override
	public Table getTimeTable(PeriodType arg0) throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.CubeManager#exchangeColumnPosition(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId, int)
	 */
	@Override
	public Table exchangeColumnPosition(TableId arg0, ColumnLocalId arg1,
			int arg2) throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}

}
