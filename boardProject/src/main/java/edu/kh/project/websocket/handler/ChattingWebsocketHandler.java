package edu.kh.project.websocket.handler;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kh.project.chatting.model.dto.Message;
import edu.kh.project.chatting.model.service.ChattingService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChattingWebsocketHandler extends TextWebSocketHandler {

	private final ChattingService service;
	private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

	// 클라이언트와 연결이 완료되고, 통신할 준비가 되면 실행하는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 즉, 채팅방에 입장했다는 의미
		sessions.add(session);
		log.info("{} 연결됨", session.getId());
		// session의 고유 id를 찍어줌
	}

	// 클라이언트와 연결이 종료되면 실행하는 메서드
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		log.info("{} 연결 끊김", session.getId());
	}

	// 클라이언트로부터 텍스트 메시지를 받았을 때 실행하는 메서드
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// message - JS에서 클라이언트로부터 전달받은 내용
		// { "senderNo" : "1", "targetNo" : "2", "chattingRoomNo" : "8",
		// "messageContent" : "하이!"}

		// Jackson 에서 제공하는 객체
		ObjectMapper objectMapper = new ObjectMapper();
		Message msg = objectMapper.readValue(message.getPayload(), Message.class);

		log.info("msg : {}", msg);

		// DB 삽입 서비스 호출
		int result = service.insertMessage(msg);

		if (result > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");
			msg.setSendTime(sdf.format(new Date()));

			// 필드에 있는 sessions에는 채팅 페이지에 접속한 로그인한 모든 회원의 세션
			// -> 이 sessions에서 현재 로그인한 회원, 대상을 구해야함
			for (WebSocketSession s : sessions) {
				// 회원의 세션객체에서 해당 회원의 memberNo를 꺼내와야함
				// 1. 가로챈 session 객체를 꺼내기
				HttpSession temp = (HttpSession) s.getAttributes().get("session");

				// 2. session에서 loginMember로 등록된 Member 객체의 memberNo 꺼내오기
				int loginMemberNo = ((Member) temp.getAttribute("loginMember")).getMemberNo();

				// 3. loginMemberNo 값이 targetNo or senderNo와 일치하는 회원만 선택
				if (loginMemberNo == msg.getTargetNo() || loginMemberNo == msg.getSenderNo()) {
					// 현재 메세지를 보내고 있는 사람, 받는 대상 번호가 선택됌
					// 메세지 전달 (서버쪽에서 해당되는 클라이언트에게 해당 메시지를 전송)
					// JAVA(Message DTO) -> JS(JSON 변환 : JS에게 보내줘야 하기 떄문)
					String jsonData = objectMapper.writeValueAsString(msg);
					s.sendMessage(new TextMessage(jsonData));
				}
			}
		}

	}
}