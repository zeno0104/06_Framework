package edu.kh.project.common.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.kh.project.board.model.service.BoardService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor : 요청/응답/뷰 완성 후 이때, 무언가(요청, 응답, 뷰)를 가로채는 객체 (Spring에서 지원)
 * 
 * * HandlerInterceptor 인터페이스를 상속받아서 구현해야 한다
 * 
 * - preHandle(전처리) 3번 : previous -> 이전에 핸들링 하는 것 DispatcherServlet이
 * Controller에게 요청을 보내줄 때, 이 사이에서 수행
 * 
 * - postHandle(후처리) 5번 : 요청받은 Controller가 응답값을 가지고 DispatcherServlet에게 보내줄 때 이
 * 사이에서 수행
 * 
 * - afterCompletion (뷰 완성후) 6번: ViewResolver -> DispatcherServlet에게 이런 화면으로
 * 응답해줘 라고 할 때 수행되는 것
 * 
 */

@Slf4j
public class BoardTypeInterceptor implements HandlerInterceptor {
	// HandlerInterceptor를 상속받았는데 왜 빨간줄이 안뜨는지
	// => 내부적으로 보면 추상메소드처럼 안보임
	// => default를 붙이면 정의를 할 수도 있음
	// => 인터셉트에서 빨간줄이 안뜨는 것, 즉 추상메소드 형태가 아니기 때문

	// 전처리 : 요청이 Controller로 들어오기 전 실행되는 메서드

	@Autowired // 의존성 주입(DI) -> BoardService타입과 일치하거나, 상속관계인 객체(Bean)을 주입받음
	private BoardService service;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// DB에 접근해서 board_type을 가져와서 화면에 뿌려줄 것

		// boardType을 DB에서 얻어오기
		// boardTypeList 형태로 가져오기
		// 모든 페이지를 돌아다니면서 화면상에 boardType이 뿌려져야 함
		// 서버가 켜지자마자 db에서 데이터를 가져와서 뿌려줘야하므로, application scope에 저장
		// application scope :
		// - 서버 시작 ~ 종료 시 까지 유지되는 Servlet 내장 객체
		// - 서버 내에 딱 한개만 존재하는 객체다 --> 모든 클라이언트가 공용으로 사용
		// session은 클라이언트마다 생성됌

		// application scope 객체 얻어오기
		ServletContext application = request.getServletContext();
		// application scope에 "boardTypeList"가 없을 경우
		if (application.getAttribute("boardTypeList") == null) {
			// boardTypeList 조회 서비스 호출
			List<Map<String, Object>> boardTypeList = service.selectBoardTypeList();
			// 첫 요청이 들어와서 application에 실어두면, 두번 째부터는 실행을 안해줘도 됌
			log.debug("boardTypeList : " + boardTypeList);

			// 조회 결과를 application scope에 추가
			application.setAttribute("boardTypeList", boardTypeList);

		}

		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	// 후처리 : 요청이 처리된 후, 뷰가 렌더링 되기 전에 실행되는 메서드
	// 즉, 응답을 가지고 DispatcherServlet에게 돌아가기 전
	// 전처리에서 모든 작업이 끝났기 때문에, 후처리에서 할게 없다.
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	// 뷰 완성 후 : 뷰 렌더링이 끝난 후 실행되는 메서드
	// 뷰를 다 뿌리고나서 할게 없기 때문에 놔두면 된다.
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
