package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller // 요청/응답 제어하는 역할 명시 + Bean 등록
@RequestMapping("param") // /param으로 시작하는 요청을 현재 컨트롤러로 매핑
@Slf4j
public class ParameterController {

	/*
	 * HttpServletReqeust :
	 * - 요청 클라이언트의 정보, 제출된 파라미터 등을 저장한 객체
	 * - 클라이언트 요청시 생성되는 객체
	 * 
	 * Spring의 Controller 단 메서드 작성 시, 
	 * 매개변수에 원하는 객체를 작성하면
	 * 존재하는 객체를 바인딩 또는 없으면 생성해서 바인딩
	 * --> ArgumentResolver가 존재함
	 * Argument(전달인자) + Resolver(해결사) => 전달인자 해결사
	 * 
	 * */
	@GetMapping("main")
	public String paramMain() {
		
		// /param/main Get 방식 요청 매핑
		// src/main/resources/templates/param/param-main.html로 forward
		return "param/param-main";
	}

	@PostMapping("test1") // /param/test1 POST 방식 요청 매핑
	public String paramTest1(HttpServletRequest req) {
		String name = req.getParameter("inputName");
		int age = Integer.parseInt(req.getParameter("inputAge"));
		String address = req.getParameter("inputAddress");
		log.debug("name : " + name);
		log.debug("address : " + address );
		log.debug("age : " + age);
		
		/*
		 * Spring에서 Redirect(재요청) 하는 방법!
		 * - Controller 메서드 반환값에
		 * "redirect:재요청주소"; 작성
		 * */
		return "redirect:/param/main";
		// redirect는 get 방식
	}

}
