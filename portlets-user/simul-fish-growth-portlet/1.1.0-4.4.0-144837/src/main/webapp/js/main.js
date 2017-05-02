$("#searchBtn").on('click' , function(){
	var $search = $("#searchBoxText");
	if($search.hasClass("_show"))
	{	
		$search.toggleClass("_hide");
		$search.removeClass("_show");
		
			}
	else{
	$search.toggleClass("_show");
	$search.removeClass("_hide");
	
	}
});

//Sort function
var sort = function(list , e , type){
	if(list.length > 0)
{
		list.sort(function(a , b){
			if(type =="desc")
				return a[e] > b[e];
			else 
				return a[e] < b[e];
		});
}
	return list;
	
};
var l = [{name:"thanos" , age:10} ,{name:"test" , age:45},{name:"GH" , age:23},{name:"kl" , age:18}];
$("th").on("click" , function(){
	console.log(sort(l , "age", "desc"));
	console.log(searchOnList(l , "t"));
});

//Search function
var searchOnList = function( list , expession )
{
	var sList = [];
	for(var i=0;i<list.length;i++)
	{
		var _obj = list[i];
		var str= "";
		for(key in _obj)
			{
				str+=" "+ _obj[key];
			}
		str = str.toLowerCase();
		var res = str.search(expession.toLowerCase());
		if(res >=0)
			sList.push(_obj);
	}
		return sList;
	};
	
	
	$(".i2s-datepicker-icon").click(function(event){
		var parent = $(event.target).parent();
		if(parent.hasClass("i2s-datepicker-icon"))
			parent = parent.parent();
		
		var input = parent.find("input");
		var is = input === document.activeElement;
		console.log(is);
		if(is == true)
			input.blur();
		else
			input.focus();
	});