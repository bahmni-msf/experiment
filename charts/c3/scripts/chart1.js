var chart1Data = {
    glucose: [6.5, 5.5, 7, 4, 5.5, 5],
    data: [
        ['wbc_date', new Date(2014, 10, 1), new Date(2014, 10, 15), new Date(2014, 11, 31), new Date(2015, 0, 4), new Date(2015, 0, 12), new Date(2015, 2, 12)],
        ['hct_date', new Date(2014, 10, 5), new Date(2014, 10, 12), new Date(2014, 11, 24), new Date(2015, 0, 4), new Date(2015, 1, 12), new Date(2015, 2, 15)],
        ['wbc', 5.5, 6.0, 5.6, 5.4, 3.5, 1.5],
        ['hct', 4.8, 5.0, 4.3, 3.4, 0.5, 0.3]
        //['high', 10],
        //['normal', 4],
        //['low', 2],
    ]
}

var chart1 = c3.generate({
    bindto: '#chart1',
    size: {
        width: 650,
        height: 500
    },
    data: {
        xs: {
            'wbc': 'wbc_date',
            'hct': 'hct_date'
            //'high': 'wbc_date',
            //'normal': 'wbc_date',
            //'low': 'wbc_date'
        },
        columns: chart1Data.data,
        colors: {
            wbc: 'blue',
            hct: 'green',
            high: 'rgba(255,0,0,0)',
            normal: 'rgba(255,0,0,0)',
            low: 'rgba(255,0,0,0)'
        }
    },
    point: {
        show: true,
        r: 6
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
                text: 'Value x 109 cells per liter',
                position: 'outer-top'
            }
//                padding: {top: 10, bottom: 0}
        }
    },
    line: {
        connectNull: true
    },
    grid: {
        y: {
            lines: [
                {value: 10, text: 'High'},
                {value: 4, text: 'Normal'},
                {value: 2, text: 'Low'}
            ]
        }
    },
    tooltip: {
        format: {
            title: function (d) {
                var idx = chart1Data.data[0].indexOf(d);
                return (idx != -1) ? "Glucose: " + chart1Data.glucose[idx - 1] : "";
            }
        }
    },
    zoom: {
        enabled: true,
        extent: [1,50]
    }
});

chart1.legend.hide();
chart1.legend.show(['wbc','hct']);
