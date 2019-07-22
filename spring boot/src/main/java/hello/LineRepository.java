package hello;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.Line;
/**
 * 
* @ClassName: LineRepository 
* @Description: TODO(通过接口查询数据库) 
* @author LvYongjian 
* @date 2019年7月3日 下午9:45:37 
*
 */
public interface LineRepository extends JpaRepository<Line, Long> {

	Iterable<Line> findAllByUid(String uid);

	Iterable<Line> deleteByUid(String uid);

}
