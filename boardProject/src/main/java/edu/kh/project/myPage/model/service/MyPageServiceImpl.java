package edu.kh.project.myPage.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageMapper mapper;

	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		Map<String, Object> map = new HashMap<>();

		// 입력된 주소가 있을 경우
		// A^^^B^^^C 형태로 가공

		// 주소가 입력이 되었을 때
		if (!inputMember.getMemberAddress().equals(",,")) {
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
		} else {
			// 주소가 입력되지 않았을 때
			inputMember.setMemberAddress(null);
		}

		// inputMember : 수정 닉네임, 수정 전화번호, 수정 주소, 회원 번호
		return mapper.updateInfo(inputMember);
	}

	@Override
	public int changePw(Member loginMember, String currentPw, String newPw) {
		// 기본 DB에 저장된 member
		Member currMember = mapper.getCurrMember(loginMember);

		// 현재 비밀번호와 DB에 위치한 비밀번호가 일치하지 않을 때
		if (!bcrypt.matches(currentPw, currMember.getMemberPw())) {
			return 0;
		}

		String pw = bcrypt.encode(newPw);
		currMember.setMemberPw(pw);

		return mapper.updatePw(currMember);
	}

}
