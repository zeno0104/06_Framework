package edu.kh.project.email.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.SpringTemplateEngine;

import edu.kh.project.email.model.mapper.EmailMapper;
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
		if(!storeAuthKey(map)) {
			return null;
		}
		// 2. DB에 저장이 성공된 경우에 메일 발송 시도
		
		return null;
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
}
