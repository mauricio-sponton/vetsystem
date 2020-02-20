/*$.ajax({
	url: 'piechart',
	success: function(result) {
		var series = [];
		var data = [];
		for(var i =0; i < result.length; i++){
			
			var object ={};
			object.name = result[i].nome;
			
			object.y = 	Object.keys(result[i]).length;
			data.push(object);
		}
		var seriesObject = {
				name: 'Especies',
				colorByPoint: true,
				data: data
		}
		series.push(seriesObject);
		drawPieChart(series);
	}
})
function drawPieChart(series){
	Highcharts.chart('container', {
	    chart: {
	        plotBackgroundColor: null,
	        plotBorderWidth: null,
	        plotShadow: false,
	        type: 'pie'
	    },
	    title: {
	        text: 'Browser market shares in January, 2018'
	    },
	    tooltip: {
	        pointFormat: '{series.name}: <b>{point.y}</b>'
	    },
	    plotOptions: {
	        pie: {
	            allowPointSelect: true,
	            cursor: 'pointer',
	            dataLabels: {
	                enabled: true,
	                format: '<b>{point.name}</b>: {point.y} '
	            }
	        }
	    },
	    series: series
	    
	});
}*/