package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.kh.project.common.interceptor.BoardTypeInterceptor;

@Configuration // 서버가 켜지면 Bean으로 등록되고, 내부 메서드가 수행
// 인터셉터가 어떤 요청을 가로챌지 설정하는 클래스
public class InterceptorConfig implements WebMvcConfigurer {
	// WebMvcConfigurer : registry에서 사용, 즉 어떤 경로에서 사용하겠다는 의미
	// => FileConfig에서 사용함
	// 인터셉터를 등록하는 메서드는 상속을 받아야함
	// -> WebMvcConfigurer

	@Bean // 인터셉터 클래스 Bean 등록, Bean은 메서드에 작성해야함
	// 개발자가 수동으로 만든 객체지만, 관리는 Spring Container가 수행하겠다는 의미
	public BoardTypeInterceptor boardTypeInterceptor() {
		return new BoardTypeInterceptor();
	}

	// 동작할 인터셉터 객체를 추가하는 메서드
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// Bean으로 등록된 BoardTypeInterceptor를 얻어와서 등록하기위한 절차
		registry.addInterceptor(boardTypeInterceptor()).addPathPatterns("/**").excludePathPatterns("/css/**");

		// addPathPatterns("/**")
		// /** : / 이하 모든 요청을 의미, 모든 요청을 가로채서, boardTypeInterceptor가 돌아가게끔 할 것

		// excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico")
		// 가로채고싶지 않은 요청 정의

	}

}
