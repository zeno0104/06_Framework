package edu.kh.project.myPage.model.service;

import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Builder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.mapper.MyPageMapper;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageMapper mapper;

	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Value("${my.profile.web-path}")
	private String profileWebPath; // /myPage/profile/

	@Value("${my.profile.folder-path}")
	private String profileFolderPath; // C:/uploadFiles/profile/

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

	@Override
	public int fileUpload2(MultipartFile uploadFile, int memberNo) throws Exception {
		// MultipartFile이 제공하는 메소드
		// - isEmpty() : 업로드된 파일이 없을경우 true / 있다면 false
		// - getSize() : 파일 크기
		// - getOriginalFileName() : 원본 파일명
		// - transferTo(경로) :
		// 메모리 또는 임시 저장 경로에 업로드된 파일을

		// 업로드된 파일이 없다면
		if (uploadFile.isEmpty()) {
			return 0;
		}

		// 업로드된 파일이 있다면

		// 1. 서버에 저장될 서버 폴더 경로 만들기

		// 파일이 저장될 서버 폴더 경로
		String folderPath = "C:/uploadFiles/test/";

		// 클라이언트가 파일이 저장된 폴더에 접근할 수 있는 주소(요청 주소)
		String webPath = "/myPage/file/";

		// 2. DB에 전달할 데이터를 DTO로 묶어서 INSERT
		// webPath, memberNo, 원본파일명, 변경된파일명

		String fileRename = Utility.fileRename(uploadFile.getOriginalFilename());

		// Builder 패턴을 이용해서 UploadFile 객체 생성
		// 장점 1) 반복되는 참조변수명, set 구문 생략
		// 장점 2) method chaining을 이용하여 한줄로 작성 가능

		UploadFile uf = UploadFile.builder().memberNo(memberNo).filePath(webPath)
				.fileOriginalName(uploadFile.getOriginalFilename()).fileRename(fileRename).build();

		int result = mapper.insertUploadFile(uf);

		// 3. 삽입 (INSERT) 성공 시 파일을 지정된 폴더에 저장
		// 삽입 실패 시
		if (result == 0)
			return 0;

		// 삽입 성공 시
		// C:/uploadFiles/test/변경된파일명으로
		// 파일을 서버 컴퓨터에 저장!
		uploadFile.transferTo(new File(folderPath + fileRename));
		// C:/uploadFiles/test/20251211100330_00001.jpg

		return result;
	}

	/**
	 * 파일 목록 조회 서비스
	 */
	@Override
	public List<UploadFile> fileList(int memberNo) {

		return mapper.fileList(memberNo);
	}

	@Override
	public int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo) throws Exception {
		// 1. aaaList 처리
		int result1 = 0;

		// 업로드된 파일이 없을 경우를 제외하고 업로드
		for (MultipartFile file : aaaList) {
			if (file.isEmpty())
				// 파일이 없으면 다음 파일로 검사하러 가기
				continue; // 아래 코드 수행 X 다음 반복으로 넘어감...

			// fileUpload2() 메서드 호출(재활용)
			// -> 파일 하나 업로드 + DB INSERT
			result1 += fileUpload2(file, memberNo);
		}

		// 2. bbbList 처리
		int result2 = 0;

		// 업로드된 파일이 없을 경우를 제외하고 업로드
		for (MultipartFile file : bbbList) {
			if (file.isEmpty())
				// 파일이 없으면 다음 파일로 검사하러 가기
				continue; // 아래 코드 수행 X 다음 반복으로 넘어감...

			// fileUpload2() 메서드 호출(재활용)
			// -> 파일 하나 업로드 + DB INSERT
			result2 += fileUpload2(file, memberNo);
		}

		return result1 + result2;
	}

	/**
	 * 프로필 이미지 변경 서비스
	 */
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws Exception {
		// 프로필 이미지 경로(수정할 경로)
		String updatePath = null;

		// 변경명 저장
		String rename = null;

		// 업로드한 이미지가 있을 경우
		if (!profileImg.isEmpty()) {
			// updatePath 경로 조합
			// filePath, fileRename을 조합해서 member에 넣을 것

			// 1. 파일명 변경
			rename = Utility.fileRename(profileImg.getOriginalFilename());

			// 2. /myPage/profile/변경된파일명
			updatePath = profileWebPath + rename;
		}

		// 수정된 프로필 이미지 경로와 어떤 회원의 프로필을 수정할 것인지 알아야함
		// => 즉, 수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체를 보내줄 것
		Member member = Member.builder().memberNo(loginMember.getMemberNo()).profileImg(updatePath).build();

		// UPDATE 수행
		int result = mapper.profile(member);

		if (result > 0) { // DB에 업데이트 성공
			// 프로필 이미지를 없앤 경우(NULL로 수정한 경우를 의미)를 제외한 경우
			// -> 즉, 업로드한 이미지가 있을 경우
			if (!profileImg.isEmpty()) {
				// 파일을 서버 지정된 폴더에 저장
				profileImg.transferTo(new File(profileFolderPath + rename));
				// C:/uploadFiles/profile/변경한이름
			}
			// 세션에 등록된 현재 로그인한 회원 정보에서
			// 프로필 이미지 경로를 DB에 업데이트한 경로로 변경
			loginMember.setProfileImg(updatePath);
		}
		return result;
	}

}
