package edu.kh.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// Spring EL 같은 경우 DTO 객체 출력할 때 getter가 필수 작성되어 있어야 함
// -> ${Student.name} == ${Student.getName()}과 같기 때문
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
	private String studentNo; // 학생번호
	private String name; // 이름
	private int age; // 나이
}
