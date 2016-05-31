jQuery.fn.alignTo = function(referrer, options) {
	var defaults = {
		positionX: 'left',
		positionY: 'bottom',
		offsetX: 0,
		offsetY: 0,
		directionH: 'right',
		directionV: 'down',
		detectH: true,
		detectV: true,
		linkToFront: false
	};

	options = jQuery.extend(defaults, options);

	var el = jQuery(this);

	if (referrer && !referrer.jquery) {
		referrer = jQuery(referrer);
	}

	var referrerOffset = referrer.offset();

	var dimensions = {
		elementWidth: el.width(),
		elementHeight: el.height(),
		referrerX: referrerOffset.left,
		referrerY: referrerOffset.top,
		referrerWidth: referrer.outerWidth(),
		referrerHeight: referrer.outerHeight()
	};

	var x = 0;
	var y = 0;

	var positionX = options.positionX;
	var positionY = options.positionY;

	el.attachPositionHelper(dimensions);

	if (positionX != 'left') {
		x = dimensions.referrerWidth;

		if (positionX == 'center') {
			x /= 2;
		}
	}

	if (positionY != 'top') {
		y = dimensions.referrerHeight;

		if (positionX == 'center') {
			y /= 2;
		}
	}

	x += options.offsetX;
	y += options.offsetY;

	var elementOffset = el.parent().offset();

	var positionOptions = {
		bottom: 'auto',
		left: 'auto',
		right: 'auto',
		top: 'auto'
	};

	var fitsHorizontally = true;
	var fitsVertically = true;

	if (options.detectH || options.detectV) {
		var win = jQuery(window);

		var windowHeight = win.height();
		var windowWidth = win.width();

		var windowScrollLeft = win.scrollLeft();
		var windowScrollTop = win.scrollTop();

		var leftValue = (elementOffset.left + dimensions.elementWidth);
		var topValue = (elementOffset.top + dimensions.elementHeight);

		if (leftValue > (windowWidth + windowScrollLeft) || (elementOffset.left - windowScrollLeft) < 0 &&
			dimensions.elementWidth <= dimensions.referrerX) {
			fitsHorizontally = false;
		}

		if ((topValue > (windowHeight + windowScrollTop) || (elementOffset.top - windowScrollTop) < 0) &&
			dimensions.elementHeight <= dimensions.referrerY) {

			fitsVertically = false;
		}
	}

	if (options.directionH == 'left' ||
	(options.directionH != 'left' && !fitsHorizontally)) {

		positionOptions.right = x;
	}
	else {
		positionOptions.left = x;
	}

	if (options.directionV == 'up' ||
	(options.directionV != 'up' && !fitsVertically)) {

		positionOptions.bottom = y;
	}
	else {
		positionOptions.top = y;
	}

	el.css(positionOptions);

	return this;
}

jQuery.fn.attachPositionHelper = function(dimensions) {
	if (!this.data('position-helper')) {
		var el = jQuery(this);
		var helper = jQuery('<div class="lfr-position-helper"></div>');

		helper.css(
			{
				height: dimensions.referrerHeight,
				left: dimensions.referrerX,
				top: dimensions.referrerY,
				width: dimensions.referrerWidth
			}
		);

		helper.append(el);

		jQuery(document.body).append(helper);

		this.data('position-helper', helper);
	}

	return this;
};

jQuery.fn.detachPositionHelper = function(appendTo) {
	var helper = this.data('position-helper');

	this.hide().appendTo(appendTo || document.body);

	if (helper) {
		this.data('position-helper', null);

		helper.remove();
	}
}
