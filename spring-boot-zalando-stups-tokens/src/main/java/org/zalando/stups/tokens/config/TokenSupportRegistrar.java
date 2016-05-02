package org.zalando.stups.tokens.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.validation.BindException;
import org.zalando.stups.oauth2.spring.client.StupsOAuth2RestTemplate;
import org.zalando.stups.oauth2.spring.client.StupsTokensAccessTokenProvider;

/**
 * 
 * @author jbellmann
 *
 */
public class TokenSupportRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final String ACCESS_TOKEN_PROVIDER = "AccessTokenProvider";

    private static final String ACCESS_TOKENS_BEAN = "accessTokensBean";

    private static final String TOKENS = "tokens";

    private final Logger logger = LoggerFactory.getLogger(TokenSupportRegistrar.class);

    private ConfigurableEnvironment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AccessTokensBeanProperties props = resolveSettings();
        for (TokenConfiguration tc : props.getTokenConfigurationList()) {

            final String providerBeanName = tc.getTokenId() + ACCESS_TOKEN_PROVIDER;
            // AccessTokenProvider
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .rootBeanDefinition(StupsTokensAccessTokenProvider.class);
            builder.addConstructorArgValue(tc.getTokenId());
            builder.addConstructorArgReference(ACCESS_TOKENS_BEAN);
            registry.registerBeanDefinition(providerBeanName, builder.getBeanDefinition());

            // RestOperations
            BeanDefinitionBuilder templateBuilder = BeanDefinitionBuilder
                    .rootBeanDefinition(StupsOAuth2RestTemplate.class);
            templateBuilder.addConstructorArgReference(providerBeanName);
            registry.registerBeanDefinition(tc.getTokenId(), templateBuilder.getBeanDefinition());
            logger.debug("register 'beanDefinition' for {}", tc.getTokenId());
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    private AccessTokensBeanProperties resolveSettings() {
        AccessTokensBeanProperties settings = new AccessTokensBeanProperties();
        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(settings);
        factory.setTargetName(TOKENS);
        factory.setPropertySources(environment.getPropertySources());
        factory.setConversionService(environment.getConversionService());
        try {
            factory.bindPropertiesToTarget();
        } catch (BindException ex) {
            throw new FatalBeanException("Could not bind DataSourceSettings properties", ex);
        }
        return settings;
    }
}