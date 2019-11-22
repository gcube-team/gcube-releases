$.widget('cite.shapefileImporter', {	
	isActive : false,
	notificator : $("#shapefile-importer-notificator"),
	options:{
		mode: "div"	,
		importShapefileURL: "",
		stylesURL: "",
		notificator: null,
		headerDiv: {},
		content: {}
	},
	createImporter : function() {
		if (this.isActive) {
			this.destroy();
		}
		this._createUI();
		this._initializeFunctionality();
		this.isActive = true;
	},
	_createShowButton : function() {
		var shapefileimporter = $('#shapefileimporter');	
		$(shapefileimporter).css("display", "inline-block");

		var showButton = 	'<button id="shapefile-importer-show-button"  ' +
									'type="button" '+
									'class="btn btn-large" '+
									'data-toggle="modal" '+
									'data-target="#shapefile-importer-container">	'	+								
								'<i class="fa fa-upload" ></i> Shapefile Importer	'+							 
							'</button>';
		shapefileimporter.append($(showButton));
	},	
	_createNonModal : function() {
		var container = '<div id="shapefile-importer-container"> ';
		this._container = $(container);		
		this._container.appendTo(this.options.content);
	},	
	_createModal : function() {	
		var container = '<div id="shapefile-importer-container" ' +
							'class="modal fade in shapefile-importer-container-button"   ' +
							'tabindex="-1" ' +
							'role="dialog"	' +
							'aria-labelledby="shapefileimporter"  ' +
							'aria-hidden="true " ' +
							'style="display:none;"> ';			
		this._container = $(container);		
		this._container.appendTo(this.element);
	},
	_createUI : function() {
		var mode = this.options.mode;		
		
		switch (mode){
			case "button":
				this._createShowButton();
				this._createModal();
				break;			
			case "div" :
				this._createNonModal();		
				break;		
		}
		
		var closeButton = "";
		var header = "" ;
		var body = "";
		var scrollableRow = "";
		
		if(this.options.mode === "button"){
			closeButton = '<button id="shapefile-importer-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>';
			header = '<div id="shapefile-importer-modal-header" class="modal-header">' +
				closeButton +
				'<h5 id="shapefile-importer-label-modal">Shapefile Importer</h5> ' +
			'</div>';	
			body = '<div id="shapefile-importer-modal-body" class="modal-body scrollable" >';
			scrollableRow = " scrollable";
		}else{
			body = '<div id="shapefile-importer-modal-body" >';
		}

		var importerBody = header + body + 	'<input type="button" id="shapefile-importer-toggle-geonetwork-metadata" />' +
		'<div class="row">' +
			'<div class="span6" id="shapefile-importer-general-form-container">' +
				'<div class="spinner" style="display: none"></div>' +
				'<span class="headerDescription">GENERAL</span>	' +
				'<div id="shapefile-importer-notificator"></div>	' +													
				'<hr>	' +
				'<form class="form-horizontal" id="shapefile-importer-general-form">	' +
					'<div class="control-group row">		' +		
						'<div class="span4">' +
							'<label>Data Location<span class="makeMeOrange">*</span></label>' +
						'</div>		' +
																					
						'<div class="span6">		' +													
							'<input id="shapefile-importer-selected-file" class="span11"  type="text" placeholder="No shapefile selected with zip extension" readonly>' + 	
							'<br>' +
							'<span class="help-inline"></span>' +
						'</div>				' +			
						'<div class="controls span2" id="shapefile-importer-patch-browsefiles">	' +														
							'<button class="span12">Browse</button>' +
							'<input id="shapefile-importer-browsefiles-button" name="browseFiles" type="file" >	' +
						'</div>	' +
					'</div>		' +
					
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-dbf-check">Set DBF charset</label>' +
						'</div>' +
						'<div class="span8">' +
							'<input type="checkbox" id="shapefile-importer-dbf-check">' +
							'<label for="shapefile-importer-dbf-check"></label>' +
						'</div>' +
					'</div>' +		
					
					'<div class="control-group row" style="margin-top: 20px; display: block;display:none">' +
						'<div class="span4">' +
							'<label style="padding-left:20px;" for="DBF-charset">DBF charset<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<select id="DBF-charset" class="span12"  name="style">' +							
								'<option value="0">Big5</option>' +
								'<option value="1">Big5-HKSCS</option>' +
								'<option value="2">CESU-8</option>' +
								'<option value="3">EUC-JP</option>' +
								'<option value="4">EUC-KR</option>' +
								'<option value="5">GB18030</option>' +
								'<option value="6">GB2312</option>' +
								'<option value="7">GBK</option>' +
								'<option value="8">IBM-Thai</option>' +
								'<option value="9">IBM00858</option>' +
								'<option value="10">IBM01140</option>' +
								'<option value="11">IBM01141</option>' +
								'<option value="12">IBM01142</option>' +
								'<option value="13">IBM01143</option>' +
								'<option value="14">IBM01144</option>' +
								'<option value="15">IBM01145</option>' +
								'<option value="16">IBM01146</option>' +
								'<option value="17">IBM01147</option>' +
								'<option value="18">IBM01148</option>' +
								'<option value="19">IBM01149</option>' +
								'<option value="20">IBM037</option>' +
								'<option value="21">IBM1026</option>' +
								'<option value="22">IBM1047</option>' +
								'<option value="23">IBM273</option>' +
								'<option value="24">IBM277</option>' +
								'<option value="25">IBM278</option>' +
								'<option value="26">IBM280</option>' +
								'<option value="27">IBM284</option>' +
								'<option value="28">IBM285</option>' +
								'<option value="29">IBM290</option>' +
								'<option value="30">IBM297</option>' +
								'<option value="31">IBM420</option>' +
								'<option value="32">IBM424</option>' +
								'<option value="33">IBM437</option>' +
								'<option value="34">IBM500</option>' +
								'<option value="35">IBM775</option>' +
								'<option value="36">IBM850</option>' +
								'<option value="37">IBM852</option>' +
								'<option value="38">IBM855</option>' +
								'<option value="39">IBM857</option>' +
								'<option value="40">IBM860</option>' +
								'<option value="41">IBM861</option>' +
								'<option value="42">IBM862</option>' +
								'<option value="43">IBM863</option>' +
								'<option value="44">IBM864</option>' +
								'<option value="45">IBM865</option>' +
								'<option value="46">IBM866</option>' +
								'<option value="47">IBM868</option>' +
								'<option value="48">IBM869</option>' +
								'<option value="49">IBM870</option>' +
								'<option value="50">IBM871</option>' +
								'<option value="51">IBM918</option>' +
								'<option value="52">ISO-2022-CN</option>' +
								'<option value="53">ISO-2022-JP</option>' +
								'<option value="54">ISO-2022-JP-2</option>' +
								'<option value="55">ISO-2022-KR</option>' +
								'<option selected="selected" value="56">ISO-8859-1</option>' +
								'<option value="57">ISO-8859-13</option>' +
								'<option value="58">ISO-8859-15</option>' +
								'<option value="59">ISO-8859-2</option>' +
								'<option value="60">ISO-8859-3</option>' +
								'<option value="61">ISO-8859-4</option>' +
								'<option value="62">ISO-8859-5</option>' +
								'<option value="63">ISO-8859-6</option>' +
								'<option value="64">ISO-8859-7</option>' +
								'<option value="65">ISO-8859-8</option>' +
								'<option value="66">ISO-8859-9</option>' +
								'<option value="67">JIS_X0201</option>' +
								'<option value="68">JIS_X0212-1990</option>' +
								'<option value="69">KOI8-R</option>' +
								'<option value="70">KOI8-U</option>' +
								'<option value="71">Shift_JIS</option>' +
								'<option value="72">TIS-620</option>' +
								'<option value="73">US-ASCII</option>' +
								'<option value="74">UTF-16</option>' +
								'<option value="75">UTF-16BE</option>' +
								'<option value="76">UTF-16LE</option>' +
								'<option value="77">UTF-32</option>' +
								'<option value="78">UTF-32BE</option>' +
								'<option value="79">UTF-32LE</option>' +
								'<option value="80">UTF-8</option>' +
								'<option value="81">windows-1250</option>' +
								'<option value="82">windows-1251</option>' +
								'<option value="83">windows-1252</option>' +
								'<option value="84">windows-1253</option>' +
								'<option value="85">windows-1254</option>' +
								'<option value="86">windows-1255</option>' +
								'<option value="87">windows-1256</option>' +
								'<option value="88">windows-1257</option>' +
								'<option value="89">windows-1258</option>' +
								'<option value="90">windows-31j</option>' +
								'<option value="91">x-Big5-HKSCS-2001</option>' +
								'<option value="92">x-Big5-Solaris</option>' +
								'<option value="93">x-COMPOUND_TEXT</option>' +
								'<option value="94">x-euc-jp-linux</option>' +
								'<option value="95">x-EUC-TW</option>' +
								'<option value="96">x-eucJP-Open</option>' +
								'<option value="97">x-IBM1006</option>' +
								'<option value="98">x-IBM1025</option>' +
								'<option value="99">x-IBM1046</option>' +
								'<option value="100">x-IBM1097</option>' +
								'<option value="101">x-IBM1098</option>' +
								'<option value="102">x-IBM1112</option>' +
								'<option value="103">x-IBM1122</option>' +
								'<option value="104">x-IBM1123</option>' +
								'<option value="105">x-IBM1124</option>' +
								'<option value="106">x-IBM1166</option>' +
								'<option value="107">x-IBM1364</option>' +
								'<option value="108">x-IBM1381</option>' +
								'<option value="109">x-IBM1383</option>' +
								'<option value="110">x-IBM300</option>' +
								'<option value="111">x-IBM33722</option>' +
								'<option value="112">x-IBM737</option>' +
								'<option value="113">x-IBM833</option>' +
								'<option value="114">x-IBM834</option>' +
								'<option value="115">x-IBM856</option>' +
								'<option value="116">x-IBM874</option>' +
								'<option value="117">x-IBM875</option>' +
								'<option value="118">x-IBM921</option>' +
								'<option value="119">x-IBM922</option>' +
								'<option value="120">x-IBM930</option>' +
								'<option value="121">x-IBM933</option>' +
								'<option value="122">x-IBM935</option>' +
								'<option value="123">x-IBM937</option>' +
								'<option value="124">x-IBM939</option>' +
								'<option value="125">x-IBM942</option>' +
								'<option value="126">x-IBM942C</option>' +
								'<option value="127">x-IBM943</option>' +
								'<option value="128">x-IBM943C</option>' +
								'<option value="129">x-IBM948</option>' +
								'<option value="130">x-IBM949</option>' +
								'<option value="131">x-IBM949C</option>' +
								'<option value="132">x-IBM950</option>' +
								'<option value="133">x-IBM964</option>' +
								'<option value="134">x-IBM970</option>' +
								'<option value="135">x-ISCII91</option>' +
								'<option value="136">x-ISO-2022-CN-CNS</option>' +
								'<option value="137">x-ISO-2022-CN-GB</option>' +
								'<option value="138">x-iso-8859-11</option>' +
								'<option value="139">x-JIS0208</option>' +
								'<option value="140">x-JISAutoDetect</option>' +
								'<option value="141">x-Johab</option>' +
								'<option value="142">x-MacArabic</option>' +
								'<option value="143">x-MacCentralEurope</option>' +
								'<option value="144">x-MacCroatian</option>' +
								'<option value="145">x-MacCyrillic</option>' +
								'<option value="146">x-MacDingbat</option>' +
								'<option value="147">x-MacGreek</option>' +
								'<option value="148">x-MacHebrew</option>' +
								'<option value="149">x-MacIceland</option>' +
								'<option value="150">x-MacRoman</option>' +
								'<option value="151">x-MacRomania</option>' +
								'<option value="152">x-MacSymbol</option>' +
								'<option value="153">x-MacThai</option>' +
								'<option value="154">x-MacTurkish</option>' +
								'<option value="155">x-MacUkraine</option>' +
								'<option value="156">x-MS932_0213</option>' +
								'<option value="157">x-MS950-HKSCS</option>' +
								'<option value="158">x-MS950-HKSCS-XP</option>' +
								'<option value="159">x-mswin-936</option>' +
								'<option value="160">x-PCK</option>' +
								'<option value="161">x-SJIS_0213</option>' +
								'<option value="162">x-UTF-16LE-BOM</option>' +
								'<option value="163">X-UTF-32BE-BOM</option>' +
								'<option value="164">X-UTF-32LE-BOM</option>' +
								'<option value="165">x-windows-50220</option>' +
								'<option value="166">x-windows-50221</option>' +
								'<option value="167">x-windows-874</option>' +
								'<option value="168">x-windows-949</option>' +
								'<option value="169">x-windows-950</option>' +
								'<option value="170">x-windows-iso2022jp</option>' +							
							'</select>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>	' +
					
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-layername">Layer Name<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="shapefile-importer-layername"  class="span12"  name="layerName"  placeholder="Please fill in your Layer Name" rows="1"></textarea>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>' +	
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for=""shapefile-importer-style"">Style<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<select id="shapefile-importer-style" class="span12"  name="style">' +
								'<option  value="" disabled selected>Choose a Style</option>' +
							'</select>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>	' +
					
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for=""shapefile-importer-template-layer"">Is Template Layer</label>' +
						'</div>' +
						'<div class="span8">			' +			
							'<input type="checkbox" id="shapefile-importer-template-layer">' +
							'<label for="shapefile-importer-template-layer"></label>' +
						'</div>' +
					'</div>' +		
					
					'<div class="control-group row" style="display:none; margin-top:20px;">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-template-layer" style="padding-left:20px;">' +
								'Template Layer Geocode System<span class="makeMeOrange">*</span>' +
							'</label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea style="resize: none;" ' +
								'id="shapefile-importer-template-layer-geocode-system" ' +
								'class="span12" ' +
								'rows="1" ' +
								'placeholder="Please give the name of the Geocode System" 	' +						
								'name="geocodeSystem"></textarea>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>' +
					
					'<div class="control-group row" style="display:none;">'	+
						'<div class="span4">'	+
							'<label for="shapefile-importer-template-layer" style="padding-left:20px;">'	+
								'Template Layer Geocode Mapping<span class="makeMeOrange">*</span>'	+
							'</label>'	+
						'</div>'	+
						'<div class="span8">'	+
							'<textarea style="resize: none;" '	+
								'id="shapefile-importer-template-layer-geocode-mapping" '	+
								'class="span12" '	+
								'rows="2" '	+
								'placeholder="Please give the shapefile attribute containing the Geocode" '	+							
								'name="geocodeMapping"></textarea>'	+
							'<span class="help-inline"></span>'	+
						'</div>'	+
					'</div>'	+	
					
					'<div class="control-group row" id="shapefile-importer-general-description">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-abstract">Layer description<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea name="abstractDescription" id="shapefile-importer-abstract" class="span12" placeholder="Please give a brief description of the layer" rows="5"></textarea>' +
						'</div>' +
					'</div>' +
									
					'<div class="control-group row" id="generalKeywords">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-tagsinput">Layer keywords<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="shapefile-importer-tagsinput" name="tagsInput" class="span12" placeholder="Please fill in the layer keywords" rows="1"></textarea>' +
						'</div>' +
					'</div>' +
				'</form>' +
			'</div>	' +
					
			'<div class="span6">' +
				'<div id="shapefile-importer-geonetwork-metadata"></div>' +
			'</div>' +
		'</div>'	;
		
		this._container.append($(importerBody));

		
		this._container.append('<div class="modal-footer">' +
									'<div id="shapefile-importer-div-submit" class="control-group pull-right">' +
										'<button type="button" data-dismiss="modal" id="shapefile-importer-cancel" class="btn" aria-hidden="true">Cancel</button>' +
										'<button type="button" id="shapefile-importer-import-button"  class="btn" >Import</button>' +
									'</div>' +				
								'</div>');	
	},
	_initializeFunctionality : function (){		
		var importShapefileURL = this.options.importShapefileURL;
		var stylesURL = this.options.stylesURL;
		var notificator = this.options.notificator;
		var toggleGeoNetworkMetadata = $("#shapefile-importer-toggle-geonetwork-metadata");
		var geoNetworkModule = window.config.geoNetworkModule.createInstance("#shapefile-importer-geonetwork-metadata");
		geoNetworkModule.createGeoNetworkForm();
		
		$(document).ready(function(){
			$('#shapefile-importer-tagsinput').tagsInput({
			   'defaultText':'Add a Tag',
			   'delimiter': [',',';',' '],   
			   'minChars' : 2,
			   'maxChars' : 40,
			   'placeholderColor' : 'rgba(153, 153, 153, 0.65)'
			});	
			
			$('#shapefile-importer-browsefiles-button').bind("change", function() {
				var fileName = $(this).val().split('\\').pop();
				$('#shapefile-importer-selected-file').val(fileName);
				var valid = $('#shapefile-importer-browsefiles-button').valid();
				
				if(valid){
					fileName += " has been selected";
					window.noty.showNoty($("#shapefile-importer-notificator"), fileName, "success", false);
				}else{
					window.noty.closeAllNotys();
				}		
			});
			
		    $.validator.addMethod("validRegex", function(value, element) {
		        return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9\_\s]+$/i.test(value);
		    }, "Field must start with a letter and contain only letters, numbers, or underscores.");		    
			
			$('#shapefile-importer-general-form').validate({
				rules: {
					layerName: {
						minlength: 2,
					    required : true
					},
					style:{
						required: true
					},
					abstractDescription: {
						required: true
					},
					browseFiles:{
						extension: "zip",
						required: true
					},
					geocodeSystem:{
						required: true
					},
					geocodeMapping:{
						required: true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('shapefile-importer-color-red');
					
					if($(element).attr('id') === 'shapefile-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#shapefile-importer-patch-browsefiles').css("border-color","red");		
					}					
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					if($(element).attr('id') === 'shapefile-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#shapefile-importer-patch-browsefiles').css("border-color","#DDD");		
					}
					label.remove();
				},		
				errorPlacement: function(error, element) {	
					if(element.attr('id') === 'shapefile-importer-browsefiles-button'){
						error.appendTo(element.closest('.control-group').find('.help-inline'));	
						element.closest('.control-group').find('#shapefile-importer-patch-browsefiles').addClass('error');			
					}else{
						error.appendTo($(element).siblings('.help-inline'));
					}
				}
			});	
			
			jQuery.extend(jQuery.validator.messages, {
				extension: "Please upload a file with .zip extension."
			});
			
			$("#shapefile-importer-template-layer").change(function() {
				$("#shapefile-importer-template-layer-geocode-system").closest(".control-group").toggle();
				$("#shapefile-importer-template-layer-geocode-mapping").closest(".control-group").toggle();
			});
			
			$('#shapefile-importer-dbf-check').change(function() {
				$("#DBF-charset").closest(".control-group").toggle();
			});
		});		
		
		toggleGeoNetworkMetadata.styledCheckbox({
			text : "Publish Layer on GeoNetwork",
			initiallyChecked : true
		});
		
		toggleGeoNetworkMetadata.on("click", function() {
			$(this).styledCheckbox("isChecked") ? geoNetworkModule.enableGeoNetworkForm() : geoNetworkModule.disableGeoNetworkForm();			
		});
		
		// Clear contents with cancel
		
		$(document).ready(function() {
			$('#shapefile-importer-cancel , #shapefile-importer-close-button').on('click',function (e){ 
				$('#shapefile-importer-general-form')[0].reset();
				geoNetworkModule.resetGeoNetworkForm();
				
				$("#shapefile-importer-container").find("*").removeClass("shapefile-importer-color-red error success");
				$('.help-inline > label').remove();
				
				window.noty.closeAllNotys();
				
				$('#shapefile-importer-patch-browsefiles').css("border-color", "gray");
				$('#shapefile-importer-tagsinput_tagsinput').find(".tag").remove();
			});
		});	
				
		$(document).ready(function() {			
			$.ajax({
				url: stylesURL,
				type: 'GET',
				cache : false,
				dataType: 'json',
				success: function(response) {
					$.each(response, function(i,v){
						var $option = $('<option></option>', {
							text : v,
							value : i
						});
						$('#shapefile-importer-style').append($option);
					});
				},
				error : function(jqXHR, exception) {
					window.noty.errorHandlingNoty(notificator, jqXHR, exception);
				}
			});	
			
			// Load Template Layers
			
			var notificator = $("#shapefile-importer-notificator");			
			
			$('#shapefile-importer-import-button').on('click',function (e){
				var validData = $('#shapefile-importer-general-form').valid();
				var validMetadata = geoNetworkModule.validateForm();
				var publishOnGeoNetwork = toggleGeoNetworkMetadata.styledCheckbox("isChecked");				
				
				if(validData && ((publishOnGeoNetwork && validMetadata) || !publishOnGeoNetwork)){				
					var spans = $('#shapefile-importer-container .tagsinput').find(".tag > span");
					var tags = [];
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
//						tags.push(spans[i]);
						tags.push( encodeURI( spans[i]) );
					}
					let dbfEncoding = $('#DBF-charset').find('option:selected').text();
					let dbfUserInput = $('#shapefile-importer-dbf-check').is(":checked");
					
					var file = document.getElementById('shapefile-importer-browsefiles-button').files[0];					

					var importFormData = new FormData();
					importFormData.append("shapefileImportFile", file);
					importFormData.append("shapefileImportProperties", new Object([JSON.stringify({
						newLayerName	: 	encodeURI( $('#shapefile-importer-layername').val() ),
						geocodeSystem	:	$("#shapefile-importer-template-layer-geocode-system").val(),
						geocodeMapping	:	$("#shapefile-importer-template-layer-geocode-mapping").val(),
						description		: 	encodeURI( $('#shapefile-importer-abstract').val().trim() ),

						isTemplate		:	$("#shapefile-importer-template-layer").is(":checked"),
						style			:   $('#shapefile-importer-style option:selected').text(),
						tags			: 	tags,
						dbfUserInput	:	dbfUserInput,
						dbfEncoding		:	dbfEncoding
					})], {
						type: "application/json"
					}));
					
					if(publishOnGeoNetwork){
						let title = encodeURI( $('#shapefile-importer-layername').val() );
						let description = encodeURI( $('#shapefile-importer-abstract').val().trim() );
						
						let purpose = encodeURI( geoNetworkModule.getPurpose() );
						let limitation = encodeURI( geoNetworkModule.getLimitation() );
						
						let author = {};
						let authorMetaData = geoNetworkModule.getAuthorMetadata();
						for( let i in authorMetaData ) {
							author[i] = encodeURI( authorMetaData[i] );
						}
						
						let distributor = {};
						let distributorMetaData = geoNetworkModule.getDistributorMetadata();
						for( let i in distributorMetaData ) {
							distributor[i] = encodeURI( distributorMetaData[i] );
						}
						
						let provider = {};
						let providerMetaData = geoNetworkModule.getProviderMetadata();
						for( let i in providerMetaData ) {
							provider[i] = encodeURI( providerMetaData[i] );
						}
						
						importFormData.append("shapefileImportMetadata", new Object([JSON.stringify({
							title			:	title,
							description		: 	description,
							purpose			:	purpose,
							keywords		: 	tags,
							
							purpose			:	purpose,
							limitation		:	limitation,
			
							author 			: 	author,
							distributor 	: 	distributor,
							provider 		: 	provider 
						})], {
							type: "application/json"
						}));	
					}
					
 					$.ajax({
 					    type : "POST", 					    
 					    url : importShapefileURL, 					    
 					    processData : false, 					   
 		                contentType : false, 
 					    data: importFormData, 	
 					    beforeSend : function() {
 					    	$('#shapefile-importer-container .spinner').show();
 					    },
 					    success: function(data){
 							window.noty.showNoty(notificator, data, "success");	
 					    }, 					    
 					    error: function(jqXHR, exception){ 	
 					    	window.noty.errorHandlingNoty(notificator, jqXHR, exception);
					    },
					    complete: function() {
					    	$('#shapefile-importer-container .spinner').hide();
					    }
 					});				    
				}				
			}); 			 
		});
	},
	destroy: function()	{
		$(this.options.content).children().remove();
	}
});