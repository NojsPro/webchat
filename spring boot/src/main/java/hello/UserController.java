package hello;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.util.DigestUtils;

import hello.User;
import hello.UserRepository;


/**
 * 
* @ClassName: UserController 
* @Description: TODO(用户注册登录控制器) 
* @author LvYongjian 
* @date 2019年7月3日 下午9:29:45 
*
 */
@Controller    
@RequestMapping(path="/user")
public class UserController {
	
	@Autowired 
	private UserRepository userRepository;  
	
	/**
	 * 
	* @Title: addNewUser 
	* @Description: TODO(注册用户：相同的用户名跟uid都不能注册) 
	* @param @param name 用户名
	* @param @param email 邮箱
	* @param @param uid 用户ID
	* @param @param password 用户密码
	* @return ResponseEntity<Object>    返回HTTP的状态码 
	* @throws
	 */
	@GetMapping(path="/register")  
	public ResponseEntity<Object> addNewUser (@RequestParam String name
			, @RequestParam String email,@RequestParam String uid, @RequestParam String password) {
		User n = new User();
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());//加密密码
		System.out.println("加密后的密码："+md5Password);
		Iterable<User> u = userRepository.findAllByUid(uid);  //查询数据库user表的uid
		long size = u.spliterator().getExactSizeIfKnown(); 
		Iterable<User> me = userRepository.findByName(name);  //查询数据库user表的name
		long sizes = me.spliterator().getExactSizeIfKnown(); 
		if(size>0||sizes>0) {
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}else {
			n.setName(name);
			n.setEmail(email);
			n.setUid(uid);
			n.setPassword(md5Password);
			userRepository.save(n);  //把数据保存到数据库
			return new ResponseEntity<Object>(HttpStatus.OK);
		}
	}
	
	/**
	 * 
	* @Title: userLogin 
	* @Description: TODO(用户登录) 
	* @param @param name 用户名
	* @param @param password 用户密码
	* @param @param httpservlet 
	* @return Iterable<User>     
	* @throws
	 */
	@RequestMapping(path="/login")  
	public @ResponseBody Iterable<User> userLogin (@RequestParam String name
			, @RequestParam String password, HttpServletRequest httpservlet) {
		HttpSession h = httpservlet.getSession();
		h.setAttribute("username", name);  //将用户名保存到Session中
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
		return userRepository.findByNameAndPassword(name,md5Password);  //登录时查询user表用户名跟密码
	}
	
}
