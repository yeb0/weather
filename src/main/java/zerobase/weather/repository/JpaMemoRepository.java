package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

@Repository
public interface JpaMemoRepository extends JpaRepository<Memo, Integer> { // 무슨 클래스로 가져올 건지? 무슨 key 형태로 되어 있는지 ?(Memo에서는 int형이 key)

}
