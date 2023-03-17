package zerobase.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement // project 안에서 transaction 이 동작하게 만듬.
@EnableScheduling // project 안에서 scheduling 기능을 사용할 수 있게끔 만듬.
public class WeatherApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}
}



/**
 * 캐싱을 사용하면 이점 ?
 * 사용자(client)의 응답속도, server의 부하도 줄일 수 있음. 백엔드 개발자는 반드시 캐싱에 대한 고민을 해봐야한다.
 *
 * 캐싱의 유의할 점
 *
 * 요청한 것에 대한 응답이 변하지 않을 때만 사용할 수 있다는 것이다.
 * 주기적으로 뭔가 계속해서 바뀔 수 있는 경우엔 그 바뀐 값에 대해 값이 들어가야 하니.. 캐싱이 힘들다.
 * 예를 들면 지금 하고 있는 날씨 데이터 같은 경우!!(이미 지난 일의 날씨는 이미 지났고 바뀌지 않으니 괜찮다!)
 */
