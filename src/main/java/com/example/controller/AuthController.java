package com.example.controller;

import com.example.config.JwtProvider;
import com.example.entity.User;
import com.example.payload.SignInDTO;
import com.example.payload.UserDTO;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    final JwtProvider jwtProvider;
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;


    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO){
        if (userRepository.existsByUsername(userDTO.username())) {
            throw new RuntimeException("Username is already in use");
        }
        User user = User
                .builder()
                .username(userDTO.username())
                .password(passwordEncoder.encode(userDTO.password()))
                .name(userDTO.name())
                .role("USER")
                .build();
        userRepository.save(user);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDTO userDTO){
        User user = userRepository.findByUsername(userDTO.username()).orElseThrow(RuntimeException::new);
        if (!passwordEncoder.matches(userDTO.password(),user.getPassword())){
            throw new RuntimeException("Wrong password or username");
        }
//        if (!user.getPassword().equals(userDTO.password())) {
//            throw new RuntimeException("Wrong password");
//        }
        String token = jwtProvider.generateToken(user);

//        byte[] encode = Base64.getEncoder().encode((user.getUsername() + ":" + user.getPassword()).getBytes());
        return ResponseEntity.ok().body(token);
    }
}
