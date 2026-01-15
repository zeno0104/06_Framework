package edu.kh.project.admin.model.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.admin.model.mapper.AdminMapper;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor

public class AdminServiceImpl implements AdminService {
	private final AdminMapper mapper;
	private final BCryptPasswordEncoder bcrypt;

	@Override
	public Member login(Member inputMember) {
		Member loginMember = mapper.login(inputMember.getMemberEmail());

		if (loginMember == null)
			return null;

		if (!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw()))
			return null;

		loginMember.setMemberPw(null);
		return loginMember;
	}

	/**
	 * 관리자 이메일 중복 여부 검사 서비스
	 */
	@Override
	public int checkEmail(String memberEmail) {

		return mapper.checkMmail(memberEmail);
	}

	/**
	 * 관리자 계정 발급 서비스
	 */
	@Override
	public String createAdminAccount(Member member) {
		// 1. 영어(대소문자), 숫자도 포함 6자리 난수로 만든
		// 비밀번호를 평문/암호화 한 값 구하기

		String rawPw = Utility.generatePassword(); // 평문 비번

		// 2. 평문 비밀번호 암호화하여 저장
		String encPw = bcrypt.encode(rawPw);

		// 3. member에 암호화된 비밀번호 세팅
		member.setMemberPw(encPw);

		// 4. DB에 암호화된 비밀번호가 세팅된 member를 전달하여 계정 발급
		int result = mapper.createAdminAccount(member);

		// 5. 계정 발급이 정상처리 되었으면, 발급된(평문) 리턴
		if (result > 0) {
			return rawPw;
		} else {
			return null;
		}
	}

	@Override
	public List<Member> adminAccountList() {
		return mapper.adminAccountList();
	}

	@Override
	public Board maxReadCount() {
		return mapper.maxReadCount();
	}

	@Override
	public Board maxLikeCount() {
		return mapper.maxLikeCount();
	}

	@Override
	public List<Member> selectWithdrawnMemberList() {
		return mapper.selectWithdrawnMemberList();
	}

	@Override
	public int restoreMember(int memberNo) {
		return mapper.restoreMember(memberNo);
	}

	@Override
	public List<Board> deletedBoard() {
		return mapper.deletedBoard();
	}

	/**
	 * 삭제 게시글 복구
	 */
	@Override
	public int restoreBoard(int boardNo) {
		return mapper.restoreBoard(boardNo);
	}

	/**
	 * 댓글 갯수 가장 많은 게시글
	 */
	@Override
	public Board maxCommentCount() {
		return mapper.maxCommentCount();
	}
}
