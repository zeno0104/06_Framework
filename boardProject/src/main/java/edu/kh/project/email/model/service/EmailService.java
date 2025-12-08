package edu.kh.project.email.model.service;

public interface EmailService {

	/**
	 * 이메일 보내기 서비스
	 * 
	 * @param type  : 무슨 이메일을 발송할지 구분할 key로 쓰임
	 * @param email :
	 * @return
	 */
	String sendEmail(String type, String email);
}
