$(document).ready(function () {
	moment.locale('pt-BR');
    var table = $('#table-clientes').DataTable({
    	searching: true,
    	order: [[ 0, "asc" ]],
    	lengthMenu: [5, 10],
        processing: true,
        serverSide: true,
        responsive: true,
        ajax: {
            url: '/clientes/datatables/server',
            data: 'data'
        },
        "columnDefs":[
        	{"width":"5%", "targets": [3,4,5]}
        ],
        columns: [
            {data: 'nome'},
            {data: 'email'},
            {data: 'telefone'},
            {orderable: false, 
             data: 'id',
                "render": function(id) {
                    return '<a class="btn btn-success  btn-sm btn-block" href="/clientes/editar/'+ 
                    	id +'" role="button"><i class="fas fa-edit"></i></a>';
                }
            },
            {orderable: false,
             data: 'id',
                "render": function(id) {
                    return '<a class="btn btn-danger btn-sm btn-block" href="/clientes/excluir/'+ 
                    	id +'" role="button" data-toggle="modal" data-target="#confirm-modal"><i class="fas fa-trash-alt"></i></a>';
                }               
            },
            {orderable: false,
                data: 'id',
                   "render": function(id) {
                	   return '<a class="btn btn-success btn-sm btn-block btn-view" href="/clientes/visualizar/'+ 
                   	id +'" role="button"><i class="fas fa-glasses"></i></a>';
                   }               
               }
        ]
    });
});    