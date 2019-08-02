package com.ruixin.esaddress;

import com.ruixin.esaddress.util.CrossDomainFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.ruixin.esaddress.mapper")
public class EsAddressApplication {

	/**
	 * 设置rest服务支持跨域请求
	 *
	 * @return
	 */
	@Bean
	public FilterRegistrationBean addCrossDomainFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CrossDomainFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

	public static void main(String[] args) {
		SpringApplication.run(EsAddressApplication.class, args);
	}

}
