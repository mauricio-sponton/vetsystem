$(document).ready(function() {
	moment.locale('pt-BR');
	var table = $('#table-usuarios').DataTable({
		searching : true,
		lengthMenu : [ 5, 10 ],
		processing : true,
		serverSide : true,
		responsive : true,
		ajax : {
			url : '/u/datatables/server/usuarios',
			data : 'data'
		},
		"columnDefs":[
        	{"width":"5%", "targets": [3,4,5]}
        ],
		columns : [
				{data : 'email'},
				{	data : 'ativo', 
					render : function(ativo) {
						return ativo == true ? 'Sim' : 'Não';
					}
				},
				{	data : 'perfis',									 
					render : function(perfis) {
						var aux = new Array();
						$.each(perfis, function( index, value ) {
							  aux.push(value.desc);
						});
						return aux;
					},
					orderable : false,
				},
				{	data : 'id',	
					render : function(id) {
						return ''.concat('<a class="btn btn-success btn-sm btn-block"', ' ')
								 .concat('href="').concat('/u/editar/credenciais/usuario/').concat(id, '"', ' ') 
								 .concat('role="button" title="Editar" data-toggle="tooltip" data-placement="right">', ' ')
								 .concat('<i class="fas fa-edit"></i></a>');
					},
					orderable : false
				},
				{	data : 'id',	
					render : function(id) {
						return ''.concat('<a class="btn btn-info btn-sm btn-block btn-view"', ' ') 
								 .concat('id="dp_').concat(id).concat('"', ' ') 
								 .concat('role="button" title="Visualizar" data-toggle="tooltip" data-placement="right">', ' ')
								 .concat('<i class="fas fa-glasses"></i></a>');
					},
					orderable : false
				},
				 {orderable: false,
		             data: 'id',
		                "render": function(id) {
		                    return '<a class="btn btn-danger btn-sm btn-block" href="/u/excluir/'+ 
		                    	id +'" role="button" data-toggle="modal" data-target="#confirm-modal"><i class="fas fa-trash-alt"></i></a>';
		                }               
		            }
		            
			
		]
	});
	
	$('#table-usuarios tbody').on('click', '[id*="dp_"]', function(){
		var data = table.row($(this).parents('tr')).data();
		var aux = new Array();
		$.each(data.perfis, function(index, value){
			aux.push(value.id);
		});
		document.location.href = '/u/visualizar/dados/usuario/' + data.id + '/perfis/' + aux;
	});
});	

$('.pass').keyup(function(){
	$('#senha1').val() === $('#senha2').val()
		? $('#senha3').removeAttr('readonly')
		: $('#senha3').attr('readonly', 'readonly');
});
