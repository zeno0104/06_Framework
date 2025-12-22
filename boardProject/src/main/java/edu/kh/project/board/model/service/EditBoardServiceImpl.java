package edu.kh.project.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")

@RequiredArgsConstructor
public class EditBoardServiceImpl implements EditBoardService {

	private final EditBoardMapper mapper;

	@Value("${my.board.web-path}")
	private String webPath;

	@Value("${my.board.folder-path}")
	private String folderPath;

	// 게시글 작성 서비스
	@Override
	public int boardInsert(Board inputBoard, List<MultipartFile> images) throws IllegalStateException, IOException {

		// 게시글 제목, 상세내용 -> BOARD 테이블
		// 게시글의 이미지들 -> BOARD_IMG
		// BOARD 테이블에 제목, 상세내용을 먼저 등록해주고,
		// 그다음 작성한 글의 번호를 가져와서 삽입을 해야함

		// 1. 게시글 부분 (inputBoard)을 먼저
		// BOARD 테이블에 INSERT 하기
		// -> INSERT 된 게시글의 번호(시퀀스 번호) 반환받기
		int result = mapper.boardInsert(inputBoard);
		// inputBoard에는 boardNo가 selectKey를 통해 필드에 값을 수정한 값이 존재
		// result의 결과 행의 갯수를 가지고 다음 과정 진행 여부 결정

		// 삽입 실패 시 return 0
		if (result == 0) {
			return 0;
		}

		// 삽입 성공 시
		// 삽입된 게시글의 번호를 변수로 저장
		int boardNo = inputBoard.getBoardNo();
		// -> mapper.xml에서 <selectKey> 태그를 이용해서 생성된
		// boardNo가 inputBoard에 세팅된 상태!!(얕은 복사 개념 이해 필수)

		// 2. 업로드된 이미지가 실제로 존재할 경우
		// 업로드된 이미지만 별도로 저장하여
		// BOARD_IMG 테이블에 삽입하는 코드 작성

		// 실제로 업로드된 이미지만 모아둘 List 생성
		List<BoardImg> uploadList = new ArrayList<>();

		// images 리스트에서 하나씩 꺼내어 파일이 있는지 검사
		for (int i = 0; i < images.size(); i++) {

			// 실제 파일이 제출된 경우
			if (!images.get(i).isEmpty()) {
				// 원본명
				String originalName = images.get(i).getOriginalFilename();
				// 변경명
				String rename = Utility.fileRename(originalName);

				// 모든 값을 저장할 BoardImg DTO로 객체 생성
				BoardImg img = BoardImg.builder().imgOriginalName(originalName).imgRename(rename).imgPath(webPath)
						.boardNo(boardNo).imgOrder(i).uploadFile(images.get(i)).build();
				// 요소의 index 번호 == DB상 BOARD_IMG 테이블의 IMG_ORDER와 같음
				// uploadList에 추가

				uploadList.add(img);
			}
		}

		// uploadList가 비어있는 경우 == 실제로 제출된 파일이 하나도 없다
		if (uploadList.isEmpty()) {
			return boardNo;
		}

		// 제출된 파일이 존재할 경우
		// -> "BOARD_IMG" 테이블에 INSERT + 서버에 파일 저장

		result = mapper.insertUploadList(uploadList);
		// result == 삽입된 행의 갯수 == uploadList.size()

		// 다중 INSERT 성공 확인

		if (uploadList.size() == result) {
			// 서버에 파일 저장
			for (BoardImg img : uploadList) {
				img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));
			}
		} else {
			// 부분적으로 삽입 실패
			// ex) uploadList에 2개 저장
			// -> 1개 삽입 성공 1개는 실패
			// -> 전체 서비스 실패로 판단
			// -> 이전에 삽입된 내용 모두 rollback

			// -> rollback 하는 방법
			// == RuntimeException 강제 발생 (@Transactional 기본 RuntimeException)
			throw new RuntimeException();
		}

		return boardNo;
	}

	// 게시글 수정 서비스
	@Override
	public int boardUpdate(Board inputBoard, List<MultipartFile> images, String deleteOrderList) throws Exception {
		// 1. 게시글 부분(제목/내용) 수정
		// 제목/내용 -> BOARD 테이블, BOARD_IMG 테이블
		// 테이블이 다르기 때문에 따로따로 진행해야함
		int result = mapper.boardUpdate(inputBoard);

		// 수정 실패 시 바로 리턴
		if (result == 0)
			return 0;

		// 2. 기존 0 -> 삭제된 이미지(deleteOrderList)가 있는 경우
		if (deleteOrderList != null && !deleteOrderList.equals("")) {
			Map<String, Object> map = new HashMap<>();
			map.put("deleteOrderList", deleteOrderList);
			map.put("boardNo", inputBoard.getBoardNo());

			// BOARD_IMG에 존재하는 행을 삭제하는 SQL 호출
			result = mapper.deleteImage(map);

			String[] delete = deleteOrderList.split(",");

			if (result == 0) {
				// 삭제 실패한 경우
				throw new RuntimeException();
			} else if (delete.length != result) {
				// 업로드된 갯수와 삭제하려던 갯수가 다른 경우
				throw new RuntimeException();
			}
			// 3. 선택한 파일이 존재할 경우
			// (클라이언트가 실제로 업로드한 이미지가 있는 경우)

			List<BoardImg> uploadList = new ArrayList<>();
			for (int i = 0; i < images.size(); i++) {
				if (!images.get(i).isEmpty()) {
					String originalName = images.get(i).getOriginalFilename();
					String rename = Utility.fileRename(originalName);
					BoardImg img = BoardImg.builder().imgOriginalName(originalName).imgRename(rename).imgPath(webPath)
							.boardNo(inputBoard.getBoardNo()).imgOrder(i).uploadFile(images.get(i)).build();

					uploadList.add(img);

					// 4. 업로드 하려는 이미지 정보(img) 이용해서
					// 수정을 할지, 삽입을 할지 결정을 해야함
					// 게시판에서 기존에 있는 사진에 다른 사진을 넣던지(수정), 아예 업로드 안됐던 곳에 사진을 업로드(삽입)
					// 1) 기존에 이미지가 있었다면 -> 새 이미지로 변경 -> 수정
					result = mapper.updateImage(img);
					// 즉, BOARD_IMG에서 수정이 됐다면 1, 없다면 0
					if (result == 0) {
						// 수정 실패 == 기존에 해당하는 순서(IMG_ORDER)에 이미지가 없었음
						// -> 삽입 수행

						// 2) 기존에 존재하지 않으므로, 새 이미지 추가

						result = mapper.insertImg(img);
					}
				}
				// 수정 또는 삽입이 실패한 경우
				if (result == 0) {
					throw new RuntimeException(); // 롤백
				}
			}
			if (uploadList.isEmpty()) {
				// 선택한 파일이 없을 경우
				// 즉, 클라이언트가 업로드한 이미지가 없을 경우
				// 1) 제목, 내용만 수정한 경우
				// 2) 이미지를 삭제한 경우
				return result;
			}
			// 수정, 새 이미지 파일을 서버에 저장
			for (BoardImg img : uploadList) {
				img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));

			}
		}

		return result;
	}

	/**
	 * 게시물 삭제
	 */
	@Override
	public int boardDelete(Board board, Member loginMember) throws Exception {
		Map<String, Integer> map = new HashMap<>();
		map.put("memberNo", loginMember.getMemberNo());
		map.put("boardNo", board.getBoardNo());

		return mapper.boardDelete(map);
	}
}
