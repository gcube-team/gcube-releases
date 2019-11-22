$(document).ready(function() {	
	$( "#searchCatalogueButton" ).click(function() {
		queryCatalogue();
	});
	
	$('#inputQueryCatalogue').on("keypress", function(e) {
        if (e.keyCode == 13) {
        	queryCatalogue();
        }
	});
});

function queryCatalogue() {
	var query =  $.trim( $('#inputQueryCatalogue').val() );
	if (query == "") {
		$('#inputQueryCatalogue').css("border","1px solid red");
	} else {
		$('#inputQueryCatalogue').css("border","1px solid #ccc");
		var encodedQuery = btoa("q="+$('#inputQueryCatalogue').val());
		location.href = $('#catalogueURL').val() + "?path=/dataset&query=" + encodedQuery;
	}	
}