package com.sponton.vetsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sponton.vetsystem.domain.HistoricoAnimal;
import com.sponton.vetsystem.service.HistoricoAnimalService;

@Controller
@RequestMapping("/historico")
public class HistoricoAnimalController {

	@Autowired
	HistoricoAnimalService service;
	
	
}
