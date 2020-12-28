package cn.visolink.activeMQ;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

/**
 * @author:
 */
@Configuration
@EnableJms
public class ActiveMQConfig {
    @Value("${queueName}")
    private String queueName;

    @Value("${topicName}")
    private String topicName;

    @Value("${activemq.user}")
    private String usrName;

    @Value("${activemq.password}")
    private  String password;

    @Value("${activemq.broker-url}")
    private  String brokerUrl;
//
//    @Bean
//    public Queue queue(){
//        return new ActiveMQQueue(queueName);
//    }
//
//    @Bean
//    public ActiveMQTopic topic(){
//        return new ActiveMQTopic(topicName);
//    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(null, null, brokerUrl);
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ActiveMQConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(connectionFactory);
        return bean;
    }

   /* @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ActiveMQConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        //设置为发布订阅方式, 默认情况下使用的生产消费者方式
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(connectionFactory);
        return bean;
    }*/
}
