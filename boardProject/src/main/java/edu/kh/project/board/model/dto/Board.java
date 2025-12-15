package edu.kh.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
	// BOARD 테이블 컬럼
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriteDate;
	private String boardUpdateDate;
	private int readCount; // 조회수
	private String boardDelFl; // 삭제 여부
	private int boardCode;
	private int memberNo;
	// 작성자, 좋아요, 댓글수 추가적으로 필요

	// MEMBER 테이블 조인
	private String memberNickname;

	// 목록 조회 시 서브쿼리 필드
	private int commentCount; // 댓글 수
	private int likeCount; // 좋아요 수
	// 추후에 작성자 옆에 프로필 사진 넣고 싶으면 추가하면 됌
	// 제목 옆에 썸네일 넣고 싶다면 썸네일 추가하면 됌
}
