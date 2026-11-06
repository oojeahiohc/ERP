package com.erp.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;

@Configuration
public class EgovConfigDatasource {
  @Bean(name = "dataSource", destroyMethod = "close")
  @Primary
  @ConfigurationProperties("erp.datasource")
  public BasicDataSource dataSource() {
    // HSQL 초기화 대신 외부 설정을 주입한 연결 풀 사용
    return new BasicDataSource();
  }
}