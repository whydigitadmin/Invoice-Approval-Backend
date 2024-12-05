package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.invoice.approval.dto.ChangePasswordFormDTO;
import com.invoice.approval.dto.LoginFormDTO;
import com.invoice.approval.dto.RefreshTokenDTO;
import com.invoice.approval.dto.ResetPasswordFormDTO;
import com.invoice.approval.dto.ResponsibilityDTO;
import com.invoice.approval.dto.RolesDTO;
import com.invoice.approval.dto.ScreenNamesDTO;
import com.invoice.approval.dto.SignUpFormDTO;
import com.invoice.approval.dto.UserResponseDTO;
import com.invoice.approval.entity.ResponsibilityVO;
import com.invoice.approval.entity.RolesVO;
import com.invoice.approval.entity.ScreenNamesVO;
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
			
	public UserVO getUserById(Long userId) throws Exception;

	public UserVO getUserByUserName(String userName);
	
	List<Map<String, Object>> getResponsibilityForRoles();
	
	Map<String, Object> createUpdateRoles(RolesDTO rolesDTO) throws ApplicationException;
	
	public List<RolesVO> getAllRoles();

	public List<RolesVO> getAllActiveRoles();
	
	RolesVO getRolesById(Long id) throws ApplicationException;
	
	Map<String, Object> createUpdateResponsibilities(ResponsibilityDTO responsibilityDTO) throws ApplicationException;
	
	public List<ResponsibilityVO> getAllResponsibility();

	public List<ResponsibilityVO> getAllActiveResponsibility();
	
	ResponsibilityVO getResponsibilityById(Long id) throws ApplicationException;

	public List<UserVO> getAllUsers();

	public Map<String, Object> createUpdateScreenNames(ScreenNamesDTO screenNamesDTO) throws ApplicationException;

	public List<ScreenNamesVO> getAllScreenNames();

	public ScreenNamesVO getScreenNamesById(Long id) throws ApplicationException;

}
