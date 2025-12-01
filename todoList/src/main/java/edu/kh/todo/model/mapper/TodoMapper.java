package edu.kh.todo.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.todo.model.dto.Todo;

// Mybatis를 사용하면 Mapper가 DAO를 대신해서 사용됌

/**
 * @Mapper - Mybatis에서 제공하는 어노테이션 - Mybatis에서 SQL과 Java 메서드를 연결해주는
 *         인터페이스(Mapper)의 구현체를 스프링의 Bean으로 등록할 수 있게 해주는 어노테이션
 * 
 *         이 어노테이션을 붙이면 Spring이 Mapper 인터페이스를 인식하고, 자동으로 구현체를 생성해줌 -> 이 구현체가
 *         Bean으로 등록됨!
 * 
 *         - ⭐⭐⭐ 해당 어노테이션이 작성된 인터페이스는 namespace에 해당 인터페이스가 작성된 xxx-mapper.xml
 *         파일과 연결되어 SQL 호출/수행/결과 반환이 가능해짐 -> 현재는 todo-mapper.xml에 namespace에 작성을
 *         해서 연결이 되어 있음
 * 
 * 
 */

// 전체 흐름은 현재 DAO가 있다는 가정하에 다음과 같다
// client -> service -> DAO -> Mapper -> xml 
// 돌아오는 것은 반대로 돌아오면 된다.
@Mapper
public interface TodoMapper {

	String testTitle();

	List<Todo> selectAll();

	int getCompleteCount();

	int addTodo(Todo todo);

	Todo todoDetail(int todoNo);

	int todoDelete(int todoNo);

	// int addTodo(@Param("todoTitle") String todoTitle,@Param("todoContent") String
	// todoContent);

}
