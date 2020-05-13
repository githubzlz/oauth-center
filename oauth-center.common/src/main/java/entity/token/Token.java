package entity.token;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2019/10/24 10:47
 */
public class Token {

    /**
     * token的值
     */
    private String token;

    /**
     * token的过时时间
     */
    private String overTime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOverTime() {
        return overTime;
    }

    public void setOverTime(String overTime) {
        overTime = overTime;
    }
}
