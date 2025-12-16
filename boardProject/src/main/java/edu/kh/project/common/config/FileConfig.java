package edu.kh.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
@PropertySource("classpath:config.properties")
// 외부에 있는 properties를 가져오는 것
public class FileConfig implements WebMvcConfigurer {
	// WebMvcConfigurer : Spring MVC 프레임워크에서 제공하는
	// 인터페이스 중 하나로, 스프링 구성을 커스터마이징하고
	// 확장하기 위한 메서드를 제공함.
	// 주로 웹 애플리케이션의 설정을 조정하거나 추가하는데 사용됨

	// 파일 업로드 임계값
	// @Value : properties에 매핑된 키를 통해 value를 가져오는 어노테이션
	@Value("${spring.servlet.miltipart.file-size-threshold}")
	private long fileSizeThreshold; // 52428800

	// 임계값 초과시 파일의 임시 저장경로
	@Value("${spring.servlet.multipart.location}")
	private String location; // C:/uploadFiles/temp/
	// 요청당 파일 최대 크기
	@Value("${spring.servlet.multipart.max-request-size}")
	private long maxRequestSize; // 52428800

	// 개별 파일당 최대 크기
	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize; // 10485760
	// 요청 주소에 따라
	// 서버 컴퓨터의 어떤 경로에 접근할 지 설정하는 메서드

	// 게시판 이미지 관련 경로
	@Value("${my.board.resource-handler}")
	private String boardResourceHandler;

	@Value("${my.board.resource-location}")
	private String boardResourceLocation;

	// ----------------------------------------

	// 프로필 이미지관련 경로
	@Value("${my.profile.resource-handler}")
	private String profileResourceHandler;

	@Value("${my.profile.resource-location}")
	private String profileResourceLocation;

	// 요청 주소에 따라
	// 서버 컴퓨터의 어떤 경로에 접근할지 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// ResourceHandlerRegistry :
		// Spring MVC에서 정적 리소스(이미지, CSS, JS 등)
		// 요청을 처리하기 위해 사용하는 클래스

		// URL 요청 패턴을 서버의 실제 파일 경로와 연결하여
		// 클라이언트가 특정 경로로 정적파일에 접근할 수 있도록 설정
		// 어떤 요청이 왔을 때 실행될지 정하는 것
		registry.addResourceHandler("/myPage/file/**").addResourceLocations("file:///C:/uploadFiles/test/");
		// 즉, /myPage/files/에 요청이 오면 C:/uploadFiles/test와 연결하겠다는 의미
		// -> 클라이언트가 /myPage/file/** 패턴으로 이미지를 요청할 때
		// 서버 폴더 경로 중 C:/uploadFiles/test/로 연결하겠다.
		
		registry.addResourceHandler(profileResourceHandler).addResourceLocations(profileResourceLocation);
		// -> 클라이언트가 /myPage/profile/** 패턴으로 이미지 요청할 때
		// 서버 폴더 경로 중 C:/uploadFiles/profile/로 연결
		
		registry.addResourceHandler(boardResourceHandler).addResourceLocations(boardResourceLocation);
		// -> 클라이언트가 /images/board/** 패턴으로 이미지 요청할 때
		// 서버 폴더 경로 중 C:/uploadFiles/board/로 연결
	}

	@Bean
	// MultipartResolver 설정
	public MultipartConfigElement configElement() {
		// MultipartConfigElement
		// 파일 업로드를 처리하는데 사용되는 MultipartConfigElement를
		// 구성을 하고 반환을 해주는 것(옵션 설정하는데 사용)
		// 업로드 파일의 최대 크기, 임시 저장경로 등등을 설정할 수 있게 해주는 객체
		MultipartConfigFactory factory = new MultipartConfigFactory();

		// 파일 업로드 임계값
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));
		// long타입으로 byte를 넣어야 함

		// 임시 저장 폴더 경로
		factory.setLocation(location);

		// HTTP 요청당 파일 최대 크기
		factory.setMaxRequestSize(DataSize.ofBytes(maxFileSize));

		// 개발 파일당 최대 크기
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));

		return factory.createMultipartConfig();
	}

	// MultipartResolver 객체를 생성하여 Bean으로 등록
	// -> 위에서 만든 MultipartConfigElement를 자동으로 이용할 것
	@Bean
	public MultipartResolver multipartResolver() {
		// MultipartResolver : MultipartFile을 처리해주는 해결사
		// -> MultipartResolver는 클라이언트로부터 받은 multipart 요청을 처리하고,
		// 그 중 업로드된 파일을 추출하여 MultipartFile 객체로 제공하는 역할
		StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
		return multipartResolver;
	}
}