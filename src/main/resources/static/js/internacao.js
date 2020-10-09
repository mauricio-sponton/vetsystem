 
$(document).ready(function () {
	moment.locale('pt-BR');
    var table = $('#table-internacoes').DataTable({
    	searching: true,
    	order: [[ 0, "asc" ]],
    	lengthMenu: [5, 10],
        processing: true,
        serverSide: true,
        responsive: true,
        ajax: {
            url: '/internacoes/datatables/server',
            data: 'data'
        },
        columns: [
        	{data: 'status', render :
        		function(data, type, row){
        		if(row.status == "Ativa"){
        			return '<strong class = "ativa">'+ row.status +'</strong>';
        		}else{
        			return '<strong class = "encerrada">'+ row.status +'</strong>';
        		}
        	}
        },
            {data: 'animal.nome'},
            {data: 'dataEntrada', render:
                function( data ) {
                    return moment(data).format('L');
                }
            },
            {data: 'dataSaida', render:
                function( data, type, row ) {
            		if(row.dataSaida == null){
            			return "Internação em andamento"
            		}
                    return moment(data).format('L');
                }
            },
            {orderable: false, 
             data: 'id',
                "render": function(id) {
                    return '<a class="btn btn-success  btn-sm btn-block" href="/internacoes/editar/'+ 
                    	id +'" role="button"><i class="fas fa-edit"></i></a>';
                }
            },
            {orderable: false,
             data: 'id',
                "render": function(id) {
                    return '<a class="btn btn-danger btn-sm btn-block" href="/internacoes/excluir/'+ 
                    	id +'" role="button" data-toggle="modal" data-target="#confirm-modal"><i class="fas fa-times-circle"></i></a>';
                }               
            },
            {orderable: false,
                data: 'id',
                   "render": function(id) {
                	   return '<a class="btn btn-success btn-sm btn-block" href="/internacoes/visualizar/'+ 
                   	id +'" role="button"><i class="fas fa-eye"></i></a>';
                   }               
               }
        ]
    });
});    
/*$(document).ready(function(){
	//
	var status = $('#status').val();
	if (status == "Ativa") {
		$('.entrada').show();
		$('.saida').hide();
	}
	if (status == "Encerrada") {
		$('.saida').show();
		$('.entrada').hide();
	}
	if(status == ""){
		$('.saida').hide();
		$('.entrada').hide();
	}
	$('#status').on('change', function() {
		var status = $(this).val();
		if (status == "Ativa") {
			$('.entrada').show();
			$('.saida').hide();
		}
		if (status == "Encerrada") {
			$('.saida').show();
			$('.entrada').hide();
		}
		if(status == ""){
			$('.saida').hide();
			$('.entrada').hide();
		}
    })
})
*/
