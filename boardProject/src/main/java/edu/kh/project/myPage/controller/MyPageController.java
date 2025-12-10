package edu.kh.project.myPage.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.service.MyPageService;
import lombok.extern.slf4j.Slf4j;

// 
/*
 * @SessionAttributes의 역할
 * - Model에 추가된 속성 중 key 값이 일치하는 속성을 session scope로 변경하는 어노테이션
 * - 클래스 상단에 @SessionAttributes({ "loginMember" })
 * 
 * @SessionAttribute의 역할
 * - @SessionAttributes를 통해 session에 등록된 속성을 꺼내올 때 사용하는 어노테이션
 * - 메서드의 매개변수
 * */
@SessionAttributes({ "loginMember" }) // SessionStatus 사용하기 위함
@Controller
@RequestMapping("myPage")
@Slf4j
public class MyPageController {

	@Autowired
	private MyPageService service;

	/**
	 * 내 정보 조회로 이동
	 * 
	 * @param loginMember : 세션에 존재하는 loginMember를 얻어와 Member 타입 매개변수 대입
	 * @return
	 */
	@GetMapping("info") // /myPage/info GET 방식 요청 매핑
	public String info(@SessionAttribute("loginMember") Member loginMember, Model model) {
		// @SessionAttribute
		// session 값 가져오는 어노테이션
		// 클래스 상단에 @SessionAttributes를 작성해줘야 함

		// 현재 로그인한 회원의 주소를 꺼내옴
		// 현재 로그인한 회원 정보 -> session scope에 등록된 상태(loginMember)
		// loginMember(memberAddress도 포함)
		// -> 만약 회원가입 당시 주소를 입력했다면 주소값 문자열이 들어가있음(^^^ 구분자로 만들어진 문자열)
		// -> 회원가입 당시 주소를 입력하지 않았다면 null

		String memberAddress = loginMember.getMemberAddress();
		// 03189^^^서울 종로구 우정국로2길 21^^^3층, 302클래스(대왕빌딩)
		// or null

		if (memberAddress != null) {
			// 주소가 있을 경우에만 동작
			// 구분자 "^^^"를 기준으로
			// memberAddress 값을 쪼개어 String[]로 반환

			String[] arr = memberAddress.split("\\^\\^\\^");
			// split는 정규표현식을 전달해줘야 함F
			// 정규표현식에서 ^는 시작을 의미
			// \\를 각각 붙여줘야 함
			// escape를 의미

			// [03189, 서울 종로구 우정국로2길 21, 3층, 302클래스 (대왕빌딩)]
			// 이런 형태로 들어오게 됌

			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
		}

		return "myPage/myPage-info";
	}

	/**
	 * 프로필 이미지 변경화면 이동
	 * 
	 * @return
	 */
	@GetMapping("profile") // /myPage/profile GET 방식 요청 매핑
	public String profile() {
		return "myPage/myPage-profile";
	}

	/**
	 * 비밀번호 변경 화면 이동
	 * 
	 * @return
	 */
	@GetMapping("changePw") // /myPage/changePw GET 방식 요청 매핑
	public String changePw() {
		return "myPage/myPage-changePw";
	}

	// 회원 탈퇴 화면 이동
	@GetMapping("secession")
	public String secession() {
		return "myPage/myPage-secession";
	}

	// 파일 테스트 화면으로 이동
	@GetMapping("fileTest")
	public String fileTest() {
		return "myPage/myPage-fileTest";
	}

	// 파일 목록 조회 화면 이동
	@GetMapping("fileList")
	public String fileList() {
		return "myPage/myPage-fileList";
	}

	/**
	 * 회원 정보 수정
	 * 
	 * @param inputMember   : 커맨드 객체(@ModelAttribute가 생략된 상태) 제출된 memberNickname,
	 *                      memberTel 세팅된 상태
	 * @param memberAddress : 주소만 따로 배열형태로 얻어옴
	 * @param loginMember   : 로그인한 회원 정보를 가지고 있는 객체 (현재 로그인한 회원의 번호를 꺼내와야 함(pk))
	 * @return
	 */
	@PostMapping("info") // /myPage/info POST 방식 요청 매핑
	public String updateInfo(Member inputMember, @RequestParam("memberAddress") String[] memberAddress,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) {
		// inputMember에 현재 로그인한 회원 번호 추가
		inputMember.setMemberNo(loginMember.getMemberNo());
		// inputMember : 수정된 회원의 닉네임, 수정된 회원의 전화번호, [주소], 회원번호

		// 회원 정보 수정 서비스 호출
		int result = service.updateInfo(inputMember, memberAddress);

		String message = null;
		if (result > 0) {
			// 업데이트 성공
			message = "회원 정보 수정 성공";

			// loginMember에 DB상 업데이트 된 내용으로 세팅해줘야 함
			// -> loginMember는 세션에 저장된 로그인한 회원 정보가
			// 저장되어있다 (로그인 할 당시의 기존 데이터)
			// -> loginMember를 수정하면 세션에 저장된 로그인한 회원의
			// 정보가 업데이트 된다.
			// == Session에 있는 회원 정보와 DB 데이터를 동기화

			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
		} else {
			// 업데이트 실패
			message = "회원 정보 수정 실패...";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:info"; // 재요청 경로 : /myPage/info GET 요청
	}

	@PostMapping("changePw")
	public String changePw(@SessionAttribute("loginMember") Member loginMember,
			@RequestParam("currentPw") String currentPw, @RequestParam("newPw") String newPw, RedirectAttributes ra) {

		// id로 가져옴

		int result = service.changePw(loginMember, currentPw, newPw);

		String path = null;
		String message = null;

		if (result > 0) {
			message = "비밀번호가 변경 되었습니다.";
			path = "info";
		} else {
			message = "현재 비밀번호가 일치하지 않습니다.";
			path = "changePw";
		}

		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}

	/**
	 * @param memberPw    : 제출받은(입력한) 비밀번호
	 * @param loginMember : 로그인한 회원 정보 저장 객체(세션에서 꺼내옴) -> 회원번호 필요!(SQL에서 조건으로 사용)
	 * @param status      : @SessionAttributes()와 함께 사용!
	 * @return
	 */
	@PostMapping("secession") // /myPage/secession POST 요청 매핑
	public String secession(@RequestParam("memberPw") String memberPw,
			@SessionAttribute("loginMember") Member loginMember, SessionStatus status, RedirectAttributes ra) {
		// 로그인한 회원의 회원번호 꺼내오기
		int memberNo = loginMember.getMemberNo();

		// 서비스 호출(입력받은 비밀번호, 로그인한 회원 번호)
		int result = service.secession(memberNo, memberPw);

		String message = null;
		String path = null;

		// 탈퇴 성공 - 메인페이지 재요청
		// 탈퇴 실패 - 탈퇴 페이지로 재요청

		if (result > 0) {
			// 삭제 성공
			message = "탈퇴 되었습니다.";
			path = "/";
			// 로그인이 됐는지 확인하는 것은 session에 있는 loginMember
			status.setComplete(); // 세션 비우기 (로그아웃 상태 변경)
		} else {
			// 삭제 실패
			message = "비밀번호가 일치하지 않습니다.";
			path = "secession";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}

	/**
	 * Spring에서 파일을 처리하는 방법
	 * 
	 * - enctype="multipart/form-data"로 클라이언트의 요청을 받으면 (문자, 숫자, 파일 등이 섞여있는 요청)
	 * 
	 * 이를 MultipartResolver(FileConfig)를 통해서 섞여있는 파라미터들을 분리해주는 작업을 해줘야 한다.
	 * 
	 * 문자열, 숫자 -> String 파일 -> MultipartFile
	 */
	@PostMapping("file/test1") // /myPage/file/test1 POST 요청 매핑
	public String fileUpload1(@RequestParam("uploadFile") MultipartFile uploadFile, RedirectAttributes ra) {
		// 파일이기 때문에, String 등이 아닌 MultipartFile 타입으로 정의한다

		String path = null;
		try {
			path = service.fileUpload1(uploadFile);
			// /myPage/file/파일명.jpg

			// 파일이 실제로 서버 컴퓨터에 저장이 되어
			// 웹에서 접근할 수 있는 경로가 반환되었을 때
			if (path != null) {
				ra.addFlashAttribute("path", path);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("파일 업로드 예제1 중 예외 발생");
		}

		return "redirect:/myPage/fileTest";
	}
}
