var $elem = $(".panzoom").panzoom({
	$zoomIn: $(".zoom-in"),
	$zoomOut: $(".zoom-out"),
	$zoomRange:$(".zoom-range"),
	$reset: $(".reset"),
	contain:'automatic',
	increment:1,
	minScale:1,
	maxScale:5

});
if ($(window.width) <= 1020) {
	$('.overflow-Hide').css("overflow","hidden");
}
	// fullscreen network map
	$('.full-screen').click(function(e){
		if($(".draggable[ismovable='true']").length)
			return
		e.preventDefault();
		$(".reposition-modal").toggleClass('zIndex');
		$("#submenu-modal").toggleClass('zIndex');
		$('.fullActive').toggleClass('fullS');
		$('#rightPanel').toggleClass('zSide');
		$('#sidebar-wrapper').toggleClass("hide");
		$("#rightPanel").toggleClass('addindex');
	});

	$('.pdf-Option a').click(function(e){
		e.preventDefault();
	})
	//$(".panzoom").panzoom("enable");	
	if ($(window).width() > 1024) {
		if($(".device-notification").length){
			$(".device-notification").niceScroll({
				cursorcolor:"#2496d8",
				cursoropacitymin: 0,
				cursoropacitymax: 1,
				cursorwidth: "4px",
				touchbehavior: true,
				cursorborder: "1px solid #2496d8",
				cursorborderradius: "0px",
				smoothscroll: true,
				preventmultitouchscrolling:false,
			});
		}
	}
