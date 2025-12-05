package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

// 런타임 예외가 일어났을 때, 롤백
// 하지만, 다른 이외의 예외가 발생했을 때도 롤백하고싶어서
// 예외의 최상위 부모인 Exception을 작성함
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

	// Bcrypt 암호화 객체 의존성 주입 (SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	/**
	 * 로그인 서비스
	 */
	@Override
	public Member login(Member inputMember) throws Exception {
		// 암호화 진행
		// bcrypt.encode(문자열) : bcrypt 패턴의 문자열을 암호화하여 반환
		String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		log.debug("bcryptPassword : " + bcryptPassword);
		// bcryptPassword : $2a$10$Vv6Dynp77iFNG5Q4VS9o6.xsj8ibwJ67VOPn/mQIm3GvbAGv6hfZC
		// 이런식으로 바뀜
		// 같은 비밀번호로 바꿔도 다른 패턴으로 나오게 됌
		return null;
	}

}
