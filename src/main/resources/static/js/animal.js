$(document).ready(function () {
	moment.locale('pt-BR');
    var table = $('#table-animais').DataTable({
    	"language":{
    		"lengthMenu": "Mostrando _MENU_ registros por páginas",
    		"zeroRecords": "Nenhum registro encontrado",
            "info": "Mostrando página _PAGE_ de _PAGES_",
            "infoEmpty": " ",
            "search": "Pesquise: ",
            "searchPlaceholder": "Nome, Dono, Espécie ou Raça...",
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
        processing: true,
        serverSide: true,
        responsive: true,
        ajax: {
            url: '/pacientes/datatables/server',
            data: 'data'
        },
        "columnDefs": [
            { "width": "10%", "targets": [4,5,6] },
            { "width": "30%", "targets": [0] }
          ],
        columns: [
            {data: 'nome'},
            {data: 'cliente.nome'},
            {data: 'especie.nome'},
            {data: 'raca.nome'},
            {orderable: false, 
             data: 'id',
                "render": function(id) {
                    return '<a class="btn btn-success btn-sm btn-block" href="/pacientes/editar/'+ 
                    	id +'" role="button" ><i class="fas fa-edit"></i></a>';
                }
            },
            {orderable: false,
             data: 'id',
                "render": function(id) {
                    return '<a class="btn btn-danger btn-sm btn-block" href="/pacientes/excluir/'+ 
                    	id +'" role="button" data-toggle="modal" data-target="#confirm-modal"><i class="fas fa-trash-alt"></i></a>';
                }               
            },
            {orderable: false,
                data: 'id',
                   "render": function(id) {
                       return '<a class="btn btn-view btn-sm btn-block" href="/pacientes/visualizar/'+ 
                       	id +'" role="button"><i class="fas fa-glasses"></i></a>';
                   }               
               }
        ]
    });
    
   
});    




