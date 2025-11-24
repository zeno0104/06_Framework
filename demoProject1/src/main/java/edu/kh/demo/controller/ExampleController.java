package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 요청/응답 제어 역할 명시 + Bean 등록
public class ExampleController {
	// 1) @RequestMapping("/example")

	// 2) @GetMapping("주소") : Get(조회) 방식 요청 매핑
	// @PostMapping("주소") : Post(삽입, 추가) 방식 요청 매핑
	// @PutMapping("주소") : Put(수정) 방식 요청 매핑
	// @DeleteMapping("주소") : Delete(삭제) 방식 요청 매핑

	// "/"를 앞에 안붙이는 이유
	// Servlet/JSP
	// @WebServlet("/example") -> /를 꼭 써줘야 함
	// Spring boot는 주소 맨 앞에 /를 안붙여도 오류가 안남
	/*
	 * 1) Spring Boot에서는 요청 주소 앞에 "/"가 없어도 요청 처리가 잘된다! (오류 발생 x)
	 * 
	 * 2) 프로젝트를 AWS같은 호스팅 서비스를 이용하여 배포 시 만약 리눅스 os를 이용하면 build 과정에서 경로 상 오류 발생...
	 */

	@GetMapping("example") // /example GET 방식 요청 매핑
	public String exampleMethod() {
		// forward하려는 html 파일 경로 return 작성
		// 단, viewResolver가 제공하는
		// 타임리프의 접두사, 접미사는 제외하고 작성하면 된다.

		// 접두사 : classpath:/templates/
		// 접미사 : .html
		return "example";
		// src/main/resources/templates/example.html
	}
}
