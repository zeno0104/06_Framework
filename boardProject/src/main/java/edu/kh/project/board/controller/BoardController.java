package edu.kh.project.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
			// 검색으로 조회할 경우 -> paramMap에 key라는 k에 접근하면 매핑된 value 반환
			// -> ex) {key=w, query=짱구}
			// -> w 반환됨

			// boardCode를 paramMap에 추가
			paramMap.put("boardCode", boardCode);
			// -> paramMap은 {key=w, query=짱구, boardCode=1}
			// 검색(내가 검색하고 싶은 게시글 목록 조회) 서비스 호출
			map = service.searchList(paramMap, cp);

		}
		// model에 결과 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));

		// src/main/resources/templates/board/boardList.html로 forward
		return "board/boardList";
	}

	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model,
			RedirectAttributes ra, HttpServletRequest req, HttpServletResponse resp) {
		// req : 요청에 담긴 쿠키 얻어오기
		// resp : 쿠키 응답하기

		// 게시글 상세 조회 서비스 호출
		// loginMember는 좋아요를 로그인한 사람이 눌렀는지 확인하기 위해서 세션에서 가져옴
		// value, required로 loginMember를 가져온 이유는
		// 없다면 로그인을 꼭 했을 때를 의미하고, 위 조건을 추가한 이유는
		// 로그인 안한 사람들도 접근할 수 있는 페이지여야 하기 때문

		// 1) Map으로 전달 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);

		// 로그인 상태인 경우에만 memberNo를 map에 추가
		// LIKE_CHECK시 이용 (로그인한 사람이 좋아요 누른 게시글인지 체크하기 위함)
		if (loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}

		// 2) 서비스 호출

		Board board = service.selectOne(map);

		// log.debug("조회된 board : " + board);

		String path = null;

		// 조회 결과가 없는 경우
		if (board == null) {
			path = "redirect:/board/" + boardCode;
			// 내가 현재 보고있는 게시판목록으로 재요청
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다");
		} else { // 조회 결과가 있는 경우

			/* ------------------ 쿠키를 이용한 조회수 시작 ------------------ */
			// 비회원 또는 로그인한 회원의 글이 아닌 경우 (== 글쓴이를 뺀 다른 사람) => 조회
			if (loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {
				Cookie[] cookies = req.getCookies();

				Cookie c = null;

				for (Cookie temp : cookies) {
					// 쿠키 중에 "readBoardNo" 가 존재할 때
					if (temp.getName().equals("readBoardNo")) {
						c = temp;
						break;
					}
				}

				int result = 0; // 조회수 증가 결과를 저장할 변수

				if (c == null) {
					// "readBoardNo" 쿠키가 쿠키에 없을 때

					// 새 쿠키 생성("readBoardNo", [2000][3000][1000] => [게시글번호])
					// [2000] : boardNo
					c = new Cookie("readBoardNo", "[" + boardNo + "]");
					result = service.updateReadCount(boardNo);

					if (result > 0) {
						// 성공
					} else {
						// 실패
					}

				} else {
					// "readBoardNo" 쿠키가 쿠키에 있을 때
					// k : v
					// readBoardNo : [2][30][400]

					// 현재 글을 처음 읽는 경우
					if (c.getValue().indexOf("[" + boardNo + "]") == -1) {
						// indexOf가 -1이면 없는 경우
						// 해당 글 쿠키에 누적 + 서비스 호출
						// 즉, 현재 읽고있는 게시글을 처음 읽는 경우
						c.setValue(c.getValue() + "[" + boardNo + "]");
						// ex) [2][30][400][2000][4000]
						result = service.updateReadCount(boardNo);
						// 조회수 업데이트
					}
				}
				// 조회수 증가 성공 / 조회 성공 시
				if (result > 0) {
					// 조회수 성공 및 조회수 가져왔을 때
					// 앞서 조회했던 board의 readCount 값을
					// 현재 가져온 result로 가져와서 다시 세팅해준다.
					board.setReadCount(result);

					// 쿠키 적용 경로 설정
					// "/" 이하 경로 요청 시 쿠키를 서버로 전달
					c.setPath("/");

					// 쿠키 수명 지정
					// 현재 시간을 얻어오기
					LocalDateTime now = LocalDateTime.now();
					// 다음날 자정 지정
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

					// 다음날 자정까지 남은 시간 계산 (초단위)
					// Duration은 남은 시간을 계산
					long secondsUntilNextDay = Duration.between(now, nextDayMidnight).getSeconds();
					
					// 쿠키 수명 설정
					c.setMaxAge((int)secondsUntilNextDay);
					
					// 응답객체(resp)을 이용해서 클라이언트에게 전달
					resp.addCookie(c);
				}
			}

			/* ------------------ 쿠키를 이용한 조회수 끝 ------------------ */

			path = "board/boardDetail";
			// src/main/resources/tempaltes/board/boardDetail.html

			// board - 게시글 일반 내용 + imageList + commentList
			model.addAttribute("board", board);

			// 조회된 이미지 목록(imageList)이 있을 경우
			if (!board.getImageList().isEmpty()) {
				BoardImg thumbnail = null;
				// imageList의 0번 인덱스 == IMG_ORDER가 가장 빠른 순서

				// 만약 이미지 목록의 0번째 요소의 IMG_ORDER가 0이면 == 썸네일
				if (board.getImageList().get(0).getImgOrder() == 0) {
					thumbnail = board.getImageList().get(0);
					// board.getImageList() -> List<BoardImg>
				}
				// thumbnail 변수에는
				// - 이미지 목록의 0번째 요소가 썸네일이면 썸네일 이미지의 BoardImg 객체
				// - 썸네일이 아니라면 null
				model.addAttribute("thumbnail", thumbnail);

				// start라는 key에 thumbnail이 null이 아닐 때 1 저장, null이면 0 저장
				model.addAttribute("start", thumbnail != null ? 1 : 0);
				// 썸네일 있을 때 : start=1
				// 썸네일 없을 때(일반이미지만 있거나, 등록된 이미지가 아예 없을 때)
				// : start=0
			}
		}
		return path;
	}
	
	/** 게시글 좋아요 체크/해제 (비동기)
	 * @param map : boardNo, memberNo, likeCheck가 들어있는 map
	 * @return
	 */
	@PostMapping("like")
	@ResponseBody // /board/like (POST) 요청 매핑
	public int boardLike(@RequestBody Map<String, Integer> map) {
		// Board DTO로 받을 수도 있고, Map으로 받을 수 있다.
		
		return service.boardLike(map);
	}

}
