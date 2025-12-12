package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.mapper.BoardMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardMapper mapper;

	// 게시판 종류 조회 서비스
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		// {BOARD_CODE : 1, BOARD_NAME: 공지게시판}
		// {BOARD_CODE : 2, BOARD_NAME: 정보게시판}
		// 자바단에서는 boardCode, boardName 와 같이 카멜케이스로
		// 즉, DB단에서 별칭을 줘야한다는 의미

		return mapper.selectBoardTypeList();
	}
}
