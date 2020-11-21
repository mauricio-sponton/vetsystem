package com.sponton.vetsystem.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private SpringTemplateEngine template;
	
	public void enviarPedidoDeConfirmacaoDeCadastro(String destino, String codigo) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = 
				new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
		Context context = new Context();
		context.setVariable("titulo", "Bem vindo a clinica Spring Security");
		context.setVariable("texto", "Precisamos que confirme seu cadastro clicando no link abaixo");
		context.setVariable("linkConfirmacao", "http://localhost:8080/u/confirmacao/cadastro?codigo=" + codigo);
		
		String html = template.process("email/confirmacao", context);
		helper.setTo(destino);
		helper.setText(html, true);
		helper.setSubject("Confirmação de cadastro");
		helper.setFrom("nao-responder@clinica.com.br");
		
		helper.addInline("logo", new ClassPathResource("/static/image/spring-security.png"));
		
		mailSender.send(message); 
	}
	public void enviarPedidoRededinicaoSenha(String destino, String verificador) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = 
				new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
		Context context = new Context();
		context.setVariable("titulo", "Redefinição de senha");
		context.setVariable("texto", "Para redefinir sua senha use o código de verificação quando exigido no formulário.");
		context.setVariable("verificador", verificador);
		
		String html = template.process("email/confirmacao", context);
		helper.setTo(destino);
		helper.setText(html, true);
		helper.setSubject("Redefinição de senha");
		helper.setFrom("nao-responder@clinica.com.br");
		
		helper.addInline("logo", new ClassPathResource("/static/image/loginho.png"));
		
		mailSender.send(message); 
	}
}
