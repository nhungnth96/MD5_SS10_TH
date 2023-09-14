package ss8.th.service;

import ss8.th.model.entity.Role;
import ss8.th.model.entity.RoleName;

import java.util.Optional;

public interface IRoleService {
    Optional<Role> findByRoleName(RoleName roleName);
}
