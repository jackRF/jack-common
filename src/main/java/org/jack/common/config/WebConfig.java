// package org.jack.common.config;

// import java.util.Date;

// import org.jack.common.core.DateConverter;
// import org.springframework.boot.web.server.ErrorPage;
// import org.springframework.boot.web.server.ErrorPageRegistrar;
// import org.springframework.boot.web.server.ErrorPageRegistry;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.convert.converter.Converter;
// import org.springframework.web.multipart.MultipartResolver;
// import org.springframework.web.multipart.commons.CommonsMultipartResolver;

// @Configuration
// public class WebConfig implements ErrorPageRegistrar {
    
//     @Bean
//     public Converter<String,Date> dateConverter(){
//         DateConverter dateConverter=new org.jack.common.core.DateConverter();
//         return dateConverter;
//     }
//     @Bean
//     public MultipartResolver multipartResolver(){
//         CommonsMultipartResolver resolver = new CommonsMultipartResolver();
//         resolver.setDefaultEncoding("UTF-8");
//         // resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
//         resolver.setResolveLazily(true);
//         resolver.setMaxInMemorySize(40960);
//         // 上传文件大小 50M
//         resolver.setMaxUploadSize(50 * 1024 * 1024);
//         return resolver;
//     }

//     @Override
//     public void registerErrorPages(ErrorPageRegistry registry) {
//         ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
//         ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
//         registry.addErrorPages(error404Page);
//         registry.addErrorPages(error500Page);
//     }

// }