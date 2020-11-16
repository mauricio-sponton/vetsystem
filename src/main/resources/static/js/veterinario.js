$(document).ready(function () {
	moment.locale('pt-BR');
    var table = $('#table-veterinarios').DataTable({
    	searching: true,
    	order: [[ 0, "asc" ]],
    	lengthMenu: [10, 10],
        processing: true,
        serverSide: true,
        responsive: true,
        ajax: {
            url: '/veterinarios/datatables/server',
            data: 'data'
        },
        columns: [
        	 {
                 "className":'details-control',
                 "orderable":false,
                 "data": null,
                 "defaultContent":  '<i class="fa fa-arrow-down" style="color:indigo"></i>'
             },
            {data: 'nome'},
            {data: 'usuario.email'}
            
        ]
    });
    $('#table-veterinarios tbody').on('click', 'td.details-control', function () {

        var tr = $(this).closest('tr');
        var row = table.row( tr );
        if ( row.child.isShown() ) {
            // This row is already open - close it
        	 $('div.slider', row.child()).slideUp( function () {
        	        row.child.hide();
                    tr.removeClass('shown');
             
        	 })
         
        }
        else {
            // Open this row
        	if ( table.row( '.shown' ).length ) {
                $('.details-control', table.row( '.shown').node()).click();
               // $('div.slider', row.child()).slideUp();
        	}
            row.child( format(row.data()), 'no-padding').show();
            tr.addClass('shown');
            $('div.slider', row.child()).slideDown();
           
        }
    } );
});  

function format ( d ) {
    // `d` is the original data object for the row
	$.get("/veterinarios/horarios/" + d.id, function(horarios){
		$.each(horarios, function (k, v){
			switch(v.diaDaSemana){
			case 1:
				v.diaDaSemana = "Domingo"
			break;
			case 2:
				v.diaDaSemana = "Segunda"
			break;
			case 3:
				v.diaDaSemana = "Terça"
			break;
			case 4:
				v.diaDaSemana = "Quarta"
			break;
			case 5:
				v.diaDaSemana = "Quinta"
			break;
			case 6:
				v.diaDaSemana = "Sexta"
			break;
			case 7:
				v.diaDaSemana = "Sábado"
			break;
			}
			if(v.inicio == null){
				$(".dentro").append(
						'<tr><td scope="row"><span class="dias">' +v.diaDaSemana+'</span></td>' +
						'<td><span class="inicio">Folga</span></td>' +
						'<td><span>Folga</span></td></tr>'
						
					).hide().fadeIn('slow')		
			}else{
				$(".dentro").append(
						'<tr><td scope="row"><span class="dias">' +v.diaDaSemana+'</span></td>' +
						'<td><span class="inicio">'+ moment(v.inicio, 'HH:mm:ss').format('HH:mm') +'</span></td>' +
						'<td><span>'+ moment(v.fim, 'HH:mm:ss').format('HH:mm') +'</span></td></tr>'
						
					).hide().fadeIn('slow')	
			}
					
		})
	});
		
	
    return '<div class="slider">'+
    '<table class="table table-dark dentro" style="margin:0;padding-left:50px; width:100%" cellspacing="0" border="0">'+
        '<tr id="tr-dentro">'+
            '<td>Telefone:</td>'+
            '<td>'+ d.telefone +'</td>'+
        '</tr>'+
    '</table>' +
    '</div>';
}