package hello;
/**
 * 
* @ClassName: Greeting 
* @Description: TODO(封装聊天属性) 
* @author LvYongjian
* @date 2019年7月3日 下午9:26:23 
*
 */
public class Greeting {

	private String username;
   
	private String content;

	private String sendname;
	
    public String getSendname() {
		return sendname;
	}

	public void setSendname(String sendname) {
		this.sendname = sendname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Greeting() {
    }

    public Greeting(String username, String content, String sendname) {
		super();
		this.username = username;
		this.content = content;
		this.sendname = sendname;
	}

	public Greeting(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


}
