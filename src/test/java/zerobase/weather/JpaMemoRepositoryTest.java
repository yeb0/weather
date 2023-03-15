package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JpaMemoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        //given - 어떤 데이터가 있을 때
        Memo newMemo = new Memo(10, "this is jpa memo");
        //when - 어떤 동작을 하게 되면 ?
        jpaMemoRepository.save(newMemo);
        //then - 어떤 결과가 나타나야 한다.
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size() > 0 );
    }

    @Test
    void findByIdTest() {
        //given - 어떤 데이터가 있을 때
        Memo newMemo = new Memo(11, "jpa");
        //when - 어떤 동작을 하게 되면 ?
        Memo memo = jpaMemoRepository.save(newMemo);
        System.out.println(memo.getId());
        //then - 어떤 결과가 나타나야 한다.
        Optional<Memo> result = jpaMemoRepository.findById(memo.getId());
        assertEquals(result.get().getText(), "jpa");
    }

}
