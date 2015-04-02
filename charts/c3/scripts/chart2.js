var reference = [3, 3.2, 3.4, 3.55, 3.7, 3.8, 3.9, 4.0, 4.1, 4.2, 4.3, 4.4, 5.3, 6.2, 6.9, 7.5];

function shiftReferenceData(weight) {
    var values = reference.slice(0);
    for (var i = 0; i < values.length; i++) {
        values[i] = values[i] + weight;
    }
    return values;
}

var weight97Percentile = ['97%'].concat(shiftReferenceData(1));
var weight85Percentile = ['85%'].concat(shiftReferenceData(0.6));
var weight50Percentile = ['50%'].concat(reference);
var weight15Percentile = ['15%'].concat(shiftReferenceData(-0.5));
var weight3Percentile = ['3%'].concat(shiftReferenceData(-1.1));

var chart2Data = [
    ['weeks', 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 24, 36, 48, 60],
    ['patient_weight', 2.8, 2.9, 2.9, 2.8, 2.9, 3, 3, 3.1, 3.1, 3.2, 3.3, 3.3, 4.3, 5.2, 6, 7.2],
    weight97Percentile,
    weight85Percentile,
    weight50Percentile,
    weight15Percentile,
    weight3Percentile

];

var chart2 = c3.generate({
    bindto: '#chart2',
    size: {
        width: 650,
        height: 500
    },
    data: {
        xs: {
            '97%': 'weeks',
            '85%': 'weeks',
            '50%': 'weeks',
            '15%': 'weeks',
            '3%': 'weeks',
            'patient_weight': 'weeks'
        },
        columns: chart2Data,
        colors: {
            '97%': 'rgba(173, 18, 23, 0.20)',
            '85%': 'rgba(153, 86, 19, 0.20)',
            '50%': 'rgba(0, 153, 0, 0.20)',
            '15%': 'rgba(153, 86, 19, 0.20)',
            '3%': 'rgba(173, 18, 23, 0.20)',
            'patient_weight': 'blue'
        }
    },
    point: {
        show: true,
        r: 1
    },
    axis: {
        x: {
            label: {
                text: "Weeks",
                position: "outer-right"
            }
        },
        y: {
            label: {
                text: "Weight (Kgs)",
                position: "outer-middle"
            }
        }
    },
    tooltip: {
        position: function () {
            return {top: 10, left: 80};
        },
        format: {
            title: function(d) { return d + " months"},
            //name: function(name, ratio, id, index) { return name + ' Patient' },
            value: function(value, ration, id, index) { return Number(value).toFixed(2)}
        }
    },
    legend: {
        show: true,
        position: 'inset',
        inset: {
            anchor: 'top-right',
            step: 1
        }
    },
    zoom: {
        enabled: true,
        extent: [1,50]
    }
});

chart2.legend.hide('patient_weight');
//chart2.legend.hide();