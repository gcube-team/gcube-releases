<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<meta name="viewport" content="width=device-width, initial-scale=1.0">



<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/noty-animate.css" />			
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/analysis.css" />	

<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-1.12.0.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui-1.10.3.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.validate.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.validate.additional-methods.min.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap-2.3.2.min.js"></script>
<script src='<%=request.getContextPath()%>/js/jquery.noty.packaged.min.js'></script>						
<script src="<%=request.getContextPath()%>/js/analysis.js"></script>

<portlet:defineObjects />

<portlet:resourceURL id="PerformAnalysis" var="PerformAnalysis"/>
<portlet:resourceURL id="SimulFishGrowthDataAPI" var="SimulFishGrowthDataAPI"/>
<portlet:resourceURL id="SimulFishGrowthDataSpecies" var="SimulFishGrowthDataSpecies"/>
<portlet:resourceURL id="SimulFishGrowthDataModel" var="SimulFishGrowthDataModel"/>

<div class="techno-economic-analysis-portlet">				
	<div id="custom-container" >
		<form class="form-horizontal custom-col" id="tea_left_form" >									
			<!-- <div class="control-group">
			  <label class="control-label" for="tea_fish_type">Fish type</label>
				<div class="controls">
					<select id="tea_production_model">
						<option disabled selected>Choose your Fish Species</option>
					</select>
				</div>
			</div> -->
						
			<div class="control-group custom-row">
				<label class="control-label" for="tea_production_model">Production model <strong style="color:red">*</strong></label>
				<div class="controls">
					<select id="tea_production_model" name="model">
						<option disabled selected>Choose a Model</option>						
					</select>
					<br><span class="help-inline"></span>
				</div>
			</div>					

			
			<div class="control-group custom-row">
				<label class="control-label" for="tea_maturity_time">Maturity Time (Months)</label>
				<div class="controls">
					<input type="number" class="tea_input_percent" id="tea_maturity_time" name="maturityTime" min="15" value="18"  max="20" step="1">	
					<br><span class="help-inline"></span>							
				</div>
			</div>	
					
			<div class="control-group custom-row">
				<label class="control-label"> Aqua farm is off shore</label>
				<div class="controls">					
					<input type="checkbox" id="tea_is_off_shore_aqua_farm" checked="checked" >  
					<label for="tea_is_off_shore_aqua_farm"><span></span></label>		 			
				</div>  
			</div>			
		</form>		
		
		<form class="form-horizontal custom-col" id="tea_mid_form">	
			<div class="control-group custom-row">
				<label class="control-label" for="tea_tax_rate">Tax rate (%)</label>
				<div class="controls">
					<input type="number" class="tea_input_percent" id="tea_tax_rate" name="taxRate" min="0" value="29"  step="0.1">	
					<br><span class="help-inline"></span>							
				</div>
			</div>	
			  
			<div class="control-group custom-row">
				<label class="control-label" for="tea_discount_rate">Discount Rate (%)</label>
				<div class="controls">
					<input type="number" class="tea_input_percent" id="tea_discount_rate" name="discountRate" min="0" value="3.75"  step="0.01">	
					<br><span class="help-inline"></span>							
				</div>
			</div>	
			
			<div class="control-group custom-row">
				<label class="control-label" for="tea_inflation_rate">Price Inflation Rate (%)</label>
				<div class="controls">
					<input type="number" class="tea_input_percent" id="tea_inflation_rate" name="inflationRate" min="0" value="0.65" step="0.01">	
					<br><span class="help-inline"></span>							
				</div>
			</div>	
		</form>
		
		<form class="form-horizontal custom-col" id="tea_right_form">	

			<div class="control-group custom-row">
				<label class="control-label" for="tea_fish_feed_price">Feed price (per kg)</label>
				<div class="controls">
					<input  type="number" class="tea_input_currency" name="feedPrice" id="tea_fish_feed_price" min="0.01" value="1.25" step="0.01">	
					<select class="tea_currency" disabled>
						<option selected="selected">&euro;</option>
						<option>$</option>
					</select>
					<br><span class="help-inline"></span>			
				</div>								
			</div>	
			  
			<div class="control-group custom-row">
				<label class="control-label" for="tea_fish_fry_price">Fry price (per kg)</label>
				<div class="controls">
					<input type="number" class="tea_input_currency" name="fryPrice" id="tea_fish_fry_price" min="0.01" value="0.20" step="0.01">
					<select class="tea_currency" disabled>
						<option selected="selected">&euro;</option>
						<option>$</option>
					</select>
					<br><span class="help-inline"></span>
				</div>
			</div>				
			
			<div class="control-group custom-row">
				<label class="control-label" for="tea_fish_selling_price">Selling price (per kg)</label>
				<div class="controls">
					<input type="number" class="tea_input_currency" name="fishPrice" id="tea_fish_selling_price"  min="0.01" value="4.80" step="0.01">
					<select class="tea_currency" disabled>
						<option selected="selected">&euro;</option>
						<option>$</option>
					</select>
					<br><span class="help-inline"></span>	
				</div>
			</div>	
		</form>
			<!-- <div class="control-group">
				<label class="control-label" for="tea_fish_mix">Mix (%)</label>
				<div class="controls"> -->
					<input type="hidden" class="form-control" name="fishMix" id="tea_fish_mix" min="0.01" value="100" step="0.1">
				<!-- </div>
				</div>		
			</div> -->
		</div>
	<div class="row-fluid">
		<div class="tea_note_text">*The analysis is based on aqua farms of annual fish production of 1000 tons</div>
		<br>
	</div>
	
	<div class="tea_perform_button">
		<button id="tea_perform_button" type="submit" class="btn" data-element="submit" disabled>Perform estimation analysis</button>
	   	<img class="tea_loader" style="display: none" alt="loading" src="<%=request.getContextPath()%>/img/loader.svg"></img>
	</div>
	
	<div id="tea_results_container">
		<hr>	
	
		<ul class="nav nav-tabs">
		  <li class="active"><a data-toggle="tab" href="#indicators">Indicators</a></li>
		  <li><a data-toggle="tab" href="#detailed">Detailed analysis</a></li>
		  <li><a data-toggle="tab" href="#cummulative">Cumulative profit/loss</a></li>
		  <li><a data-toggle="tab" href="#netprofit">Yearly net profit margin</a></li>
		  <li><a data-toggle="tab" href="#tableView">Table view</a></li>
		</ul>
		
		<div id="tea-noty-container"></div>	
		
		<div class="tab-content"  style="display: none">
			<div id="indicators" class="tab-pane fade in active">		  
				<div class="tea_results_container">				
					<div class="tea_results_label_div">
						<span class="tea_results_label">Net Present Value (NPV)&nbsp;:&nbsp;</span> <br>
						<span class="tea_results_label">Internal Rate of Return (IRR)&nbsp;:&nbsp;</span><br>
					</div>
					
					<div class="tea_results_value_div">
						<span id="tea_dep_npv" class="tea_results_value">#</span><br>
						<span id="tea_dep_irr" class="tea_results_value">#</span><br>
					</div>
				</div>
			</div>
			
			<div id="detailed" class="tab-pane fade">
				<div id="tea_dep_details_chart_area"></div>
				<div class="tea_help_container">
					<ul>
						<li>
							<div class="tea_help_text">
								<p style="margin-bottom:0px;color:gray"> <strong>EBITDA</strong> = Earnings Before Interest, Taxes, Depreciation and Amortization </p>
							</div>
						</li>
						<li>
							<div class="tea_help_text">
								<p style="margin-bottom:0px;color:gray"><strong>EBIAT</strong>  = Earnings Before Interest After Taxes</p> 
							</div>
						</li>
					</ul>
				</div>
			</div>
			
			<div id="cummulative" class="tab-pane fade">
				<div id="tea_dep_profit_loss_chart_area"></div>
				<div style="height: 30px"></div>
			</div>
			
			<div id="netprofit" class="tab-pane fade">
				<div id="tea_dep_net_profit_chart_area"></div>
				<div style="height: 30px"></div>
			</div>
			
			<div id="tableView" class="tab-pane fade">
				<table id="tea_dep_table_view"></table>
				<div class="tea_help_container">
					<ul>
						<li>
							<div class="tea_help_text">
								<p style="margin-bottom:0px;color:gray"><strong>OA Cost</strong>  = Operation & Administration Cost</p> 
							</div>
						</li>
						<li>
							<div class="tea_help_text">
								<p style="margin-bottom:0px;color:gray"> <strong>EBITDA</strong> = Earnings Before Interest, Taxes, Depreciation and Amortization </p>
							</div>
						</li>
						<li>
							<div class="tea_help_text">
								<p style="margin-bottom:0px;color:gray"><strong>EBIAT</strong>  = Earnings Before Interest After Taxes</p> 
							</div>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>	
</div>

<script defer="defer" type="text/javascript">
	(function() {
	    $(document).ready(function () {
	    	
	    	window.Analytics.init({
	    		'ContextPath': '<%=request.getContextPath()%>/',
	    		'ResourceURL': '<portlet:resourceURL id="{url}?{params}" />',
	    		'NameSpaceNative': 'portlet:namespace',
	    		'PerformAnalysisUrl': '<%= PerformAnalysis %>',
			    'SimulFishGrowthDataSpeciesUrl' : 	'<%= SimulFishGrowthDataSpecies %>',
			    'SimulFishGrowthDataModelUrl'	:	'<%= SimulFishGrowthDataModel %>'
	    	});
			
		});
	}());
	
    var SimulFishGrowthDataSpeciesUrl	= 	'<%= SimulFishGrowthDataSpecies %>';
    var SimulFishGrowthDataModelUrl		=	'<%= SimulFishGrowthDataModel %>';	
</script>


<script defer="defer" type="text/javascript">
</script>