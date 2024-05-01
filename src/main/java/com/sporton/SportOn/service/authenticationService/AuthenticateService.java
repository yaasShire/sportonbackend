package com.sporton.SportOn.service.authenticationService;


import com.sporton.SportOn.entity.AppUser;
import com.sporton.SportOn.exception.authenticationException.AuthenticationException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.authenticationModel.*;

public interface AuthenticateService {
    public OTPResponseModel signUpUser(SignUpRequestModel body) throws AuthenticationException;

    SignUpResponseModel verifyOTP(OTP otp) throws AuthenticationException;

    SignInResponseModel signIn(SignInRequestModel body) throws AuthenticationException;

    OTPResponseModel sendForgetPasswordOTP(RequestForgetPasswordOTP phoneNumber) throws AuthenticationException;

    ForgetPasswordResponse updatePassword(ChangePasswordCredentials changePasswordCredentials) throws AuthenticationException;

    OTPResponseModel verifyForgetPasswordOTP(OTP otp) throws AuthenticationException;

    AppUser getProfileData(String phoneNumber) throws AuthenticationException;

    CommonResponseModel updateProfileData(UpdateProfileData body, String phoneNumber) throws AuthenticationException;

    CommonResponseModel changePassword(ChangePasswordAuthrorizedRequest body, String phoneNumber) throws AuthenticationException;

    SignInResponseModel singInAsCustomer(SignInRequestModel body) throws AuthenticationException;
}
