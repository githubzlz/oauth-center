package entity.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户与角色对应
 * @author zhulinzhong
 */
@Data
public class TbUserRole implements Serializable {
    private static final long serialVersionUID = 9127452272864633844L;
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 角色 ID
     */
    private Long roleId;

}