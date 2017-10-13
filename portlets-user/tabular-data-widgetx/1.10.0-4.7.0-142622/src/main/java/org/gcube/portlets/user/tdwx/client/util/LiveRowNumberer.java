/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.util;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 * @param <M>
 */
public class LiveRowNumberer<M> extends RowNumberer<M> {
	
	protected int offset;

	public LiveRowNumberer(IdentityValueProvider<M> valueProvider) {
		super(valueProvider);
		
		setCell(new AbstractCell<M>() {
		      @Override
		      public void render(Context context, M value, SafeHtmlBuilder sb) {
		    	  System.out.println("Index "+context.getIndex()+" offset: "+offset);
		        sb.append(offset+context.getIndex() + 1);
		      }
		    });
	}

	@SuppressWarnings("unchecked")
	protected void doUpdate() {
		int col = grid.getColumnModel().indexOf(this);
		ModelKeyProvider<M> kp = (ModelKeyProvider<M>) grid.getStore().getKeyProvider();

		//int offset = getOffset();

		for (int i = 0, len = grid.getStore().size(); i < len; i++) {
			Element cell = grid.getView().getCell(i, col);
			if (cell != null) {
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				//int index = offset + i;
				getCell().render(new Context(i, col, kp.getKey(grid.getStore().get(i))), null, sb);
				cell.getFirstChildElement().setInnerHTML(sb.toSafeHtml().asString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected int getOffset()
	{
		ListLoader<?, ?> loader = grid.getLoader();
		if (loader instanceof PagingLoader<?,?>) {
			PagingLoader<PagingLoadConfig, PagingLoadResult<?>> pagingLoader = (PagingLoader<PagingLoadConfig, PagingLoadResult<?>>)loader;
			PagingLoadConfig loadConfig = pagingLoader.getLastLoadConfig();
			offset = loadConfig.getOffset();
			return offset;
		}
		return 0;
	}

}
