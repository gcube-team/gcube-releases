<%@ include file="/html/commons/inc.jsp"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="org.gcube.portlets.user.simulfishgrowth.model.util.ModelerFullUtil"%>
<%@page import="gr.i2s.fishgrowth.model.ModelerFull"%>
<%@page import="gr.i2s.fishgrowth.model.Species"%>
<%@page import="gr.i2s.fishgrowth.model.Site"%>
<%@page import="gr.i2s.fishgrowth.model.BroodstockQuality"%>
<%@page import="gr.i2s.fishgrowth.model.FeedQuality"%>
<%@page import="java.io.File"%>
<%@page import="com.google.common.io.Files"%>
<%@page import="com.google.common.base.Strings"%>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.js"></script>
<link type="css/stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker.css" />
<script src="https://cdn.ckeditor.com/4.6.2/basic/ckeditor.js"></script>

<%
	Log logger = LogFactoryUtil
			.getLog("org.gcube.portlets.user.simulfishgrowth.portlet.jsp." + this.getClass().getSimpleName());
%>


<%!public String shortenFilename(String compactFilename, int maxDisplayFileLength) {
		int length = compactFilename.length();
		if (length > maxDisplayFileLength) {
			length = maxDisplayFileLength; //truncate
			String ext = Files.getFileExtension(compactFilename);
			String name = Files.getNameWithoutExtension(compactFilename);
			if (!Strings.isNullOrEmpty(ext)) {
				length = length - ext.length(); //mind the dot
				length--; //the dot
			}
			length--; // the ellipsis
			compactFilename = name.substring(0, length) + "&hellip;";
			if (!Strings.isNullOrEmpty(ext)) {
				compactFilename = compactFilename + "." + ext;
			}
		}
		return compactFilename;
	}%>

<jsp:useBean id="specieList" type="java.util.ArrayList<Species>"
	scope="request" />
<jsp:useBean id="siteList" type="java.util.ArrayList<Site>"
	scope="request" />
<jsp:useBean id="broodstockQualityList"
	type="java.util.ArrayList<BroodstockQuality>" scope="request" />
<jsp:useBean id="feedQualityList"
	type="java.util.ArrayList<FeedQuality>" scope="request" />
<jsp:useBean id="addGCubeHeaders"
	type="org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders" scope="request" />


<portlet:renderURL var="cancel">
	<portlet:param name="mvcPath" value="/html/modeler/view.jsp"></portlet:param>
</portlet:renderURL>
<portlet:actionURL name="save" var="addUrl"></portlet:actionURL>
<%
	ModelerFull entity = null;
	long id = ParamUtil.getLong(request, "id");
	logger.debug(String.format("trying to load [%s]", id));
	if (id > 0) {
		try {
			entity = new ModelerFullUtil(addGCubeHeaders).getModelerFull(id);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	logger.debug(String.format("Edit-add [%s]", entity));
	final int maxDisplayFileLength = 20;
%>

<form action="<%=addUrl%>" id="<portlet:namespace />fm"
	name="<portlet:namespace />fm" enctype="multipart/form-data"
	method="post">
	<input type="hidden" name="<portlet:namespace />doRun"
		id="<portlet:namespace />doRun" value="true" />
	<fieldset class="fieldset i2s i2s-medium">
		<input type="hidden" name="<portlet:namespace />id"
			id="<portlet:namespace />id"
			value="<%=(entity == null ? "0" : entity.getId())%>" />
		
			<div class="column">
				<!-- Designation -->
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Name :</label> <input type="text"
							class="field field-first" id="<portlet:namespace />designation"
							name="<portlet:namespace />designation"
							value="<%=(entity == null ? "" : entity.getDesignation())%>">
					</div>
				</div>
				<!-- Species -->
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Species :</label> <select
							class="form-control" name="<portlet:namespace />speciesId"
							id="<portlet:namespace />speciesId">
							<%
								for (Species item : specieList) {
							%>
							<option
								<%=(item.getId() == (entity == null ? 0L : entity.getSpeciesId())) ? "selected" : ""%>
								value="<%=item.getId()%>"><%=item.getDesignation()%>
							</option>
							<%
								}
							%>
						</select>
					</div>
				</div>
				<!-- Broodstock Genetic Improvement -->
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label" style="width:250px!important;">Broodstock Genetic Improvement
							:</label> <input type="checkbox" class="field field-first"
							id="<portlet:namespace />broodstockGeneticImprovement"
							name="<portlet:namespace />broodstockGeneticImprovement"
							checked="<%=(entity == null ? false : entity.isBroodstockGeneticImprovement())%>">
					</div>
				</div>
			</div>

			<!-- Site -->
			<div class="column">
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Site :</label> <select
							class="form-control" name="<portlet:namespace />siteId"
							id="<portlet:namespace />siteId">
							<%
								for (Site item : siteList) {
							%>
							<option
								<%=(item.getId() == (entity == null ? 0L : entity.getSiteId())) ? "selected" : ""%>
								value="<%=item.getId()%>"><%=item.getDesignation()%>
							</option>
							<%
								}
							%>
						</select>
					</div>
				</div>
				<!-- Broodstock Quality -->
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Broodstock Quality :</label> <select
							class="form-control"
							name="<portlet:namespace />broodstockQualityId"
							id="<portlet:namespace />broodstockQualityId">
							<%
								for (BroodstockQuality item : broodstockQualityList) {
							%>
							<option
								<%=(item.getId() == (entity == null ? 0L : entity.getBroodstockQualityId())) ? "selected" : ""%>
								value="<%=item.getId()%>"><%=item.getDesignation()%>
							</option>
							<%
								}
							%>
						</select>
					</div>
				</div>
			</div>


			<div class="column">
				<!-- Status -->
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Status :</label> <input type="text"
							name="<portlet:namespace />statusDesignation"
							class="field field-first"
							id="<portlet:namespace />statusDesignation"
							value="<%=(entity == null ? "" : entity.getStatusDesignation())%>"
							disabled>
					</div>
				</div>
				<!-- Feed Quality -->
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Feed Quality :</label> <select
							class="form-control" name="<portlet:namespace />feedQualityId"
							id="<portlet:namespace />feedQualityId">
							<%
								for (FeedQuality item : feedQualityList) {
							%>
							<option
								<%=(item.getId() == (entity == null ? 0L : entity.getFeedQualityId())) ? "selected" : ""%>
								value="<%=item.getId()%>"><%=item.getDesignation()%>
							</option>
							<%
								}
							%>
						</select>
					</div>
				</div>
			</div>
		

	</fieldset>

	<div class="">
		<label class="i2s-label">Comments</label>
		<textarea id="<portlet:namespace />comments" class="field" cols="300"
			rows="5" name="<portlet:namespace />comments"
			style="width: 50%; max-width: 800px; min-width: 400px;"><%=(entity == null ? "" : entity.getComments())%></textarea>
	</div>

	<fieldset class="fieldset i2s  i2s-large"
		style="display: -webkit-inline-box; display: -moz-box;">
		<legend class="fieldset-legend">Upload datasets</legend>

		<div class="column">
			<div class="column-content">
				<div class="control-group form-inline input-text-wrapper">
					<label class="control-label" style="margin-top: 5px;">Sample data</label> 
						<%
							if (entity == null || Strings.isNullOrEmpty(entity.getUploadFilenameData())) {
						%>
						
							<input type="hidden" name="<portlet:namespace />fileUploadActionData" id="<portlet:namespace />fileUploadActionData" value="replace" />
						<%
							} else {
								String compactFilename = shortenFilename(entity.getUploadFilenameData(), maxDisplayFileLength);
						%>
					  <select class="form-control"
						name="<portlet:namespace />fileUploadActionData"
						id="<portlet:namespace />fileUploadActionData">
						<option value="keep"><%=String.format("keep `%s`", compactFilename)%></option>
						<option value="replace" selected><%=String.format("replace `%s` with ", compactFilename)%></option>
					  </select>
						<%
							}
						%>
				</div>
			</div>
			<div class="column-content">
				<div class="control-group form-inline input-text-wrapper">
					<label class="control-label" style="margin-top: 19px;">Weight limits</label>
						<%
							if (entity == null || Strings.isNullOrEmpty(entity.getUploadFilenameWeights())) {
						%>
						<input type="hidden" name="<portlet:namespace />fileUploadActionWeights" id="<portlet:namespace />fileUploadActionWeights" value="replace" />
						<%
							} else {
								String compactFilename = shortenFilename(entity.getUploadFilenameWeights(), maxDisplayFileLength);
						%>
					  <select class="form-control"
						name="<portlet:namespace />fileUploadActionWeights"
						id="<portlet:namespace />fileUploadActionWeights">
						<option value="keep"><%=String.format("keep `%s`", compactFilename)%></option>
						<option value="replace" selected><%=String.format("replace `%s` with ", compactFilename)%></option>
					  </select>
						<%
							}
						%>
				</div>
			</div>
		</div>
		<div class="column">
			<div class="column-content">
				<div class="control-group form-inline input-text-wrapper">
					<input type="file" name="<portlet:namespace />fileUploadData" id="<portlet:namespace />fileUploadData" class="inputfile inputfile-2" data-multiple-caption="{count} files selected" />
					<label for="<portlet:namespace />fileUploadData"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="17" viewBox="0 0 20 17"><path d="M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z"/></svg> <span>Choose a file&hellip;</span></label>
			
				</div>
			</div>
			<div class="column-content">
				 <input type="file" name="<portlet:namespace />fileUploadWeights" id="<portlet:namespace />fileUploadWeights" class="inputfile inputfile-2" data-multiple-caption="{count} files selected" />
					<label for="<portlet:namespace />fileUploadWeights"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="17" viewBox="0 0 20 17"><path d="M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z"/></svg> <span>Choose a file&hellip;</span></label>
			
				</div>
			</div>
		</div>
	<fieldset style="padding-top: 20px;">
		<div class="column" style="width:464px;">
		  <button type="submit"class="btn i2s-btn  i2s-success-btn"><i class="icon-save"></i> &nbsp; Save and Generate Model</button>
		  <button class="btn i2s-cancel-btn" onClick="window.location='<%=cancel.toString()%>'; return false;">Cancel</button>
		</div>
		<div class="column" style="width:464px;text-align:right;">
			<button class="btn i2s-cancel-btn" onClick="window.location='<%=cancel.toString()%>'; return false;" disabled="disabled"><i class="icon-download"></i> Export</button>
		</div>
	</fieldset>

</form>

<script>
	$(document).ready(function(){
		$('.datepicker').datepicker({
			format : 'dd/mm/yyyy',
			autoclose : true
		});

		
		CKEDITOR.replace('textarea_comments');
		
		var inputs = $(".inputfile");
		Array.prototype.forEach.call(inputs, function(input){

		var label	 = input.nextElementSibling,
					labelVal = label.innerHTML;

				input.addEventListener( 'change', function( e )
				{
				
					var fileName = '';
					if( this.files && this.files.length > 1 )
						fileName = ( this.getAttribute( 'data-multiple-caption' ) || '' ).replace( '{count}', this.files.length );
					else
						fileName = e.target.value.split( '\\' ).pop();

					if( fileName )
						label.querySelector( 'span' ).innerHTML = fileName;
					else
						label.innerHTML = labelVal;
				});

				// Firefox bug fix
				input.addEventListener( 'focus', function(){ input.classList.add( 'has-focus' ); });
				input.addEventListener( 'blur', function(){ input.classList.remove( 'has-focus' ); });
		})
		});
</script>
