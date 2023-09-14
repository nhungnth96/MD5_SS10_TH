package ss8.th.service;


import ss8.th.model.entity.User;

import java.util.Optional;

public interface IUserService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    User save(User user);
}
