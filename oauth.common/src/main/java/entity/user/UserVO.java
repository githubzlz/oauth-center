package entity.user;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2019/10/18 17:56
 */
@Data
@TableName("user")
public class UserVO extends BaseEntity{

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 备注
     */
    private String note;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    /**
     * 是否封禁
     */
    private Integer isBanned;

    /**
     * 是否是管理员
     */
    private Integer isAdmin;

}
