package edu.kh.project.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.service.CommentService;

// @Controller // Controller 역할임을 명시해주고 Bean으로 등록
// 비동기, 동기든 모두 받아줄 수 있는 controller다 라는 의미
// @Controller(Controller 역할임을 명시, 즉 요청/응답 제어 역할 + Bean 등록) + @ResponseBody(응답 본문으로 응답데이터 자체를 반환) 합친 것
// => @RestController

// -> @RestController는 모든 요청에 대한 응답을 응답 본문으로 반환하는 컨트롤러

@RestController // REST API 구축을 위해서 사용하는 컨트롤러(@Controller + @RestController)
// 즉, 비동기 연산을 처리해줄 수 있다는 것
@RequestMapping("comment") // /comment로 시작하는 모든 요청 매핑을 받아줌
public class CommentController {
	
	@Autowired
	private CommentService service;
	
	/** 댓글 목록 조회
	 * @return
	 */
	@GetMapping("")
	public List<Comment> select(@RequestParam("boardNo") int boardNo){
		
		return service.select(boardNo);
	}
	
	/** 댓글/답글 등록 
	 * 댓글 - 1레벨
	 * 답글 - 최소 2레벨 -> 답글일 때는 parentCommentNo가 추가됨
	 * @param comment : 비동기 연산시 body로 전달되는 값
	 * @return
	 */
	@PostMapping("")
	public int insert(@RequestBody Comment comment) {
		return service.insert(comment);
	}
	
	/** 댓글 삭제
	 * @return
	 */
	@DeleteMapping("")
	public int delete(@RequestBody int commentNo) {
		return service.delete(commentNo);
	}
	
	@PutMapping("")
	public int update(@RequestBody Comment comment) {
		return service.update(comment);
	}
}
