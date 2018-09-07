	console.log("0: we enter");
	var dconfigField = document.getElementById('dconfig');
	var dconfig = dconfigField.value;
	dconfig = dconfig.trim();
	console.log("1:" + dconfig);
	if (dconfig && dconfig != null && dconfig != "") {
		console.log("2:" + dconfig);
		dconfig = JSON.parse(dconfig);
	} else {
		dconfig = {
    			radio2g : [{}],	
				interfaces2g : [{}],
				radio5g : [{}],	
				interfaces5g : [{}]
			};
		console.log("3:" + dconfig);
	}
	//console.log("4:" + dconfig);

	// Initialize the editor
	var editor = new JSONEditor(document.getElementById('editor_holder'), {
		// Enable fetching schemas via ajax
		ajax : true,
		
		theme: "bootstrap3",
		
		iconlib : 'fontawesome4',
		
		no_additional_properties : true,
		
		disable_array_reorder : true,
		
		remove_empty_properties : true,
		

		// The schema for the editor
		schema : {
			$ref : "/facesix/page/device-config-def",
			format : "normal"
		},

		// Seed the form with a starting value
		startval : dconfig

	});

	// Hook up the submit button to log to the console
	document.getElementById('submit').addEventListener('click', function() {
		// Get the value from the editor
		// console.log(editor.getValue());
		$("#dconfig").val(JSON.stringify(editor.getValue()));
		//console.log("new :" + JSON.stringify(editor.getValue()));
	});

	// Hook up the Restore to Default button
	//document.getElementById('restore').addEventListener('click', function() {
	//	editor.setValue(dconfig);
	//});

	// Hook up the validation indicator to update its 
	// status whenever the editor changes
	editor.on('change', function() {});
