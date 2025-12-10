package edu.kh.project.myPage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

	@Override
	public int secession(int memberNo, String memberPw) {

		// 1. 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회
		String encPw = mapper.selectPw(memberNo);

		// 2. 입력받은 비밀번호와 암호화된 DB에 저장된 비밀번호가 같은지 비교

		// 다를 경우
		if (!bcrypt.matches(memberPw, encPw)) {
			return 0;
		}

		// 같은 경우
		return mapper.secession(memberNo);
	}

	/**
	 * 파일 업로드 테스트1
	 */
	@Override
	public String fileUpload1(MultipartFile uploadFile) throws Exception {
		// uploadFile에 파일이 업로드 됐는지부터 확인
		if (uploadFile.isEmpty()) {
			// 업로드한 파일이 없을 경우(즉, 껍데기만 넘어온 경우)
			return null;
		}

		// 업로드한 파일이 있을 경우
		// C:/uploadFiles/test/파일명으로 서버에 저장
		uploadFile.transferTo(new File("C:/uploadFiles/test/" + uploadFile.getOriginalFilename()));
		// 하기와.jpg로 해당 경로에 저장하겠다는 의미

		// 웹에서 해당 파일에 접근할 수 있는 경로를 만들어 반환
		// 이미지가 최종 저장된 서버 컴퓨터상의 경로
		// C:/uploadFiles/test/파일명
		
		// 클라이언트가 브라우저에 해당 이미지를 보기위해 요청하는 경로
		// <img src="경로">
		// /myPage/file/파일명.jpg -> <img src="/myPage/file/파일명.jpg">
		
		return "/myPage/file/" + uploadFile.getOriginalFilename();
	}

}
