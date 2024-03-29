package com.schooltraining.storesdistribution.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MyBatisAnnotationConfig {

	@Bean
	public ConfigurationCustomizer configurationCustomizer() {
		return new ConfigurationCustomizer() {
			@Override
			public void customize(org.apache.ibatis.session.Configuration configuration) {
				// 设置驼峰命名
//                configuration.setMapUnderscoreToCamelCase(true);
			}
		};
	}

	@ConfigurationProperties(prefix = "spring.datasource")
	@Bean
	public DataSource druid() {
		DruidDataSource druidDataSource = new DruidDataSource();
		List<Filter> filterList = new ArrayList<>();

		filterList.add(wallFilter());

		druidDataSource.setProxyFilters(filterList);

		return druidDataSource;
	}

	// 配置Druid的监控
	// 1、配置一个管理后台的Servlet
	@Bean
	public ServletRegistrationBean statViewServlet() {
		ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
		Map<String, String> initParams = new HashMap<>();

		initParams.put("loginUsername", "admin");
		initParams.put("loginPassword", "123456");
		initParams.put("allow", "");// 默认就是允许所有访问
//        initParams.put("deny","192.168.15.21");

		bean.setInitParameters(initParams);
		return bean;
	}

	// 2、配置一个web监控的filter
	@Bean
	public FilterRegistrationBean webStatFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new WebStatFilter());

		Map<String, String> initParams = new HashMap<>();
		initParams.put("exclusions", "*.js,*.css,/druid/*");

		bean.setInitParameters(initParams);

		bean.setUrlPatterns(Arrays.asList("/*"));

		return bean;
	}


	@Bean
	public WallFilter wallFilter() {

		WallFilter wallFilter = new WallFilter();
		wallFilter.setConfig(wallConfig());
		return wallFilter;

	}

	@Bean
	public WallConfig wallConfig() {
		WallConfig config = new WallConfig();
		config.setMultiStatementAllow(true);// 允许一次执行多条语句
		config.setNoneBaseStatementAllow(true);// 允许非基本语句的其他语句
		return config;

	}

}
