function responsiveMode(){
	$('#settingsResponsive, #adminSettingsButtonContainer').off('click').on('click', function(){
		$('#addPluginModal').modal('show');
	});
	$('#settingsResponsive, #adminSettingsButtonContainer').one('click', function(){
		$('#pluginDescription').val('');
		//annoying blank strings appearing in the textarea
	});
//	functions
	$('#functionResponsiveSection').off('click').on('click', function(){
		$('.closeResponsivePane').addClass('hidden');
		$(this).closest('.toolbarMenuNotVisibleInDesktopsBtns').find('.closeResponsivePane').removeClass('hidden');
		$('.respTabClicked').removeClass('respTabClicked');
		$(this).addClass('respTabClicked');
		$('#functionsResponsive')
		$('.triangleAdded').removeClass('triangleAdded');
		$('#functionsResponsive').addClass('triangleAdded');
		$('#toolbarNotVisibleInDesktops').removeClass('hiddenPane');
		$('.responsivePanes').addClass('hidden');
		$('#selectedFunctionsResponsivePane').removeClass('hidden');
	});
	$('#functionsButtonCaret').off('click').on('click', function(){
		$('.closeResponsivePane').addClass('hidden');
		$(this).closest('.toolbarMenuNotVisibleInDesktopsBtns').find('.closeResponsivePane').removeClass('hidden');
		$('.respTabClicked').removeClass('respTabClicked');
		$(this).addClass('respTabClicked');
		$('.triangleAdded').removeClass('triangleAdded');
		$('#functionsResponsive').addClass('triangleAdded');
		$('#toolbarNotVisibleInDesktops').removeClass('hiddenPane');
		$('.responsivePanes').addClass('hidden');
		$('#functionsSelectionResponsivePane').removeClass('hidden');
		if($(this).hasClass('redrawSlickTheFirstTime')) {
			$(this).removeClass('redrawSlickTheFirstTime');
			functionsCarouselSlickRedraw();
			$('.slick-dots').css('display', 'none');
			functionsSliderDivWidth = $('.slick-slide.slick-current.slick-active').width();
		}
	});
//	layers
	$('#layersResponsive').off('click').on('click', function(){
		if(closeButtonClicked){
			closeButtonClicked = false;
			return;
		}
		$('.closeResponsivePane').addClass('hidden');
		$(this).find('.closeResponsivePane').removeClass('hidden');
		$('.triangleAdded').removeClass('triangleAdded');
		$('#layersResponsive').addClass('triangleAdded');
		$('#toolbarNotVisibleInDesktops').removeClass('hiddenPane');
		$('.responsivePanes').addClass('hidden');
		$('#layersSelectorResponsivePane').removeClass('hidden');
	});
//	$('#layersButtonCaret').off('click').on('click', function(){
//		$('.closeResponsivePane').addClass('hidden');
//		$(this).closest('.toolbarMenuNotVisibleInDesktopsBtns').find('.closeResponsivePane').removeClass('hidden');
//		$('.respTabClicked').removeClass('respTabClicked');
//		$(this).addClass('respTabClicked');
//		$('.triangleAdded').removeClass('triangleAdded');
//		$('#layersResponsive').addClass('triangleAdded');
//		$('#toolbarNotVisibleInDesktops').removeClass('hiddenPane');
//		$('.responsivePanes').addClass('hidden');
//		$('#layersSelectorResponsivePane').removeClass('hidden');
//	});
	$('#layersButton').off('click').on('click', function(){
		return;
		$('.closeResponsivePane').addClass('hidden');
		$(this).closest('.toolbarMenuNotVisibleInDesktopsBtns').find('.closeResponsivePane').removeClass('hidden');
		$('.respTabClicked').removeClass('respTabClicked');
		$(this).addClass('respTabClicked');
		$('.triangleAdded').removeClass('triangleAdded');
		$('#layersResponsive').addClass('triangleAdded');
		$('#toolbarNotVisibleInDesktops').removeClass('hiddenPane');
		$('.responsivePanes').addClass('hidden');
		$('#selectedLayersResponsivePane').removeClass('hidden');
	});
	$('.closeResponsivePane').off('click').on('click', function(){
		$(this).addClass('hidden');
		$('#functionsSelectionResponsivePane').addClass('hidden');
		$('#layersSelectorResponsivePane').addClass('hidden');
		$(this).closest('.toolbarMenuNotVisibleInDesktopsBtns').removeClass('triangleAdded');
		$('#functionsSelectionResponsivePane').removeClass('hidden');
		closeButtonClicked = true;
//		$('#toolbarNotVisibleInDesktops').addClass('hiddenPane');
	});
	
	
//	functionsCarousel();
	tags();
}

function functionsCarousel(){
	$functionsCarousel = $('#functionsResponsiveList');
	$functionsCarousel.itemslide();
	
	$functionsCarousel.on('clickSlide changePos', function(event){
//		alert('index of clikced slide ' + $functionsCarousel.getCurrentPos());
//		console.log('slide: ' + event.slide);
		$('#functionResponsiveSubHeader').text($(this).find('.itemslide-active .functionResponsiveName').text());
	});
	
	$('#sliderRightButton').off('click').on('click',function(event){
		$functionsCarousel.next();
	}).off('hover').hover(function(event){
		dontChangeButtonColorOnHover($(this));
	});
	
	$('#sliderLeftButton').off('click').on('click',function(event){
		$functionsCarousel.previous();
	}).off('hover').hover(function(event){
		dontChangeButtonColorOnHover($(this));
	});
}

function dontChangeButtonColorOnHover($element){
	$element.removeClass('endOfAvailableSlides');
	var slidesCount = $functionsCarousel.find('li').length;
	var $slides = $functionsCarousel.find('li');
	var $activeSlide = $functionsCarousel.find('li.itemslide-active');
	if($slides.index($activeSlide) == slidesCount - 1 && $element.attr('id')==='sliderRightButton'){
		$element.addClass('endOfAvailableSlides');
	}else if($slides.index($activeSlide) == 0 && $element.attr('id')==='sliderLeftButton'){
		$element.addClass('endOfAvailableSlides');
	}
}

function tags(){
//	$('#layersTagsInput').tagit({
//		readOnly : true
//	});
}

function functionsCarouselSlickRedraw(){
	//Since a the plugin doesn't provide a redraw function, I did this "hack" to force the carousel render properly
	$('.slick-dots button:first').trigger('click');
}