package edu.kh.project.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/*
 * AOP(Aspect-Oriented Programming) : 분산되어 있는 관심사/관점을 모듈화 시키는 기법
 * - 주요 비즈니스 로직과 관련이 없는 부가적인 기능을 추가할 때 유용
 * ex) 코드 중간중간 로그 찍을 때, 트랜잭션 처리하고 싶을 때, 보안 처리 추가 등...
 * 
 * 주요 어노테이션
 * - @Aspect : Aspect를 정의하는데 사용되는 어노테이션으로, 클래스 상단에 작성함
 * => 즉, 공통된 관심사가 모여있는 클래스에 붙이라는 것
 * - @Before(포인트컷) : 대상 메서드(포인트컷), 즉 이곳에 advice를 추가하겠다라고 지정한 joinPoint중 하나
 * => 대상 메서드(포인트컷) 실행 전에 Advice를 실행함.
 * - @After(포인트컷) : 대상 메서드 실행 후에 Advice를 실행함.
 * - @Around(포인트컷) : 대상 메서드 실행 전/후로 Advice를 실행함 (@Before + @After)
 * */

@Component // bean 등록
// @Aspect // 공통 관심사가 작성된 클래스임을 명시(AOP 동작용 클래스)
@Slf4j // log 찍을 수 있는 객체(Logger) 생성 코드를 추가
public class TestAspect {
	// Advice : 끼워 넣을 코드(메서드)
	// PointCut : 실제로 Advice가 적용될 JoinPoint(여러 지점) 특점 시점을 지정
	
	// * 클래스명은 패키지명부터 모두 작
	
	// execution(* edu.kh.project..*Controller*.*(..))
	// execution : 메서드 실행 지점을 가리키는 키워드
	// * : 모든 리턴타입을 나타냄
	// => 맨 앞 *은 리턴타입을 말함
	// edu.kh.project : 3레벨 패키지명을 나타냄
	// .. : 0개 이상의 하위 패키지를 나타냄
	// -> 즉, edu.kh.project 하위에 Controller를 찾겠다는 의미
	// *Controller* : 이름에 "Controller" 라는 문자열을 포함하는 모든 클래스를 대상으로 함
	// .* : 모든 메서드를 나타냄
	// (..) : 0개 이상의 파라미터를 나타냄
	// -> 즉, edu.kh.project 하위에 모든 Controller에 모든 메서드를 의미
	// @Before는 edu.kh.project 하위에 모든 Controller에 모든 메서드가 수행되기 전에
	// advice를 수행시키겠다는 의미
	
	// @Before(포인트컷)
	@Before("execution(* edu.kh.project..*Controller*.*(..))")
	public void testAdvice() {
		log.info("-----------------testAdvice() 수행됨-----------------");
	}
	
	// 컨트롤러의 모든 메서드가 수행되고 난 이후에 수행되게끔 PointCut을 지정
	@After("execution(* edu.kh.project..*Controller*.*(..))")
	public void controllerEnd(JoinPoint jp) {
		// JoinPoint : AOP 기능이 적용된 대상
		// AOP가 적용된 클래스 이름을 얻어오기
		String className = jp.getTarget().getClass().getSimpleName(); 
		// ex) MainController, MemberController.. 이런 형식으로 전달됌
		
		// 실행된 컨트롤러의 메서드명 얻어오기
		String methodName = jp.getSignature().getName(); // ex) mainPage(), login...
		
		log.info("---------{}.{} 수행 완료---------", className, methodName);
		
		
	}
}
