package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
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

	private final BoardService boardService;

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

	/**
	 * 게시글 수정 화면 전환
	 * 
	 * @param boardCode
	 * @param boardNo
	 * @param loginMember : 로그인한 회원이 작성한 글이 맞는지
	 * @param ra          : 글 작성 실패했을 때, redirect시 메세지 전달하기 위함
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			@SessionAttribute("loginMember") Member loginMember, Model model, RedirectAttributes ra) {
		// 수정 화면에 출력할 기존의 제목/내용/이미지 조회
		// -> 게시글 상세 조회 서비스
		Map<String, Integer> map = new HashMap<>();

		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);

		// BoardService.selectOne(map) 호출 결과값 반환 받기
		Board board = boardService.selectOne(map);
		// memberNo는 안필요한지?
		// selectOne에서 boardLike에 대한 조건을 처리하는데,
		// 필요하지 않은 데이터임 -> 굳이 필요없는 데이터는 안넣어도 됌

		String message = null;
		String path = null;

		if (board == null) {
			message = "해당 게시글이 존재하지 않습니다";
			// get방식이기 떄문에, 사용자가 일부로 url을 조작할 수 있기 때문에,
			// 조건문 처리를 필수적으로 해줘야 한다.
			path = "redirect:/";

			ra.addFlashAttribute("message", message);
		} else if (board.getMemberNo() != loginMember.getMemberNo()) {
			// board는 가져왔는데, 로그인한 사람과, 게시글을 작성한 회원의 번호가 같지 않을떄의 상황을 처리
			message = "자신이 작성한 글만 수정할 수 있습니다";

			// 해당 글 상세조회 리다이렉트 (/board/1/2000)
			path = String.format("redirect:/board/%d/%d", boardCode, boardNo);
			ra.addFlashAttribute("message", message);
		} else {
			path = "board/boardUpdate"; // forward templates/board/boardUpdate.html
			model.addAttribute("board", board);
		}

		return path;
	}

	/**
	 * 게시글 수정
	 * 
	 * @param inputBoard      : 커맨드 객체(제목, 내용)
	 * @param images          : 제출된 type="file"인 모든 요소
	 * @param deleteOrderList : 삭제된 이미지 순서가 기록된 문자열 (ex. "1,2,3")
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			Board inputBoard, @RequestParam("images") List<MultipartFile> images,
			@RequestParam(value = "deleteOrderList", required = false) String deleteOrderList,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) throws Exception {

		// 1. 커맨드 객체(inputBoard)에 boardCoe, boardNo, memberNo를 세팅
		inputBoard.setBoardCode(boardCode);
		inputBoard.setBoardNo(boardNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		// inputBoard -> 제목, 내용, boardCode, boardNo, 회원번호 세팅됨

		// 2. 게시글 수정 서비스 호출 후 결과 반환 받기
		int result = service.boardUpdate(inputBoard, images, deleteOrderList);

		// 3. 서비스 결과에 따라 응답 제어
		String message = null;
		String path = null;

		if (result > 0) {
			message = "게시글이 수정되었습니다";
			path = String.format("/board/%d/%d?cp=%d", boardCode, boardNo, cp);
			// /board/1/2000?cp=3
		} else {
			message = "수정 실패";
			path = "update";
			// 1/2000/update -> 즉, GetMapping으로 받겠다는 것 => 다시한번 시도해보라는 것
			// GET (수정 화면 전환)

		}
		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}

	// 삭제할 때 BOARD_DEL_FL, 즉 진짜 삭제하지말고 UPDATE하라는 것
	// /editBoard/2/1997/delete?cp=1
	@RequestMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete")
	public String boardDelete(Board board, @SessionAttribute("loginMember") Member loginMember,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, RedirectAttributes ra)
			throws Exception {
		
		int result = service.boardDelete(board, loginMember);

		String message = null;
		String path = null;

		if (result > 0) {
			// 삭제 성공
			message = "삭제에 성공했습니다!";
			path = "/board/" + String.format("%d?cp=%d", board.getBoardCode(), cp);
		} else {
			// 삭제 실패
			message = "삭제에 실패했습니다...";
			path = "/board/" + String.format("%d/%d?cp%d", board.getBoardCode(), board.getBoardNo(), cp);
		}
		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}

}
