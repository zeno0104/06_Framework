package edu.kh.project.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.kh.project.admin.model.service.AdminService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin")
@CrossOrigin(origins = "http://localhost:5173")
@SessionAttributes({ "loginMember" })
// 세션 영역으로 올리기
@RequiredArgsConstructor
public class AdminController {
	private final AdminService service;

	@PostMapping("login")
	public Member login(@RequestBody Member inputMember, Model model) {
		Member loginMember = service.login(inputMember);

		if (loginMember == null)
			return null;

		model.addAttribute("loginMember", loginMember);
		return loginMember;
	}

	@GetMapping("logout")
	public ResponseEntity<String> logout(HttpSession session) {
		// ResponseEntity
		// React랑 서버단이랑 나눠서 소통할 때 비동기 응답 값으로 ResponseEntity를 많이 씀
		// Spring에서 제공하는 Http 응답 데이터를
		// 커스터마이징 할 수 있도록 지원하는 클래스
		// -> Http 상태코드, 헤더, 응답 본문(body)을 모두 설정 가능

		try {
			session.invalidate(); // 세션 무효화 처리
			return ResponseEntity.status(HttpStatus.OK).body("로그아웃이 완료되었습니다.");
			// .body 안에 들어가는 String이 ResponseEntity<String>와 같음
		} catch (Exception e) {
			// HttpStatus.INTERNAL_SERVER_ERROR : 500번 에러
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 중 예외 발생 : " + e.getMessage());
		}
	}
}
