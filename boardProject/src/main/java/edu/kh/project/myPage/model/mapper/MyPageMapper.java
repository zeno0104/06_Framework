package edu.kh.project.myPage.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MyPageMapper {

	/**
	 * 회원 정보 수정 SQL
	 * 
	 * @param inputMember
	 * @return
	 */
	int updateInfo(Member inputMember);

	// 추후 수정
	Member getCurrMember(Member loginMember);

	int updatePw(Member currMember);

	String selectPw(int memberNo);

	int secession(int memberNo);
}