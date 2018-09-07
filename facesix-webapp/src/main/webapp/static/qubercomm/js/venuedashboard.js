var splinechart = c3.generate({
    size: {
        height: 220,
    },
    bindto: '#fd_chart2',

    padding: {
        top: 10,
        right: 25,
        bottom: 0,
        left: 40,
    },
    data: {
        transition: {
            duration: 20000
        },
        columns: [],
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
            padding: {
                left: -0.5,
                right: -0.5,
            },
            categories: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],

        },
        y: {
            padding: {
                bottom: 0
            },
            min: 0,
            tick: {
                format: d3.format("s")
            }
        },
    }


});

// Netflow chart
var Netflow = c3.generate({
    size: {
        height: 220,
    },
    bindto: '#vdChart1',
    padding: {
        top: 10,
        right: 25,
        bottom: 0,
        left: 50,
    },
    data: {
        columns: [
            ['Downtime', 13000, 14500, 13500, 15000, 1550, 1765, 19500],
            ['Total', 8400, 9800, 11800, 14400, 18800, 24800, 3080],
            ['Web', 1000, 12500, 15800, 18400, 20800, 30800, 40800],
            ['Chat', 2400, 1900, 15800, 19400, 24800, 26800, 47800]
        ],
        types: {
            Downtime: 'area-spline',
            Total: 'area-spline',
            Web: 'area-spline',
            Chat: 'area-spline',
        },
        colors: {
            Downtime: '#f36e65',
            Total: '#4fc586',
            Web: '#1a78dd',
            Chat: "#d1cc46"
        },
    },
    tooltip: {
        show: false
    },
    point: {
        show: false
    },
    axis: {
        y: {
            padding: {
                bottom: 0
            },
            min: 0,
            tick: {
                // format: d3.format("s"),
                count: 6
            }
        },
        x: {
            type: 'category',
            padding: {
                left: -0.5,
                right: -0.5,
            },
            categories: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
        }
    }

});

// device chart
var device = c3.generate({
    size: {
        height: 270,
    },
    bindto: '#fd_chart3',
    padding: {
        top: 0,
        right: 15,
        bottom: 0,
        left: 15,
    },
    data: {
        columns: [
            ['IOS', 7],
            ['Mac', 12],
            ['Win', 83],
            ['Android', 83],
            ['Others', 83],
        ],
        colors: {
            IOS: '#f14e5a',
            Mac: '#f1f494',
            Win: '#79d58a',
            Android: '#85d1fb',
            Others: '#c278ed',
        },
        type: 'donut'
    },
    donut: {
        title: "",
        label: {
            threshold: 0.03,
            format: function(value, ratio, id) {
                0;
            }
        },
        width: 40
    },
    tooltip: {
        format: {
            value: function(value, ratio, id) {
                return value + ' (' + d3.format('%')(ratio) + ')';
            }
        }
    },
    axis: {
        x: {
            show: false
        }
    },
    legend: {
        show: true
    }

});