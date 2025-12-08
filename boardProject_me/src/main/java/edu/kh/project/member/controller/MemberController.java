package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * [로그인] - 특정 사이트에 아이디(이메일)/비밀번호 등을 입력해서 해당 정보가 있으면 조회/서비스 이용
 * 
 * - 로그인 한 회원 정보를 session에 기록하여 로그아웃 또는 브라우저 종료 시까지 해당 정보를 계속 이용할 수 있게 함
 * 
 * @param inputMember : 커맨드 객체 (@ModelAttribute 생략) memberEmail, memberPw 세팅된 상태
 * @return
 */

/*
 * @SessionAttributes({"key", "key", "key", ....}) key값은 원하는 만큼 올릴 수 있음 - Model에
 * 추가된 속성 중 key 값이 일치하는 속성을 session scope로 변경
 */

@SessionAttributes({ "loginMember" })
@Controller
@RequestMapping("member")
@Slf4j
public class MemberController {
	@Autowired // 의존성 주입(DI)
	private MemberService service;

	@PostMapping("login")
	public String login(Member inputMember, RedirectAttributes ra, Model model,
			@RequestParam(value = "saveId", required = false) String saveId, HttpServletResponse resp) { // /member/login
		// required는 true가 기본값
		// false면 있어도, 없어도 에러가 안나옴
		// label은 체크가 되면 check가 되면 on, 아니면 null값

		// 로그인 서비스 호출
		try {
			Member loginMember = service.login(inputMember);

			log.debug("loginMember : " + loginMember);

			// 로그인 실패 시
			if (loginMember == null) {
				ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
			} else {
				model.addAttribute("loginMember", loginMember);
				// request scope이지만 @SessionScope를 통해서 session scope로 변환
				// 1단계 : request scope에 세팅됨
				// 2단계 : 클래스 위에 @SessionAttributes() 어노테이션을 작성하여
				// session scope로 이동

				// ************** Cookie **************
				// 이메일 저장

				// 쿠키 객체 생성 (K:V)
				Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
				// saveId = user01@kh.or.kr

				// 쿠키가 적용될 경로 설정
				// -> 클라이언트가 어떤 요청을 할 때 쿠키가 첨부될 지 지정
				cookie.setPath("/");
				// 즉, 메인페이지의 하위에 있는 모든 다른 페이지까지 다 적용하겠다는 의미
				// "/" -> IP 또는 도메인 또는 localhost
				// -> 메인페이지 + 그 하위에 있는 주소 모두 경로가 된다!!

				// 쿠키의 만료 기간 지정
				if (saveId != null) { // 아이디 저장 체크시
					cookie.setMaxAge(60 * 60 * 24 * 30); // 30일을 초단위로 지정
				} else {
					// 미체크 시
					cookie.setMaxAge(0);
					// MaxAge를 0으로해서 기존에 있던 것을 삭제하겠다는 의미
					// 즉, 기존에 있는 client의 쿠키를 삭제해주겠다는 의미
					
				}
				// 응답객체에 쿠키 추가 -> 클라이언트 전달
				resp.addCookie(cookie);

			}
			// 로그인 성공 시

		} catch (Exception e) {
			e.printStackTrace();
			log.info("로그인 중 예외 발생"); // 메인페이지 재요청
		}
		return "redirect:/";
	}

	/**
	 * 로그아웃 : session에 저장된 로그인된 회원 정보를 없앰
	 * 
	 * @param SessionStatus : @SessionAttributes로 지정된 특정 속성을 세션에서 제거할 수 있는 기능을 제공하는
	 *                      객체
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) { // /member/logout 요청 GET 방식 매핑
		status.setComplete(); // 세션을 완료 시킴
		// 즉, 세션에 들어가있는 내용을 초기화 시켜준다는 의미

		return "redirect:/";
	}
}
