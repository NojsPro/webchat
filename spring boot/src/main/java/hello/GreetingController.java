package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import hello.Greeting;

/**
 * 
* @ClassName: GreetingController 
* @Description: TODO(聊天控制器) 
* @author LvYongjian
* @date 2019年7月3日 下午10:01:43 
*
 */
@Controller
public class GreetingController {
	
	@Autowired
    public SimpMessagingTemplate template;
	
	/**
	 * 
	* @Title: loginPage 
	* @Description: TODO(用户登录) 
	* @return String     
	* @throws
	 */
	@GetMapping(path="/login")
	public String loginPage() {
		return "index.html";
	}
	
	/**
	 * 
	* @Title: online 
	* @Description: TODO(发送在线用户uid) 
	* @param @param ol 在线用户
	* @return void
	* @throws
	 */
	@MessageMapping("/online")
    public void online(Line ol) throws Exception{
       System.out.println("用户在线："+ol.getUid());
       template.convertAndSendToUser("online","/message",ol.getUid());
    }
	
	/**
	 * 
	* @Title: offline 
	* @Description: TODO(发送离线用户uid) 
	* @param @param ol 离线用户
	* @return void    
	* @throws
	 */
	@MessageMapping("/offline")
    public void offline(Line ol) throws Exception{
        System.out.println("用户断开："+ol.getUid());
        template.convertAndSendToUser("offline","/message",ol.getUid());
    }

	/**
	 * 
	* @Title: queue 
	* @Description: TODO(聊天功能) 
	* @param @param rm 在线用户
	* @return void  
	* @throws
	 */
    @MessageMapping("/queue")
    public void queue(Greeting rm) throws Exception{
        System.out.println(rm.getUsername()+"用户输出了："+rm.getContent());
        template.convertAndSendToUser(rm.getSendname(),"/message",
        		"{\"form\":\""+rm.getUsername()+"\",\"message\":\""+rm.getContent()+"\",\"to\":\""+rm.getSendname()+"\"}");
        Thread.sleep(1000);
    }

}
