package edu.kh.demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.demo.model.dto.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller // 요청/응답 제어하는 역할 명시 + Bean 등록
@RequestMapping("param") // /param으로 시작하는 요청을 현재 컨트롤러로 매핑
@Slf4j // log를 이용한 메시지 콘솔창에 출력할 때 사용(lombok 제공)
public class ParameterController {

	// ⭐⭐⭐⭐⭐⭐⭐ 시험 ⭐⭐⭐⭐⭐⭐⭐⭐시
	/*
	 * HttpServletReqeust : - 요청 클라이언트의 정보, 제출된 파라미터 등을 저장한 객체 - 클라이언트 요청시 생성되는 객체
	 * 
	 * Spring의 Controller 단 메서드 작성 시, 매개변수에 원하는 객체를 작성하면 존재하는 객체를 바인딩 또는 없으면 생성해서
	 * 바인딩 --> ArgumentResolver가 존재함 Argument(전달인자) + Resolver(해결사) => 전달인자 해결사
	 * 
	 */
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
		log.debug("address : " + address);
		log.debug("age : " + age);
		// log.debug는 String형만 들어올 수 있음

		/*
		 * Spring에서 Redirect(재요청) 하는 방법! - Controller 메서드 반환값에 "redirect:재요청주소"; 작성 =>
		 * 어노테이션에 들어가는 요청 주소를 작성하라는 의미 redirect는 get방식
		 */
		return "redirect:/param/main";
		// redirect는 get 방식
	}

	/*
	 * 2. @RequestParam 어노테이션 - 낱개 파라미터 얻어오기
	 *
	 * - request 객체를 이용한 파라미터 전달 어노테이션 - 매개변수 앞에 해당 어노테이션을 작성하면, 매개변수에 값이 주입됨. -
	 * 주입되는 데이터는 매개변수의 타입에 맞게 형변환이 자동으로 수행됨.
	 *
	 * [기본 작성법]
	 * 
	 * @RequestParam("key") 자료형 매개변수명
	 *
	 * [속성 추가 작성법]
	 * 
	 * @RequestParam(value="key", required=false, defaultValue="1") required=true가
	 * 기본값 => 즉, 필수적으로 매개변수가 넘어와야 한다는 의미 => 안오면 400에러가 나옴
	 *
	 * value : 전달받은 input 태그의 name 속성값(파라미터 key) required : 입력된 name 속성값 파라미터 필수 여부
	 * 지정(기본값 true) -> required=true인 파라미터가 존재하지 않는다면 400(Bad Request) 에러 발생 -> ""
	 * (빈문자열)일 때는 에러 발생 X (파라미터가 존재하지 않는것이 아니라 name속성값="" 로 넘어오기 때문에)
	 *
	 * defaultValue : 파라미터 중 일치하는 name속성값이 없을 경우에 대입할 값 지정. -> required=false 인 경우
	 * 사용 => 기본값을 의미
	 *
	 */

	@PostMapping("test2") // /param/test2 POST 방식 요청 매핑
	public String paramTest2(@RequestParam("title") String title, @RequestParam("writer") String writer,
			@RequestParam("price") int price,
			// 매개변수의 자동형변환이 사용된 경우에는
			// 파라미터 값(value)이 필수로 작성되어야 한다!
			@RequestParam(value = "publisher", required = false) String publisher) {
		// - Method parameter 'price': Failed to convert value of type
		// 'java.lang.String' to required type 'int'; For input string: "”
		// - int형으로 자동 형변환을 하는데, 비어있기 때문에, 빈문자열로 넘어와서
		// int로 바꿀려고 할 때 문제가 발생
		log.debug("title : " + title);
		log.debug("writer : " + writer);
		log.debug("price : " + price);

		log.debug("publisher : " + publisher);
		// 만약 publisher의 name값이 없이 넘겨오면 다음과 같은 에러가 발생한다.
		// There was an unexpected error (type=Bad Request, status=400).
		// Required parameter 'publisher' is not present.

		return "redirect:/param/main";
	}

	// 3. @RequestParam 여러개 파라미터 얻어오기
	// - 같은 name 속성값을 가진 파라미터 얻어오기
	// - 제출된 파라미터 한번에 묶어서 얻어오기
	@PostMapping("test3")
	public String paramTest3(@RequestParam("color") String[] colorArr, @RequestParam("fruit") List<String> fruitList,
			@RequestParam Map<String, Object> paramMap) {

		log.debug("colorArr : " + Arrays.toString(colorArr));
		log.debug("fruitList: " + fruitList);
		log.debug("paramMap : " + paramMap);
		// @RequestParam Map<String, Object> paramMap
		// -> 제출된 모든 파라미터가 Map에 저장된다
		// -> 같은 name 속성을 가진 파라미터는 배열이나 List 형태가 아님!!!
		// -> 첫번째로 제출된 valeu값만 저장이 됨
		return "redirect:/param/main";
	}

	// ⭐⭐⭐ (자주 사용) ModelAttribute를 이용한 파라미터 얻어오기
	// @ModelAttribute
	// - DTO (또는 VO)와 같이 사용하는 어노테이션

	// ⭐ 전달받은 파라미터의 name속성값이
	// 함께 사용되는 DTO의 필드명과 같으면
	// 자동으로 setter를 호출해서 필드에 값을 저장

	// id, pw, name, age는 db에 저장하기위해서 사용자에게 입력 받음

	// *** 주의 사항 ***
	// - DTO에 기본 생성자가 필수로 존재해야 함
	// - DTO에 setter가 필수로 존재해야 함

	// ⭐ @ModelAttribute를 이용해 값이 필드에 세팅된 객체를
	// "커맨드 객체"라고 부름
	// @ModelAttribute는 생략 가능!
	// -> 커맨드 객체라고 생각함

	@PostMapping("test4")
	public String paramTest4(/* @ModelAttribute */Member inputMember) {
		// name값과 필드명이 같다면, setter가 자동으로 진행됌
		// 또한, DTO(Member)는 기본생성자가 필요
		// Member inputMember = new Member(); => 내부적으로 자동으로 일어남
		// 그리고, Setter가 진행되기 때문에, Setter 또한 정의되어 있어야 한다.

		// ModelAttribute로 만들어진 Member 객체인 inputMember는 커맨드 객체로 불림
		log.debug("inputMember : " + inputMember);
		// return "redirect:/param/main";
		// /param/main => 절대경로 작성법
		// ⭐⭐⭐ 상대경로에서 중요한 부분! -> 현재 위치!!
		// -> 현재 경로의 가장 마지막 레벨의 주소값을
		// "redirect:이주소"로 변경하기 때문
		// /main은 localhost/main으로 보내겠다는 의미(절대경로)
		return "redirect:main"; // 상대경로 작성법
	}
}
