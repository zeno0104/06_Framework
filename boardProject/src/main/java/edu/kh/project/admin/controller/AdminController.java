package edu.kh.project.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import oracle.jdbc.proxy.annotation.Post;

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
			// body 안에 들어가는 String이 ResponseEntity<String>와 같음.
			// 즉, return 해주는 값을 타입으로 넣어주면 됌
		} catch (Exception e) {
			// HttpStatus.INTERNAL_SERVER_ERROR : 500번 에러
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 중 예외 발생 : " + e.getMessage());
		}
	}

	/**
	 * 관리자 계정 발급
	 * 
	 * @param member
	 * @return
	 */
	@PostMapping("createAdminAccount")
	public ResponseEntity<String> createAdminAccount(@RequestBody Member member) {
		try {
			// 1. 기존에 있는 이메일인지 검사
			int checkEmail = service.checkEmail(member.getMemberEmail());

			// 2. 있으면 발급 안함
			if (checkEmail > 0) {
				// HttpStatus.CONFLICT(409) : 요청이 서버의 현재 상태와 충돌할 때 사용
				// == 이미 존재하는 리소스(email) 때문에 새로운 리소스를 만들 수 없다.
				return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용중인 이메일입니다.");
			}
			// 3. 없으면 새로 발급해서 return -> 비밀번호를
			String accountPw = service.createAdminAccount(member);

			// HttpStatus.OK(200) : 요청이 정상적으로 처리되었으나 기존 리소스에 대한 단순 처리
			// HttpStatus.CREATED(201) : 자원이 성공적으로 생성되었음을 나타냄
			return ResponseEntity.status(HttpStatus.CREATED).body(accountPw);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("관리자 계정 생성 중 문제 발생(서버 문의 바람)");
		}
	}

	/**
	 * 관리자 계정 목록 조회
	 * 
	 * @return
	 */
	@GetMapping("adminAccountList")
	public ResponseEntity<List<Member>> adminAccountList() {
		try {
			List<Member> adminList = service.adminAccountList();
			return ResponseEntity.status(HttpStatus.OK).body(adminList);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
