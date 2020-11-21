package com.sponton.vetsystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.service.UsuarioService;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
	private static final String VETERINARIO = PerfilTipo.VETERINARIO.getDesc();
	private static final String SECRETARIA = PerfilTipo.SECRETARIA.getDesc();

	@Autowired
	UsuarioService service;
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	
		http.authorizeRequests()
		.antMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll()
		.antMatchers("/").permitAll()
		.antMatchers("/u/p/**").permitAll()
		.antMatchers("/veterinarios/dados", "/veterinarios/salvar", "/veterinarios/editar").hasAuthority(VETERINARIO)
		.antMatchers("/secretarias/dados", "/secretarias/salvar", "/secretarias/editar").hasAuthority(SECRETARIA)
		
		.anyRequest().authenticated()
		.and()
			.formLogin()
			.loginPage("/login")
			.defaultSuccessUrl("/home", true)
			.failureUrl("/login-error")
			.permitAll()
		.and()
			.logout()
			.logoutSuccessUrl("/login")
		.and()
			.exceptionHandling()
			.accessDeniedPage("/acesso-negado");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
	}
}
