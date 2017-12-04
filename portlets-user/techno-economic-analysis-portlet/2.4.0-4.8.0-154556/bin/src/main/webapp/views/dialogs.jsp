<div id="tea-workspace-dialog"  class="tea-dialog" title="WORKSPACE" >	 
	<ul class="tea-workspace-container nav nav-list">
		<li id="tea-workspace"> </li>
	</ul>
</div>
	
<div id="tea-message-dialog" class="tea-dialog" align="center" title="Help Message">
	There is no Analysis to save. Please perform an analysis first.
</div>

<div id="tea-create-dialog" class="tea-dialog" title="Create Folder">	
	<form class="form-horizontal custom-col">		
	 	<div class="control-group  custom-row">
			<label class="control-label" for="folderName">Folder Name <span style="color:red">&#10033;</span></label>
			<div class="controls">
		  		<input type="text" id="folderName" name="folderName" style="float: left;" autocomplete="off">
		  		<div class="tea_dialog_loader loader" style="display: none"></div>	
		  		<br><span class="help-inline"></span>	  				  					  		
			</div>				
	  	</div>
	  	<br>	
	  	<div class="control-group  custom-row">
			<label class="control-label" for="folderDescription">Folder Description</label>
			<div class="controls">
		  		<textarea id="folderDescription" rows="4" cols="60"> </textarea>
			</div>							
		</div>			
	</form>		
</div>

<div id="tea-save-dialog" class="tea-dialog" title="Save Analysis">	
	<form class="form-horizontal custom-col">		
	 	<div class="control-group  custom-row">
			<label class="control-label" for="analysisName">Analysis Name <span style="color:red">&#10033;</span></label>
			<div class="controls">
		  		<input type="text" id="analysisName" name="analysisName" style="float: left;" autocomplete="off">
		  		<div class="tea_dialog_loader loader" style="display: none"></div>
		  		<br><span class="help-inline"></span>	  				  					  		
			</div>				
	  	</div>
	  	<br>	
	  	<div class="control-group  custom-row">
			<label class="control-label" for="analysisDescription">Analysis Description</label>
			<div class="controls">
		  		<textarea id="analysisDescription" rows="4" cols="60"> </textarea>
			</div>							
		</div>			
	</form>		
</div>

<div id="tea-remove-dialog"  class="tea-dialog" title="Remove ">
	<div>
		<p style="margin:0"> Are you sure you want to delete the following file ? </p> 
		<p style="margin:0" id="remove-file-name">  </p>
	</div>
	<div class="tea_dialog_loader loader" style="display: none"></div>
</div>

<div id="tea-rename-dialog"  class="tea-dialog" title="Rename ">
	<form class="form-horizontal custom-col">
		<div class="control-group  custom-row">
			<label class="control-label" for="fileNewName">New Name</label>
			<div class="controls">
		  		<input type="text" id="fileNewName" style="float: left;" autocomplete="off">
		  		<div class="tea_dialog_loader loader" style="display: none"></div>	
		  		<br><span class="help-inline"></span>	
			</div>							
		</div>	
	</form>
</div>
	
<ul class="tea-context-menu nav nav-list">
	<li> <a class="btn create-folder" href="#"><i class="fa fa-fw fa-floppy-o" 		aria-hidden="true">	</i> Create New Folder</a> </li>
	<li> <a class="btn refresh" 	href="#"><i class=" fa fa-fw fa-refresh" 		aria-hidden="true">	</i> Refresh</a> </li>
	<li> <a class="btn rename-file" href="#"><i class=" fa fa-fw fa-eye" 			aria-hidden="true">	</i> Rename	</a> </li>
	<li> <a class="btn info" 		href="#"><i class=" fa fa-fw fa-info-circle"	aria-hidden="true">	</i> Info	</a> </li>	
	<li> <a class="btn remove-file" href="#"><i class=" fa fa-fw fa-trash" 			aria-hidden="true">	</i> Remove	</a> </li>	
<!-- 		<li> <a class="btn load" href="#"><i class="fa fa-fw fa-arrow-circle-down" aria-hidden="true"></i> Export as CSV</a> </li> -->
<!-- 		<li> <a class="btn load" href="#"><i class="fa fa-fw fa-file-pdf-o" aria-hidden="true"></i> Export as PDF</a> </li> -->
<!-- 		<li> <a class="btn load" href="#"><i class="fa fa-fw fa-file-zip-o" aria-hidden="true"></i> Export as ZIP</a> </li> -->

</ul>

<div id="tea-info-dialog"  class="tea-dialog" title="Info"></div>