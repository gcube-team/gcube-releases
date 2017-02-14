function tagEvents(theList, teamsList){
	//Roles
	$('#roleList').textext({
        plugins : 'autocomplete arrow tags',
        html : {
        	arrow:'<div class="text-arrow"><span class="caretContainer"><i class="caret"></i></span></div>'
        },
        ext : {
        	tags : {
        		addTags : function(tags) {
        			if(!alreadyExists(tags)) {
        				$.fn.textext.TextExtTags.prototype.addTags.apply(this, arguments);
        			}
        		}
        	}
        }
    })
    .bind('getSuggestions', function(e, data)
    {
    	var list = theList,
            textext = $(e.target).textext()[0],
            query = (data ? data.query : '') || ''
            ;

        $(this).trigger(
            'setSuggestions',
            {
            	result : textext.itemManager().filter(list, query)
            }
        );
    });
	
	$('#roleListInAssignRolesModal').textext({
        plugins : 'autocomplete arrow tags',
        html : {
        	arrow:'<div class="text-arrow"><span class="caretContainer"><i class="caret"></i></span></div>'
        },
        ext : {
        	tags : {
        		addTags : function(tags) {
        			if(!alreadyExistsInAssignModal(tags)) {
        				$.fn.textext.TextExtTags.prototype.addTags.apply(this, arguments);
        			}
        		}
        	}
        }
    })
    .bind('getSuggestions', function(e, data)
    {
    	var list = theList,
            textext = $(e.target).textext()[0],
            query = (data ? data.query : '') || ''
            ;

        $(this).trigger(
            'setSuggestions',
            {
            	result : textext.itemManager().filter(list, query)
            }
        );
    });
	
	//Teams
	$('#teamsList').textext({
        plugins : 'autocomplete arrow tags',
        html : {
        	arrow:'<div class="text-arrow"><span class="caretContainer"><i class="caret"></i></span></div>'
        },
        ext : {
        	tags : {
        		addTags : function(tags) {
        			if(!alreadyExists(tags)) {
        				$.fn.textext.TextExtTags.prototype.addTags.apply(this, arguments);
        			}
        		}
        	}
        }
    })
    .bind('getSuggestions', function(e, data)
    {
    	var list = teamsList,
            textext = $(e.target).textext()[0],
            query = (data ? data.query : '') || ''
            ;

        $(this).trigger(
            'setSuggestions',
            {
            	result : textext.itemManager().filter(list, query)
            }
        );
    });
	
	$('#teamsListInAssignUsersToGroupsModal').textext({
        plugins : 'autocomplete arrow tags',
        html : {
        	arrow:'<div class="text-arrow"><span class="caretContainer"><i class="caret"></i></span></div>'
        },
        ext : {
        	tags : {
        		addTags : function(tags) {
        			if(!alreadyExistsInAssignUsersToGroupsModal(tags)) {
        				$.fn.textext.TextExtTags.prototype.addTags.apply(this, arguments);
        			}
        		}
        	}
        }
    })
    .bind('getSuggestions', function(e, data)
    {
    	var list = teamsList,
            textext = $(e.target).textext()[0],
            query = (data ? data.query : '') || ''
            ;

        $(this).trigger(
            'setSuggestions',
            {
            	result : textext.itemManager().filter(list, query)
            }
        );
    });
	
	//Emails
	$('#tagsForEmails').textext({
        plugins : 'tags'
    });
	$('#tagsForEmails').closest('.row').find('.text-core').addClass('span11');
	$('#usersManagementPortletContainer div.text-tags').off().bind('DOMNodeInserted', function(event) {
		var element = event.target;
	    var tagName = $(element).prop("tagName");
	    if(tagName !== 'DIV')return;
		$(this).find('.text-button').addClass('span12');
		$(this).find('.text-label').addClass('span11');
		$('#tagsForEmails').parent().find('a.text-remove').html('<i class="fa fa-times"></i>').removeClass('text-remove').addClass('tag-remove span1');
	});
	$('span#textAboveTagsInput div.row div.text-core:first-of-type').addClass('span9');
}

function alreadyExists(tags){
	var roleTexts = $('#roleList').parent().find('.text-button.span12 .text-label');
	var teamTexts = $('#teamsList').parent().find('.text-button.span12 .text-label');
	var elements = $.merge(roleTexts, teamTexts);
	
	for(var i = 0; i < elements.length; i++){
		if(tags === null) return false;
		for(var j = 0; j < tags.length; j++){
			if($(elements[i]).text().trim() === tags[j].trim()){
				return true;
			}
		}
	}
	return false;
}

function alreadyExistsInAssignModal(tags){
	var roleTextsInAssignModal = $('#roleListInAssignRolesModal').parent().find('.text-button.span12 .text-label');
	var elements = roleTextsInAssignModal;
	
	for(var i = 0; i < elements.length; i++){
		if(tags === null) return false;
		for(var j = 0; j < tags.length; j++){
			if($(elements[i]).text().trim() === tags[j].trim()){
				return true;
			}
		}
	}
	return false;
}

function alreadyExistsInAssignUsersToGroupsModal(tags){
	var teamTexts = $('#teamsListInAssignUsersToGroupsModal').parent().find('.text-button.span12 .text-label');
	var elements = teamTexts;
	
	for(var i = 0; i < elements.length; i++){
		if(tags === null) return false;
		for(var j = 0; j < tags.length; j++){
			if($(elements[i]).text().trim() === tags[j].trim()){
				return true;
			}
		}
	}
	return false;
}

function teamEditedOrDeleted(teamsList){
	$('#teamsList').closest('.text-core.span9').remove();
	$('#textAboveTagsInput .row:nth-of-type(5)').append(
			$('<textarea></textarea>', {
				id : 'teamsList'
			})
	);
	$('#teamsList').textext({
        plugins : 'autocomplete arrow tags',
        html : {
        	arrow:'<div class="text-arrow"><span class="caretContainer"><i class="caret"></i></span></div>'
        },
        ext : {
        	tags : {
        		addTags : function(tags) {
        			if(!alreadyExists(tags)) {
        				$.fn.textext.TextExtTags.prototype.addTags.apply(this, arguments);
        			}
        		}
        	}
        }
    })
    .bind('getSuggestions', function(e, data)
    {
    	var list = teamsList,
            textext = $(e.target).textext()[0],
            query = (data ? data.query : '') || ''
            ;

        $(this).trigger(
            'setSuggestions',
            {
            	result : textext.itemManager().filter(list, query)
            }
        );
    });
	
	$('span#textAboveTagsInput div.row:nth-of-type(5) div.text-core:first').addClass('span9');
	
	$('#teamsList').parent().find('div.text-tags').off().bind(
			'DOMNodeInserted',
			function(event) {
				var element = event.target;
				var tagName = $(element).prop("tagName");
				if (tagName !== 'DIV')
					return;
				// $('#roleList').parent().find('div.text-tag').addClass('span5');
				$('#teamsList').parent().find('div.text-button').addClass(
						'span12');
				$('#teamsList').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
						.removeClass('text-remove').addClass('tag-remove');
				$('textarea#teamsList').parent().find('a.tag-remove').off().on(
						'click', function() {
							$(this).closest('.text-tag').remove();
						});
				var matched = false;
				var tagsTextt = $('#teamsList').parent().find(
						'div.text-tag.span5');
				for (var i = 0; i < tagsTextt.length; i++) {
					for (var j = i + 1; j < tagsTextt.length; j++) {
						if ($(tagsTextt[i]).text() === $(tagsTextt[j]).text()) {
							tagsTextt[j].remove();
						}
					}
				}
			});
	
	
	$('#teamsListInAssignUsersToGroupsModal').closest('.text-core.span9').remove();
	$('#textAboveTagsInputInAssignUsersToGroupsModal .row:last').append(
			$('<textarea></textarea>', {
				id : 'teamsListInAssignUsersToGroupsModal'
			})
	);
	
	$('#teamsListInAssignUsersToGroupsModal').textext({
        plugins : 'autocomplete arrow tags',
        html : {
        	arrow:'<div class="text-arrow"><span class="caretContainer"><i class="caret"></i></span></div>'
        },
        ext : {
        	tags : {
        		addTags : function(tags) {
        			if(!alreadyExistsInAssignUsersToGroupsModal(tags)) {
        				$.fn.textext.TextExtTags.prototype.addTags.apply(this, arguments);
        			}
        		}
        	}
        }
    })
    .bind('getSuggestions', function(e, data)
    {
    	var list = teamsList,
            textext = $(e.target).textext()[0],
            query = (data ? data.query : '') || ''
            ;

        $(this).trigger(
            'setSuggestions',
            {
            	result : textext.itemManager().filter(list, query)
            }
        );
    });
	
	$('span#textAboveTagsInputInAssignUsersToGroupsModal div.row:last div.text-core:first').addClass('span9');
	
	$('#teamsListInAssignUsersToGroupsModal').parent().find('div.text-tags').off().bind(
			'DOMNodeInserted',
			function(event) {
				var element = event.target;
				var tagName = $(element).prop("tagName");
				if (tagName !== 'DIV')
					return;
				// $('#roleList').parent().find('div.text-tag').addClass('span5');
				$('#teamsListInAssignUsersToGroupsModal').parent().find('div.text-button').addClass(
						'span12');
				$('#teamsListInAssignUsersToGroupsModal').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
						.removeClass('text-remove').addClass('tag-remove');
				$('textarea#teamsListInAssignUsersToGroupsModal').parent().find('a.tag-remove').off().on(
						'click', function() {
							$(this).closest('.text-tag').remove();
						});
				var matched = false;
				var tagsTextt = $('#teamsListInAssignUsersToGroupsModal').parent().find(
						'div.text-tag.span5');
				for (var i = 0; i < tagsTextt.length; i++) {
					for (var j = i + 1; j < tagsTextt.length; j++) {
						if ($(tagsTextt[i]).text() === $(tagsTextt[j]).text()) {
							tagsTextt[j].remove();
						}
					}
				}
			});
}