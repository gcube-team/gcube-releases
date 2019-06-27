(function() {
	'use strict';
    var ruleCounter = 1;

    $(document).ready(function () {
        $('#geoadmin-property-icon-0').hide();
        $('#discard-rule-btn').hide();
    });

    $(document.body).on("click", '.rule-close-btn', function() {
        var str = (this.id).split("-");
        $('div#rule-container-'+str[2]).remove();
        if( ruleCounter == parseInt(str[2])) {
            ruleCounter--;
        }
    });

    $('#geoadmin-create-style-modal-editor-submit').click(function () {
        var style = {
            name: $("#geoadmin-create-style-editor-name").val(),
            description: $("#geoadmin-create-style-editor-description").val()
        };
        for( var i=0; i <= ruleCounter; i++) {
            style["Rule"+i] = {
                title: $("#geoadmin-create-rule-title-"+i).val(),
                propertyName: $("#geoadmin-rule-property-name-"+i).val(),
                symbol: $("#geoadmin-rule-symbol-"+i).val(),
                propertyLessThan: $("#geoadmin-rule-property-range-to-"+i).val(),
                propertyMoreThan: $("#geoadmin-rule-property-range-from-"+i).val(),
                propertyFill: $("#geoadmin-rule-property-fill-"+i).val(),
            }
        }
        var url = window.config.createResourceURL('styles/createStyleXml');

		var importFormData = new FormData();
        importFormData.append("styleProperties", new Object([JSON.stringify(style)], {
            type: "application/json"
        }));
        for( var i=0; i <= ruleCounter; i++) {
            if($("#geoadmin-rule-property-fill-"+i).val() == "icon" && document.getElementById('geoadmin-upload-icons-btn-'+i) != undefined){
                var fileIcon = document.getElementById('geoadmin-upload-icons-btn-'+i).files[0];
                importFormData.append("iconImportFile-"+i, fileIcon);
            }

        }

        $.ajax({
            url : url,
            type : 'POST',
            data : importFormData,
            contentType : false,
            processData : false,
            beforeSend : function() {
                $("#geoadmin-create-style-modal").modal('hide');
                styles.spinner.show();
            },
            success : function(id) {
                styles.dataTable.refreshData();
                styles.showMessage("Style \"" + id + "\" has been created successfully!", "success");
            },
            error : function(jqXHR, exception) {
                styles.errorHandling(jqXHR, exception);
            },
            complete : function() {
                styles.spinner.hide();
            },
            timeout : 20000
        });
    });

    $("#add-rule-btn").on('click', function() {
        var prev = ruleCounter - 1;
        var next = ruleCounter + 1;
        $("#rule-container-" + prev).after('<div id="rule-container-' + ruleCounter + '">' +
         '<div class="control-group row">' +
                '<span class="span6">' +
                    '<label>Rule ' + next + '</label>' +
                '</span>' +
                '<span class="span5"></span>' +
                '<button id="discard-rule-' + ruleCounter + '" type="button" class="rule-close-btn span1">×</button>' +
                '<span class="help-inline"></span>' +
         '</div>' +
             '<div class="control-group row">' +
                  '<label class="control-label" for="geoadmin-create-rule-title-' + ruleCounter + '">Rule Title</label>' +
                   '<div class="controls">' +
                       '<input id="geoadmin-create-rule-title-' + ruleCounter + '" name="ruleTitle" placeholder="Rule Title" class="span10" type="text"></input>' +
                       '<span class="help-inline"></span>' +
                   '</div>' +
             '</div>' +
             '<div class="control-group row">' +
                 '<label class="control-label" for="geoadmin-rule-symbol-' + ruleCounter + '">Rule Symbol</label>' +
                 '<div class="controls">' +
                     '<select id="geoadmin-rule-symbol-' + ruleCounter + '" name="geoadmin-rule-symbol" >' +
                         '<option value="polygon">Polygon</option>' +
                         '<option value="point">Point</option>' +
                         '<option value="line">Line</option>' +
                     '</select>' +
                 '</div>' +
             '</div>' +
             '<div class="control-group row">' +
                  '<label class="control-label" for="geoadmin-rule-property-name-' + ruleCounter + '">Property Name</label>' +
                   '<div class="controls">' +
                       '<input id="geoadmin-rule-property-name-' + ruleCounter + '" name="styleDescription" placeholder="Property Name" class="span10" />' +
                       '<span class="help-inline"></span>' +
                   '</div>' +
             '</div>' +
             '<div class="control-group row">' +
                 '<label class="control-label" for="geoadmin-rule-property-range-' + ruleCounter + '">Property Range</label>' +
                 '<div class="controls row flex" id="geoadmin-rule-property-range-' + ruleCounter + '">' +
                     '<span class="span1"></span>' +
                     '<input id="geoadmin-rule-property-range-from-' + ruleCounter + '" name="rangeFrom" placeholder="From" class="span3" type="number"/>' +
                      '-' +
                     '<input id="geoadmin-rule-property-range-to-' + ruleCounter + '" name="rangeTo" placeholder="To" class="span3" type="number"/>' +
                 '</div>' +
             '</div>' +

           '<div class="control-group row">' +
              '<label class="control-label" for="geoadmin-rule-property-fill-' + ruleCounter + '">Fill Color</label>' +
              '<div class="controls">' +
                   '<select id="geoadmin-rule-property-fill-' + ruleCounter + '" name="fillSelector" >' +
                         '<option value="-">-</option>' +
                         '<option value="red"><span class="color-box" style="background-color: #d80015;"></span>Red</option>' +
                         '<option value="blue"><span class="color-box" style="background-color: #1a4ce0;"></span>Blue</option>' +
                         '<option value="yellow"><span class="color-box" style="background-color: #f2ff00;"></span>Yellow</option>' +
                         '<option value="green"><span class="color-box" style="background-color: #00b226;"></span>Green</option>' +
                         '<option value="icon">Icon</option>' +
                   '</select>' +
               '</div>' +
               '<span class="help-inline"></span>' +
           '</div>' +
           '<div id="geoadmin-property-icon-'+ruleCounter+'" class="control-group">' +
              '<div class="span4">' +
               '   <label class="control-label" for="iconUpload-content-'+ruleCounter+'">Add property icon</label>' +
              '</div>  ' +
              '<div class="span5" id="selectIconInput-'+ruleCounter+'">' +
                  '<input id="iconUpload-content-'+ruleCounter+'" class="span11"  type="text" placeholder="No icon selected" readonly/>' +
                  '<span class="help-inline"></span>' +
              '</div>' +
              '<div id="iconUpload-'+ruleCounter+'" class="fileUpload file-icon span2">' +
                  '<span>Upload</span>' +
                  '<input id="geoadmin-upload-icons-btn-'+ruleCounter+'" name="browseIconFiles" type="file" class="upload" />' +
              '</div>' +
          '</div>'
         );

            $('#geoadmin-property-icon-' + ruleCounter).hide();
            $('#geoadmin-rule-property-fill-' + ruleCounter).on('change', function() {
                var str = (this.id).split("-");
                if (this.value  == "icon") {
                   $('#geoadmin-property-icon-' + str[4]).show();

                }
                else{
                   $("#geoadmin-property-icon-" + str[4]).hide();
                }
            });


            $(document.body).on("change", "#geoadmin-upload-icons-btn-"+ruleCounter, function() {
                var str = (this.id).split("-");
                var filename = this.value;
                var separators = ['/','\\\\'];
                filename = filename.split(new RegExp(separators.join('|'), ''))

                $("#iconUpload-content-" + str[4]).val(filename[filename.length -1]);

            });



           ruleCounter++;
           $('#discard-rule-btn').show();
    });



})();