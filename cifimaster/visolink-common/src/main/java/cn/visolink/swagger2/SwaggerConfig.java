package cn.visolink.swagger2;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * api页面 /swagger-ui.html
 * @author WCL
 * @date 2018-11-23
 */

@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig  {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${swagger.enabled}")
    private Boolean enabled;

    @Bean
    public Docket createRestApi() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar.name(tokenHeader).description("token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .defaultValue("Bearer ")
                .required(true)
                .build();
        pars.add(ticketPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enabled)
                .apiInfo(apiInfo())
                .select()
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .globalOperationParameters(pars);
    }

    /**
     * 处理包路径配置规则,支持多路径扫描匹配以逗号隔开
     *
     * @param basePackage 扫描包路径
     * @return Function
     */
    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            for (String strPackage : basePackage.split(",")) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }
    /**
     * @param input RequestHandler
     * @return Optional
     */
    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.getClass());
    }
    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("visolink 接口文档")
                .version("2.1")
                .build();
    }

}
