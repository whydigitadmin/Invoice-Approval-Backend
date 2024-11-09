package com.invoice.approval.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.invoice.approval.dto.ChangePasswordFormDTO;
import com.invoice.approval.dto.LoginFormDTO;
import com.invoice.approval.dto.RefreshTokenDTO;
import com.invoice.approval.dto.ResetPasswordFormDTO;
import com.invoice.approval.dto.SignUpFormDTO;
import com.invoice.approval.dto.UserResponseDTO;
import com.invoice.approval.entity.UserVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface AuthService {
	
	public void signup(SignUpFormDTO signUpRequest);

	public UserResponseDTO login(LoginFormDTO loginRequest, HttpServletRequest request) throws ApplicationException;

	public void logout(String userName);

	public void changePassword(ChangePasswordFormDTO changePasswordRequest);

	public void resetPassword(ResetPasswordFormDTO resetPasswordRequest);

	public RefreshTokenDTO getRefreshToken(String userName, String tokenId) throws ApplicationException;
			
	public UserVO getUserById(Long userId);

	public UserVO getUserByUserName(String userName);

}
