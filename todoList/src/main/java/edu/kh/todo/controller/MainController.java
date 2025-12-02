package edu.kh.todo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Controller // 요청/응답 제어 역할 명시 + Bean 등록
@Slf4j // 로그 객체 자동 생성 (lombok 제공)
public class MainController {
	@Autowired // 타입이 같거나 상속관계를 DI 함
	private TodoService service;

	// private TodoService service = new TodoServiceImpl();과 같음
	// 해당 코드는 개발자 주도하에 주입이 된 것
	// @Autowired는 Spring 주도하에 의존성이 주입이 된 것

	// TodoServiceImpl이 @Service로 등록되어 Bean으로 등록되어있기 때문에,
	// @Autowired로 의존성 주입을 해줄 수 있다.
	// Beans 안에 TodoService 타입과 같거나, 상속관계인 것들을 주입해준다.
	// => 즉, TodoServiceImpl이 주입이 됨
	@RequestMapping("/")
	public String mainPage(Model model) {
		// 접두사 : src/main/resources/templates/
		// 접미사 : .html
		log.debug("service + " + service);
		// service + edu.kh.todo.controller.TodoServiceImpl@2ffb873b
		// 객체 주소가 있다는 것은 객체화가 되었다는 의미

		// todoNo가 1인 todo의 제목 조회하여 request scope에 추가

		String testTitle = service.testTitle();
		model.addAttribute("testTitle", testTitle);
		// Model은 request scope임

		// -----------------------------------------

		// TB_TODO 테이블에 저장된 전체 할 일 목록 조회
		// + 완료된 할 일 개수

		// service 메서드 호출 후 결과 반환 받기

		Map<String, Object> map = service.selectAll();

		List<Todo> todoList = (List<Todo>) map.get("todoList");
		// todoList는 object 타입이기 때문에, List로 다운캐스팅
		int completeCount = (int) map.get("completeCount");

		model.addAttribute("todoList", todoList);
		model.addAttribute("completeCount", completeCount);

		// src/main/resources/templates/common/main.html로 forward
		return "common/main";
	}
}
