if($('#sidebar-wrapper').hasClass("open")){
	$('.common-slide').removeClass("postion-f OverscrollFixed");
}

var touch = 'ontouchstart' in document.documentElement?true:false

if(!touch && ($(window).width() < 768)){
	$("#page-content-wrapper").css("position","static");
}

$(window).resize(function() {
	if(!touch && $(window).width() < 768){
		$("#page-content-wrapper").css("position","static");
	}
});

$( document ).ready(function() {
	if ($(window).width() < 768) {
		if(touch){
			$("#sidebar-wrapper").css("width","0px")
			$("#wrapper").css("padding-left","0px")
			$(".container-fluid").css("padding-left","0px")
			$(".container-fluid").css("padding-right","0px")
		}
	}
});

// homepage banner and conntact page banner
if ($(window).width() >= 768) {
	function setHeight() {
		var windowHeight = $(window).height();
		$('#banner, #contact_banner').css('height', windowHeight);
	};
	setHeight();
}
$(window).resize(function() {
	setHeight();
});
