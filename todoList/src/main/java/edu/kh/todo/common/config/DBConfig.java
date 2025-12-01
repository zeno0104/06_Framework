package edu.kh.todo.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/*
 * @Configuration
 * - 스프링 설정용 클래스임을 명시 (스프링이 해당 클래스 설정 정보로 인식하고 사용)
 * + 객체로 생성해서 내부 코드를 서버 실행 시 모두 바로 실행
 * 
 * @PropertySource : properties 파일의 내용을 이용하겠다는 어노테이션
 * 다른 properties도 추가하고 싶으면 어노테이션을 계속 추가 -> 컴파일러에게 알림 
 * */

@Configuration
//@PropertySource : properties 파일의 내용을 이용하겠다는 어노테이션
//다른 properties도 추가하고 싶으면 어노테이션을 계속 추가
// DBConfig를 객체로 생성해줌 -> 서버가 실행되면 바로 사용할 수 있도록 하는 것
@PropertySource("classpath:/config.properties")
// classpath : src/main/resources
// DataSource 객체를 만들기 위한 클래스
public class DBConfig {
	// 필드
	@Autowired // (DI, 의존성 주입)
	private ApplicationContext applicationContext;
	// application scope 객체 : 즉, 현재 프로젝트 그 자체를 나타내는 객체
	// -> 현재 프로젝트의 전반적인 DB 설정과, Bean 관리에 접근할 수 있도록 해주는 객체
	// -> 스프링이 관리하고 있는 ApplicationContext 객체를 의존성 주입받는다.

	// @Bean
	// - 개발자가 수동으로 bean을 등록하는 어노테이션
	// - @Bean 어노테이션이 작성된 메서드에서 반환된 객체는
	// Spring Container가 관리함(IOC)

	// 메서드
	////////////////////////////// HikariCP 설정 //////////////////////////////

	@Bean
	// @ConfigurationProperties(prefix = "spring.datasource.hikari")
	// properties 파일의 내용을 이용해서 생성되는 bean을 설정하는 어노테이션
	// prefix를 지정하여 spring.datasource.hikari으로 시작하는 설정을 모두 적용
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	// hikariConfig에 config.properties에 있는 prefix(접두사)에 해당하는 것들을 HikariConfig 객체에
	// 전달하겠다는 의미
	public HikariConfig hikariConfig() {
		// -> config.properties 파일에서 읽어온
		// spring.datasource.hikari로 시작하는 모든 값이
		// 자동으로 HikariConfig라는 객체의 알맞은 필드에 세팅이 되어
		// 객체화 됨 -> Bean으로 등록까지
		// 개발자 주도하에 객체를 생성했지만, Spring Container가 관리하게끔 하기위해
		// @Bean을 사용
		// -> IoC가 됌
		return new HikariConfig();
	}

	@Bean
	public DataSource dataSource(HikariConfig config) {
		// 매개변수 HikariConfig config
		// Argument Resolver가 도움을 줌
		// -> 등록된 Bean 중 HikariConfig 타입의 Bean을 바인딩
		// -> DataSource 객체를 생성하는데 이용된다.

		DataSource dataSource = new HikariDataSource(config);

		// DataSource :
		// 애플리케이션이 데이터베이스에 연결할 때 사용하는 설정 객체임
		// 1) DB 연결 정보 제공(url, username, password)
		// 2) Connection pool 관리 (Connection 생성/생명주기 관리)
		// 3) 트랜잭션 관리
		return dataSource;
	}

	// Mybatis가 추가되었을 때는 여기까지 작성되었을 때 서버가 켜짐
	// 만약 추가가 되었다면 아래 설정이 없다면 서버가 안켜짐

//////////////////////////// Mybatis 설정 추가 ////////////////////////////
// Mybatis : Java 애플리케이션에서 SQL을 더 쉽게 사용할 수 있도록 도와주는 
// 영속성 프레임워크(Persistence Framework)는 애플리케이션의 데이터를
// 데이터베이스와 같은 저장소에 영구적으로 저장하고,
// 이를 쉽게 CRUD 할 수 있도록 도와주는 프레임워크

	// SqlSessionFactory : SqlSession을 만드는 객체
	// DataSource 필요 -> 커넥션 풀을 만들기 위함
	@Bean
	public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		// SqlSessionFactoryBean : 공장
		sessionFactoryBean.setDataSource(dataSource);

		// 세팅1. 매퍼 파일이 모여있는 경로 지정
		// 매퍼 파일
		// 
		sessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mappers/**.xml"));

		// 세팅2. 별칭을 지정해야하는 DTO가 모여있는 패키지 지정
		// -> 해당 패키지에 있는 모든 클래스가 클래스명으로 별칭이 지정됨
		sessionFactoryBean.setTypeAliasesPackage("edu.kh.todo");
		// -> edu.kh.todo 세팅시 패키지 하위에 있는 모든 클래스가
		// 클래스명으로 별칭이 지정됨.
		// -> ex) edu.kh.todo.model.dto.Todo 클래스가 있다면 -> Todo가 별칭으로 등록됌
		// 원래 풀네임으로 작성해야함 => 코드가 길어짐 
		// 따라서 별칭으로 세팅

		// 세팅3. 마이바티스 설정 파일 경로 지정
		sessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));

		// SqlSession 객체 반환
		return sessionFactoryBean.getObject();
	}

	// SqlSessionTemplate : 기본 SQL 실행 + 트랜잭션 처리
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sessionFactory) {
		return new SqlSessionTemplate(sessionFactory);
	}

	// DataSourceTransactionManager : 트랜잭션 매니저
	// commit + rollback을 자동으로 해줌
	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}