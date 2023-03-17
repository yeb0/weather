package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> { // Diary 형식의 ID 값은 Integer 형식
    List<Diary> findAllByDate(LocalDate date);

    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    Diary getFirstByDate(LocalDate date);

    @Transactional
    void deleteAllByDate(LocalDate date);

}
/**
 * @Transactional ?
 * testCode를 통해서 db 의 상태가 변경되는 것을 원치 않을 때 test 과정에서 db 바꾼 걸 원래대로 복구해놓는 게 이 어노테이션의 역할이다.
 * ...
 * 이것은 이 @Transactional 의 기능 일부.
 * 데이터를 부트와 주고 받을 때 그 과정 중 일어날 수 있는 예외들이나 오류들이 있는데,
 * 그런 것들 관련해서 .......
 */