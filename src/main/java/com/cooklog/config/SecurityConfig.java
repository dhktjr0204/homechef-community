// package com.cooklog.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;
//
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig{
// 	@Bean
// 	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// 		http
// 			.authorizeRequests(authorizeRequests ->
// 				authorizeRequests
// 					.requestMatchers("/css/**", "/js/**", "/media/**", "/font/**").permitAll() // 정적 리소스에 대한 접근 허용
// 					.requestMatchers("/").permitAll() // 홈 페이지 접근 허용
// 					.anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
// 			)
// 			.formLogin(formLogin ->
// 				formLogin
// 					.loginPage("/login") // 커스텀 로그인 페이지 (필요한 경우)
// 					.permitAll() // 로그인 페이지 접근 허용
// 			)
// 			.logout(logout -> logout.permitAll()); // 로그아웃에 대해 모든 사용자에게 허용
// 		return http.build();
// 	}
// }
