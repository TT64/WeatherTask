package task.weathertask.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Weather {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String date;

    private String cityName;

    private String temperature;

    private String pressure;

    private String humidity;

    private String sunriseTime;

    private String sunsetTime;

    private String cloudnessMsg;

    private String windMsg;

    public Weather(String date, String cityName, String temperature, String pressure, String humidity, String sunriseTime, String sunsetTime, String cloudnessMsg, String windMsg) {
        this.date = date;
        this.cityName = cityName;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
        this.cloudnessMsg = cloudnessMsg;
        this.windMsg = windMsg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(String sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public String getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(String sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public String getCloudnessMsg() {
        return cloudnessMsg;
    }

    public void setCloudnessMsg(String cloudnessMsg) {
        this.cloudnessMsg = cloudnessMsg;
    }

    public String getWindMsg() {
        return windMsg;
    }

    public void setWindMsg(String windMsg) {
        this.windMsg = windMsg;
    }
}
