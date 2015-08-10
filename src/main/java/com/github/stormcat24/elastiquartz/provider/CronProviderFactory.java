package com.github.stormcat24.elastiquartz.provider;

import com.github.stormcat24.elastiquartz.config.Configuration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author stormcat24
 */
@Component
public class CronProviderFactory implements FactoryBean<CronProvider> {

    @Autowired
    private Configuration configuration;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public CronProvider getObject() throws Exception {

        String cronLocationType = configuration.getCronLocationType();
        if (StringUtils.isEmpty(cronLocationType)) {
            return null;
        } else if ("s3".equals(cronLocationType)) {
            return (CronProvider) applicationContext.getBean("s3Provider");
        }
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return CronProvider.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
