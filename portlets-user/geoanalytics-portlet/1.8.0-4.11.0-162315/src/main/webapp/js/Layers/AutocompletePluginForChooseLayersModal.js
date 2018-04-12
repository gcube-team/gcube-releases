function initPluginForLayersOnChooseLayersModal(layerNamesLayersModal) {
	var termTemplate = "<span class='ui-autocomplete-term-layers-modal'>%s</span>";
	layerNamesLayersModal.sort();
	
	$('#searchbar-layers').autocomplete({
		source : layerNamesLayersModal,
		autoFocus : true,
		minLength: 0,
		
		create : function() {
			$(this).data('ui-autocomplete')._renderItem = function(ul, item) {
				return $("<li>").addClass("autocomplete-item").attr("data-value", item.value)
				.append(item.label).appendTo(ul);
			};
		},

		response : function(e, ui) {
			autocompleteHintInitLayersModal(ui);			
		},
		
		select: function (e, ui) {
			$('#autocomplete-hint-layers-modal').val('');
			$('#treeviewLayers').jstree(true).search(ui.item.value);
		},
		
		open: function(){
			var acData = $(this).data('ui-autocomplete');
			acData.menu.element.find('li').each(function() {
				var me = $(this);
				var keywords = acData.term.split(' ').join('|');
				me.html(me.text().replace(new RegExp("(" + keywords + ")", "gi"), '<b>$1</b>'));
			});
			$('ul.ui-autocomplete').addClass('opened');
		},

		close : function() {
			$('ul.ui-autocomplete').removeClass('opened').css('display', 'block');
		}
	});
	
	// clear hint when search input gets cleared
	
	$('#searchbar-layers').on('input', function() {
		if (!$('#searchbar-layers').val().length) {
			$('#autocomplete-hint-layers-modal').val('');
		}
	});

	// attach jstree search plugin to search bar

	var to = false;

	$('#searchbar-layers').keyup(function(){
	    $('#treeviewLayers').jstree(true).show_all();
	    $('#treeviewLayers').jstree('search', $(this).val());
	});
	
	$('#treeviewLayers').on('search.jstree', function (nodes, str, res) {
	    if (str.nodes.length===0) {
	    	$('#treeviewLayers').jstree(true).hide_all();
	    }
	})
}

function autocompleteHintInitLayersModal(ui){
	var currentInput = $('#searchbar-layers').val();			
	var hint = true;

	if(ui.content.length && currentInput.length){
		var firstSuggestion = ui.content[0].label;

		for (i = 0; i < currentInput.length; i++) {
			if (currentInput[i].toLowerCase() !== firstSuggestion[i].toLowerCase()) {
				hint = false;
				break;
			}
		}
	} else {
		hint = false;
		$('#searchbar-layers').autocomplete('close');
	}

	if (hint) {
		currentInput = firstSuggestion.substr(0, currentInput.length);
		$('#autocomplete-hint-layers-modal').val(firstSuggestion);
		$('#searchbar-layers').val(currentInput);
	} else {
		$('#autocomplete-hint-layers-modal').val('');
	}
}