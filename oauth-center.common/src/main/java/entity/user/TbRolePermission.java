package entity.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色对应权限
 * @author  zhulinzhong
 */
@Data
public class TbRolePermission implements Serializable {
    private static final long serialVersionUID = -6087005144617445198L;
    private Long id;

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 权限 ID
     */
    private Long permissionId;

}