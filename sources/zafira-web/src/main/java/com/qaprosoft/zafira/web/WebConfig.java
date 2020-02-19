/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.web.util.dozer.NullSafeDozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@EnableAsync
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class WebConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

    private static final String BASENAME_LOCATION = "classpath:i18n/messages";

    private final boolean multitenant;

    public WebConfig(@Value("${zafira.multitenant}") boolean multitenant) {
        this.multitenant = multitenant;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(BASENAME_LOCATION);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        if (!multitenant) {
            // add extra resource handler to serve local resources - single host deployment only
            registry.addResourceHandler("/assets/**")
                    .addResourceLocations("file:/opt/assets/");
        }
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

    @Bean
    public NullSafeDozerBeanMapper mapper(ResourceLoader resourceLoader) {
        List<String> mappingsFileNames = retrieveDozerMappingsUrls(resourceLoader, "classpath*:dozer/**/*.xml");
        NullSafeDozerBeanMapper beanMapper = new NullSafeDozerBeanMapper();
        beanMapper.setMappingFiles(mappingsFileNames);
        return beanMapper;
    }

    @Bean
    public Docket api(@Value("${zafira.debug-enabled:false}") boolean debugMode) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("zafira-api")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.qaprosoft.zafira.web"))
                .paths(PathSelectors.any())
                .build()
                .enable(debugMode)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Zafira REST API")
                .description("Zafira REST API documentation")
                .termsOfServiceUrl("http://springfox.io")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("2.0")
                .build();
    }

    private List<String> retrieveDozerMappingsUrls(ResourceLoader resourceLoader, String mappingsLocationPattern) {
        List<String> mappingsFileNames = null;
        try {
            Resource[] dozerMappings = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                                                           .getResources(mappingsLocationPattern);
            mappingsFileNames = Arrays.stream(dozerMappings).map(resource -> {
                String mappingPath = null;
                try {
                    mappingPath = resource.getURL().toString();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return mappingPath;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return mappingsFileNames;
    }

    /**
     * Registers placeholder configurer to resolve properties
     * Order is required, `cause  there is at least one placeholder configurer in servlet context by default.
     * Order is necessary to resolve their conflicts
     * @return a created placeholder configurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        placeholderConfigurer.setOrder(Integer.MIN_VALUE);
        return placeholderConfigurer;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

}
