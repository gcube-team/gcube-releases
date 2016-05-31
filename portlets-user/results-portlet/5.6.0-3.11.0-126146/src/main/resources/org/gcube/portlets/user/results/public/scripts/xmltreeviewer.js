(function( $ ){

	var methods = {
		init: function(options) {			
			var settings = $.extend({}, options);
			var xmlDoc = $.parseXML(settings.xml);
			var $xml = $(xmlDoc);
			var rootNode = $xml.find("*").eq(0);
			
			this.addClass('xmltreeviewer');
			var counter = 0;
			
			var processNode = function(node, level) {
				var nodeId = 'n_' + counter;
				counter++;
				var ch = node.children();
				
				// Padding calculation
				var padLeft = (level + 1) * 16;
				if (ch.length > 0) padLeft -= 16;
				
				// Padding
				var s = '<div class="xmlpart ib" style="width:' + padLeft + 'px"></div>';
				
				// Expand/collapse icon
				if (ch.length > 0)
					s += '<div id="' + nodeId + '" class="xmlpart ib ecHandle exp"></div>';
				
				// Node
				var textNodes = node.contents().filter(function() { return this.nodeType == 3 && $.trim($(this).text()).length > 0; });
				var extraText = [];
				if (textNodes.length > 0) extraText.push('<span class="textval"><strong>Value</strong> = ' + $(textNodes[0]).text() + '</span>');
				for (var i=0; i<node[0].attributes.length; i++)
                    extraText.push('<strong>' + node[0].attributes.item(i).nodeName + '</strong> = ' + node[0].attributes.item(i).value);
				extraText = extraText.length > 0 ? (' [' + extraText.join(', ') + ']') : '';

				s += '<div class="ib xmltextpart">' + node[0].nodeName + extraText + '</div><div class="xmltvc"></div>';
				s += '<div id="' + nodeId + '_ch" class="chCont">';
				
				ch.each(function() {
					s += processNode($(this), level + 1);
				});
				s += '</div>';
				
				return s;
			}
			
			this.html(processNode(rootNode, 0));
			
			var self = this;
			$('.ecHandle', this).click(function() {
				var $this = $(this);
				var id = this.id;
				var isCollapsed = $this.hasClass('exp');
				var childDiv = $('#' + id + '_ch', self);
				if (isCollapsed) {
					childDiv.slideDown();
					$this.removeClass('exp').addClass('col');
				}
				else {
					childDiv.slideUp();
					$this.removeClass('col').addClass('exp');
				}
			});
		}
	};

	$.fn.xmltreeviewer = function(method) {
		if (methods[method]) {
			return methods[ method ].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if ( typeof method === 'object' || ! method ) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('invalid method call');
		}
	};

})( jQuery );

function showTree(divid, text){
	$('#' + divid).xmltreeviewer({ xml: text });
}
