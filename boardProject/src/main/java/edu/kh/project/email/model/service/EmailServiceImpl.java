package edu.kh.project.email.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import edu.kh.project.email.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final EmailMapper mapper;
	private final JavaMailSender mailSender;
	// JavaMailSender 정의한 config에 bean으로 등록되어있음
	// JavaMailSender : 실제 메일 발송을 담당하는 객체(EmailConfig 참고)
	private final SpringTemplateEngine templateEngine;
	// SpringTemplateEngine : 타임리프를 이용해서 html 코드를 Java 코드로 변환시켜줌

	@Override
	public String sendEmail(String type, String email) {
		// 1. 인증키 생성 및 DB에 저장
		String authKey = createAuthKey();
		Map<String, String> map = new HashMap<String, String>();

		map.put("email", email);
		map.put("authKey", authKey);

		// DB 저장 시도 - 실패 시 해당 메서드 종료
		if (!storeAuthKey(map)) {
			return null;
		}
		// 2. DB에 저장이 성공된 경우에 메일 발송 시도

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		// 메일 발송 시 사용하는 객체

		try {
			// 메일 발송을 도와주는 Helper 클래스 사용해서 파일 첨부 or html 등 템플릿으로 설정해서 보낼지
			// 쉽게 처리할 수 있도록 도와주는 클래스

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			// - mimeMessage : MimeMessage 객체로, 이메일 메시지의 내용을 담고있음
			// (이메일의 본문, 제목, 수신자 정보 등을 포함하고 있음)
			// - true : 파일 첨부를 사용할 지 말지에 대한 여부를 지정해주는 것
			// (파일첨부 및 내부 이미지 삽입 가능)
			// - "UTF-8" : 이메일 내용이 UTF-8 인코딩으로 전송

			// 메일 기본 정보 셋팅
			helper.setTo(email); // 받는 사람(수신자)
			helper.setSubject("[boardProject] 회원 가입 인증번호 입니다."); // 제목 세팅
			helper.setText(loadHtml(authKey, type), true);
			// setText => html을 보내고 싶을 때, 첫번째는 html결과, 그리고 true는 html 요소를
			// 전달할지 말지에 대한 매개변수
			// HTML 내용 설정

			// helper.setText("인증번호 입니다 : " + authKey);
			// 인증번호 입니다 : agdsey

			// 메일에 이미지를 첨부할 것인데, 어떤 이미지를 첨부할 지 넣는것(여기선 로고를 넣을 것)
			helper.addInline("logo", new ClassPathResource("static/images/logo.jpg"));

			// 실제 메일 발송
			mailSender.send(mimeMessage);
			
			return authKey;
		} catch (Exception e) {
			e.printStackTrace();
			return null; // 메일 발송 실패 시 null 반환
		}

	}

	// HTML 템플릿에 데이터를 넣어서 최종 HTML 생성
	private String loadHtml(String authKey, String type) {
		// Context(org.thymeleaf.context.Context)
		// : 타임리프에서 제공하는 HTML 템플릿에
		// 데이터를 전달하기 위해 사용하는 클래스
		Context context = new Context();
		context.setVariable("authKey", authKey);
		// signup.html이라면, ${authKey}로 해당 html에 타임리프 문법인 th:text로 받을 수 있음
		return templateEngine.process("email/" + type, context);
		// 접두사(src/main/resources/) + email/signup + 접미사(.html)
		// 자바 코드로 바꿔야 하기 떄문에, tempaleEngine.procese가 진행해줌
	}

	// 인증키와 이메일을 DB에 저장하는 메서드
	// DML이 이곳에서 일어나기 때문에, 즉 트랜잭션(commit, rollback)이 이곳에서 일어나기 때문에
	// class에서 전체로 관리할 필요 없이 메서드 내에서 관리해도 된다.
	@Transactional(rollbackFor = Exception.class) // 메서드 레벨에서도 이용 가능
	public boolean storeAuthKey(Map<String, String> map) {
		// 1. 기존 이메일에 대한 인증키 update 수행
		int result = mapper.updateAuthKey(map);

		// 2. update 실패 시 insert 수행
		if (result == 0) {
			result = mapper.insertAuthKey(map);
		}
		// 3. 성공 여부 반환 (true / false)
		return result > 0;
	}

	// 인증번호 발급 메서드
	// UUID를 사용하여 인증키 생성
	// (Universally Unique Identifier)
	// 전세계에서 고유한 식별자를 생성하기위한 표준
	// 매우 낮은 확률로 중복되는 식별자를 생성
	// 주로 데이터베이스 기본키, 고유한 식별자를 생성해야 할 때 사용
	public String createAuthKey() {
		return UUID.randomUUID().toString().substring(0, 6);
	}

	@Override
	public int checkAuthKey(Map<String, String> map) {
		return mapper.checkAuthKey(map);
	}
}
