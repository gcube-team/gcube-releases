function isPresent(obj)
{
	return (typeof obj !== undefined) && obj != null && obj !== "";
}

function getTextNodesIn(el) {
    return $(el).find(":not(iframe)").addBack().contents().filter(function() {
        return this.nodeType == 3;
    });
};

function getTextNodesInChildrenOf(el) {
    return $(el).children(":not(iframe)").addBack().contents().filter(function() {
        return this.nodeType == 3;
    });
};

function formFindValue(values, paramName)
{
	for (index = 0; index < values.length; ++index) {
	    if (values[index].name == paramName) {
	        return values[index].value;
	    }
	}
	return null;
}

function formReplaceValue(values, paramName, paramValue)
{
	var index;

	// Get the parameters as an array
	//values = $(form).serializeArray();

	for (index = 0; index < values.length; ++index) {
	    if (values[index].name == paramName) {
	        values[index].value = paramValue;
	        break;
	    }
	}

	// Add it if it wasn't there
	if (index >= values.length) {
	    values.push({
	        name: paramName,
	        value: paramValue
	    });
	}

	return values;
}


function formReplaceIfValue(values, paramName, paramValue, paramNewValue)
{
	var index;

	// Get the parameters as an array
	//values = $(form).serializeArray();

	for (index = 0; index < values.length; ++index) {
	    if (values[index].name == paramName && values[index].value == paramValue) {
	        values[index].value = paramNewValue;
	        break;
	    }
	}

	return values;
}

function formRemoveIfValue(values, paramName, paramValue)
{
	var index;

	// Get the parameters as an array
	//values = $(form).serializeArray();

	for (index = 0; index < values.length; ++index) {
	    if (values[index].name == paramName && values[index].value == paramValue) {
	        values.splice(index, 1);
	        break;
	    }
	}

	return values;
}

function formConvertDelimitedToList(values, paramNames)
{
	var index;
	
	for(index=0; index<paramNames.length; index++)
	{
		for(var i=0; i < values.length; i++)
		{
			if(values[i].name == paramNames[index])
			{		var paramValues = values[i].value.split(/[\s,]+/);
	    			values[i].value = [];
	    			for(var j=0; j<paramValues.length; j++)
	    				values[i].value.push(paramValues[j]);	
				break;
			}
		}
	}
}

function formParameterPresent(values, paramName)
{
	for(var i=0; i < values.length; i++)
	{
		if(values[i].name == paramName && isPresent(values[i].value)) return true;
	}
	return false;
}

function formReset($form) {
    $form.find('input:text, input:password, input:file, select, textarea').val('');
    $form.find('input:radio, input:checkbox')
         .removeAttr('checked').removeAttr('selected');
}



function applyFuncs(funcs, ret)
{
	if(isPresent(funcs)) 
	{
		for(var i=0; i<funcs.length; i++)
		{
			funcs[i].args = funcs[i].args ? funcs[i].args : [];
			for(var a=0; a<funcs[i].args.length; a++)
			{
				if(funcs[i].args[a] == "__funcRet")
					funcs[i].args[a] = ret;
			}
			funcs[i].func.apply(this, funcs[i].args);
		}
	}
}

function populateSelector(select, options, defaultOption, defaultOptionText, defaultOptionDisabled)
{
	$(select).empty();
	
	var option;
	if(defaultOption)
	{
		option = document.createElement("option");
		option.value = defaultOption;
		option.text = defaultOptionText;
		option.selected = true;
		if(defaultOptionDisabled)
		{
			option.disabled = true;
			option.style.display = 'none';
		}
		select.appendChild(option);
	}
	
	for(var i=0; i<options.length; i++)
	{
		option = document.createElement("option");
		option.value = options[i];
		option.text = options[i];
		select.appendChild(option);
	}
}

function toTitleCase(str)
{
	var titleCase = "";
	var parts = str.match(/\S+/g);
	for(var i=0; i<parts.length; i++)
	{
		if(i != 0) titleCase += " ";
		var first = parts[i].charAt(0).toUpperCase();
		var substr = parts[i].substr(1, parts[i].length-2);
		var last = parts[i].charAt(parts[i].length-1);
		last = last == 'Σ' ? 'ς' : last.toLowerCase();
		titleCase += first + substr.toLowerCase() + last;
	}
	return titleCase;
}

function toTitleCase3(str)
{
    return str.replace(/\w\S*/g, function(txt){
    		return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    	});
}

/*
* Title Caps
* 
* Ported to JavaScript By John Resig - http://ejohn.org/ - 21 May 2008
* Original by John Gruber - http://daringfireball.net/ - 10 May 2008
* License: http://www.opensource.org/licenses/mit-license.php
*/

(function(){
	var small = "(a|an|and|as|at|but|by|en|for|if|in|of|on|or|the|to|v[.]?|via|vs[.]?)";
	var punct = "([!\"#$%&'()*+,./:;<=>?@[\\\\\\]^_`{|}~-]*)";
 
	this.toTitleCase2 = function(title){
		var parts = [], split = /[:.;?!] |(?: |^)["Ò]/g, index = 0;
		
		while (true) {
			var m = split.exec(title);

			parts.push( title.substring(index, m ? m.index : title.length)
				.replace(/\b([A-Za-z][a-z.'Õ]*)\b/g, function(all){
					return /[A-Za-z]\.[A-Za-z]/.test(all) ? all : upper(all);
				})
				.replace(RegExp("\\b" + small + "\\b", "ig"), lower)
				.replace(RegExp("^" + punct + small + "\\b", "ig"), function(all, punct, word){
					return punct + upper(word);
				})
				.replace(RegExp("\\b" + small + punct + "$", "ig"), upper));
			
			index = split.lastIndex;
			
			if ( m ) parts.push( m[0] );
			else break;
		}
		
		return parts.join("").replace(/ V(s?)\. /ig, " v$1. ")
			.replace(/(['Õ])S\b/ig, "$1s")
			.replace(/\b(AT&T|Q&A)\b/ig, function(all){
				return all.toUpperCase();
			});
	};
   
	function lower(word){
		return word.toLowerCase();
	}
   
	function upper(word){
	  return word.substr(0,1).toUpperCase() + word.substr(1);
	}
})();

function pathToFile(str)
{
    var nOffset = Math.max(0, Math.max(str.lastIndexOf('\\'), str.lastIndexOf('/')));
    var eOffset = str.lastIndexOf('.');
    if(eOffset < 0)
    {
        eOffset = str.length;
    }
    return {isDirectory: eOffset == str.length,
            path: str.substring(0, nOffset),
            name: str.substring(nOffset > 0 ? nOffset + 1 : nOffset, eOffset),
            extension: str.substring(eOffset > 0 ? eOffset + 1 : eOffset, str.length)};
}

function timestampToDateString(timestamp)
{
	var date = new Date(timestamp);
    
	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
	var dateStr = addZero(date.getDate()) + '/' + addZero(date.getMonth()+1) + "/" + date.getFullYear();
	return dateStr;
}

function timestampToDateTimeString(timestamp)
{
	var date = new Date(timestamp);
    
	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
	var dateStr = addZero(date.getDate()) + '/' + addZero(date.getMonth()+1) + "/" + date.getFullYear() +
	" " + addZero(date.getHours()) + ":" + addZero(date.getMinutes()) + ":" + addZero(date.getSeconds());
	return dateStr;
}

function millisToDurationString (millis) {
    function numberEnding (number) {
        return (number > 1) ? 's' : '';
    }

    var temp = millis / 1000;
    var years = Math.floor(temp / 31536000);
    if (years) {
        return years + ' year' + numberEnding(years);
    }
    var days = Math.floor((temp %= 31536000) / 86400);
    if (days) {
        return days + ' day' + numberEnding(days);
    }
    var hours = Math.floor((temp %= 86400) / 3600);
    if (hours) {
        return hours + ' hour' + numberEnding(hours);
    }
    var minutes = Math.floor((temp %= 3600) / 60);
    if (minutes) {
        return minutes + ' minute' + numberEnding(minutes);
    }
    var seconds = temp % 60;
    if (seconds) {
        return seconds + ' second' + numberEnding(seconds);
    }
    return '<0 seconds';
}

function fileSize(bytes)
{
	var suffix = ["B", "KB", "MB", "GB", "TB"];
	var i = 0;
	var remainder = bytes;
	while(remainder > 1024 && i < suffix.length)
	{
		remainder /= 1024;
		i++;
	}
	
	return +(Math.round(remainder + "e+2")  + "e-2") + " " + suffix[i];
}

function validateEmail(email) { 
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
} 

function postToUrl(options) 
{
    var method = options.method || "post"; // Set method to post by default if not specified.

    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", options.path);
    if(options.target)
    	form.setAttribute("target", options.target);

    for(var key in options.params) {
        if(options.params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", options.params[key]);

            form.appendChild(hiddenField);
         }
    }

    document.body.appendChild(form);
    form.submit();
    $(form).remove();
}

function disableElement(el)
{
	 var target = $(el);
     if(target.length == 0)
    	 return;
	 if($.hasData(target[0])){    //  if the elment has event || target = element
         target[0].event_name = [];
         target[0].event_handler = [];

         events = $._data(target[0], 'events');  // take all events of the element

         $.each(events, function(event_name, event_handler){ 
        	 target[0].event_name.push(event_name);
             var _handlers=[];
             for(var i=0;i<event_handler.length;i++){
                 _handlers.push(event_handler[i].handler);
             }
             target[0].event_handler.push(_handlers); 
         });  //store the events

         target.off();  // delete the events
     }
}

function enableElement(el)
{
	var target = $(el);
	if(target.length == 0)
		return;
	if(!isPresent(target[0].event_handler))
		return;
	for(var i=0; i < target[0].event_handler.length; i++){
        for(var ii=0;ii<target[0].event_handler[i].length;ii++){
            target.on(target[0].event_name[i], target[0].event_handler[i][ii]);
        }          
    }  // re-store the events
    target[0].event_name = [];
    target[0].event_handler = [];  //reset
}

function enableZMaxIndex()
{
	/** Copied verbatim from Rick Strahl's weblog:
	 * http://www.west-wind.com/weblog/posts/2009/Sep/12/jQuery-UI-Datepicker-and-zIndex
	 */
	$.maxZIndex = $.fn.maxZIndex = function(opt) {
	    /// <summary>
	    /// Returns the max zOrder in the document (no parameter)
	    /// Sets max zOrder by passing a non-zero number
	    /// which gets added to the highest zOrder.
	    /// </summary>    
	    /// <param name="opt" type="object">
	    /// inc: increment value, 
	    /// group: selector for zIndex elements to find max for
	    /// </param>
	    /// <returns type="jQuery" />
	    var def = { inc: 10, group: "*" };
	    $.extend(def, opt);
	    var zmax = 0;
	    $(def.group).each(function() {
	        var cur = parseInt($(this).css('z-index'));
	        zmax = cur > zmax ? cur : zmax;
	    });
	    console.log("maxZIndex called");
	    if (!this.jquery)
	    {
	    	console.log("maxZIndex return non-jquery: " + zmax);
	        return zmax;
	    }

	    return this.each(function() {
	        zmax += def.inc;
	        $(this).css("z-index", zmax);
	    });
	}
}

function onConnectionLostFunction(){
	$('#InternalServerErrorModal').modal('show');
	$('.layersDataTableContainer .dataTables_empty').hide();
	$('#tab3').hide();
}

Object.keys = Object.keys || (function () {
    var hasOwnProperty = Object.prototype.hasOwnProperty,
        hasDontEnumBug = !{toString:null}.propertyIsEnumerable("toString"),
        DontEnums = [ 
            'toString', 'toLocaleString', 'valueOf', 'hasOwnProperty',
            'isPrototypeOf', 'propertyIsEnumerable', 'constructor'
        ],
        DontEnumsLength = DontEnums.length;

    return function (o) {
        if (typeof o != "object" && typeof o != "function" || o === null)
            throw new TypeError("Object.keys called on a non-object");

        var result = [];
        for (var name in o) {
            if (hasOwnProperty.call(o, name))
                result.push(name);
        }

        if (hasDontEnumBug) {
            for (var i = 0; i < DontEnumsLength; i++) {
                if (hasOwnProperty.call(o, DontEnums[i]))
                    result.push(DontEnums[i]);
            }   
        }

        return result;
    };
})();