package entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 * @author zhulinzhong
 */
@Data
@TableName("tb_user")
public class TbUser implements Serializable {
    private static final long serialVersionUID = -4540933489036634187L;
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码，加密存储
     */
    private String password;

}