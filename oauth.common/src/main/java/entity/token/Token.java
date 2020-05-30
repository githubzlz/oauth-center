package entity.token;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2019/10/24 10:47
 */
@Data
public class Token implements Serializable {

    private static final long serialVersionUID = 3795349702952403673L;
    /**
     * token的值
     */
    private String tokenValue;

    /**
     * token的过时时间
     */
    private String overTime;

}
