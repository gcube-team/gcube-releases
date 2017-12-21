function timestampToDateString(timestamp)
{
	var date = new Date(timestamp);
    
	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
	var dateStr = addZero(date.getMonth()+1) + '/' + addZero(date.getDate()) + "/" + date.getFullYear();
	return dateStr;
}

function timestampToDateStringDots(timestamp)
{
	var date = new Date(timestamp);
    
	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
	var dateStr = addZero(date.getMonth()+1) + '.' + addZero(date.getDate()) + "." + date.getFullYear();
	return dateStr;
}

function timestampToLocale(timestamp)
{
	return new Date(timestamp).toLocaleDateString(userinfoObject.locale.replace("_","-"))
}

function createBootstrapModal(
		idName, modalClassName,
		modalHeaderClassName,modalHeader,
		$customElements,
		dissmissButtonId, dissmissClassName, dismissModalBtnName,
		$modalFooterBtns){
//	Basic modal elements
	var div = '<div></div>';
	var theClassname = "modal fade" + modalClassName;
	var $modal = $(div, {
		id: idName,
		class: theClassname,
		hidden: true,
		tabindex : -1,
		role: "dialog"
	});
	
	var $modalHeader = $(div, {
		class: "modal-header"
	});
	var $blueLine = $(div, {
		id: 'blueLineBottom'
	});
	var theModalHeaderClassName = 'modalHeader' + modalHeaderClassName;
	var $spanInHeader = $('<span></span>', {
		class: theModalHeaderClassName,
		text: modalHeader
	});
	var $closeButton = $('<button></button>', {
		type:"button",
		class: 'close',
		'data-dismiss': 'modal',
		'aria-hidden': true,
		text: '×'
	});
	$blueLine.append($spanInHeader).append($closeButton);
	$modalHeader.append($blueLine);
	
	var $modalBody = $(div, {
		class: 'modal-body'
	});
	$modalBody.append($customElements);
	
	var $modalFooter = $(div, {
		class: 'modal-footer'
	});
	var theDissmissClassName = 'btn btn-link btn-large cancelBtns' + dissmissClassName;
	var $dismissModalBtn = $('<button></button>',{
		id: dissmissButtonId,
		class: theDissmissClassName,
		'data-dismiss':"modal",
		'aria-hidden':"true",
		text: dismissModalBtnName
	});
	if(typeof $modalFooterBtns === "undefined"){
		$modalFooter.append($modalFooterBtns);
	}
	$modalFooter.append($dismissModalBtn);
	
	$modal.append($modalHeader).append($modalBody).append($modalFooter);
	
//	$('#pickProjectContainer').append($modal);
	$($modal).insertAfter('#authorizationMessageModal');
}