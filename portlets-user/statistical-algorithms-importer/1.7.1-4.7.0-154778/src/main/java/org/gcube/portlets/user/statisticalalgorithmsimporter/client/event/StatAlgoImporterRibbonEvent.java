package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.StatAlgoImporterRibbonType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StatAlgoImporterRibbonEvent extends
		GwtEvent<StatAlgoImporterRibbonEvent.StatRunnerRibbonEventHandler> {

	public static Type<StatRunnerRibbonEventHandler> TYPE = new Type<StatRunnerRibbonEventHandler>();
	private StatAlgoImporterRibbonType statAlgoImporterRibbonType;

	public interface StatRunnerRibbonEventHandler extends EventHandler {
		void onSelect(StatAlgoImporterRibbonEvent event);
	}

	public interface HasStatRunnerRibbonEventHandler extends HasHandlers {
		public HandlerRegistration addStatRunnerRibbonEventHandler(
				StatRunnerRibbonEventHandler handler);
	}

	public StatAlgoImporterRibbonEvent(StatAlgoImporterRibbonType statRunnerRibbonType) {
		this.statAlgoImporterRibbonType = statRunnerRibbonType;
	}

	@Override
	protected void dispatch(StatRunnerRibbonEventHandler handler) {
		handler.onSelect(this);
	}

	@Override
	public Type<StatRunnerRibbonEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<StatRunnerRibbonEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			StatAlgoImporterRibbonEvent statRunnerRibbonEvent) {
		source.fireEvent(statRunnerRibbonEvent);
	}

	public StatAlgoImporterRibbonType getStatRunnerRibbonType() {
		return statAlgoImporterRibbonType;
	}

	@Override
	public String toString() {
		return "StatAlgoImporterRibbonEvent [statAlgoImporterRibbonType="
				+ statAlgoImporterRibbonType + "]";
	}

	

}
