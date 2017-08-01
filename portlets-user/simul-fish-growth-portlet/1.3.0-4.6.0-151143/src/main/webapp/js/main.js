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
	
	
	//Google Map
	
	var map;
	var mapLocation;
	var markers=[];
	function initMap(a, b) {
        // Create a map object and specify the DOM element for display.
			if(a!= null && b!= null)
				mapLocation = {lat: a, lng: b};
			else {
				a= 34.5;
				b =34.5;
			}

         map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: a, lng: b},
          scrollwheel: true,
          zoom: 8
        });
        
        google.maps.event.addListener(map, "dblclick", function (e) { 
            console.log("Double Click" , e.latLng.lat()); 
            mapLocation = e.latLng;
            setTimeout(placeMarker, 600);
         });
        
        if(mapLocation != null && mapLocation != undefined)
        	placeMarker();
      }
	function placeMarker() {
		deleteMarkers();
	var  marker = new google.maps.Marker({
	          position: mapLocation,
	          map: map,
	          title: 'Hello World!'
	        });
	 markers.push(marker);

	    
	}
	function setMapOnAll(map) {
        for (var i = 0; i < markers.length; i++) {
          markers[i].setMap(map);
        }
      }
	function clearMarkers() {
        setMapOnAll(null);
      }
	
	  function deleteMarkers() {
	        clearMarkers();
	        markers = [];
	      }
///////map button listener
	  $("#mapBtn").click(function(e){
		  var modal = document.getElementById('myModal');
		   var lat = $("#lat").val();
		   var lng = $("#lng").val();
		   	if(lat != null && lat != undefined)
		   		{
		   		try{
		   			lat = Number(lat);
		   		}catch(e){
		   			lat = null;
		   		}
		   		}
		   	if(lng != null && lng != undefined)
	   		{
	   		try{
	   			lng = Number(lng);
	   		}catch(e){
	   			lng = null;
	   		}
	   		}
		    modal.style.display = "block";
		    var span = document.getElementsByClassName("i2s-close")[0];
		    initMap(lat , lng);
		    span.onclick = function () {
		      modal.style.display = "none";

		    };
		    
		    
	  });
	  
	  $("#map_select_btn_ok").click(function(e){
		  if(mapLocation != null && mapLocation != undefined)
			  {
			  $("#lat").val(mapLocation.lat);
			  $("#lng").val(mapLocation.lng);
			  var modal = document.getElementById('myModal');
			  modal.style.display = "none";
			  }
	  })
	  
	  
	  /////////////////////////////////////