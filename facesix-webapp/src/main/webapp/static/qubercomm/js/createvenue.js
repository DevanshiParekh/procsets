//  splinechart 1
var  splinechart1 = c3.generate({
	size: {
		height: 290,
	},
	bindto: '#chart_div1',

	padding: {
		top: 10,
		right: 15,
		bottom: 0,
		left: 40,
	},
	data: {
		transition: {
			duration: 20000
		},
		columns: [
		['Tx', 300, 350, 300, 0, 0, 0,200],
		['Rx', 130, 100, 140, 200, 150, 50,300],


		],
		types: {
			Tx: 'area-spline',
			Rx: 'area-spline',


		},
		colors: {
			Tx: '#5cd293',
			Rx: '#1a78dd',

		},

	},
	tooltip: {
		show: false
	},
	point: {
		show: false
	},
	axis: {
		x: {
			type: 'category',
			padding: {left:-0.5,right:-0.5},
			categories: ["Mon","Tue","Wed","Thu","Fri","Sat","Sun"],

		},
		y: {
			padding: {bottom: 0},
			min: 0,
			tick: {
				format: d3.format("s")
			}
		},
	}

});

	 // splinechart 2
	 var  splinechart2 = c3.generate({
	 	size: {
	 		height: 290,
	 	},
	 	bindto: '#chart_div2',

	 	padding: {
	 		top: 10,
	 		right: 15,
	 		bottom: 0,
	 		left: 40,
	 	},
	 	data: {
	 		transition: {
	 			duration: 20000
	 		},
	 		columns: [
	 		['Tx', 300, 350, 300, 0, 0, 0,200],
	 		['Rx', 130, 100, 140, 200, 150, 50,300],


	 		],
	 		types: {
	 			Tx: 'area-spline',
	 			Rx: 'area-spline',


	 		},
	 		colors: {
	 			Tx: '#5cd293',
	 			Rx: '#1a78dd',

	 		},

	 	},
	 	tooltip: {
	 		show: false
	 	},
	 	point: {
	 		show: false
	 	},
	 	axis: {
	 		x: {
	 			type: 'category',
	 			padding: {left:-0.5,right:-0.5},
	 			categories: ["Mon","Tue","Wed","Thu","Fri","Sat","Sun"],

	 		},
	 		y: {
	 			padding: {bottom: 0},
	 			min: 0,
	 			tick: {
	 				format: d3.format("s")
	 			}
	 		},
	 	}

	 });
	 
	 // splinechart 3
	 var  splinechart3 = c3.generate({
	 	size: {
	 		height: 290,
	 	},
	 	bindto: '#chart_div3',

	 	padding: {
	 		top: 10,
	 		right: 15,
	 		bottom: 0,
	 		left: 40,
	 	},
	 	data: {
	 		transition: {
	 			duration: 20000
	 		},
	 		columns: [
	 		['Tx', 300, 350, 300, 0, 0, 0,200],
	 		['Rx', 130, 100, 140, 200, 150, 50,300],


	 		],
	 		types: {
	 			Tx: 'area-spline',
	 			Rx: 'area-spline',


	 		},
	 		colors: {
	 			Tx: '#5cd293',
	 			Rx: '#1a78dd',

	 		},

	 	},
	 	tooltip: {
	 		show: false
	 	},
	 	point: {
	 		show: false
	 	},
	 	axis: {
	 		x: {
	 			type: 'category',
	 			padding: {left:-0.5,right:-0.5},
	 			categories: ["Mon","Tue","Wed","Thu","Fri","Sat","Sun"],

	 		},
	 		y: {
	 			padding: {bottom: 0},
	 			min: 0,
	 			tick: {
	 				format: d3.format("s")
	 			}
	 		},
	 	}

	 });

//circular bar chart
$('#demo-pie-1').circles({
	innerHTML: '',
	showProgress: 1,
	initialPos:3,
	targetPos:3,
	scale: 6,
	rotateBy: 360 / 6,
	speed: 700,
	progPreText: ' ',
	progPostText: '75',
	delayAnimation: 1000,
	onFinishMoving:function(pos){
		console.log('done ',pos);
	}
});

$('#demo-pie-2').circles({
	innerHTML: '',
	showProgress: 1,
	initialPos:3,
	targetPos:3,
	scale: 6,
	rotateBy: 360 / 6,
	speed: 700,
	progPreText: ' ',
	progPostText: '75',
	delayAnimation: 1000,
	onFinishMoving:function(pos){
		console.log('done ',pos);
	}
});