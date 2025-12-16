package edu.kh.project.main.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.main.model.mapper.MainMapper;
import edu.kh.project.member.model.dto.Member;

@Service
@Transactional(rollbackFor = Exception.class)
public class MainServiceImpl implements MainService {

	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Autowired
	private MainMapper mapper;

	@Override
	public List<Member> getAllUser() {

		return mapper.getAllUser();
	}

	@Override
	public int resetPw(int memberNo) {
		Member member = mapper.getUser(memberNo);

		if (member == null) {
			return 0;
		}
		String encPw = bcrypt.encode("pass01!");

		Map<String, Object> map = new HashMap<>();
		map.put("memberNo", memberNo);
		map.put("encPw", encPw);

		return mapper.resetPw(map);
	}

	@Override
	public int restorationMember(int memberNo) {
		Member member = mapper.getUser(memberNo);

		if (member == null) {
			return 0;
		}
		return mapper.restorationMember(memberNo);
	}
}
