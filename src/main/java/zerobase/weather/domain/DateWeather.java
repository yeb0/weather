package zerobase.weather.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * 매일 1시마다 전날의 날씨를 이 DB에 담아주기 위하여 Entity 설정해놨음. 여기에 전날 날씨를 넣어두고
 * 일기를 쓸 때, open api에서 불러오는 것이 아닌 이 DB 에 저장된 날씨 데이터를 가져올 예정.(캐싱)
 */

@Entity(name = "date_weather")
@Getter
@Setter
@NoArgsConstructor
public class DateWeather {
    @Id
    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;

}


