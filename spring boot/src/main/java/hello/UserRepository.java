package hello;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.User;
/**
 * 
* @ClassName: UserRepository 
* @Description: TODO(通过接口查询数据库) 
* @author LvYongjian 
* @date 2019年7月3日 下午9:41:19 
*
 */
public interface UserRepository extends JpaRepository<User, Long> {

	Iterable<User> findByNameAndPassword(String name, String password);

	Iterable<User> findAllByUid(String uid);

	Iterable<User> findByName(String name);

}
