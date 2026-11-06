package com.erp.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.mapper.EgovMapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
// MyBatis가 Mapper 인터페이스와 SQL XML을 어디서 찾을지 설정
@Configuration
@MapperScan(basePackages = "com.erp", annotationClass = EgovMapper.class, sqlSessionFactoryRef = "sqlSession")
public class EgovConfigMapper {

	@Bean(name = "sqlSession") // DAO 방식을 활용할 경우 EgovAbstractMapper 가 찾는 Bean Name
	SqlSessionFactoryBean sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws IOException {
		var resolver = new PathMatchingResourcePatternResolver();
    	var factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);

		// 기존 설정 XML을 유지하고 업무 Mapper 검색 경로만 변경
		factory.setConfigLocation(
			resolver.getResource("classpath:/egovframework/sqlmap/example/sql-mapper-config.xml"));
		factory.setMapperLocations(resolver.getResources("classpath*:mapper/**/*.xml"));
		return factory;
	}

	@Bean(name = "sqlSessionTemplate") // 혼동 방지를 위한 명시적 Bean Name 을 표기
	public SqlSessionTemplate sqlSession(@Qualifier("sqlSession") SqlSessionFactory factory) {
    	return new SqlSessionTemplate(factory);
  	}

}
