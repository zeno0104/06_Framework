package edu.kh.project.main.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MainMapper {

	List<Member> getAllUser();

	Member getUser(int memberNo);


	int resetPw(Map<String, Object> map);

	int restorationMember(int memberNo);
}
