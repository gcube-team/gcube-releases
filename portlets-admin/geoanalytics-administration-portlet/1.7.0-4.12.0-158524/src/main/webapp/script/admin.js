(function() {
'use strict';

var pageState = {};

function init(contextPath, renderURL, resourceURL) {
	pageState.contextPath = contextPath;
	pageState.renderURL = renderURL;
	pageState.resourceURL = resourceURL;
	
	tabsEvents(resourceURL);
}

function tabsEvents(resourceURL) {
	$('.importTab').one('click', function() {
		$('#importOptions .dropDownSelection').append('<option id="tsvImport">TSV Import</option>');
		$('#importOptions .dropDownSelection').append('<option id="wfsImport">WFS Import</option>');
		$('#importOptions .dropDownSelection').append('<option id="shapefileImport">Shapefile Import</option>');
		$('#importOptions .dropDownSelection').append('<option id="geotiffImport">GeoTIFF Import</option>');
		
		// click listener for import dropdown
		var url = window.config.createResourceURL('users/listUsers');
		$('#importOptions .dropDownSelection').change(function(){
			if ($(this).children(":selected").attr('id') == 'wfsImport') {
				$('#tab3').WFSImport({
					headerDiv: "#importOptions",
					content: "#contentOfImporter"
				})
				.WFSImport("cleanMe")
				.WFSImport("createAsDiv", pageState);
			} else if ($(this).children(":selected").attr('id') == 'tsvImport') {			
				var geocodeSystems = window.config.createResourceURL('shapes/listTemplateGeocodeSystems');
				var stylesPath = window.config.createResourceURL('styles/getAllStyles');
				var importTsvPath = window.config.createResourceURL('import/tsv');

				$('#tab3').tsvImporter({
					mode				: 	"div" 	,					// or "button" 
					geocodeSystemsURL	: 	geocodeSystems,
					importTsvURL		:	importTsvPath,	
					stylesURL			:   stylesPath,
					headerDiv			: 	"#importOptions",
					content				: 	"#contentOfImporter"
				}).tsvImporter("createImporter");
			}else if ($(this).children(":selected").attr('id') == 'shapefileImport') {
				var importShapefileURLPath = window.config.createResourceURL('import/shapeFile');
				var stylesPath = window.config.createResourceURL("styles/getAllStyles");

				$('#tab3').shapefileImporter({
					mode				: 	"div" 	,					// or "button" 
					importShapefileURL	:	importShapefileURLPath,		
					stylesURL			:   stylesPath,
					headerDiv			: 	"#importOptions",
					content				: 	"#contentOfImporter"
				})
				.shapefileImporter("destroy")
				.shapefileImporter("createImporter");
			} else if ($(this).children(":selected").attr('id') == 'geotiffImport'){
				var stylesPath = window.config.createResourceURL("styles/getAllStyles");
				var importGeotiffPath = window.config.createResourceURL("import/geotiff");

				$('#tab3').geotiffImporter({
					mode				: 	"div" 	,					// or "button" 
					importGeotiffURL	:	importGeotiffPath,	
					stylesURL			:   stylesPath,
					headerDiv			: 	"#importOptions",
					content				: 	"#contentOfImporter"
				}).geotiffImporter("createImporter");
			}
			
		});
		$('#importOptions .dropDownSelection #tsvImport').prop('selected', true);	
		$('#importOptions .dropDownSelection').change();
	});
}

	window.Admin = {};
	window.Admin.init = init;
}());