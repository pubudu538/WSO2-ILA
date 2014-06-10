// FLAT Theme v2.0
(function( $ ){
	$.fn.retina = function(retina_part) {
		// Set default retina file part to '-2x'
		// Eg. some_image.jpg will become some_image-2x.jpg
		var settings = {'retina_part': '-2x'};
		if(retina_part) jQuery.extend(settings, { 'retina_part': retina_part });
		if(window.devicePixelRatio >= 2) {
			this.each(function(index, element) {
				if(!$(element).attr('src')) return;

				var checkForRetina = new RegExp("(.+)("+settings['retina_part']+"\\.\\w{3,4})");
				if(checkForRetina.test($(element).attr('src'))) return;

				var new_image_src = $(element).attr('src').replace(/(.+)(\.\w{3,4})$/, "$1"+ settings['retina_part'] +"$2");
				$.ajax({url: new_image_src, type: "HEAD", success: function() {
					$(element).attr('src', new_image_src);
				}});
			});
		}
		return this;
	}
})( jQuery );
function icheck(){
	var j = jQuery.noConflict();
	if(j(".icheck-me").length > 0){
		j(".icheck-me").each(function(){
			var $el = j(this);
			var skin = ($el.attr('data-skin') !== undefined) ? "_"+$el.attr('data-skin') : "",
			color = ($el.attr('data-color') !== undefined) ? "-"+$el.attr('data-color') : "";

			var opt = {
				checkboxClass: 'icheckbox' + skin + color,
				radioClass: 'iradio' + skin + color,
				increaseArea: "10%"
			}

			$el.iCheck(opt);
		});
	}
}

var k = jQuery.noConflict();

k(document).ready(function() {
	var mobile = false,
	tooltipOnlyForDesktop = true,
	notifyActivatedSelector = 'button-active';

	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) ) {
		mobile = true;
	}

	icheck();

	

	if(k(".complexify-me").length > 0){
		k(".complexify-me").complexify(function(valid, complexity){
			if(complexity < 40){
				k(this).parent().find(".progress .bar").removeClass("bar-green").addClass("bar-red");
			} else {
				k(this).parent().find(".progress .bar").addClass("bar-green").removeClass("bar-red");
			}

			k(this).parent().find(".progress .bar").width(Math.floor(complexity)+"%").html(Math.floor(complexity)+"%");
		});
	}

	// Round charts (easypie)



	// Calendar (fullcalendar)
	if(k('.calendar').length > 0)
	{
		k('.calendar').fullCalendar({
			header: {
				left: '',
				center: 'prev,title,next',
				right: 'month,agendaWeek,agendaDay,today'
			},
			buttonText:{
				today:'Today'
			},
			editable: true
		});
		k(".fc-button-effect").remove();
		k(".fc-button-next .fc-button-content").html("<i class='icon-chevron-right'></i>");
		k(".fc-button-prev .fc-button-content").html("<i class='icon-chevron-left'></i>");
		k(".fc-button-today").addClass('fc-corner-right');
		k(".fc-button-prev").addClass('fc-corner-left');
	}

	// Tooltips (only for desktop) (bootstrap tooltips)
	if(tooltipOnlyForDesktop)
	{
		if(!mobile)
		{
			k('[rel=tooltip]').tooltip();
		}
	}
	


	// datepicker
	if(k('.datepick').length > 0){
		k('.datepick').datepicker();
	}

	// timepicker
	if(k('.timepick').length > 0){
		k('.timepick').timepicker({
			defaultTime: 'current',
			minuteStep: 1,
			disableFocus: true,
			template: 'dropdown'
		});
	}
	// colorpicker
	if(k('.colorpick').length > 0){
		k('.colorpick').colorpicker();	
	}
	// uniform
	if(k('.uniform-me').length > 0){
		k('.uniform-me').uniform({
			radioClass : 'uni-radio',
			buttonClass : 'uni-button'
		});
	}
	// Chosen (chosen)
	if(k('.chosen-select').length > 0)
	{
		k('.chosen-select').each(function(){
			var $el = k(this);
			var search = ($el.attr("data-nosearch") === "true") ? true : false,
			opt = {};
			if(search) opt.disable_search_threshold = 9999999;
			$el.chosen(opt);
		});
	}

	if(k(".select2-me").length > 0){
		k(".select2-me").select2();
	}

	// multi-select
	if(k('.multiselect').length > 0)
	{
		k(".multiselect").each(function(){
			var $el = k(this);
			var selectableHeader = $el.attr('data-selectableheader'),
			selectionHeader  = $el.attr('data-selectionheader');
			if(selectableHeader != undefined)
			{
				selectableHeader = "<div class='multi-custom-header'>"+selectableHeader+"</div>";
			}
			if(selectionHeader != undefined)
			{
				selectionHeader = "<div class='multi-custom-header'>"+selectionHeader+"</div>";	
			}
			$el.multiSelect({
				selectionHeader : selectionHeader,
				selectableHeader : selectableHeader
			});
		});
	}

	// spinner
	if(k('.spinner').length > 0){
		k('.spinner').spinner();
	}

	// dynatree
	if(k(".filetree").length > 0){
		k(".filetree").each(function(){
			var $el = k(this),
			opt = {};
			opt.debugLevel = 0;
			if($el.hasClass("filetree-callbacks")){
				opt.onActivate = function(node){
					k(".activeFolder").text(node.data.title);
					k(".additionalInformation").html("<ul style='margin-bottom:0;'><li>Key: "+node.data.key+"</li><li>is folder: "+node.data.isFolder+"</li></ul>");
				};
			}
			if($el.hasClass("filetree-checkboxes")){
				opt.checkbox = true;

				opt.onSelect = function(select, node){
					var selNodes = node.tree.getSelectedNodes();
					var selKeys = k.map(selNodes, function(node){
						return "[" + node.data.key + "]: '" + node.data.title + "'";
					});
					k(".checkboxSelect").text(selKeys.join(", "));
				};
			}

			$el.dynatree(opt);
		});
	}

	if(k(".colorbox-image").length > 0){
		k(".colorbox-image").colorbox({
			maxWidth: "90%",
			maxHeight: "90%",
			rel: k(this).attr("rel")
		});
	}

	// PlUpload
	if(k('.plupload').length > 0){
		k(".plupload").each(function(){
			var $el = k(this);
			$el.pluploadQueue({
				runtimes : 'html5,gears,flash,silverlight,browserplus',
				url : 'js/plupload/upload.php',
				max_file_size : '10mb',
				chunk_size : '1mb',
				unique_names : true,
				resize : {width : 320, height : 240, quality : 90},
				filters : [
				{title : "Image files", extensions : "jpg,gif,png"},
				{title : "Zip files", extensions : "zip"}
				],
				flash_swf_url : 'js/plupload/plupload.flash.swf',
				silverlight_xap_url : 'js/plupload/plupload.silverlight.xap'
			});
			k(".plupload_header").remove();
			var upload = $el.pluploadQueue();
			if($el.hasClass("pl-sidebar")){
				k(".plupload_filelist_header,.plupload_progress_bar,.plupload_start").remove();
				k(".plupload_droptext").html("<span>Drop files to upload</span>");
				k(".plupload_progress").remove();
				k(".plupload_add").text("Or click here...");
				upload.bind('FilesAdded', function(up, files) {
					setTimeout(function () { 
						up.start(); 
					}, 500);
				});
				upload.bind("QueueChanged", function(up){
					k(".plupload_droptext").html("<span>Drop files to upload</span>");
				});
				upload.bind("StateChanged", function(up){
					k(".plupload_upload_status").remove();
					k(".plupload_buttons").show();
				});
			} else {
				k(".plupload_progress_container").addClass("progress").addClass('progress-striped');
				k(".plupload_progress_bar").addClass("bar");
				k(".plupload_button").each(function(){
					if(k(this).hasClass("plupload_add")){
						k(this).attr("class", 'btn pl_add btn-primary').html("<i class='icon-plus-sign'></i> "+k(this).html());
					} else {
						k(this).attr("class", 'btn pl_start btn-success').html("<i class='icon-cloud-upload'></i> "+k(this).html());
					}
				});
			}
		});
}

	// Wizard
	if(k(".form-wizard").length > 0){
		k(".form-wizard").formwizard({ 
			formPluginEnabled: true,
			validationEnabled: true,
			focusFirstInput : false,
			disableUIStyles:true,
			validationOptions: {
				errorElement:'span',
				errorClass: 'help-block error',
				errorPlacement:function(error, element){
					element.parents('.controls').append(error);
				},
				highlight: function(label) {
					k(label).closest('.control-group').removeClass('error success').addClass('error');
				},
				success: function(label) {
					label.addClass('valid').closest('.control-group').removeClass('error success').addClass('success');
				}
			},
			formOptions :{
				success: function(data){
					alert("Response: \n\n"+data.say);
				},
				dataType: 'json',
				resetForm: true
			}	
		});
	}

	// Validation
	if(k('.form-validate').length > 0)
	{
		k('.form-validate').each(function(){
			var id = k(this).attr('id');
			k("#"+id).validate({
				errorElement:'span',
				errorClass: 'help-block error',
				errorPlacement:function(error, element){
					element.parents('.controls').append(error);
				},
				highlight: function(label) {
					k(label).closest('.control-group').removeClass('error success').addClass('error');
				},
				success: function(label) {
					label.addClass('valid').closest('.control-group').removeClass('error success').addClass('success');
				}
			});
		});
	}


	// force correct width for chosen
	resize_chosen();

	// file_management
	if(k('.file-manager').length > 0)
	{
		k('.file-manager').elfinder({
			url:'js/plugins/elfinder/php/connector.php'
		});
	}

	// slider
	if(k('.slider').length > 0)
	{
		k(".slider").each(function(){
			var $el = $(this);
			var min = parseInt($el.attr('data-min')),
			max = parseInt($el.attr('data-max')),
			step = parseInt($el.attr('data-step')),
			range = $el.attr('data-range'),
			rangestart = parseInt($el.attr('data-rangestart')),
			rangestop = parseInt($el.attr('data-rangestop'));

			var opt = {
				min: min,
				max: max,
				step: step,
				slide: function( event, ui ) {
					$el.find('.amount').html( ui.value );
				}
			};

			if(range !== undefined)
			{
				opt.range = true;
				opt.values = [rangestart, rangestop];
				opt.slide = function( event, ui ) {
					$el.find('.amount').html( ui.values[0]+" - "+ui.values[1] );
					$el.find(".amount_min").html(ui.values[0]+"k");
					$el.find(".amount_max").html(ui.values[1]+"k");
				};
			}

			$el.slider(opt);
			if(range !== undefined){
				var val = $el.slider('values');
				$el.find('.amount').html(val[0] + ' - ' + val[1]);
				$el.find(".amount_min").html(val[0]+"k");
				$el.find(".amount_max").html(val[1]+"k");
			} else {
				$el.find('.amount').html($el.slider('value'));
			}
		});
}

if(k(".ckeditor").length > 0){
	CKEDITOR.replace("ck");
}

k(".retina-ready").retina("@2x");

});

k(window).resize(function() {
	// chosen resize bug
	resize_chosen();
});

function resize_chosen(){
	k('.chzn-container').each(function() {
		var $el = k(this);
		$el.css('width', $el.parent().width()+'px');
		$el.find(".chzn-drop").css('width', ($el.parent().width()-2)+'px');
		$el.find(".chzn-search input").css('width', ($el.parent().width()-37)+'px');
	});
}


