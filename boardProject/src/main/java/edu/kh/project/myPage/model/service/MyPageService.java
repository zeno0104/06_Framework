package edu.kh.project.myPage.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;

public interface MyPageService {

	/**
	 * 회원 정보 수정 서비스
	 * 
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int updateInfo(Member inputMember, String[] memberAddress);

	int changePw(Member loginMember, String currentPw, String newPw);

	/**
	 * 회원 탈퇴 서비스
	 * 
	 * @param memberNo
	 * @param memberPw
	 * @return
	 */
	int secession(int memberNo, String memberPw);

	/**
	 * 파일 업로드 테스트1
	 * 
	 * @param uploadFile
	 * @return
	 */
	String fileUpload1(MultipartFile uploadFile) throws Exception;

	int fileUpload2(MultipartFile uploadFile, int memberNo) throws Exception;

	/**
	 * 파일 목록 서비스
	 * 
	 * @param memberNo
	 * @return
	 */
	List<UploadFile> fileList(int memberNo);

	/**
	 * 여러 파일 업로드 서비스
	 * 
	 * @param aaaList
	 * @param bbbList
	 * @param memberPw
	 * @return
	 */

	int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo) throws Exception;

	int profile(MultipartFile profileImg, Member loginMember) throws Exception;

}
