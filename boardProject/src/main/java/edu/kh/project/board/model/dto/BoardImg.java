package edu.kh.project.board.model.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardImg {
	private int imgNo;
	private String imgPath;
	private String imgOriginalName;
	private String imgRename;
	private int imgOrder; // 0번=섬네일, 1~4는 그냥 이미지
	private int boardNo; // 어느 게시물에 삽입된 이미지인지
	
	// 게시글 이미지를 삽입하거나, 수정할 때 사용해야하는 필드
	private MultipartFile uploadFile;
	
}
