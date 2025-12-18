package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.EditBoardService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("editBoard")
@RequiredArgsConstructor
@Slf4j
public class EditBoardController {

	private final EditBoardService service;

	@GetMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode) {

		return "board/boardWrite"; // src/main/resource/templates/board/boardWrite.html로 forward
	}

	/**
	 * @param boardCode
	 * @param inputBoard  : 입력된 값(제목, 내용) 세팅되어 있음 (커맨드 객체)
	 * @param loginMember : 로그인한 회원 번호를 얻어오는 용도(세션에 등록되어있음)
	 * @param images      : 제출된 file 타입 input태그가 전달한 데이터들 (이미지 파일..)
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode, Board inputBoard,
			@SessionAttribute("loginMember") Member loginMember, @RequestParam("images") List<MultipartFile> images,
			RedirectAttributes ra) throws IllegalStateException, IOException {
		log.debug("images : " + images);
		// [MultipartFile, MultipartFile,MultipartFile,MultipartFile,MultipartFile]
		// images에 사진이 아무것도 업로드 되지 않았을 때 MultipartFile이 넘어오는데 빈값(empty)으로 넘어온다
		/*
		 * ** List<MultipartFile> images는 실제 제출된 파일이 있던 없던 무조건 길이 5의 MultipartFile이 요소로
		 * 있는 List가 제출됨 **
		 * 
		 * - 5개 모두 업로드 O -> 0 ~ 4 인덱스에 실제 파일 들어간 MultipartFile이 저장됨 - 5개 모두 업로드 X -> 0 ~
		 * 4 인덱스에 비어있는 MultipartFile이 저장됨 - 2번 인덱스만 업로드 된다면 -> 2번 인덱스 파일이 저장된
		 * MultipartFile, 나머지 0, 1, 3, 4번 인덱스에는 MultipartFile 비어있음
		 * 
		 * - 무작정 서버에 저장 X -> List의 각 인덱스에 들어있는 MultipartFile에 실제로 제출된 파일이 있는지 확인하는 로직을
		 * 구성
		 * 
		 * + List 요소의 index 번호 == DB상 BOARD_IMG 테이블의 IMG_ORDER와 같음
		 * 
		 * 
		 * 
		 */

		// 1. boardCode, 로그인한 회원 번호를 inputBoard에 세팅
		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		// -> inputBoard 총 네가지 세팅됨(boardTitle, boardContent, boardCode, memberNo)

		// 2. 서비스 호출 후 결과 반환 받기
		// -> 성공 시 [상세 조회]를 요청할 수 있도록
		// 삽입된 게시글 번호를 반환받기

		// 등록하고자 하는 최종 게시글 번호
		// redirect를 해줘야 하기 때문
		int boardNo = service.boardInsert(inputBoard, images);

		// 3. 서비스 결과에 따라 message, 리다이렉트 할 경로 지정
		String path = null;
		String message = null;

		if (boardNo > 0) {
			path = "/board/" + boardCode + "/" + boardNo;
			message = "게시글이 작성되었습니다!";
		} else {
			path = "insert"; // 상대경로 / editBoard/1/insert -> redirect이므로 get
			// 다시 작성하고있는 페이지로 돌아가기
			message = "게시글 작성 실패";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}
}
