package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;
    private final DiaryRepository diaryRepository;

    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional // DB 를 건드는, SAVE 하는 함수이니 @Transactional ...
    @Scheduled(cron = "0 0 1 * * *") // 초 분 시 일 월 .. -> 0초 0분 1시 매일 매달 (매일 새벽 1시마다 이 함수가 동작함)
    public void saveWeatherDate() {
        logger.info("오늘도 새벽 1시에 날씨를 잘 가져왔습니다.");
        dateWeatherRepository.save(getWeatherFromApi());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");
//        //open weather data map 에서 날씨 데이터 가져오기.
//        String weatherData = getWeatherString();
//        // 받아온 날씨 json 파싱하기.
//        Map<String, Object> parsedWeather = parseWeather(weatherData);

        // 날씨 데이터 가져오기. (api 에서 가져오기 / db 에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);
        // 파싱된 데이터 + 일기 값 db 에 넣기.
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);

        diaryRepository.save(nowDiary);
        logger.info("end to create diary - !");
    }

    private DateWeather getWeatherFromApi() {
        //open weather data map 에서 날씨 데이터 가져오기.
        String weatherData = getWeatherString();
        // 받아온 날씨 json 파싱하기.
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double)parsedWeather.get("temp"));

        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);

        if (dateWeatherListFromDB.size() == 0) { // not found
            // 새로 api 에서 날씨 정보를 가져올 것.
            // 정책상.. 현재 날씨를 가져 오도록 하거나, 날씨 없이 일기를 쓰도록..
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
//        logger.debug("read - diary");
//        if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
//            throw new InvalidDate();
//        }
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    /**
     * open api 날씨 가져오기
     */
    private String getWeatherString() {
        String apuUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(apuUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // HttpURLConnection 을 열어줌 (connection)
            connection.setRequestMethod("GET"); // GET 요청 받기
            int responseCode = connection.getResponseCode(); // 응답 코드 int 형으로 가져옴 (200, 404.. )
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream())); // 200 이면 success
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream())); // 아닌 것들 fail
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        }catch (Exception e) {
            return "failed to get response";
        }
    }

    /**
     * 가져온 날씨 데이터 Json 형식으로 된 것을 파싱해오기
     */
    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser(); // json parser를 통해 파싱하여
        JSONObject jsonObject; // jsonObject 에 담을 것임

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString); // JSONObject 타입 맞추면서 jsonObject에 담음
        }catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather"); // [] 대괄호로 시작하니 Array인데, 요소가 하나밖에 없어 0 번째로 뽑아오는 것이다.
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }
}
