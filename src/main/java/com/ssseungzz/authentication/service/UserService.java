package com.ssseungzz.authentication.service;

import com.ssseungzz.authentication.common.response.ResponseMessage;
import com.ssseungzz.authentication.config.security.JwtTokenProvider;
import com.ssseungzz.authentication.domain.User;
import com.ssseungzz.authentication.domain.UserRepository;
import com.ssseungzz.authentication.dto.request.SignInRequestDto;
import com.ssseungzz.authentication.dto.request.SignUpRequestDto;
import com.ssseungzz.authentication.dto.response.SignInResponseDto;
import com.ssseungzz.authentication.dto.response.SignUpResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public SignInResponseDto loginByEmailAndPassword(SignInRequestDto signInRequestDto) throws Exception {
        Optional<User> optUser = userRepository.findByEmail(signInRequestDto.getEmail());
        if (optUser.isPresent()) {
            User user = optUser.get();
            logger.trace(user.getEmail());
            logger.trace(user.getPassword());
            if(passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
                return new SignInResponseDto(user.getId(),
                        jwtTokenProvider.createToken(user.getEmail(), user.getRoles()),
                        user.getEmail());
            } else {
                throw new Exception(ResponseMessage.INVALID_ID_OR_PASSWORD);
            }
        } else {
            throw new Exception(ResponseMessage.NEED_TO_SIGN_UP);
        }
    }

    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) throws Exception{
        if("".equals(signUpRequestDto.getEmail()) || "".equals(signUpRequestDto.getPassword())) {
            logger.info("EMPTY USER ID OR PASSWORD");
            throw new Exception(ResponseMessage.EMPTY_USER_ID_OR_PASSWORD);
        }



        if(userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            logger.info("DUPLICATE EMAIL");
            throw new Exception(ResponseMessage.DUPLICATE_EMAIL);
        }

        logger.info("NO PROBLEM");
        User user = userRepository.save(User.builder()
                        .email(signUpRequestDto.getEmail())
                        .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build());
        return new SignUpResponseDto(user.getId(), user.getEmail());

    }

}
