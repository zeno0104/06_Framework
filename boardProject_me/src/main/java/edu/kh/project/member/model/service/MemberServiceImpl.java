package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

// 런타임 예외가 일어났을 때, 롤백
// 하지만, 다른 이외의 예외가 발생했을 때도 롤백하고싶어서
// 예외의 최상위 부모인 Exception을 작성함
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {
	@Autowired // 의존성 주입(DI)
	private MemberMapper mapper;
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
		// String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		// log.debug("bcryptPassword : " + bcryptPassword);
		// bcryptPassword : $2a$10$Vv6Dynp77iFNG5Q4VS9o6.xsj8ibwJ67VOPn/mQIm3GvbAGv6hfZC
		// 이런식으로 바뀜
		// 같은 비밀번호로 바꿔도 다른 패턴으로 나오게 됌

		// 1. 이메일이 일치하면서 탈퇴하지 않은 회원의 비밀번호 조회
		Member loginMember = mapper.login(inputMember.getMemberEmail());

		// 2. 만약에 일치하는 이메일이 없어서 조회결과가 null일경우
		if (loginMember == null)
			return null;

		// 3. 입력 받은 비밀번호(평문 : 암호화가 되지않은 일반 문장, inputMember.getMemberPw()) 와
		// 암호화된 비밀번호(loginMember.getMemberPw())
		// 두 비밀번호가 일치하는지 확인

		// bcrypt.matches(평문, 암호화된 문자) : 평문과 암호화가 내부적으로
		// 일치한다고 판단이 되면 true, 아니면 false

		// 일치하지 않으면
		if (!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}
		// 일치할 때
		
		// 로그인한 회원 정보에서 비밀번호 제거
		loginMember.setMemberPw(null);
		return loginMember;
	}
}
