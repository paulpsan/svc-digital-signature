package com.msejemplo01.msfirmadigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@SpringBootApplication
public class MsFirmaDigitalApplication {

	public static void main(String[] args) {

		SpringApplication.run(MsFirmaDigitalApplication.class, args);
		disableWarning();
	}
	public static void disableWarning() {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			Unsafe u = (Unsafe) theUnsafe.get(null);

			Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
			Field logger = cls.getDeclaredField("logger");
			u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
		} catch (Exception e) {
			// ignore
		}
	}
	/*@Bean
	public WebMvcConfigurer corsConfiguration(){
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.exposedHeaders("access-control-expose-headers", "Authorization", "Expiration",
								"x-customer-id", "x-audit-host", "x-audit-agency", "x-audit-user", "x-audit-source")
						.allowedHeaders("Authorization", "Content-Type", "x-customer-id",
								"x-audit-host", "x-audit-agency", "x-audit-user", "x-audit-source")
						.allowedMethods("*")
						.allowedOrigins("*");
			}
		};

	}*/


	//@Bean
	/*public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/*")
						.allowedOrigins("http://127.0.0.1:5173")
						.allowedMethods("PUT", "DELETE", "GET", "POST")
						.allowedHeaders("*")
						.exposedHeaders("*")
						.allowCredentials(false).maxAge(3600);
			}
		};
	}*/
}
