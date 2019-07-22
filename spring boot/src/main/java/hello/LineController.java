package hello;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hello.Line;
import hello.LineRepository;

/**
 * 
* @ClassName: LineController 
* @Description: TODO(用户在线列表控制器) 
* @author LvYongjian ,luo
* @date 2019年7月3日 下午9:53:47 
*
 */
@Controller    
@RequestMapping(path="/line")
public class LineController {
	@Autowired 
	private LineRepository lineRepository;
	
	/**
	 * 
	* @Title: addOnlineUser   
	* @Description: TODO(保存在线用户) 
	* @param @param name 用户名
	* @param @param avater 头像
	* @param @param uid 用户ID
	* @return ResponseEntity<Object>    返回HTTP状态码
	* @throws
	 */
	@GetMapping(path="/online")  
	public ResponseEntity<Object> addOnlineUser (@RequestParam String name
			, @RequestParam String avater,@RequestParam String uid) {
		System.err.println("在线用户"+name+avater+uid);
		Iterable<Line> l = lineRepository.findAllByUid(uid);//查询数据库line表的uid
		long size = l.spliterator().getExactSizeIfKnown(); 
		System.err.println(size);//
		if(size > 0) {//line表已经有这个用户了
			return new ResponseEntity<Object>(HttpStatus.ACCEPTED);
		}else {  //没有这个用户，保存到line表中
			Line n = new Line();
			n.setName(name);
			n.setUid(uid);
			n.setAvater(avater);
			lineRepository.save(n);
			return new ResponseEntity<Object>(HttpStatus.OK);
		}
	}
	
	/**
	 * 
	* @Title: offlineUser 
	* @Description: TODO(删除离线用户) 
	* @param @param uid  用户ID
	* @return ResponseEntity<Object>    返回HTTP状态码 
	* @throws
	 */
	@Transactional
	@GetMapping(path="/offline") 
	public ResponseEntity<Object> offlineUser (@RequestParam String uid) {
		System.err.println("下线用户"+uid);
		Iterable<Line> l = lineRepository.findAllByUid(uid);//查询数据库line表的uid
		long size = l.spliterator().getExactSizeIfKnown(); 
		System.err.println(size);//
		if(size == 0) {//line表已经没有这个用户了
			return new ResponseEntity<Object>(HttpStatus.ACCEPTED);
		}else {  //还有这个用户，删除line表中的用户
			lineRepository.deleteByUid(uid);
			return new ResponseEntity<Object>(HttpStatus.OK);
		}
	}
	
	/**
	 * 
	* @Title: lineList 
	* @Description: TODO(在线用户列表显示) 
	* @param @param httpservlet
	* @return Iterable<Line>    返回在线用户
	* @throws
	 */
	@GetMapping(path="/list")
	public @ResponseBody Iterable<Line> lineList (HttpServletRequest httpservlet) {
		HttpSession h = httpservlet.getSession();
		String auth = (String) h.getAttribute("username"); 
		System.err.println(auth);
		if(auth != null) {  //用户名存在
			return lineRepository.findAll();  //在line表中查找在线用户
		}else {
			return null;
		}
	}
	
}
