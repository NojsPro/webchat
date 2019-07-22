package hello;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 
* @ClassName: WebSocketConfig 
* @Description: TODO(webSocket配置) 
* @author LvYongjian 
* @date 2019年7月3日 下午9:45:51 
*
 */
@Configuration  
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    	System.out.println("启动");
    	//表示客户端订阅地址的前缀信息，也就是客户端接收服务端消息的地址的前缀信息
        config.enableSimpleBroker("/user"); 
        //指服务端接收地址的前缀，意思就是说客户端给服务端发消息的地址的前缀
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	System.out.println("注册");
    	//表示添加了一个/queueServer端点，客户端就可以通过这个端点来进行连接,开启SockJS支持
        registry.addEndpoint("/queueServer").withSockJS();  
        
    }

}