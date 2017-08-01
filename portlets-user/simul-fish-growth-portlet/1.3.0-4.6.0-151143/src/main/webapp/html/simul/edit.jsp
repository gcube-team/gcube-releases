<%@ include file="/html/commons/inc.jsp"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page
	import="org.gcube.portlets.user.simulfishgrowth.model.util.ScenarioFullUtil"%>
<%@page import="gr.i2s.fishgrowth.model.ScenarioFull"%>
<%@page import="gr.i2s.fishgrowth.model.ModelerFull"%>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.js"></script>
<link type="css/stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker.css" />
<script src="//cdn.ckeditor.com/4.5.11/standard/ckeditor.js"></script>

<%
	Log logger = LogFactoryUtil
			.getLog("org.gcube.portlets.user.simulfishgrowth.portlet.jsp." + this.getClass().getSimpleName());
%>

<jsp:useBean id="modelerList" type="java.util.ArrayList<ModelerFull>"
	scope="request" />
<jsp:useBean id="addGCubeHeaders"
	type="org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders"
	scope="request" />

<portlet:renderURL var="cancel">
	<portlet:param name="mvcPath" value="/html/simul/view.jsp"></portlet:param>
</portlet:renderURL>
<portlet:actionURL name="save" var="addUrl"></portlet:actionURL>

<%
	ScenarioFull entity = null;
	long id = ParamUtil.getLong(request, "id");
	logger.debug(String.format("trying to load [%s]", id));
	if (id > 0) {
		try {
			entity = new ScenarioFullUtil(addGCubeHeaders).getScenarioFull(id);
		} catch (Exception e) {
			logger.error("Coluld not load scenario for " + id, e);
		}
	}
	logger.debug(String.format("Edit-add [%s]", entity));
%>

<form action="<%=addUrl%>" id="<portlet:namespace />fm"
	name="<portlet:namespace />fm" method="post">
	<input type="hidden" name="<portlet:namespace />doRun"
		id="<portlet:namespace />doRun" value="true" />
	<fieldset class="fieldset i2s">
		<input type="hidden" name="<portlet:namespace />id"
			id="<portlet:namespace />id"
			value="<%=(entity == null ? "0" : entity.getId())%>" />
		<div class="">
			<!-- Designation -->
			<div class="column">
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Name :</label> <input type="text"
							class="field field-first" id="<portlet:namespace />designation"
							name="<portlet:namespace />designation"
							value="<%=(entity == null ? "" : entity.getDesignation())%>">
					</div>
				</div>
			</div>

			<!-- Use model -->
			<div class="column">
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Use model :</label> <select
							class="form-control" name="<portlet:namespace />modelerId"
							id="<portlet:namespace />modelerId">
							<%
								for (ModelerFull item : modelerList) {
							%>
							<option
								<%=(item.getId() == (entity == null ? 0L : entity.getModelerId())) ? "selected" : ""%>
								value="<%=item.getId()%>"><%=item.getDesignation()%> (<%=item.getStatusDesignation()%>)
							</option>
							<%
								}
							%>
						</select>
					</div>
				</div>
			</div>

			<!-- Status -->
			<div class="column">
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
			</div>
		</div>

	</fieldset>

	<fieldset class="fieldset i2s  i2s-large"
		style="display: -webkit-inline-box; display: -moz-box;">
		<legend class="fieldset-legend">Hypothesis</legend>
		<div class="">
			<div class="column">
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Initial stock count :</label> <input
							type="number" step="1000" min="0" class="field field-first"
							id="fishNo" name="<portlet:namespace />fishNo"
							value="<%=(entity == null ? "" : entity.getFishNo())%>">
					</div>
				</div>
			</div>

			<div class="column">
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Initial fish weight (gr) :</label> <input
							type="number" step="0.01" min="1" class="field field-first"
							id="<portlet:namespace />weight"
							name="<portlet:namespace />weight"
							value="<%=(entity == null ? "" : entity.getWeight())%>">
					</div>
				</div>
			</div>
		</div>
		<div class="">
			<div class="column">
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Start date: </label> <input
							type="text" class="field field-first datepicker"
							id="<portlet:namespace />startDateFrm"
							name="<portlet:namespace />startDateFrm"
							value="<%=(entity == null ? "" : entity.getStartDateFrm())%>">
						<span class="i2s-datepicker-icon"> <span
							class="icon-calendar"
							id="yui_patched_v3_11_0_1_1474277305639_1206"></span>
						</span>
					</div>
				</div>
			</div>

			<div class="column">
				<div class="column-content">
					<div class="control-group form-inline input-text-wrapper">
						<label class="control-label">Target date</label> <input
							type="text" class="field field-first datepicker"
							id="<portlet:namespace />targetDateFrm"
							name="<portlet:namespace />targetDateFrm"
							value="<%=(entity == null ? "" : entity.getTargetDateFrm())%>">
						<span class="i2s-datepicker-icon"> <span
							class="icon-calendar"
							id="yui_patched_v3_11_0_1_1474277305639_1206"></span>
						</span>
					</div>
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

	<fieldset style="padding-top: 20px;">
		<button type="submit" class="btn i2s-btn i2s-success-btn">
			<i class="icon-save"></i>&nbsp; Save
			<%=entity == null ? "" : entity.getModelerStatusId() == org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil.STATUS_READY
							? " and Calculate"
							: " and Return"%></button>
		<button class="btn i2s-cancel-btn"
			onClick="window.location='<%=cancel.toString()%>'; return false;">Cancel</button>
	</fieldset>
</form>

<script>
	$(document).ready(function() {
		$('.datepicker').datepicker({
			format : 'dd/mm/yyyy',
			autoclose : true
		});

		CKEDITOR.replace('comments');

	});
</script>
