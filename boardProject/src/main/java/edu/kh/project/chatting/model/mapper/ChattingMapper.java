package edu.kh.project.chatting.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.chatting.model.dto.ChattingRoom;
import edu.kh.project.chatting.model.dto.Message;
import edu.kh.project.member.model.dto.Member;

@Mapper
public interface ChattingMapper {

	/**
	 * 채팅방 목록 조회 SQL
	 * 
	 * @param memberNo
	 * @return
	 */
	List<ChattingRoom> selectRoomList(int memberNo);

	/**
	 * 채팅 상대 검색 SQL
	 * 
	 * @param map
	 * @return
	 */
	List<Member> selectTarget(Map<String, Object> map);

	/**
	 * 채팅방 번호 체크 SQL
	 * 
	 * @param map
	 * @return
	 */
	int checkChattingRoomNo(Map<String, Integer> map);

	/**
	 * 새로운 채팅방 생성 SQL
	 * 
	 * @param map
	 * @return 생성된 채팅방의 번호
	 */
	int createChattingRoom(Map<String, Integer> map);

	/**
	 * 메세지 목록 조회 SQL
	 * 
	 * @param integer
	 * @return
	 */
	List<Message> selectMessageList(Object chattingRoomNo);

	/**
	 * 채팅 메시지 읽음 처리 SQL
	 * 
	 * @param paramMap
	 * @return
	 */
	int updateReadFlag(Map<String, Object> paramMap);

	int insertMessage(Message msg);
}