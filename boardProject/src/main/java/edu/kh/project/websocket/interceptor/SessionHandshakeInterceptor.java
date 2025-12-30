package edu.kh.project.websocket.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;

// SessionHandshakeInterceptor 클래스

// - WebSocketHandler가 동작하기 전/후에
// 연결된 클라이언트의 세션을 가로채는 동작을 작성할 클래스

// Handshake : 클라이언트와 서버가 Websocket 연결을 수립하기 위해 HTTP 프로토콜을 통해
// 수행하는 초기 단계
// -> 즉, 기존 HTTP 연결을 WebSocket 연결로 변경

// 클라이언트마다 고유한 세션을 가짐
// 클라이언트의 세션을 가져와서 해당 클라이언트에게 메세지를 보낸다는 것을 해주기 위해
// 클라이언트의 세션을 가로채는 동작이 필요

// 핸드쉐이크 코드는 js에서 진행
@Component // 일반적인 Bean으로 등록할 때 사용하는 어노테이션
public class SessionHandshakeInterceptor implements HandshakeInterceptor {

	// 핸들러 동작 전에 수행되는 메서드
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		// ServerHttpRequest : HttpServletRequest의 부모 인터페이스
		// ServerHttpResponse : HttpServletResponse의 부모 인터페이스

		// attributes : 해당 맵에 세팅된 속성(데이터)은
		// 다음에 동작할 Handler 객체에게 전달됨
		// HandshakeInterceptor -> Handler 데이터 전달하는 역할

		// request가 참조하는 객체가
		// SetvletServerHttpRequest로 다운캐스팅이 가능한가?
		if (request instanceof ServletServerHttpRequest) {
			// instanceof : 부모 자식관계인지 확인하는 키워드
			// 다운캐스팅
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			// ServerHttpRequest(부모) -> ServletServerHttpRequest(자식)

			// 웹소켓 동작을 요청한 클라이언트의 세션을 얻어옴
			HttpSession session = servletRequest.getServletRequest().getSession();
			// servletRequest.getServletRequest() = 여기까지가 request
			// request.getSession() 가져오기

			// 가로챈 클라이언트의 세션을 Handler에 전달할 수 있게 세팅
			attributes.put("session", session);

		}
		// true or false에 따라 인터셉터를 진행할지 말지를 결정
		// 즉, 가로채기 진행 여부 : true로 작성해야 세션을 가로채서 핸들러에게 전달하는게 가능해짐

		// 현재까지는 클라이언트에서 서버에게 요청을 보내는 (session전달) 역할을 하는 코드를 작성한 것
		return true;
	}

	// 핸들러 동작 후에 수행되는 메서드
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {

	}

}
