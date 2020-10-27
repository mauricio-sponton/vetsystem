package com.sponton.vetsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sponton.vetsystem.domain.Agendamento;

public interface AgendamentoRepository  extends JpaRepository<Agendamento, Long>{

}
