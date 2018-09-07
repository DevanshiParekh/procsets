// jQuery.validator.setDefaults({
//     debug: true,
//     success: "valid"
// });

$("#save").on("click", function(e){
     $('#ac-settings').submit(); 
});
$("#resetForm").on("click",function(){
        $("#AddfloorForm #floorimg").val("")
        $(".addfloor-option").hide();
        $('#uploadedimg').hide();
        $(".upload-notes").show()
        $(".UploadFloorText").text("")
        $("#floorimg").rules('add',{required:true})
        $(".cropper-container").hide()
    })

$("#upload-edit").on("click", function(e){
    $("#user_pic").click();
});


$("#user_pic").on("dragover", function(event) {
    var file = document.querySelector("#user_pic").files[0];
    if (this.files && this.files[0]) {
        var reader = new FileReader();

        reader.onload = function(e) {
            $('#upload-drag').attr('src', e.target.result);
        }

        reader.readAsDataURL(this.files[0]);
        $(".as-right h5").text(file.name).css({"color":"black"})
    }
});
// account settings
$("#user_pic").on("change", function(event) {
    if (this.files && this.files[0]) {
    var file = document.querySelector("#user_pic").files[0];
        var reader = new FileReader();
        var $this=$(this);
        reader.onload = function(e) {
            $('#upload-drag').attr('src', e.target.result);
            $this.addClass("valid").removeClass("error");
        }

        reader.readAsDataURL(this.files[0]);
        $(".as-right h5").text(file.name).css({"color":"black"})
    }
});
// account settings
$("#ac-settings").validate({
    rules: {
        fname: {
            required:   true
        },
        lname: {
            required:   true
        },
         group: {
            required:   true  
        },
        description: {
            required:   true            
        },
        email: {
            required: true,
            email:  true
        },
        phone: {
            required: true,
            minlength: 10,
            maxlength: 10,
            number: true
        },
        password: {
            required:   true,
            minlength:  6
        },
        c_password: {
            required: true,
            equalTo: "#password"
        },
        user_pic: {
          required: true,
          accept: "image/*"
      }
  },
  messages:{
    user_pic: {
        required: 'Please Upload a Image', 
        accept: 'Not an image!'
    }
},errorPlacement: function (error, element) {
  if($("#user_pic").hasClass("error")){
         $(".as-right h5").text("Please Upload an Image").css({"color":"red"})
         //$("#uploadPic h5").text("Please Upload a Image")
    }

},submitHandler: function(form) {
    $('#ac-settings input').each(function(){
    console.log($(this).val()=='',$(this).val())
     if($(this).val()==''){
        return;
     }
    })
    form.submit();

    }
});// account settings
$("#loginForm").validate({
    rules: {
      u:{
        required:false
      },
      p:{
        required:false
      }   
  },submitHandler: function(form) {
	$('#loginForm input').each(function(){
	console.log($(this).val()=='',$(this).val())
	 if($(this).val()==''){
	 	return;
	 }
	})
    form.submit();

    }
});
// createvenue
$("#cancelVenue").on("click", function(e){
    $("#createVenue label").addClass("hide");
});

// $("#venueSubmit").on("click", function(e){
//  $("#createVenue label").removeClass("hide");
//  $("#createVenue").submit(); 
// });

$("#createVenue").validate({
    rules: {
        uid: {
            required:   true
        },
        name: {
            required:   true
        },
        description: {
            required:   true  
        }
    },
    messages:{
        venueTitle: "Please Enter Venue Title",
        venueAddress: "Please Enter Venue Address",
        venueDesc:  "Please Enter Venue Description",
    },

    submitHandler: function(form) {
        form.submit();
    }
});

//config
$("#configform").on("click", function(e){ 
    $('#configform').submit(); 
    
});
$("#configform").validate({
    rules: {
        uuid: {
            required:   true
        },
        alias: {
            required:   true
        }
    },
    submitHandler: function(form) {
        form.submit();
    }
});

// contact us

$("#contactusSend").on("click", function(e){
 $("#contactusForm").submit(); 
});

$("#contactusForm").validate({
    rules: {
        name: {
            required:   true
        },
        email: {
            required: true,
            email:  true
        },
        pno: {
            required: true,
            minlength: 10,
            maxlength: 10,
            number: true
        },
        Cdesc: {
            required:   false  
        }
    },

    submitHandler: function(form) {
        var data={
            'name': $('#email').val(),  
            'email': $('#venueAddress').val(),
            'pno': $('#pno').val(),
            'Cdesc': $('#Cdesc').val(),
        };
        // var data = {};
        $("#contactusForm").serializeArray().map(function(x){data[x.name] = x.value;});
        console.log(data)
        form.submit();
    }
});


// addfloor

// drag function
$("#user_pic").on("dragover", function(event) {
    var file = document.querySelector("#floorimg").files[0];
    var name = file.name;
    var form_obj = new FormData();
    form_obj.append('user_image', file);
    form_obj.append('image_name', name);
    console.log(name);
    file_name=name;
    $("#floorimg-error").addClass("hide");
    $(".UploadFloorText").text(file_name)
});

$("#floorimg").on("change", function(e){
    var file = document.querySelector("#floorimg").files[0];
    var name = file.name;
    var form_obj = new FormData();
    form_obj.append('user_image', file);
    form_obj.append('image_name', name);
    console.log(name);
    file_name=name;
    $("#floorimg-error").addClass("hide");
    $(".UploadFloorText").text(file_name).css({"color":"black"})
});
$("#SavePlan").on("click", function(e){
    $('#AddfloorForm').submit(); 
});
$("#AddfloorForm").validate({
    rules: {
        uid: {
            required: true
        },
        description: {
            required: true
        },
        file: {
            required: $("#floorimg").attr("data-edit")=="true"?false:true,
        
        }
    },
    messages: {
        uid: "Please Enter FloorPlan Title",
        description: "Please Enter FloorPlan Description",
        file: {
            required: 'Please Upload a Image',
            accept: 'Not an image!'
        }        

    },
    submitHandler: function(form) {

        form.submit()

    },
   errorPlacement: function (error, element) {
        if($("#floorimg").hasClass("error")){
          $(".UploadFloorText").text("Please Upload an Image").css({"color":"red"})
         //$("#uploadPic h5").text("Please Upload a Image")
    }
    }
});



// Qubercast  settings

$("#quber-settings").on("click", function(e){ 
    $('#quber-settings').submit(); 
    
});



$("#quber-settings").validate({
    rules: {
        mediaPath: {
            required:   true
        },multicastPort: {
            required:   true
        },mulicastAddress: {
            required:   true
        },logFile: {
           required:   true,
           number: true            
        },logLevel: {
            required: true,
            number: true
        }
  },submitHandler: function(form) {
        var data={
            'mediaPath': $('#mediaPath').val(),  
            'multicastPort': $('#multicastPort').val(),
            'mulicastAddress': $('#mulicastAddress').val(),
            'logFile': $('#logFile').val(),
            'logLevel': $('#logLevel').val()
        };
        $("#quber-settings").serializeArray().map(function(x){data[x.name] = x.value;});
        console.log(data)
        form.submit();
    }

});
