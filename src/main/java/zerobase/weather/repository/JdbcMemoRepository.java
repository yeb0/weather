package zerobase.weather.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMemoRepository(DataSource dataSource) { // application.properties에 지정한 datasource 들이 여기 생성자에 있는 dataSource 라는 객체에 담기게 된다.
        jdbcTemplate = new JdbcTemplate(dataSource); // 이것을 활용해 JdbcTemplate 를 만들고 jdbcTemplate 라는 변수에 담아주는 것.
    }

    /**
     * JDBC의 특징 - 쿼리를 직접 써야 한다.
     */
    public Memo save(Memo memo) {
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    public List<Memo> findAll() {
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    /**
     * Optional ?
     * ex) id 가 3인 row를 찾아보려하니 없을 때, null 처리하기위함
     */
    public Optional<Memo> findById(int id) {
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

    /**
     * RowMapper ?
     * jdbc를 통해서 db(mysql)에서 데이터를 가져오면, 가져온 데이터 값은 ResultSet 이라는 데이터 형식으로 가져옴.
     * 예를 들어, {id = 1, text='this is memo'} 라는 형식으로 가져오게 됨.
     * 결국 이 데이터를 대입을 시켜야 하는데, 밑의 코드로 예를 들어..
     * ResultSet 을 Memo 타입으로 return (mapping)
     */

    private RowMapper<Memo> memoRowMapper() {
        // rs == ResultSet
        return(rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }
}
