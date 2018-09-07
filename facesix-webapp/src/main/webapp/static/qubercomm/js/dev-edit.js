	console.log("0: we enter");
	var deconfigField = document.getElementById('deconfig');
	var deconfig = deconfigField.value;
	deconfig = deconfig.trim();
	console.log("1:" + deconfig);
	if (deconfig && deconfig != null && deconfig != "") {
		console.log("2:" + deconfig);
		deconfig = JSON.parse(deconfig);
	} else {
		deconfig = {
    			radio2g : [{}],
				interfaces2g : [{}],
				radio5g : [{}],	
				interfaces5g : [{}]
			};
		console.log("3:" + deconfig);
	}
	//console.log("4:" + deconfig);

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
			$ref : "/facesix/template/qubercomm/dev-config-data",
			format : "normal"
		},

		// Seed the form with a starting value
		startval : deconfig

	});

	// Hook up the submit button to log to the console
	document.getElementById('submit').addEventListener('click', function() {
		// Get the value from the editor
		// console.log(editor.getValue());
		$("#deconfig").val(JSON.stringify(editor.getValue()));
		//console.log("new :" + JSON.stringify(editor.getValue()));
	});

	// Hook up the Restore to Default button
	//document.getElementById('restore').addEventListener('click', function() {
	//	editor.setValue(deconfig);
	//});

	// Hook up the validation indicator to update its 
	// status whenever the editor changes
	editor.on('change', function() {});
