$(document).ready(function () {
	moment.locale('pt-BR');
    var table = $('#table-consultas').DataTable({
    	"language":{
    		"lengthMenu": "Mostrando _MENU_ registros por páginas",
    		"zeroRecords": "Nenhum registro encontrado",
            "info": "Mostrando página _PAGE_ de _PAGES_",
            "infoEmpty": " ",
            "search": "Pesquise: ",
            "paginate": {
                "first":      "Primeiro",
                "last":       "Ultimo",
                "next":       "Próximo",
                "previous":   "Anterior"
            } 
    	},
    	searching: true,
    	order: [[ 0, "asc" ]],
    	lengthMenu: [5, 10],
        processing: false,
        serverSide: true,
        responsive: true,
        ajax: {
            url: '/consultas/datatables/server',
            data: 'data'
        },
        columns: [
        	{data: 'id'},
        	{data: 'data', render:
                function( data ) {
                    return moment(data).format('L');
                }
            },
            {data: 'hora'},
            {data: 'animal.nome'},
            {orderable: false, 
             data: 'id',
                "render": function(id) {
                    return '<a class="btn btn-success btn-sm btn-block" href="/consultas/editar/'+ 
                    	id +'" role="button"><i class="fas fa-edit"></i></a>';
                }
            },
            {orderable: false,
             data: 'id',
                "render": function(id) {
                    return '<a class="btn btn-danger btn-sm btn-block" href="/consultas/excluir/'+ 
                    	id +'" role="button" data-toggle="modal" data-target="#confirm-modal"><i class="fas fa-times-circle"></i></a>';
                }               
            },
            {orderable: false,
                data: 'id',
                   "render": function(id) {
                       return '<a class="btn btn-danger btn-sm btn-block" href="/consultas/visualizar/'+ 
                       	id +'" role="button"><i class="fas fa-eye"></i></a>';
                   }               
               }
        ]
    });
});  

$('#animal').on('change', function() {
	$('#alergias').text("");
	var termo = $(this).val(); 
	if ( termo != '' ) {			
		$.get( "/pacientes/titulo/" + termo , function( result ) {
			$("#alergias").show().animate({height: "50px", opacity: "0.5"}, 100);
				$('#alergias').append('<span>'+ result +'</span>');
			
		})
	}
	if(termo == ""){
		$('#alergias').text("");
	} 
})