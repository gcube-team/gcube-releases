/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface StatAlgoImporterResources extends ClientBundle {

	public static final StatAlgoImporterResources INSTANCE = GWT
			.create(StatAlgoImporterResources.class);

	@Source("SAIStyles.css")
	SAIStyles saiStyles();

	@Source("WikiLink.txt")
	ExternalTextResource wikiLink();

	@Source("sai-logo.png")
	ImageResource saiLogo();
	
	@Source("help_32.png")
	ImageResource help32();

	@Source("help_24.png")
	ImageResource help24();

	@Source("service_profile_32.png")
	ImageResource serviceProfile32();

	@Source("service_profile_24.png")
	ImageResource serviceProfile24();

	
	@Source("download_32.png")
	ImageResource download32();

	@Source("download_24.png")
	ImageResource download24();

	@Source("download_16.png")
	ImageResource download16();

	@Source("upload_32.png")
	ImageResource upload32();

	@Source("upload_24.png")
	ImageResource upload24();
	
	@Source("github_32.png")
	ImageResource gitHub32();

	@Source("github_24.png")
	ImageResource gitHub24();
	

	@Source("job_32.png")
	ImageResource job32();

	@Source("reload_32.png")
	ImageResource reload32();

	@Source("reload_24.png")
	ImageResource reload24();

	@Source("reload_16.png")
	ImageResource reload16();

	@Source("input_16.png")
	ImageResource input16();

	@Source("output_16.png")
	ImageResource output16();

	@Source("cancel_32.png")
	ImageResource cancel32();

	@Source("cancel_24.png")
	ImageResource cancel24();

	@Source("algorithm_32.png")
	ImageResource algorithm32();

	@Source("algorithm_24.png")
	ImageResource algorithm24();

	@Source("publish_32.png")
	ImageResource publish32();

	@Source("publish_24.png")
	ImageResource publish24();

	@Source("zip_32.png")
	ImageResource zip32();

	@Source("zip_24.png")
	ImageResource zip24();

	@Source("project-create_32.png")
	ImageResource projectCreate32();

	@Source("project-create_24.png")
	ImageResource projectCreate24();

	@Source("project-open_32.png")
	ImageResource projectOpen32();

	@Source("project-open_24.png")
	ImageResource projectOpen24();

	@Source("project-save_32.png")
	ImageResource projectSave32();

	@Source("project-save_24.png")
	ImageResource projectSave24();

	@Source("add_24.png")
	ImageResource add24();

	@Source("add_16.png")
	ImageResource add16();

	@Source("delete_24.png")
	ImageResource delete24();

	@Source("delete_16.png")
	ImageResource delete16();

	@Source("save_24.png")
	ImageResource save24();

	@Source("save_16.png")
	ImageResource save16();
}
