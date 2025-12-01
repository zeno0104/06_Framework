package edu.kh.todo.model.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.todo.model.mapper.TodoMapper;

// DAO와 Mapper가 연결됌
// DAO는 Mapper로 함

// Controller -> Service -> DAO -> Mapper

// -> Mybatis가 생기면서 DAO가 필요없어짐

@Repository
// DB와 직접적인 연관이 있는 것을 의미
// DAO 계층 역할 명시 + Bean 등록
public class TodoDAO {

	@Autowired
	private TodoMapper mapper;
	// 상속받아서 구현된 구현체를 mybatis와 spring으로 인해 주입됌
	// mapper에는 TodoMapper 인터페이스의 구현체가
	// 의존성 주입됨
	// -> 그 구현체가 sqlSessionTemplate을 이용
	public String testTitle() {
		// TODO Auto-generated method stub
		return mapper.testTitle();
	}
//	public Map<String, Object> selectAll() {
//		return mapper.selectAll();
//	}

}
