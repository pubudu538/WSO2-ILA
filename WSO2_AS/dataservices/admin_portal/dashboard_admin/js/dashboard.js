$(function() {
    var d1 = [
        [0, 10],
        [1, 20],
        [2, 33],
        [3, 24],
        [4, 45],
        [5, 96],
        [6, 47],
        [7, 18],
        [8, 11],
        [9, 13],
        [10, 21]

    ];
    var data = ([{
        label: "Too",
        data: d1,
        lines: {
            show: true,
            fill: true,
            lineWidth:2,
            fillColor: {
                colors: [ "rgba(255,255,255,.1)","rgba(160,220,220,.8)"]
            }
        }
    }
    ]);
    var options = {
        grid: {
            backgroundColor: { colors: [ "#fff", "#fff" ] },
            borderWidth:0,borderColor:"#f0f0f0",
            margin:0,
            minBorderMargin:0,
            labelMargin:20,
            hoverable: true,
            clickable: true
        },
// Tooltip
        tooltip: true,
        tooltipOpts: {
            content: "%s X: %x Y: %y",
            shifts: {
                x: -60,
                y: 25
            },
            defaultTheme: false
        },

        legend: {
            labelBoxBorderColor: "#ccc",
            show: false,
            noColumns: 0
        },
        series: {
            stack: true,
            shadowSize: 0,
            highlightColor: 'rgba(30,120,120,.5)'

        },
        xaxis: {
            tickLength: 0,
            tickDecimals: 0,
            show:true,
            min:2,

            font :{

                style: "normal",


                color: "#666666"
            }
        },
        yaxis: {
            ticks: 3,
            tickDecimals: 0,
            show:true,
            tickColor: "#f0f0f0",
            font :{

                style: "normal",


                color: "#666666"
            }
        },
//        lines: {
//            show: true,
//            fill: true
//
//        },
        points: {
            show: true,
            radius: 2,
            symbol: "circle"
        },
        colors: ["#87cfcb", "#48a9a7"]
    };
    var plot = $.plot($("#daily-visit-chart"), data, options);
});


// Use Morris.Area instead of Morris.Line
Morris.Donut({
    element: 'graph-donut',
    data: [
        {value: 70, label: 'Google', formatted: 'approx 70%' },
        {value: 15, label: 'Facebook', formatted: 'approx. 15%' },
        {value: 10, label: 'Stackoveflow', formatted: 'approx. 10%' },
        {value: 5, label: 'ESPN', formatted: 'at most 5%' }
    ],
    backgroundColor: '#fff',
    labelColor: '#1fb5ac',
    colors: [
        '#E67A77','#D9DD81','#79D1CF','#95D7BB'
    ],
    formatter: function (x, data) { return data.formatted; }
});

// Use Morris.Area instead of Morris.Line
Morris.Donut({
    element: 'graph-donut2',
    data: [
        {value: 70, label: 'Chamika', formatted: 'approx 70%' },
        {value: 15, label: 'Asiri', formatted: 'approx. 15%' },
        {value: 10, label: 'Chamila', formatted: 'approx. 10%' },
        {value: 5, label: 'Tharindu', formatted: 'at most 5%' }
    ],
    backgroundColor: '#fff',
    labelColor: '#1fb5ac',
    colors: [
        '#E67A77','#D9DD81','#79D1CF','#95D7BB'
    ],
    formatter: function (x, data) { return data.formatted; }
});

// Use Morris.Area instead of Morris.Line
Morris.Donut({
    element: 'graph-donut3',
    data: [
        {value: 70, label: '071529500', formatted: 'approx 70%' },
        {value: 15, label: '071894565', formatted: 'approx. 15%' },
        {value: 10, label: '071548965', formatted: 'approx. 10%' },
        {value: 5, label: '072586954', formatted: 'at most 5%' }
    ],
    backgroundColor: '#fff',
    labelColor: '#1fb5ac',
    colors: [
        '#E67A77','#D9DD81','#79D1CF','#95D7BB'
    ],
    formatter: function (x, data) { return data.formatted; }
});

// Use Morris.Area instead of Morris.Line
Morris.Donut({
    element: 'graph-donut4',
    data: [
        {value: 70, label: 'Chamika', formatted: 'approx 70%' },
        {value: 15, label: 'Asiri', formatted: 'approx. 15%' },
        {value: 10, label: 'Chamila', formatted: 'approx. 10%' },
        {value: 5, label: 'Tharindu', formatted: 'at most 5%' }
    ],
    backgroundColor: '#fff',
    labelColor: '#1fb5ac',
    colors: [
        '#E67A77','#D9DD81','#79D1CF','#95D7BB'
    ],
    formatter: function (x, data) { return data.formatted; }
});


$(function() {

    var dataPie = [
        { label: "Samsung",  data: 50},
        { label: "Nokia",  data: 50},
        { label: "Syphony",  data: 100}
    ];
// DONUT
    $.plot($(".sm-pie"), dataPie,
        {
            series: {
                pie: {
                    innerRadius: 0.7,
                    show: true,
                    stroke: {
                        width: 0.1,
                        color: '#ffffff'
                    }
                }

            },

            legend: {
                show: true
            },
            grid: {
                hoverable: true,
                clickable: true
            },

            colors: ["#ffdf7c", "#b2def7", "#efb3e6"]
        });
});







$(function() {
    $('.epie-chart').easyPieChart({
        onStep: function(from, to, percent) {
            $(this.el).find('.percent').text(Math.round(percent));
        },
        barColor: "#f8a20f",
        lineWidth: 5,
        size:80,
        trackColor: "#efefef",
        scaleColor:"#cccccc"

    });

});




/*Slim Scroll*/
$(function () {
    $('.event-list').slimscroll({
        height: '305px',
        wheelStep: 20
    });
    $('.conversation-list').slimscroll({
        height: '360px',
        wheelStep: 35
    });
    $('.to-do-list').slimscroll({
        height: '300px',
        wheelStep: 35
    });

});


/*Calendar*/
$(function () {
    $('.evnt-input').keypress(function (e) {
        var p = e.which;
        var inText = $('.evnt-input').val();
        if (p == 13) {
            if (inText == "") {
                alert('Empty Field');
            } else {
                $('<li>' + inText + '<a href="#" class="event-close"> <i class="ico-close2"></i> </a> </li>').appendTo('.event-list');
            }
            $(this).val('');
            $('.event-list').scrollTo('100%', '100%', {
                easing: 'swing'
            });
            return false;
            e.epreventDefault();
            e.stopPropagation();
        }
    });
});


/*Chat*/
$(function () {
    $('.chat-input').keypress(function (ev) {
        var p = ev.which;
        var chatTime = moment().format("h:mm");
        var chatText = $('.chat-input').val();
        if (p == 13) {
            if (chatText == "") {
                alert('Empty Field');
            } else {
                $('<li class="clearfix"><div class="chat-avatar"><img src="images/chat-user-thumb.png" alt="male"><i>' + chatTime + '</i></div><div class="conversation-text"><div class="ctext-wrap"><i>John Carry</i><p>' + chatText + '</p></div></div></li>').appendTo('.conversation-list');
            }
            $(this).val('');
            $('.conversation-list').scrollTo('100%', '100%', {
                easing: 'swing'
            });
            return false;
            ev.epreventDefault();
            ev.stopPropagation();
        }
    });
    $('.chat-send .btn').click(function(){
        var chatTime = moment().format("h:mm");
        var chatText = $('.chat-input').val();
        if (chatText == "") {
            alert('Empty Field');
            $(".chat-input").focus();
        }
        else
        {
            $('<li class="clearfix"><div class="chat-avatar"><img src="images/chat-user-thumb.png" alt="male"><i>' + chatTime + '</i></div><div class="conversation-text"><div class="ctext-wrap"><i>John Carry</i><p>' + chatText + '</p></div></div></li>').appendTo('.conversation-list');
            $('.chat-input').val('');
            $(".chat-input").focus();
            $('.conversation-list').scrollTo('100%', '100%', {
                easing: 'swing'
            });
        }
    });
});
$(function () {
    $(document).on('click', '.event-close', function () {
        $(this).closest("li").remove();
        return false;
    });
});

/*===Vertical Bar===*/
$(function () {
    "use strict";
    jQuery('.progress-stat-bar li').each(function () {
        jQuery(this).find('.progress-stat-percent').animate({
            height: jQuery(this).attr('data-percent')
        }, 1000);
    });
});

$(function () {
    $('.todo-check label').click(function(){
        $(this).parents('li').children('.todo-title').toggleClass('line-through');
    });

    $(function () {
        $(document).on('click', '.todo-remove', function () {
            $(this).closest("li").remove();
            return false;
        });
    });
});

//custom select box

$(function(){
    $('select.styled').customSelect();
});

$(function()
{

    $('.stat-tab .stat-btn').click(function(){

            $(this).addClass('active');
            $(this).siblings('.btn').removeClass('active');

    });
});

$(function(){
$("#sortable-todo").sortable();
});



$(function () {
// Use Morris.Area instead of Morris.Line
    Morris.Area({
        element: 'graph-area',
        padding: 10,
        behaveLikeLine: true,
        gridEnabled: false,
        gridLineColor: '#dddddd',
        axes: true,
        fillOpacity:.7,
        data: [
            {period: '2010 Q1', iphone: 10, ipad: 10, itouch: 10},
            {period: '2010 Q2', iphone: 1778, ipad: 7294, itouch: 18441},
            {period: '2010 Q3', iphone: 4912, ipad: 12969, itouch: 3501},
            {period: '2010 Q4', iphone: 3767, ipad: 3597, itouch: 5689},
            {period: '2011 Q1', iphone: 6810, ipad: 1914, itouch: 2293},
            {period: '2011 Q2', iphone: 5670, ipad: 4293, itouch: 1881},
            {period: '2011 Q3', iphone: 4820, ipad: 3795, itouch: 1588},
            {period: '2011 Q4', iphone: 25073, ipad: 5967, itouch: 5175},
            {period: '2012 Q1', iphone: 10687, ipad: 34460, itouch: 22028},
            {period: '2012 Q2', iphone: 1000, ipad: 5713, itouch: 1791}


        ],
        lineColors:['#E67A77','#D9DD81','#79D1CF'],
        xkey: 'period',
        ykeys: ['iphone', 'ipad', 'itouch'],
        labels: ['iPhone', 'iPad', 'iPod Touch'],
        pointSize: 0,
        lineWidth: 0,
        hideHover: 'auto'

    });
});

$(function () {
// Use Morris.Area instead of Morris.Line
    Morris.Area({
        element: 'graph-area2',
        padding: 10,
        behaveLikeLine: true,
        gridEnabled: false,
        gridLineColor: '#dddddd',
        axes: true,
        fillOpacity:.7,
        data: [
            {period: '2010 Q1', iphone: 10, ipad: 10, itouch: 10},
            {period: '2010 Q2', iphone: 1778, ipad: 7294, itouch: 18441},
            {period: '2010 Q3', iphone: 4912, ipad: 12969, itouch: 3501},
            {period: '2010 Q4', iphone: 3767, ipad: 3597, itouch: 5689},
            {period: '2011 Q1', iphone: 6810, ipad: 1914, itouch: 2293},
            {period: '2011 Q2', iphone: 5670, ipad: 4293, itouch: 1881},
            {period: '2011 Q3', iphone: 4820, ipad: 3795, itouch: 1588},
            {period: '2011 Q4', iphone: 25073, ipad: 5967, itouch: 5175},
            {period: '2012 Q1', iphone: 10687, ipad: 34460, itouch: 22028},
            {period: '2012 Q2', iphone: 1000, ipad: 5713, itouch: 1791}


        ],
        lineColors:['#E67A77','#D9DD81','#79D1CF'],
        xkey: 'period',
        ykeys: ['iphone', 'ipad', 'itouch'],
        labels: ['iPhone', 'iPad', 'iPod Touch'],
        pointSize: 0,
        lineWidth: 0,
        hideHover: 'auto'

    });
});


/*==Weather==*/
var skycons = new Skycons({"color": "#aec785"});
// on Android, a nasty hack is needed: {"resizeClear": true}
// you can add a canvas by it's ID...
skycons.add("icon1", Skycons.RAIN);
// start animation!
skycons.play();
// you can also halt animation with skycons.pause()
// want to change the icon? no problem:
skycons.set("icon1", Skycons.RAIN);



/*Knob*/
var opts = {
    lines: 12, // The number of lines to draw
    angle: 0, // The length of each line
    lineWidth: 0.48, // The line thickness
    pointer: {
        length: 0.6, // The radius of the inner circle
        strokeWidth: 0.03, // The rotation offset
        color: '#464646' // Fill color
    },
    limitMax: 'true', // If true, the pointer will not go past the end of the gauge
    colorStart: '#fa8564', // Colors
    colorStop: '#fa8564', // just experiment with them
    strokeColor: '#F1F1F1', // to see which ones work best for you
    generateGradient: true
};
var target = document.getElementById('gauge'); // your canvas element
var gauge = new Gauge(target).setOptions(opts); // create sexy gauge!
gauge.maxValue = 100; // set max gauge value
gauge.animationSpeed = 32; // set animation speed (32 is default value)
gauge.set(30); // set actual value
gauge.setTextField(document.getElementById("gauge-textfield"));

