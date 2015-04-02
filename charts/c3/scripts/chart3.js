var chart3Data = [
    ['Date', new Date(2014, 6, 1), new Date(2014, 8, 15), new Date(2014, 10, 30), new Date(2014, 12, 4), new Date(2015, 1, 12), new Date(2015, 4, 12)],
    ['B.P.', 100, 110, 120, 122, 100, 97],
    ['Pulse', 80, 82, 85, 83, 83, 81]
];

var chart3 = c3.generate({
    bindto: '#chart3',
    size: {
        width: 650,
        height: 500
    },
    data: {
        columns: chart3Data,
        axes: {
            'B.P.': 'y',
            'Pulse' : 'y2'
        },
        xs: {
            'B.P.': 'Date',
            'Pulse': 'Date'
        }
        //colors: {
        //    wbc: 'blue',
        //    hct: 'green'
        //}
    },
    point: {
        r: 4
    },
    axis: {
        x: {
            label: {
                text: 'Date',
                position: 'outer-right'
            },
            type: 'timeseries',
            tick: {
                culling: {
                    max: 3
                },
                format: function (x) {
                    return x.getDay() + "-" + (x.getMonth() + 1) + "-" + x.getFullYear();
                }
            }
        },

        y: {
            label: {
                text: 'Blood Pressure',
                position: 'outer-top'
            }
//                padding: {top: 10, bottom: 0}
        },

        y2: {
            show: true,
            label: {
                text: 'Pulse',
                position: 'outer-top'
            }
        }
    },
    zoom: {
        enabled: true
    }
    //grid: {
    //    y: {
    //        lines: [
    //            {value: 10, text: 'High'},
    //            {value: 4, text: 'Normal'},
    //            {value: 2, text: 'Low'}
    //        ]
    //    }
    //},
    //tooltip: {
    //    format: {
    //        title: function (d) {
    //            var idx = chart1Data.data[0].indexOf(d);
    //            console.log(idx);
    //            return (idx != -1) ? "Glucose: " + chart1Data.glucose[idx - 1] : "";
    //        }
    //    }
    //}
});