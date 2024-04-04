
 package com.cooklog.config;

 import com.cooklog.exception.CustomFailureHandler;
 import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
 import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 import org.springframework.security.web.SecurityFilterChain;


 @Configuration
 @EnableWebSecurity
 public class SecurityConfig{

     // 특정 HTTP 요청에 대한 웹 기반 보안 구성
 	@Bean
 	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

		 // "/login", "/join", "/joinProc 에는 인증 없이 접근 가능하도록 - permitAll()
 		http
 			.authorizeHttpRequests((auth) -> auth
 				.requestMatchers("/login","/join", "/joinProc", "/loginProc").permitAll()
 				.requestMatchers("/admin").hasAuthority("ADMIN")
 				//                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")

 				.anyRequest().authenticated()
 			);

		 // 접근 불가능한 페이지 들어가면 "/login" 으로 이동
 		http
 			.formLogin((auth) -> auth.loginPage("/login") // GET
					// 로그인 form action 기본 설정
 				.loginProcessingUrl("/loginProc") // POST
				.defaultSuccessUrl("/")
 				.usernameParameter("email")
				.passwordParameter("password")
				.failureHandler(customFailureHandler()) // 로그인 실패 핸들링
 				.permitAll()
 			)
			.logout(auth -> auth.logoutSuccessUrl("/login") // 로그아웃 설정
					.invalidateHttpSession(true))
			.csrf(auth -> auth.disable());

 		http
 			.csrf((auth) -> auth.disable());

 		return http.build();
 	}

	 @Bean
	 public WebSecurityCustomizer webSecurityCustomizer() {
		 // 스프링 시큐리티 비활성화 (모든 정적 파일에 ignoring 적용)
		 return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	 }

	 // 패스워드 인코더로 사용할 빈 등록
 	@Bean
 	public BCryptPasswordEncoder bCryptPasswordEncoder() {
 		return new BCryptPasswordEncoder();
 	}
	 @Bean
	 public CustomFailureHandler customFailureHandler() {
		 return new CustomFailureHandler();
	 }


 }

