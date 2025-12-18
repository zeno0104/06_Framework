package edu.kh.project.board.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;

public interface EditBoardService {

	/** 게시글 작성 서비스
	 * @param inputBoard
	 * @param images
	 * @return
	 */
	int boardInsert(Board inputBoard, List<MultipartFile> images) throws IllegalStateException, IOException ;

}
