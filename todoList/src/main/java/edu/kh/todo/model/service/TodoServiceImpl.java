package edu.kh.todo.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todo.model.dao.TodoDAO;
import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.mapper.TodoMapper;

/**
 * @Transactional - 트랜잭션 처리를 수행하라고 지시하는 어노테이션 -> 커밋, 롤백이 필요한 상황이 있을 때 하라고 지시하는
 *                어노테이션 - 정상 코드 수행시 commit이 되고, 그 이외에는 rollback이 됨 - 기본값 :
 *                Service 내부 코드 수행 중에 RuntimeException이 발생하면 rollback
 */

@Transactional(rollbackFor = Exception.class) // 커밋, 롤백을 자동으로 진행
/**
 * rollbackFor = Exception.class => 어떠한 예외가 발생했을 때, 롤백을 해줄지 지정해주는 속성인데
 * Exception.class는 모든 예외 발생시 rollback 하겠다는 의미
 */
@Service // 비즈니스 로직(데이터 가공, 트랜잭션 처리 등)의 역할을 명시 + Bean 등록
public class TodoServiceImpl implements TodoService {
	@Autowired // TodoDAO와 같은 타입/상속관계 Bean 의존성 주입(DI)
	private TodoDAO dao;

	@Autowired
	private TodoMapper mapper;

	@Override
	public String testTitle() {
		return dao.testTitle();
	}

	@Override
	public Map<String, Object> selectAll() {
		// 1. 할 일 목록 조회
		List<Todo> todoList = mapper.selectAll();

		// 2. 완료된 할 일 개수 조회
		int completeCount = mapper.getCompleteCount();

		// 3. 위 두개 결과값을 Map으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();
		map.put("todoList", todoList);
		map.put("completeCount", completeCount);
		return map;
	}

	@Override
	public int addTodo(String todoTitle, String todoContent) {
		Todo todo = new Todo();
		todo.setTodoTitle(todoTitle);
		todo.setTodoContent(todoContent);
		// Todo.builder().todoTitle(todoTitle).todoContent(todoContent).build();

		int result = mapper.addTodo(todo);

		// Connection을 안만들어도 되는 이유
		// DBCP를 사용하게 되어서, Connection pool에 있는
		// 커넥션을 5개를 만들어서 대기하게끔 만듬
		// 이 관리를 Spring에서 해줌

		// 마이바티스에서 SQL에 전달할 수 있는 파라미터 개수는 오직 1개
		// -> TodoMapper에 생성될 추상메서드의 매개변수도 1개여야 한다는 것

		// 근데 2개 전달인자 가능
		// int result = mapper.addTodo(todoTitle, todoContent);
		return result;
	}

	@Override
	public Todo todoDetail(int todoNo) {

		return mapper.todoDetail(todoNo);
	}

	@Override
	public int todoDelete(int todoNo) {

		return mapper.todoDelete(todoNo);
	}

	@Override
	public int changeComplete(Todo todo) {
		return mapper.changeComplete(todo);
	}

	@Override
	public int todoUpdate(Todo todo) {
		// TODO Auto-generated method stub
		return mapper.todoUpdate(todo);
	}

}
