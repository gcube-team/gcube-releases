/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portlets.admin.gcubereleases.client.GcubeReleasesAppController;
import org.gcube.portlets.admin.gcubereleases.client.event.DisplaySelectedReleaseEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.FilterPackageEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.ManagePackagesEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.ManageReleasesEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.NewInsertReleasesEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.ShowClickReportEvent;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class PageTemplate.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 19, 2015
 */
public class PageTemplate extends Composite {

	private LinkedHashMap<String, String> hashReleases = new LinkedHashMap<String, String>();// Ordered-HashMap

	@UiField
	com.github.gwtbootstrap.client.ui.NavList navList;

	@UiField
	com.github.gwtbootstrap.client.ui.AccordionGroup accordionGroup;

	@UiField
	com.github.gwtbootstrap.client.ui.FluidRow h_filter;

	@UiField
	com.github.gwtbootstrap.client.ui.FluidRow h_title;

	@UiField
	com.github.gwtbootstrap.client.ui.FluidRow h_release_info;

	@UiField
	com.github.gwtbootstrap.client.ui.FluidRow h_release_notes;

	@UiField
	FlowPanel p_release_manager;

	@UiField
	com.github.gwtbootstrap.client.ui.Button b_release_new;
	@UiField
	com.github.gwtbootstrap.client.ui.Button b_releases_manage;
	@UiField
	com.github.gwtbootstrap.client.ui.Button b_release_package;
	@UiField
	com.github.gwtbootstrap.client.ui.Button b_click_statistics;

	@UiField
	com.github.gwtbootstrap.client.ui.TextBox filterText;

	@UiField
	com.github.gwtbootstrap.client.ui.Dropdown dropdown_releases;

	@UiField
	com.github.gwtbootstrap.client.ui.Button filterBtn;

	private static PageTemplateUiBinder uiBinder = GWT
			.create(PageTemplateUiBinder.class);

	/**
	 * The Interface PageTemplateUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 19,
	 *         2015
	 */
	interface PageTemplateUiBinder extends UiBinder<Widget, PageTemplate> {
	}

	/**
	 * Instantiates a new page template.
	 */
	public PageTemplate() {

		initWidget(uiBinder.createAndBindUi(this));
		h_filter.setMarginTop(20);

		filterBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// Window.alert(filterText.getText());
				GWT.log(filterText.getText());
				GcubeReleasesAppController.eventBus
						.fireEvent(new FilterPackageEvent(filterText.getText()));
			}
		});

		// Listen for keyboard events in the input box.
		filterText.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					filterBtn.click();
				}
			}
		});

		b_releases_manage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GcubeReleasesAppController.eventBus
						.fireEvent(new ManageReleasesEvent());

			}
		});

		b_release_new.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GcubeReleasesAppController.eventBus
						.fireEvent(new NewInsertReleasesEvent());

			}
		});

		b_release_package.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GcubeReleasesAppController.eventBus
						.fireEvent(new ManagePackagesEvent());

			}
		});

		b_click_statistics.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GcubeReleasesAppController.eventBus
						.fireEvent(new ShowClickReportEvent());

			}
		});

		// DEFAULT ARE NOT SHOWED MANAGEMENT OPTIONS
		showManagement(false);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				isDisplayManagementMode();
			}
		});
	}

	/**
	 * Checks if is display management mode.
	 */
	private void isDisplayManagementMode() {
		GcubeReleasesServiceAsync.Util.getInstance().isManagementMode(
				new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						showManagement(false);

					}

					@Override
					public void onSuccess(Boolean result) {
						GWT.log("isDisplayManagementMode " + result);
						showManagement(result);
						// headerPgMng.showManagementOptions(true);
					}
				});
	}

	/**
	 * Adds the subsystems.
	 *
	 * @param heading
	 *            the heading
	 * @param widthPX
	 *            the width px
	 * @param anchors
	 *            the anchors
	 */
	public void addSubsystems(String heading, String widthPX,
			List<String> anchors) {
		accordionGroup.setWidth(widthPX);
		accordionGroup.setHeading(heading);

		// hrefHeading.setText(heading);
		// hrefHeading.setIcon(IconType.GEAR);

		try {
			navList.clear();
		} catch (Exception e) {
			// silent
		}
		for (String anchor : anchors) {
			addNavigation(anchor);
		}
	}

	/**
	 * Adds the navigation.
	 *
	 * @param navigation
	 *            the navigation
	 */
	private void addNavigation(final String navigation) {
		final NavLink navLink = new NavLink(navigation);
		navLink.setHref("#" + navigation);
		// dropDownRelease.add(navLink);
		hashReleases.put(navigation, navigation);

		navLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// Window.alert("clicked "+navigation);
			}
		});

		navList.add(navLink);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		navList.clear();
		hashReleases.clear();
	}

	/**
	 * Adds the title.
	 *
	 * @param header
	 *            the header
	 * @param subText
	 *            the sub text
	 * @param size
	 *            the size
	 */
	public void addTitle(String header, String subText, int size) {
		com.github.gwtbootstrap.client.ui.Heading heading = new com.github.gwtbootstrap.client.ui.Heading(
				size, header);
		heading.setText(header);
		heading.setSubtext(subText);
		h_title.add(heading);
	}

	/**
	 * Gets the ui binder.
	 *
	 * @return the ui binder
	 */
	public static PageTemplateUiBinder getUiBinder() {
		return uiBinder;
	}

	/**
	 * Sets the other releases.
	 *
	 * @param otherReleases
	 *            the new other releases
	 */
	public void setOtherReleases(List<Release> otherReleases) {

		dropdown_releases.clear();

		for (final Release release : otherReleases) {
			final NavLink anchor = new NavLink(release.getName());
			// anchor.setTitle(release.getName());
			anchor.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					GcubeReleasesAppController.eventBus
							.fireEvent(new DisplaySelectedReleaseEvent(release));

				}
			});

			dropdown_releases.add(anchor);
		}

	}

	/**
	 * Sets the release info.
	 *
	 * @param reportURI
	 *            the report uri
	 * @param svnURI
	 *            the svn uri
	 * @param othersInfo
	 *            the others info
	 */
	public void setReleaseInfo(String reportURI, String svnURI,
			List<String> othersInfo) {

		Column column = new Column(12);

		for (String other : othersInfo)
			column.add(new HTML(
					"<p style=\"margin-left:5px; margin-bottom:5px;\">" + other
							+ "</p>"));

		if (reportURI != null && !reportURI.isEmpty())
			column.add(new HTML(
					"<span style=\"margin-left:5px;\"><a target=\"_blank\" href=\""
							+ reportURI + "\">Etics Report</a></span>"));

		if (svnURI != null && !svnURI.isEmpty())
			column.add(new HTML(
					"<span style=\"margin-left:5px;\"><a target=\"_blank\" href=\""
							+ svnURI + "\">SVN source</a></span>"));

		h_release_info.add(column);
	}

	/**
	 * Sets the release notes.
	 *
	 * @param notes
	 *            the new release notes
	 */
	public void setReleaseNotes(String notes) {
		h_release_notes.add(new HTML("<div style=\"margin-left:5px; margin-bottom:5px;\">"+
			"<a id=\"release_notes\" href=\"javascript:changeVisibility('release_notes', 'Release_View', 'Release Notes');\">Hide Release Notes</a>" +
			"<div id='Release_View' style=\"display: block;\">" +
				"<p style=\"margin-left:5px; margin-bottom:5px;\">" + notes+ "</p>" +
			"</div></div>"));
	}

	/**
	 * Show release notes.
	 */
	public void showReleaseNotes(boolean bool){
		h_release_notes.setVisible(bool);
	}

	/**
	 * Enable filter by subsystem.
	 *
	 * @param b
	 *            the b
	 */
	public void enableFilterBySubsystem(boolean b) {
		// collapseOne.setVisible(b);

		if (!b)
			accordionGroup.getElement().getStyle().setOpacity(0.2);
		else
			accordionGroup.getElement().getStyle().setOpacity(1);

		navList.setVisible(b);
	}

	/**
	 * Show management.
	 *
	 * @param bool
	 *            the bool
	 */
	public void showManagement(boolean bool) {
		p_release_manager.setVisible(bool);
	}
}
