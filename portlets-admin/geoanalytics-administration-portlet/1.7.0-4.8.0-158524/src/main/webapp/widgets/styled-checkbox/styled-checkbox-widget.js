$.widget("dt.styledCheckbox", {
    options : {
        text : "",
        value : "",
        name : "",
        classes : "",
        initiallyChecked : false
    },
    _create : function() {
	    var widgetInstance = this;
	    var element = this.element;    
	  
	    var wrapperLabel = $('<label class="formui-checkbox option">' + this.options.text + '</label>');
	    this.element.wrap(wrapperLabel);
	    this.element.addClass(this.options.classes);
	    
	    this.element.on("click", function () {
	        $(this).parent(".formui-checkbox").toggleClass("checked");
	    });
	    
	    if(this.options.initiallyChecked){
	    	this.element.parent(".formui-checkbox").addClass("checked");
	    }
    },
    isChecked : function(){
    	return this.element.closest(".formui-checkbox").hasClass("checked");
    }
});