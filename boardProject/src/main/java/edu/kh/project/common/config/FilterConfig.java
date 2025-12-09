package edu.kh.project.common.config;


import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.kh.project.common.filter.LoginFilter;

@Configuration // 서버가 켜질 때 클래스 내 모든 메서드가 실행되도록 하는 어노테이션 + 빈등록
public class FilterConfig {
	// 언제 LoginFilter가 동작할지 정의해주면 됌

	// 만들어 놓은 LoginFilter 클래스가 언제 적용될 지 설정하는 것
	@Bean // 반환된 객체를 Bean으로 등록 : 
	// LoginFilter로 타입을 제한한 객체를 Bean으로 등록한다.
	public FilterRegistrationBean<LoginFilter> loginFilter() {
		// FilterRegistrationBean : 필터를 Bean으로 등록하는 객체
		FilterRegistrationBean<LoginFilter> filter = new FilterRegistrationBean<>();
		// 사용할 필터 객체 세팅
		filter.setFilter(new LoginFilter());
		
		// 필터가 동작할 URL을 세팅
		// /myPage/*  : /myPage로 시작하는 모든 요청
		String[] filteringURL = {"/myPage/*", "/chatting/*", "/editBoard/*"};
		
		// String[]을 List 변환
		// Arrays.asList(배열) -> List 변환
		filter.setUrlPatterns(Arrays.asList(filteringURL));
		
		// 필터 이름 지정
		filter.setName("loginFilter");
		
		// 필터 순서 지정
		filter.setOrder(1);
		
		return filter; // 반환된 객체가 Bean 등록
	}

}
