(function($) {

$.widget("ui.tree", {
	init: function() {

		var self = this;
		this.identifier = (new Date()).getTime()+Math.random();

		this.element.sortable({
			items: this.options.sortOn,
			scope: this.identifier,
			distance: this.options.distance,
			placeholder: "ui-tree-placeholder",
			helper: this.options.helper,
			handle: this.options.handle,
			scroll: this.options.scroll,
			appendTo: this.options.appendTo,
			start: function(e, ui) {
				var inst = $(this).data("sortable");
				inst.placeholder.hide();
				inst.helperProportions.height = inst.currentItem.find(self.options.dropOn).length ? inst.currentItem.find(self.options.dropOn).outerHeight() : inst.currentItem.outerHeight();
				inst._preserveHelperProportions = true;
				inst.refreshPositions(true);

				self.originalParent = ui.item.parent();

				(self.options.start && self.options.start.apply(this, [e, ui]));
			},
			stop: function(e, ui) {
				var sortable = $(this).data("sortable");
				$(sortable.options.items, sortable.element).removeClass(self.options.sortIndicatorDown).removeClass(self.options.sortIndicatorUp);

				if ( self.originalParent.is(':empty') )
					self.originalParent.remove();

				(self.options.stop && self.options.stop.apply(this, [e, ui]));
			},
			sortIndicator: function(e, item, append, hardRefresh) {

				append ? append[0].appendChild(this.placeholder[0]) : item.item[0].parentNode.insertBefore(this.placeholder[0], (this.direction == 'down' ? item.item[0] : item.item[0].nextSibling));

				$(this.options.items, this.element).removeClass(self.options.sortIndicatorDown).removeClass(self.options.sortIndicatorUp);
				item.item.addClass(this.direction == "down" ? self.options.sortIndicatorDown : self.options.sortIndicatorUp);

			}
		});

		//Make certain nodes droppable
		$(this.options.dropOn, this.element).droppable({
			accept: this.options.sortOn,
			hoverClass: this.options.dropHoverClass,
			tolerance: "pointer",
			scope: this.identifier,
			over: function(e, ui) {
				$(self.options.sortOn, self.element).removeClass(self.options.sortIndicatorDown).removeClass(self.options.sortIndicatorUp);
				self.overDroppable = true;
				self.trigger('over', e, ui);
			},
			out: function(e, ui) {
				self.overDroppable = false;
				(self.options.out && self.options.out.apply(this, [e, ui]));
			},
			drop: function(e, ui) {

				var ul = $(this).parent().find("ul");
				if(!ul.length) var ul = $("<ul></ul>").appendTo($(this).parent());

				ui.draggable.appendTo( $(this).parent().find("> ul") );

				self.element.data("sortable")._noFinalSort = true;

				(self.options.drop && self.options.drop.apply(this, [e, ui]));
			}
		});

	}
});

$.extend($.ui.tree, {
	defaults: {
		sortOn: "*",
		dropOn: "div",
		dropHoverClass: "ui-tree-hover",
		sortIndicatorDown: "hover-down",
		sortIndicatorUp: "hover-up"
	}
});

})(jQuery);