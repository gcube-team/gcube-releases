<%@ include file="/html/commons/inc.jsp"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="gr.i2s.fishgrowth.model.Site"%>
<%@page import="gr.i2s.fishgrowth.model.Region"%>
<%@page import="gr.i2s.fishgrowth.model.OxygenRating"%>
<%@page import="gr.i2s.fishgrowth.model.CurrentRating"%>
<%@page
	import="org.gcube.portlets.user.simulfishgrowth.model.util.SiteFullUtil"%>

<%
	Log logger = LogFactoryUtil
			.getLog("org.gcube.portlets.user.simulfishgrowth.portlet.jsp." + this.getClass().getSimpleName());
%>


<jsp:useBean id="regionList" type="java.util.ArrayList<Region>"
	scope="request" />
<jsp:useBean id="oxygenRatingList"
	type="java.util.ArrayList<OxygenRating>" scope="request" />
<jsp:useBean id="currentRatingList"
	type="java.util.ArrayList<CurrentRating>" scope="request" />
<jsp:useBean id="addGCubeHeaders"
	type="org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders"
	scope="request" />
<jsp:useBean id="GoogleKey" type="java.lang.String" scope="request" />

<%
	
	if (GoogleKey == null) 
		GoogleKey = "GoogleMapsKeyNeeded";	
	
%>
<script src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleKey%>&callback=initMap" async defer></script>


<portlet:renderURL var="cancel">
	<portlet:param name="mvcPath" value="/html/site/view.jsp"></portlet:param>
</portlet:renderURL>
<portlet:actionURL name="save" var="addUrl"></portlet:actionURL>
<%
	Site entity = null;
	long id = ParamUtil.getLong(request, "id");
	logger.debug(String.format("trying to load [%s]", id));
	if (id > 0) {
		try {
			entity = new SiteFullUtil(addGCubeHeaders).getSiteFull(id);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	logger.debug(String.format("Edit-add [%s]", entity));
%>
   <div id="myModal" class="i2s-modal">
        <span class="i2s-close">&times;</span>
        <a id="map_select_btn_ok">OK</a>
        <div class="modal-content" id="map"style="height:80%"></div>
        <div id="caption"></div>
    </div>
<form action="<%=addUrl%>" id="<portlet:namespace />fm"
	name="<portlet:namespace />fm" method="post">
	<input type="hidden" name="<portlet:namespace />doRun"
		id="<portlet:namespace />doRun" value="true" />


	<fieldset class="fieldset i2s  i2s-large"
		style="display: -webkit-inline-box; display: -moz-box; width: 100%;">
		<legend class="fieldset-legend">Environment</legend>
		<input type="hidden" name="<portlet:namespace />id"
			id="<portlet:namespace />id"
			value="<%=(entity == null ? "0" : entity.getId())%>" />
		<input type="hidden" name="<portlet:namespace />regionId" id="<portlet:namespace />regionId" value="1" />
		<input type="hidden" name="<portlet:namespace />oxygenRatingId" id="<portlet:namespace />oxygenRatingId" value="1" />
		<input type="hidden" name="<portlet:namespace />currentRatingId" id="<portlet:namespace />currentRatingId" value="1" />
		<!-- Name - Oxygen Rating-->
		<div class="column">
			<div class="column-content">
				<div class="control-group form-inline input-text-wrapper">
					<label class="control-label">Name :</label> <input type="text" style="margin-bottom:0px!important;"
						class="field field-first" id="<portlet:namespace />designation"
						name="<portlet:namespace />designation"
						value="<%=(entity == null ? "" : entity.getDesignation())%>">
				</div>
			</div>
		</div>
		
		<!-- Lat-Long - Current Rating -->
		<div class="column">
			<div class="column-content">
				<div class="control-group form-inline input-text-wrapper">
					<label class="control-label">Lat , Long :</label> 
					<input type="text" style="max-width:70px;" id="lat"
						class="field" id="<portlet:namespace />latitude"
						name="<portlet:namespace />latitude"
						value="<%=(entity == null ? "" : entity.getLatitude())%>">,
					<input type="text" style="max-width:70px;" id="lng"
						class="field" id="<portlet:namespace />longitude"
						name="<portlet:namespace />longitude"
						value="<%=(entity == null ? "" : entity.getLongitude())%>"> 
					<a id="mapBtn" style="font-size:22px;cursor:pointer;" title="Select your location from the map"><i class='icon-map-marker'></i></a>	
				</div>
			</div>
		</div>
	</fieldset>

	<fieldset class="fieldset i2s i2s-medium i2s-sm-inputs"
		style="display: -webkit-inline-box; display: -moz-box; width: 100%;">
		<legend class="fieldset-legend">Average temperature
			fortnightly</legend>

		<!-- TEST -->
		<div>
			<div>

				<div class="column"
					style="border-right: 1px solid grey; padding-right: 30px;">
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">January&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodJanA"
								name="<portlet:namespace />periodJanA"
								value="<%=(entity == null ? "" : entity.getPeriodJanA())%>">
							<label class="control-label align-right">16-31 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodJanB"
								name="<portlet:namespace />periodJanB"
								value="<%=(entity == null ? "" : entity.getPeriodJanB())%>">
						</div>
					</div>
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">February&nbsp1-14 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodFebA"
								name="<portlet:namespace />periodFebA"
								value="<%=(entity == null ? "" : entity.getPeriodFebA())%>">
							<label class="control-label align-right">15-end </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodFebB"
								name="<portlet:namespace />periodFebB"
								value="<%=(entity == null ? "" : entity.getPeriodFebB())%>">
						</div>
					</div>
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">March&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodMarA"
								name="<portlet:namespace />periodMarA"
								value="<%=(entity == null ? "" : entity.getPeriodMarA())%>">
							<label class="control-label align-right">16-31 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodMarB"
								name="<portlet:namespace />periodMarB"
								value="<%=(entity == null ? "" : entity.getPeriodMarB())%>">
						</div>
					</div>
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">April&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodAprA"
								name="<portlet:namespace />periodAprA"
								value="<%=(entity == null ? "" : entity.getPeriodAprA())%>">
							<label class="control-label align-right">16-30 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodAprB"
								name="<portlet:namespace />periodAprB"
								value="<%=(entity == null ? "" : entity.getPeriodAprB())%>">
						</div>
					</div>
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">May&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodMayA"
								name="<portlet:namespace />periodMayA"
								value="<%=(entity == null ? "" : entity.getPeriodMayA())%>">
							<label class="control-label align-right">16-31 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodMayB"
								name="<portlet:namespace />periodMayB"
								value="<%=(entity == null ? "" : entity.getPeriodMayB())%>">
						</div>
					</div>

					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">June&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodJunA"
								name="<portlet:namespace />periodJunA"
								value="<%=(entity == null ? "" : entity.getPeriodJunA())%>">
							<label class="control-label align-right">16-30 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodJunB"
								name="<portlet:namespace />periodJunB"
								value="<%=(entity == null ? "" : entity.getPeriodJunB())%>">
						</div>
					</div>
				</div>
				<div class="column">
					<div class="column-content">
						<div style="width: 2px; height: auto; background: grey"></div>
					</div>
				</div>


				<div class="column" style="padding-left: 30px;">
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">July&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodJulA"
								name="<portlet:namespace />periodJulA"
								value="<%=(entity == null ? "" : entity.getPeriodJulA())%>">
							<label class="control-label align-right">16-31 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodJulB"
								name="<portlet:namespace />periodJulB"
								value="<%=(entity == null ? "" : entity.getPeriodJulB())%>">
						</div>
					</div>
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">August&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodAugA"
								name="<portlet:namespace />periodAugA"
								value="<%=(entity == null ? "" : entity.getPeriodAugA())%>">
							<label class="control-label align-right">16-31 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodAugB"
								name="<portlet:namespace />periodAugB"
								value="<%=(entity == null ? "" : entity.getPeriodAugB())%>">
						</div>
					</div>

					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">September&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodSepA"
								name="<portlet:namespace />periodSepA"
								value="<%=(entity == null ? "" : entity.getPeriodSepA())%>">
							<label class="control-label align-right">16-30 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodSepB"
								name="<portlet:namespace />periodSepB"
								value="<%=(entity == null ? "" : entity.getPeriodSepB())%>">
						</div>
					</div>
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">October&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodOctA"
								name="<portlet:namespace />periodOctA"
								value="<%=(entity == null ? "" : entity.getPeriodOctA())%>">
							<label class="control-label align-right">16-31 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodOctB"
								name="<portlet:namespace />periodOctB"
								value="<%=(entity == null ? "" : entity.getPeriodOctB())%>">
						</div>
					</div>


					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">November&nbsp1-15</label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodNovA"
								name="<portlet:namespace />periodNovA"
								value="<%=(entity == null ? "" : entity.getPeriodNovA())%>">
							<label class="control-label align-right">16-30 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodNovB"
								name="<portlet:namespace />periodNovB"
								value="<%=(entity == null ? "" : entity.getPeriodNovB())%>">
						</div>
					</div>
					<div class="column-content">
						<div class="control-group form-inline input-text-wrapper">
							<label class="control-label">December&nbsp1-15 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodDecA"
								name="<portlet:namespace />periodDecA"
								value="<%=(entity == null ? "" : entity.getPeriodDecA())%>">
							<label class="control-label align-right">16-31 </label> <input
								type="number" step="1" min="0" class="field"
								id="<portlet:namespace />periodDecB"
								name="<portlet:namespace />periodDecB"
								value="<%=(entity == null ? "" : entity.getPeriodDecB())%>">
						</div>
					</div>

				</div>
			</div>
		</div>
	</fieldset>

	<fieldset style="padding-top: 20px;">
		
		<button type="submit"class="btn i2s-btn  i2s-success-btn"><i class="icon-save"></i> &nbsp; Save</button>
		<button class="btn i2s-cancel-btn"
			onClick="window.location='<%=cancel.toString()%>'; return false;">Cancel</button>
	</fieldset>
</form>