package cn.visolink.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * WebMvcConfigurer
 *
 * @author WCL
 * @date 2018-11-30
 */
@Configuration
@EnableWebMvc
public class ConfigurerAdapter implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("GET","POST","PUT","DELETE");

    }

// 可解决Long 类型在 前端精度丢失的问题， 如不想全局 直接添加注解 @JsonSerialize(using= ToStringSerializer.class) 到相应的字段

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);


        /**
         * 创建配置类
         * WriteNullListAsEmpty  ：List字段如果为null,输出为[],而非null
         * WriteNullStringAsEmpty ： 字符类型字段如果为null,输出为"",而非null
         * DisableCircularReferenceDetect ：消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
         * WriteNullBooleanAsFalse：Boolean字段如果为null,输出为false,而非null
         * WriteMapNullValue：是否输出值为null的字段,默认为false。
         *
         */
        //创建fastJson消息转换器
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //修改配置返回内容的过滤
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteMapNullValue
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        List<MediaType> mediaTypeList=new ArrayList<>();
        //处理中文乱码问题
        mediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypeList.add(MediaType.TEXT_HTML);
        stringHttpMessageConverter.setSupportedMediaTypes(mediaTypeList);
        //将fastjson添加到视图消息转换器列表内
        converters.add(stringHttpMessageConverter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /**
         * 开放静态文件访问
         */
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCachePeriod(0);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/").setCachePeriod(0);

    }
}
