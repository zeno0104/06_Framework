package edu.kh.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;

/*
* @ResponseBody
* - 컨트롤러 메서드의 반환값을
*   Http 응답 본문에 직접 바인딩하는 역할임을 명시
*
* -> 컨트롤러 메서드의 반환값을
*  비동기 요청했던 HTML/JS 파일 부분에 값을 그대로 돌려 보낼 것이다 를 명시.
*
* -> 해당 어노테이션이 붙은 컨트롤러의 메서드는 return 에 작성된 값이 forward/redirect 로 인식 X
*
*
* @RequestBody
* - 비동기 요청시 전달되는 데이터 중 body 부분에 포함된 요청 데이터를
*   알맞은 Java 객체 타입으로 바인딩하는 어노테이션
*  
* - 기본적으로 JSON 형식을 기대함.
*
*
* [HttpMessageConvertor]
* Spring에서 비동기 통신 시
* - 전달받은 데이터의 자료형
* - 응답하는 데이터의 자료형
* 위 두가지를 알맞은 형태로 가공(변환)해주는 객체
*
*    Java                       JS
* 문자열,숫자 <--------------> TEXT
*    Map      <->   JSON   <-> JS Object
*    DTO      <->   JSON   <-> JS Object
*   
* (참고)
* Spring에서 HttpMessageConvertor 작동하기 위해서는
* jackson-data-bind 라이브러리가 필요한데
* Spring boot에는 모듈에 내장되어 있음
*
*
* */

@Controller // 요청/응답 제어하는 역할 명시 + Bean 등록 (IoC)
@RequestMapping("ajax")
public class AjaxController {
	// 등록된 Bean 중 같은 타입 또는 상속관계인 Bean을 찾아
	// 해당 필드에 의존성 주입
	@Autowired // (DI) 의존성 주입
	private TodoService service;

	@GetMapping("main")
	public String ajaxMain() {
		return "ajax/main";
	}

	// @Responsebody
	// 반환값을 HTTP의 응답 본문으로 직접 전송
	// (즉, js가 요청을 했을 때, js에게 값을 그대로 돌려보내주겠다는 의미)

	@ResponseBody
	@GetMapping("totalCount") // /ajax/totalCount로 요청을 Get 매핑
	// 전체 Todo 개수 비동기 조회
	// -> forward / redirect를 원하는게 아님
	// -> ex) 전체 Todo 개수라는 데이터를 비동기 요청을 보낸 클라이언트(브라우저)에게
	// 반환되는 것을 원하는 것!!
	// -> 반환되어야하는 결과값의 자료형을 반환형에 써줘야 한다.

	/**
	 * @return
	 */
	public int getTotalCount() {
		// @ResponseBody일 때는
		// 내가 반환하고자 하는 타입으로 타입이 바뀜

		// 전체 할 일 갯수 조회 서비스 호출 결과 반환받기
		int totalCount = service.getTotalCount();

		// 결과 리턴하기 (JS쪽으로)
		return totalCount;
	}

	@ResponseBody
	@GetMapping("completeCount")
	public int getCompleteCount() {
		return service.getCompleteCount();
	}

	@ResponseBody
	@PostMapping("add")
	public int addTodo(@RequestBody Todo todo) {
		// 요청 body(본문)에 담긴 값을 Todo라는 DTO에 저장해서
		// 반환을 해준다

		// 할 일 추가 서비스 호출 후 결과값 리턴하기
		return service.addTodo(todo.getTodoTitle(), todo.getTodoContent());

	}

	/**
	 * 전체 할 일 목록 조회
	 */
	@ResponseBody
	@GetMapping("selectList")
	public List<Todo> selectList() {
		return service.selectList();
		// List (Java 전용 타입)를 반환
		// -> JS에는 List 타입이 없기 때문에
		// 인식할 수 없기 때문에 JSON으로 변환을 해줘야 함
		// HTTPMessageConveter가 알맞은 형태로 변환해줌
	}

}
