package com.sporton.SportOn.controller;

import com.sporton.SportOn.configuration.JWTService;
import com.sporton.SportOn.entity.AppUser;
import com.sporton.SportOn.exception.authenticationException.AuthenticationException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.authenticationModel.*;
import com.sporton.SportOn.service.authenticationService.AuthenticateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {
    private  final AuthenticateService authenticateService;
    private final JWTService jwtService;

    @PostMapping("/signUp")
    public OTPResponseModel signUpUser(@RequestBody SignUpRequestModel body) throws AuthenticationException {
        return authenticateService.signUpUser(body);
    }

    @PostMapping("/verifyOTP")
    public SignUpResponseModel verifyOTP(@RequestBody OTP otp) throws AuthenticationException {
        return authenticateService.verifyOTP(otp);
    }

    @PostMapping("/signIn")
    public SignInResponseModel singIn(@RequestBody SignInRequestModel body) throws AuthenticationException {
        return authenticateService.signIn(body);
    }

    @PostMapping("/singInAsCustomer")
    public SignInResponseModel singInAsCustomer(
            @RequestBody SignInRequestModel body
    ) throws AuthenticationException {
        return authenticateService.singInAsCustomer(body);
    }
    @PostMapping("/requestForgetPasswordOTP")
    public OTPResponseModel forgetPasswordVerificationPhoneNumber(@RequestBody RequestForgetPasswordOTP phoneNumber) throws AuthenticationException {
        return authenticateService.sendForgetPasswordOTP(phoneNumber);
    }

    @PostMapping("/verifyForgetPasswordOTP")
    public OTPResponseModel verifyForgetPasswordOTP(@RequestBody OTP otp) throws AuthenticationException {
        return authenticateService.verifyForgetPasswordOTP(otp);
    }

    @PostMapping("/changeForgottedPassword")
    public ForgetPasswordResponse updateForgettedPassword(@RequestBody ChangePasswordCredentials changePasswordCredentials) throws AuthenticationException {
        return authenticateService.updatePassword(changePasswordCredentials);
    }

    @GetMapping("/profileData")
    public AppUser getProfileData(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws AuthenticationException {
        try {
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String phoneNumber = jwtService.extractUsername(token);
        return authenticateService.getProfileData(phoneNumber);
        }catch (Exception e){
            throw new AuthenticationException(e.getMessage());
        }
    }
    @PutMapping("/updateProfileData")
    public CommonResponseModel updateProfileData(
            @RequestBody UpdateProfileData body,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws AuthenticationException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
         return authenticateService.updateProfileData(body, phoneNumber);
        }catch (Exception e){
            throw new AuthenticationException(e.getMessage());
        }
    }

    @PutMapping("/changePassword")
    public CommonResponseModel changePassword(
            @RequestBody ChangePasswordAuthrorizedRequest body,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws AuthenticationException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return authenticateService.changePassword(body, phoneNumber);
        }catch (Exception e){
            throw new AuthenticationException(e.getMessage());
        }
    }
}
