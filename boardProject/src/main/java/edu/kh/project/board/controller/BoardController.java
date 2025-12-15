package edu.kh.project.board.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.project.board.model.service.BoardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("board")
@Slf4j
public class BoardController {
	@Autowired
	private BoardService service;

	/**
	 * 게시글 목록 조회
	 * 
	 * {boardCode} - /board/xxx : /board 이하 1레벨 자리에 어떤 주소값이 들어오든 모두 이 메서드에 매핑하겠다는 것
	 * 
	 * /board/ 이하 1레벨 자리에 숫자로 된 요청 주소가 작성되어 있을 때만 동작 -> 정규표현식으로 제한해주면 된다.
	 * 
	 * {boardCode:[0-9]+} - [0-9] : 한 칸에 0~9사이 숫자 입력 가능 - [0-9]+ : 모든 숫자 가능
	 * 
	 * @param boardCode   : 게시판 종류 구분 번호 (1, 2, 3) => 공지, 정보, 자유게시판
	 * @param cp          : 현재 조회 요청한 페이지 번호(없으면 1)
	 * @param paramMap(검색 시 이용) : 제출된 파라미터가 모두 저장된 Map 검색 시, key, query 담겨있음 ex)
	 *                    {key=t, query=폭탄}
	 * 
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}")
	// /board/1, 2, 3 -> 뒤 숫자 받을 수 있도록
	// 쓰고싶은 변수명을 작성
	// 위처럼 작성하면 /board/list 이렇게도 받을 수도 있다는 것
	// 숫자만 받아주겠다면 :[] 범위를 작성 -> 0-9는 0~9까지 숫자가 들어올 수 있음
	// 10부터는 [0-9]+, 즉 모든 숫자를 다 받아주겠다는 의미
	public String selectBoardList(@PathVariable("boardCode") int boardCode,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@RequestParam Map<String, Object> paramMap) {
		// paramMap => 검색시 {key=t, query=폭탄}
		// 검색 X => {} -> 빈 상태

		// src/main/resources/templates/board/boardList.html

		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = null;

		// 검색으로 조회하지 않을 경우
		if (paramMap.get("key") == null) {
			// 게시글 목록 조회 서비스 호출
			map = service.selectBoardList(boardCode, cp); // cp : 페이지네이션 객체 만들때 필요
		} else {
			// 검색으로 조회할 경우

			// 검색(내가 검색하고 싶은 게시글 목록 조회) 서비스 호출
		}
		// model에 결과 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));

		// src/main/resources/templates/board/boardList.html로 forward
		return "board/boardList";
	}

}
