package ss8.th.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ss8.th.model.entity.Role;
import ss8.th.model.entity.RoleName;
import ss8.th.repository.IRoleRepository;
import ss8.th.service.IRoleService;

import java.util.Optional;

@Service
public class RoleService implements IRoleService {
    @Autowired
    IRoleRepository repository;
    @Override
    public Optional<Role> findByRoleName(RoleName roleName) {
        return repository.findByRoleName(roleName);
    }
}
