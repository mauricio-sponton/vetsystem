package com.sponton.vetsystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long>{

	@Query("select n from Notificacao n join n.secretarias s where s.id =:id order by n.id DESC")
	List<Notificacao> findNotificacaoBySecretariaId(Long id);

	@Query("select n from Notificacao n join n.veterinarios v where v.id =:id order by n.id DESC")
	List<Notificacao> findNotificacaoPorVeterinarioId(Long id);
	/*
	@Query("select n from Notificacao n " +
				"join n.veterinarios v "   +
				"join n.agendamentos a "   +
				"where v.id =:idVet and a.id =:idAgendamento")
	Notificacao buscar(Long idAgendamento, Long idVet);

	@Query("select n from Notificacao n join n.veterinarios v where v.id =:id and n.data = :hoje order by n.id DESC")
	List<Notificacao> findNotificacaoByVeterinarioIdAndData(Long id, LocalDate hoje);

	*/
}
