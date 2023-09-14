package ss8.th.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ss8.th.model.dto.request.FormLoginDto;
import ss8.th.model.dto.request.FormRegisterDto;
import ss8.th.model.dto.response.JwtResponse;
import ss8.th.model.dto.response.ResponseMessage;
import ss8.th.model.entity.Role;
import ss8.th.model.entity.RoleName;
import ss8.th.model.entity.User;
import ss8.th.security.jwt.JwtProvider;
import ss8.th.security.principal.UserPrincipal;
import ss8.th.service.IRoleService;
import ss8.th.service.IUserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    IUserService userService;
    @Autowired
    IRoleService roleService;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody FormRegisterDto formRegisterDto) {
        if (userService.existsByUsername(formRegisterDto.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Failed -> Username is already taken!"),
                    HttpStatus.OK);
        }

        if (userService.existsByEmail(formRegisterDto.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Failed -> Email is already in use!"),
                    HttpStatus.OK);
        }

        // Creating user's account
        User user = new User();
        user.setId(1L);
        user.setName(formRegisterDto.getName());
        user.setUsername(formRegisterDto.getUsername());
        user.setEmail(formRegisterDto.getEmail());
        user.setPassword(passwordEncoder.encode(formRegisterDto.getPassword()));
        System.out.println("user ROW1"+user.toString());
        Set<String> strRoles = formRegisterDto.getRoles();
        Set<Role> roles = new HashSet<>();
        if(formRegisterDto.getRoles()==null||formRegisterDto.getRoles().isEmpty()){
            Role userRole = roleService.findByRoleName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Failed! -> Cause: User Role not find."));
            roles.add(userRole);

        }
//        strRoles.forEach(role -> {
//            switch (role) {
//                case "admin":
//                    Role adminRole = roleService.findByRoleName(RoleName.ADMIN)
//                            .orElseThrow(() -> new RuntimeException("Failed! -> Cause: User Role not find."));
//                    roles.add(adminRole);
//
//                    break;
//                case "pm":
//                    Role pmRole = roleService.findByRoleName(RoleName.PM)
//                            .orElseThrow(() -> new RuntimeException("Failed! -> Cause: User Role not find."));
//                    roles.add(pmRole);
//
//                    break;
//                default:
//                    Role userRole = roleService.findByRoleName(RoleName.USER)
//                            .orElseThrow(() -> new RuntimeException("Failed! -> Cause: User Role not find."));
//                    roles.add(userRole);
//            }
//        });

        user.setRoles(roles);

        userService.save(user);
        System.out.println("user"+user.toString());
        return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody FormLoginDto formLoginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(formLoginDto.getUsername(), formLoginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
               userDetails.getName(), userDetails.getEmail() ,
                userDetails.getAuthorities()
        ));
    }

}
