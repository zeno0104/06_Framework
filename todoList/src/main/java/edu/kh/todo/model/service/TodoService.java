package edu.kh.todo.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.todo.model.dto.Todo;

/**
 * 
 */
public interface TodoService {

	/**
	 * (TEST) todoNo가 1인 할 일 제목 조회
	 * 
	 * @return title
	 */
	String testTitle();

	/**
	 * 할 일 목록 + 완료된 할 일 갯수 조회
	 * 
	 * @return map
	 */
	Map<String, Object> selectAll();

	/**
	 * 할 일 추가
	 * 
	 * @param todoTitle
	 * @param todoContent
	 * @return
	 */
	int addTodo(String todoTitle, String todoContent);

	Todo todoDetail(int todoNo);


	int todoDelete(int todoNo);

	int changeComplete(Todo todo);

	int todoUpdate(Todo todo);

	int getTotalCount();

	int getCompleteCount();

	/** 전체 할 일 목록 조회
	 * @return
	 */
	List<Todo> selectList();



}