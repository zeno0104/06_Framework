package edu.kh.todo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter + Setter + toString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {
	// DB의 UnderScore (_) 표기법 <-> Java의 Camel 표기법 두 표기법이
	// 서로 변환 될 수 있도록 매핑하여
	// Java 필드명(memberId)과 DB 컬럼명(MEMBER_ID)가 서로 연결되게 함

	// mybatis-config.xml에 정의되어 있음
	private int todoNo; // 할 일 번호 (TODO_NO)
	private String todoTitle; // 할 일 제목(TODO_TITLE)
	private String todoContent; // 할 일 내용(TODO_CONTENT)
	private String complete; // 할 일 완료 여부 (COMPLETE "Y" / "N")
	private String regDate; // 할 일 등록일 (REG_DATE)
}
