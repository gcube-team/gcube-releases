function exportMap(){
	exportMapAsPNG();
	exportMapAsPDF();
	exportMapAsJPEG();
}

function exportMapAsPNG(){
	$('#exportAsPNG').off().on('click', function(){
		var thisLink = this;
	    map.once('postcompose', function(event) {
	      var canvas = event.context.canvas;
	      thisLink.href = canvas.toDataURL('image/png');
	    });
	    map.renderSync();
	});
}

function exportMapAsPDF(){
	var dims = {
	  a0: [1189, 841],
	  a1: [841, 594],
	  a2: [594, 420],
	  a3: [420, 297],
	  a4: [297, 210],
	  a5: [210, 148]
	};
	
	$('#exportAsPDF').off().on('click', function(e) {
	
	  var format = 'a4';//document.getElementById('format').value;
	  var resolution = 300;//document.getElementById('resolution').value;
	  var dim = dims[format];
	  var width = Math.round(dim[0] * resolution / 25.4);
	  var height = Math.round(dim[1] * resolution / 25.4);
	  var size = /** @type {ol.Size} */ (map.getSize());
	  var extent = map.getView().calculateExtent(size);
	
//	  To prevent potential unexpected division-by-zero
//	  behaviour, tileTotalCount must be larger than 0.
	  var data = map.getRenderer().canvas_.toDataURL('image/jpeg');
	  var pdf = new jsPDF('landscape', undefined, format);
	  pdf.addImage(data, 'JPEG', 0, 0, dim[0], dim[1]);
//	        pdf.save('map.pdf');
    
	  e.target.href = pdf.output('datauristring');
	});
}

function exportMapAsJPEG(){
	$('#exportAsJPEG').off('click').on('click', function(e){
		e.target.href = map.getRenderer().canvas_.toDataURL('image/jpeg');
	});
}