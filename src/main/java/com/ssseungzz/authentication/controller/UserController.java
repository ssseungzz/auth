package com.ssseungzz.authentication.controller;

import com.ssseungzz.authentication.common.response.Response;
import com.ssseungzz.authentication.common.response.ResponseCode;
import com.ssseungzz.authentication.common.response.ResponseMessage;
import com.ssseungzz.authentication.config.security.JwtTokenProvider;
import com.ssseungzz.authentication.domain.User;
import com.ssseungzz.authentication.domain.UserRepository;
import com.ssseungzz.authentication.dto.request.SignInRequestDto;
import com.ssseungzz.authentication.dto.request.SignUpRequestDto;
import com.ssseungzz.authentication.dto.response.SignUpResponseDto;
import com.ssseungzz.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public Response signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        try {
            return Response.builder()
                    .responseCode(ResponseCode.SUCCESS)
                    .responseData(userService.signUp(signUpRequestDto))
                    .build();
        } catch (Exception e) {
                return Response.builder()
                        .responseCode(ResponseCode.FAIL)
                        .responseData(e.getMessage())
                        .build();
        }
    }

    @PostMapping("/sign-in")
    public Response login(@RequestBody SignInRequestDto signInRequestDto) {
        try {
            return Response.builder()
                    .responseCode(ResponseCode.SUCCESS)
                    .responseData(userService.loginByEmailAndPassword(signInRequestDto))
                    .build();
        } catch (Exception e) {
            if(e.getMessage().equals(ResponseMessage.NEED_TO_SIGN_UP)) {
                return Response.builder()
                        .responseCode(ResponseCode.NEED_TO_SIGN_UP)
                        .responseData(e.getMessage())
                        .build();
            } else {
                return Response.builder()
                        .responseCode(ResponseCode.FAIL)
                        .responseData(e.getMessage())
                        .build();
            }
        }
    }
}

