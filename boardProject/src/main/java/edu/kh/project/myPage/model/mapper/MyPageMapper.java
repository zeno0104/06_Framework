package edu.kh.project.myPage.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;

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

	/**
	 * 파일 정보를 DB에 삽입 SQL (insert)
	 * 
	 * @param uf
	 * @return
	 */
	int insertUploadFile(UploadFile uf);

	/**
	 * 파일 목록 조회하는 SQL
	 * 
	 * @param memberNo
	 * @return
	 */
	List<UploadFile> fileList(int memberNo);

	/**
	 * 프로필 이미지 변경 SQL
	 * 
	 * @param member
	 * @return
	 */
	int profile(Member member);
}