package edu.kh.project.common.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * Filter : 요청, 응답 시 걸러내거나 추가할 수 있는 객체
 * 
 * [필터 클래스 생성 방법]
 * 1. jakarta.servlet.Filrer 인터페이스 상속받기
 * 2. doFilter() 메서드 오버라이딩
 * 
 * */

// 로그인이 되어있지 않은 경우, 특정 페이지를 접근할 수 없도록 필터링하기
public class LoginFilter implements Filter {

	// 필터 동작을 정의하는 메서드
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// ServletRequest : HttpServletRequest의 부모타입
		// ServletResponse : HttpServletResponse 의 부모타입

		// session이 필요함 -> 왜 필요한가? -> loginMember가 session에 담기기 때문

		// HttpServletRequest 형태(자식형태)로 다운캐스팅

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// 현재 요청의 URI를 가져옴
		String path = req.getRequestURI(); // /myPage/profile

		// 요청 URI가 "/myPage/profile"로 시작하는지 확인
		if (path.startsWith("/myPage/profile/")) {
			// 필터를 통과하도록 함
			// FilterChain : 다음 필터로 체이닝, 또는 다음 필터가 없다면 DispatcherServler로 보냄

			// 요청 URI 값이 "/myPage/profile"로 시작하는지 확인
			// 해당 요청은 막아주지 않고 통과시킬 것
			// 파일 업로드할 때 문제가 발생함
			// 마이페이지에서 각종 이미지는 서버에 요청을 보내서 가져오는 것
			// 그리고나서 화면에 뿌려주는 것
			// 근데, 프로필 이미지는 서버에 요청을 보낼 때, /myPage/profile/스폰지밥.jpg을 사용할 것
			// 로그인을 하지 않았어도, 게시판 글을 확인할 수 있고, 이미지를 볼 수 있어야 함
			// 하지만, 로그인이 되어있지 않은 상태에서 다른 유저의 프로필 이미지가 다 깨짐
			// 이미지를 가져오는 서버 매핑이 /myPage/profile...로 시작함

			chain.doFilter(request, response);
			// DispatcherServler로 보냄
			// 즉, 다음 필터가 없다는 것
			// 필터를 통과한 후 아래에 문장이 실행되지 않도록 return해줌
			return;
		}
		// session 객체 얻어오기

		HttpSession session = req.getSession();

		// 세션에서 로그인한 회원 정보를 꺼내옴
		// loginMember가 있는지, null인지 확인하기
		if (session.getAttribute("loginMember") == null) {
			// 로그인이 되어있지 않은 상태를 의미
			resp.sendRedirect("/loginError");
		} else {
			// -> 로그인이 되어 있는 상태
			
			// 다음 필터로 이동하던지, 혹은 다음 필터가 없다면 DispatcherServlet로 요청과 응답객체를 전달
			chain.doFilter(request, response);
		}

	}

}
