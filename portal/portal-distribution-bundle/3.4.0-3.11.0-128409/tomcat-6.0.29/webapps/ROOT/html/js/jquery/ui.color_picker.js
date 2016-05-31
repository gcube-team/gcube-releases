;(function($) {

	$.fn.extend({
		colorpicker: function(options) {
			var args = Array.prototype.slice.call(arguments, 1);
			return this.each(function() {
				if (typeof options == "string") {
					var inst = $.data(this, "colorpicker");
					if(inst) inst[options].apply(inst, args);
				} else if(!$.data(this, "colorpicker"))
					new $.ui.colorpicker(this, options);
			});
		}
	});
	
	$.ui.colorpicker = function(element, options) {

		//Initialize needed constants
		var self = this;
		this.element = $(element);
		var o = this.options = $.extend({}, options);
		
		$.data(element, "colorpicker", this);
		this.element.addClass("ui-colorpicker")
			.append('<div class="ui-colorpicker-color">')
				.find('div.ui-colorpicker-color').append('<div class="ui-colorpicker-color-handle">').end()
			.append('<div class="ui-colorpicker-hue">')
				.find('div.ui-colorpicker-hue').append('<div class="ui-colorpicker-hue-handle">').end()
			.append('<div class="ui-colorpicker-current">')
				.find('div.ui-colorpicker-current').append('<div class="ui-colorpicker-last">').end()
			.append('<div class="ui-colorpicker-values">')
				.find('div.ui-colorpicker-values').append('<table cellpadding="0" cellspacing="2">')
					.find('table')
					.append('<tr><td>R:</td><td><input type="text" size="2" class="ui-colorpicker-rgbR" value="255" /></td></tr>')
					.append('<tr><td>G:</td><td><input type="text" size="2" class="ui-colorpicker-rgbG" value="255" /></td></tr>')
					.append('<tr><td>B:</td><td><input type="text" size="2" class="ui-colorpicker-rgbB" value="255" /></td></tr>')
					.append('<tr><td>#</td><td><input type="text" size="5" class="ui-colorpicker-hex" value="FFFFFF" /></td></tr>')
					.end()
				.end()
		;
		
		$(element).bind("setData.colorpicker", function(event, key, value){
			self.options[key] = value;
		}).bind("getData.colorpicker", function(event, key){
			return self.options[key];
		});

		this.baseColor = {r:255,g:0,b:0};
		this.currentColor = {r:255,g:255,b:255};
		this.lastValues = [0,0];
		this.colorfieldCurrent = $('div.ui-colorpicker-current', this.element);
		this.colorfieldLast = $('div.ui-colorpicker-last', this.element);
		
		$('div.ui-colorpicker-color', this.element).slider({
			handle: "div",
			axis: "both",
			distance: 0,
			slide : function(e, ui) {
				self.lastValues = [parseInt(ui.value.x * 255/100),parseInt(ui.value.y * 255/100)];
				self.setGradientColor();
				self.propagate("picking", e);
			},
			change : function(e) {
				self.colorfieldLast.css("backgroundColor", 'rgb(' + self.currentColor.r + ',' + self.currentColor.g + ',' + self.currentColor.b + ')');
				self.propagate("change", e);
			},
			stop: function(e) { self.propagate("pick", e); },
			start: function(e) { self.propagate("start", e); }
		});

		$('div.ui-colorpicker-hue', this.element).slider({
			handle: "div",
			distance: 0,
			slide : function(e, ui) {
				self.setVertColor(parseInt(ui.value * 255 / 100));
				self.setGradientColor();
				self.propagate("picking", e);
			},
			change : function(e) {
				self.colorfieldLast.css("backgroundColor", 'rgb(' + self.currentColor.r + ',' + self.currentColor.g + ',' + self.currentColor.b + ')');
				self.propagate("change", e);
			},
			stop: function(e) { self.propagate("pick", e); },
			start: function(e) { self.propagate("start", e); }
		});		
		
		
	};
	
	$.extend($.ui.colorpicker.prototype, {
		plugins: {},
		ui: function(e) {
			return {
				instance: this,
				options: this.options,
				element: this.element,
				rgb: this.currentColor,
				hex: (this.toHex(this.currentColor.r) + this.toHex(this.currentColor.g) + this.toHex(this.currentColor.b)).toUpperCase()
			};
		},
		propagate: function(n,e) {
			$.ui.plugin.call(this, n, [e, this.ui()]);
			return this.element.triggerHandler(n == "pick" ? n : "pick"+n, [e, this.ui()], this.options[n]);
		},
		destroy: function() {
			if(!$.data(this.element[0], 'colorpicker')) return;
			this.element
				.removeClass("ui-colorpicker ui-colorpicker-disabled")
				.removeData("colorpicker")
				.unbind(".colorpicker");
		},
		enable: function() {
			this.element.removeClass("ui-colorpicker-disabled");
			this.options.disabled = false;
		},
		disable: function() {
			this.element.addClass("ui-colorpicker-disabled");
			this.options.disabled = true;
		},
		setVertColor: function(indic){
			var n=256/6, j=256/n, C=indic, c=C%n;
			this.baseColor = {
				r : parseInt(C<n?255:C<n*2?255-c*j:C<n*4?0:C<n*5?c*j:255),
				g : parseInt(C<n*2?0:C<n*3?c*j:C<n*5?255:255-c*j),
				b : parseInt(C<n?c*j:C<n*3?255:C<n*4?255-c*j:0)
			};
			
			$("div.ui-colorpicker-color", this.element).css('backgroundColor', 'rgb(' + this.baseColor.r + ',' + this.baseColor.g + ',' + this.baseColor.b + ')');
		},
		setGradientColor: function(){
			var r = Math.round((1-(1-(this.baseColor.r/255))*(this.lastValues[0]/255))*(255-this.lastValues[1]));
			var g = Math.round((1-(1-(this.baseColor.g/255))*(this.lastValues[0]/255))*(255-this.lastValues[1]));
			var b = Math.round((1-(1-(this.baseColor.b/255))*(this.lastValues[0]/255))*(255-this.lastValues[1]));
			this.colorfieldCurrent.css('backgroundColor','rgb(' + r + ',' + g + ',' + b + ')');
			$('input.ui-colorpicker-rgbR', this.element)[0].value = r;
			$('input.ui-colorpicker-rgbG', this.element)[0].value = g;
			$('input.ui-colorpicker-rgbB', this.element)[0].value = b;
			$('input.ui-colorpicker-hex', this.element)[0].value = (this.toHex(r) + this.toHex(g) + this.toHex(b)).toUpperCase();
			this.currentColor = {r:r,g:g,b:b};
		},
		toHex: function(color){
			color=parseInt(color).toString(16);
			return color.length<2?"0"+color:color;
		}
	});
	
})(jQuery);