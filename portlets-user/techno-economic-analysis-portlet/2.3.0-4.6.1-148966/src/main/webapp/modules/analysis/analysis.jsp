<div class="techno-economic-analysis-portlet">
	
	<form class="custom-container" id="analysisForm" >
		<div class="form-horizontal custom-col" id="tea_left_form" >	
			<div class="control-group custom-row">
				<label class="control-label" for="tea_production_model">Production model <span style="color:red">&#10033;</span></label>
				<div class="controls">
					<select id="tea_production_model" name="model">
						<option disabled selected>Choose a Model</option>						
					</select>
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>											
				</div>
			</div>					

			<div class="control-group custom-row">
				<label class="control-label" for="tea-fish-species">Fish species</label>
				<div class="controls">
					<input type="text" class="tea_input_percent" id="tea-fish-species" name="fishSpecies" readonly>	
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>		
				</div>
			</div>	
					
			<div class="control-group custom-row">
				<label class="control-label" for="tea_maturity_time">Maturity Time (Months)</label>
				<div class="controls">
					<input type="number" class="tea_input_percent" id="tea_maturity_time" name="maturityTime" min="15" value="18"  max="20" step="1">
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>						
				</div>
			</div>	
					
			<div class="control-group custom-row">
				<label class="control-label"> Aqua farm is off shore</label>
				<div class="controls">					
					<input type="checkbox" id="tea_is_off_shore_aqua_farm" checked="checked" >
					<label for="tea_is_off_shore_aqua_farm" style="display: inline-block;"></label>		
				</div>  
			</div>			
		</div>		
		
		<div class="form-horizontal custom-col" id="tea_mid_form">	
			<div class="control-group custom-row">
				<label class="control-label" for="tea_tax_rate">Tax rate (%)</label>
				<div class="controls">
					<input type="number" class="tea_input_percent" id="tea_tax_rate" name="taxRate" min="0" value="29"  step="0.1">
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>							
				</div>
			</div>	
			  
			<div class="control-group custom-row">
				<label class="control-label" for="tea_discount_rate">Discount Rate (%)</label>
				<div class="controls">
					<input type="number" class="tea_input_percent" id="tea_discount_rate" name="discountRate" min="0" value="3.75"  step="0.01">
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>							
				</div>
			</div>	
			
			<div class="control-group custom-row">
				<label class="control-label" for="tea_inflation_rate">Price Inflation Rate (%)</label>
				<div class="controls">
					<input type="number" class="tea_input_percent" id="tea_inflation_rate" name="inflationRate" min="0" value="0.65" step="0.01">
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>							
				</div>
			</div>	
		</div>
		
		<div class="form-horizontal custom-col" id="tea_right_form">	

			<div class="control-group custom-row">
				<label class="control-label" for="tea_fish_feed_price">Feed price (per kg)</label>
				<div class="controls">
					<input  type="number" class="tea_input_currency" name="feedPrice" id="tea_fish_feed_price" min="0.01" value="1.15" step="0.01">
					<select class="tea_currency" disabled>
						<option selected="selected">&euro;</option>
						<option>$</option>
					</select>
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>	
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
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>	
				</div>
			</div>				
			
			<div class="control-group custom-row">
				<label class="control-label" for="tea_fish_selling_price">Selling price (per kg)</label>
				<div class="controls">
					<input type="number" class="tea_input_currency" name="fishPrice" id="tea_fish_selling_price"  min="0.01" value="5.20" step="0.01">
					<select class="tea_currency" disabled>
						<option selected="selected">&euro;</option>
						<option>$</option>
					</select>
					<i class="fa fa-info-circle label-tooltip" aria-hidden="true"></i>											
				</div>
			</div>	
		</div>

	
		<div class="row-fluid">
			<p class="tea_note_text">*The analysis is based on aqua farms of annual fish production of 1000 tonnes</p>			
		</div>
		
		<div class="tea-perform-button">
			<button id="tea_perform_button" type="submit" class="btn" data-element="submit" disabled>Perform estimation analysis</button>
			<img class="tea_loader" style="display: none" alt="loading" src="<%=request.getContextPath()%>/img/loader.svg"></img>
			<input type="button" id="tea-reset-button" value="Reset"></input>
		</div>
	
		<div id="tea-results-container">
		
			<hr>
			<div id="tea-results-container-header" style="display: inline-block; width: 100%;"> 
			  	<button class="btn save-analysis"><i class="fa fa-fw fa-floppy-o" aria-hidden="true"></i>Save in Workspace</button>
				
				<div id="tea-noty-container"></div>		
			</div>
			
			<ul class="nav nav-tabs">
			  <li class="active"><a data-toggle="tab" href="#indicators">Indicators</a></li>
			  <li><a data-toggle="tab" href="#detailed">Detailed analysis</a></li>
			  <li><a data-toggle="tab" href="#cummulative">Cumulative profit/loss</a></li>
			  <li><a data-toggle="tab" href="#netprofit">Yearly net profit margin</a></li>
			  <li><a data-toggle="tab" href="#tableView">Table view</a></li>
			</ul>			

			<div id="tea-info-container" align="center">
				<p id="tea-insert-text">Insert desired parameters and perform estimation analysis <br> or</p>
				<button class="btn load-analysis"><i class="fa fa-fw fa-folder-open" aria-hidden="true"></i>Load from Workspace</button>
			</div>
			
			<div class="tab-content"  style="display: none">
				<div id="indicators" class="tab-pane fade in active">		  
					<div class="tea-indicators-container">				
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
	</form>
</div>