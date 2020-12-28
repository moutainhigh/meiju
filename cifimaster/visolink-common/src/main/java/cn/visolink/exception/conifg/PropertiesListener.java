package cn.visolink.exception.conifg;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/8/26 10:31 上午
 */
public class PropertiesListener implements ApplicationListener<ApplicationStartedEvent> {
    private String propertyFileName;

    public PropertiesListener(String propertyFileName) {
        this.propertyFileName = propertyFileName;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        PropertiesListenerConfig.loadAllProperties(propertyFileName);
    }
}
