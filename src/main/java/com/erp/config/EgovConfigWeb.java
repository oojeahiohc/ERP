package com.erp.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.erp.pagination.EgovKrdsPaginationRenderer;
import com.erp.pagination.EgovPaginationDialect;

@Configuration
@Import({
		EgovConfigCommon.class,
		EgovConfigDatasource.class,
		EgovConfigMapper.class,
		EgovConfigProperties.class,
		EgovConfigTransaction.class,
		EgovConfigValidation.class
})
public class EgovConfigWeb implements WebMvcConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	// Thymeleaf 캐시 설정 적용, 설정값이 없으면 캐시 비활성화
	@org.springframework.beans.factory.annotation.Value("${spring.thymeleaf.cache:false}")
	private boolean templateCache;

	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Bean
	SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(this.applicationContext);
		templateResolver.setPrefix("classpath:/templates/thymeleaf/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(templateCache); // 설정값에 따라 HTML 템플릿 캐시 활성화 또는 비활성화
		return templateResolver;
	}

	@Bean
	SpringTemplateEngine templateEngine(EgovKrdsPaginationRenderer egovKrdsPaginationRenderer) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		// add custom tag
		templateEngine.addDialect(new EgovPaginationDialect(egovKrdsPaginationRenderer));
		return templateEngine;
	}

	@Bean
	ThymeleafViewResolver thymeleafViewResolver(EgovKrdsPaginationRenderer egovKrdsPaginationRenderer) {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setCharacterEncoding("UTF-8");
		viewResolver.setTemplateEngine(templateEngine(egovKrdsPaginationRenderer));
		return viewResolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");

        // favicon.ico 처리를 위한 빈 핸들러 (404 오류 방지)
        registry.addResourceHandler("/favicon.ico")
        		.addResourceLocations("classpath:/static/")
        		.setCachePeriod(3600);

        // .well-known 경로도 처리 (Chrome DevTools 자동 요청)
        registry.addResourceHandler("/.well-known/**")
        		.addResourceLocations("classpath:/static/.well-known/");
	}

	@Bean
	SessionLocaleResolver localeResolver() {
		return new SessionLocaleResolver();
	}

	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("language");
		return interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

}
