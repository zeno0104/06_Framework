package edu.kh.project.websocket.config;

import edu.kh.project.websocket.handler.ChattingWebsocketHandler;
import edu.kh.project.websocket.handler.TestWebSocketHandler;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration // 서버 실행 시 작성된 메서드를 모두 수행 + Bean 등록
@EnableWebSocket // 웹소켓 활성화 설정
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

	private final TestWebSocketHandler testWebSocketHandler;
	private final HandshakeInterceptor handshakeInterceptor;
	private final ChattingWebsocketHandler chattingWebsocketHandler;

	// 의존성 주입 : 같은 타입 or 상속관계의 타입,
	// 즉 Bean으로 등록된 SessionHandshakeInterceptor가 의존성 주입됌

	// WebSocketConfigurer :
	// Spring에서 웹소켓 통신을 어디에서, 어떤 방식으로 할 것인지
	// 규칙을 정의하는 설정용 인터페이스
	// 1. 핸들러 등록
	// 2. 접속 주소 매핑
	// 3. 부가 기능 설정

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// addHandler(웹소켓핸들러, "웹소켓 요청 주소")
		registry.addHandler(testWebSocketHandler, "/testSock")
				// 클라이언트가 http://localhost/testSock으로 클라이언트 요청을 보내면
				// Websocket 통신으로 변환 후 testWebsocketHandler가 처리하도록 등록
				.addInterceptors(handshakeInterceptor)
				// 클라이언트 연결 시 session을 가로채 핸들러에게 전달하는 handshakeInterceptor 등록
				.setAllowedOriginPatterns("http://localhost/", "http://127.0.0.1/", "http://192.168.132.20")
				// 웹소켓 요청이 허용되는 ip/도메인 지정
				.withSockJS();
		// ------------------------------------------
		registry.addHandler(chattingWebsocketHandler, "/chattingSock").addInterceptors(handshakeInterceptor)
				.setAllowedOriginPatterns("http://localhost/", "http://127.0.0.1/", "http://192.168.132.20")
				.withSockJS();
	}
}
