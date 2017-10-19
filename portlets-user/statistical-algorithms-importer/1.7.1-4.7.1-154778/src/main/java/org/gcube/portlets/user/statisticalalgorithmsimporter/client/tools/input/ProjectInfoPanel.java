package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.ProjectInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ProjectInfoPanel extends ContentPanel {

	private static final int LABAEL_WIDTH = 120;
	@SuppressWarnings("unused")
	private EventBus eventBus;
	private TextField algorithmName;
	private TextField algorithmDescription;
	private TextField algorithmCategory;

	public ProjectInfoPanel(Project project, EventBus eventBus) {
		super();
		Log.debug("ProjectInfoPanel");
		this.eventBus = eventBus;

		try {
			init();
			create(project);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void init() {
		setHeaderVisible(false);
		setResize(true);
		setBodyBorder(false);
		setBorders(false);
		forceLayoutOnResize = true;
	}

	private void create(Project project) {

		//
		algorithmName = new TextField();
		algorithmName.setAllowBlank(false);
		algorithmName.addValidator(
				new RegExValidator("^[a-zA-Z0-9_]*$", "Attention only characters a-z,A-Z,0-9 are allowed"));
		algorithmName.addValidator(new RegExValidator("^((?!(TEST|test|Test)).)*$",
				"Attention the words that contain TEST are invalid names"));

		algorithmName.addValidator(new MaxLengthValidator(100));

		algorithmName.setEmptyText("Enter name...");

		FieldLabel nameLabel = new FieldLabel(algorithmName, "Name");
		nameLabel.setLabelWidth(LABAEL_WIDTH);

		//
		algorithmDescription = new TextField();
		algorithmDescription.setAllowBlank(false);
		algorithmDescription.setEmptyText("Enter description...");
		algorithmDescription.addValidator(new RegExValidator("^[^\"]*$", "Attention character \" is not allowed"));
		algorithmDescription.addValidator(new RegExValidator("^[^|]*$", "Attention character | is not allowed"));

		FieldLabel descriptionLabel = new FieldLabel(algorithmDescription, "Description");
		descriptionLabel.setLabelWidth(LABAEL_WIDTH);

		//
		algorithmCategory = new TextField();
		algorithmCategory.setAllowBlank(false);
		algorithmCategory.setEmptyText("Enter description...");
		algorithmCategory.addValidator(
				new RegExValidator("^[a-zA-Z0-9_]*$", "Attention only characters a-z,A-Z,0-9 are allowed"));
		algorithmCategory.addValidator(new RegExValidator("^((?!(TEST|test|Test)).)*$",
				"Attention the words that contain TEST are invalid names"));
		algorithmCategory.addValidator(new MaxLengthValidator(32));
		FieldLabel categoryLabel = new FieldLabel(algorithmCategory, "Category");
		categoryLabel.setLabelWidth(LABAEL_WIDTH);

		if (project != null && project.getInputData() != null && project.getInputData().getProjectInfo() != null) {

			if (project.getInputData().getProjectInfo().getAlgorithmName() != null) {
				algorithmName.setValue(project.getInputData().getProjectInfo().getAlgorithmName());
			}
			if (project.getInputData().getProjectInfo().getAlgorithmDescription() != null) {
				algorithmDescription.setValue(project.getInputData().getProjectInfo().getAlgorithmDescription());
			}

			if (project.getInputData().getProjectInfo().getAlgorithmCategory() != null) {
				algorithmCategory.setValue(project.getInputData().getProjectInfo().getAlgorithmCategory());
			}

		}

		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.setAdjustForScroll(false);
		vlc.setScrollMode(ScrollMode.NONE);

		vlc.add(nameLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(descriptionLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(categoryLabel, new VerticalLayoutData(1, -1, new Margins(0)));

		add(vlc, new MarginData(new Margins(0)));
	}

	public void update(Project project) {
		Log.debug("Update Project Info: " + project);
		if (project != null && project.getInputData() != null && project.getInputData().getProjectInfo() != null) {

			if (project.getInputData().getProjectInfo().getAlgorithmName() != null) {
				algorithmName.setValue(project.getInputData().getProjectInfo().getAlgorithmName());
			} else {
				algorithmName.clear();
			}
			if (project.getInputData().getProjectInfo().getAlgorithmDescription() != null) {
				algorithmDescription.setValue(project.getInputData().getProjectInfo().getAlgorithmDescription());
			} else {
				algorithmDescription.clear();
			}
			if (project.getInputData().getProjectInfo().getAlgorithmCategory() != null) {
				algorithmCategory.setValue(project.getInputData().getProjectInfo().getAlgorithmCategory());
			} else {
				algorithmCategory.clear();
			}

		} else {
			algorithmName.clear();
			algorithmDescription.clear();
			algorithmCategory.clear();

		}

	}

	public ProjectInfo getProjectInfo() throws Exception {

		if (!algorithmName.isValid()) {
			Log.debug("AlgorithmName " + algorithmName.getCurrentValue());
			throw new Exception("Invalid algorithm name");
		}

		if (!algorithmDescription.isValid()) {
			Log.debug("AlgorithmDescription " + algorithmDescription.getCurrentValue());
			throw new Exception("Invalid algorithm description");
		}

		if (!algorithmCategory.isValid()) {
			Log.debug("AlgorithmCategory " + algorithmCategory.getCurrentValue());
			throw new Exception("Invalid algorithm category");
		}

		String name = algorithmName.getCurrentValue();
		String description = algorithmDescription.getCurrentValue();
		String category = algorithmCategory.getCurrentValue();

		return new ProjectInfo(name, description, category);

	}
}
