package com.invoice.approval.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.invoice.approval.common.AuthConstant;
import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.ChangePasswordFormDTO;
import com.invoice.approval.dto.LoginFormDTO;
import com.invoice.approval.dto.RefreshTokenDTO;
import com.invoice.approval.dto.ResetPasswordFormDTO;
import com.invoice.approval.dto.SignUpFormDTO;
import com.invoice.approval.dto.UserResponseDTO;
import com.invoice.approval.entity.TokenVO;
import com.invoice.approval.entity.UserVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.TokenRepo;
import com.invoice.approval.repo.UserActionRepo;
import com.invoice.approval.repo.UserRepo;
import com.invoice.approval.security.TokenProvider;
import com.invoice.approval.util.CryptoUtils;

@Service
public class AuthServiceImpl implements AuthService {

	public static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Autowired
	UserRepo userRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserActionRepo userActionRepo;


	@Autowired
	TokenProvider tokenProvider;

	@Autowired
	TokenRepo tokenRepo;

	@Autowired
	UserService userService;


	@Override
	public void signup(SignUpFormDTO signUpRequest) {
		String methodName = "signup()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (ObjectUtils.isEmpty(signUpRequest) || StringUtils.isBlank(signUpRequest.getEmail())
				|| StringUtils.isBlank(signUpRequest.getUserName())) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_REGISTER_INFORMATION);
		}
//		else if (userRepo.existsByUserNameOrEmail(signUpRequest.getUserName(), signUpRequest.getEmail())) 
//		{
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_ALREADY_REGISTERED);
//		}
		UserVO userVO = getUserVOFromSignUpFormDTO(signUpRequest);
		userRepo.save(userVO);
		userService.createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_ADD_ACCOUNT);
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	}

	private UserVO getUserVOFromSignUpFormDTO(SignUpFormDTO signUpFormDTO) {

		UserVO userVO = new UserVO();

//		userVO=userRepo.findByUserNameOrEmailOrMobileNo(signUpFormDTO.getUserName(), signUpFormDTO.getEmail(), signUpFormDTO.getEmail());
		if (userRepo.existsByUserName(signUpFormDTO.getUserName())) {
			userVO = userRepo.findByUserName(signUpFormDTO.getUserName());
		}
		userVO.setUserName(signUpFormDTO.getUserName());
		if (ObjectUtils.isEmpty(userVO.getId())) {
			try {
				userVO.setPassword(encoder.encode(CryptoUtils.getDecrypt(signUpFormDTO.getPassword())));
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
			}
		}
		userVO.setNickName(signUpFormDTO.getNickName());
		userVO.setEmail(signUpFormDTO.getEmail());
		userVO.setEmployeeCode(signUpFormDTO.getEmployeeCode());
		userVO.setUserType(signUpFormDTO.getUserType());
		userVO.setActive(signUpFormDTO.isActive());
		return userVO;
	}

	@Override
	public UserResponseDTO login(LoginFormDTO loginRequest, HttpServletRequest request) throws ApplicationException {
		String methodName = "login()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (ObjectUtils.isEmpty(loginRequest) || StringUtils.isBlank(loginRequest.getUserName())
				|| StringUtils.isBlank(loginRequest.getPassword())) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_LOGIN_INFORMATION);
		}
		UserVO userVO = userRepo.findByUserName(loginRequest.getUserName());

		if (ObjectUtils.isNotEmpty(userVO)) {
			if(userVO.getActive()=="In-Active")
			{
				throw new ApplicationException("Your account is In-Active, Please Contact Administrator");
			}
			if (compareEncodedPasswordWithEncryptedPassword(loginRequest.getPassword(), userVO.getPassword())) {
				updateUserLoginInformation(userVO, request);
			} else {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_PASSWORD_MISMATCH);
			}
		} else {
			throw new ApplicationContextException(
					UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND_AND_ASKING_SIGNUP);
		}
		UserResponseDTO userResponseDTO = mapUserVOToDTO(userVO);
		TokenVO tokenVO = tokenProvider.createToken(userVO.getId(), loginRequest.getUserName());
		userResponseDTO.setToken(tokenVO.getToken());
		userResponseDTO.setTokenId(tokenVO.getId());
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return userResponseDTO;
	}

	/**
	 * @param encryptedPassword -> Data from user;
	 * @param encodedPassword   ->Data from DB;
	 * @return
	 */
	private boolean compareEncodedPasswordWithEncryptedPassword(String encryptedPassword, String encodedPassword) {
		boolean userStatus = false;
		try {
			userStatus = encoder.matches(CryptoUtils.getDecrypt(encryptedPassword), encodedPassword);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
		}
		return userStatus;
	}

	@Override
	public void logout(String userName) {
		String methodName = "logout()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (StringUtils.isBlank(userName)) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_LOGOUT_INFORMATION);
		}
		UserVO userVO = userRepo.findByUserName(userName);
		if (ObjectUtils.isNotEmpty(userVO)) {
			updateUserLogOutInformation(userVO);
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	}

	@Override
	public void changePassword(ChangePasswordFormDTO changePasswordRequest) {
		String methodName = "changePassword()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (ObjectUtils.isEmpty(changePasswordRequest) || StringUtils.isBlank(changePasswordRequest.getUserName())
				|| StringUtils.isBlank(changePasswordRequest.getOldPassword())
				|| StringUtils.isBlank(changePasswordRequest.getNewPassword())) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_CHANGE_PASSWORD_INFORMATION);
		}
		UserVO userVO = userRepo.findByUserName(changePasswordRequest.getUserName());
		if (ObjectUtils.isNotEmpty(userVO)) {
			if (compareEncodedPasswordWithEncryptedPassword(changePasswordRequest.getOldPassword(),
					userVO.getPassword())) {
				try {
					userVO.setPassword(encoder.encode(CryptoUtils.getDecrypt(changePasswordRequest.getNewPassword())));
				} catch (Exception e) {
					throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
				}
				userRepo.save(userVO);
				userService.createUserAction(userVO.getUserName(), userVO.getId(),
						UserConstants.USER_ACTION_TYPE_CHANGE_PASSWORD);
			} else {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_OLD_PASSWORD_MISMATCH);
			}
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	}

	@Override
	public void resetPassword(ResetPasswordFormDTO resetPasswordRequest) {
		String methodName = "resetPassword()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (ObjectUtils.isEmpty(resetPasswordRequest) || StringUtils.isBlank(resetPasswordRequest.getUserName())
				|| StringUtils.isBlank(resetPasswordRequest.getNewPassword())) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_RESET_PASSWORD_INFORMATION);
		}
		UserVO userVO = userRepo.findByUserName(resetPasswordRequest.getUserName());
		if (ObjectUtils.isNotEmpty(userVO)) {
			try {
				userVO.setPassword(encoder.encode(CryptoUtils.getDecrypt(resetPasswordRequest.getNewPassword())));
			} catch (Exception e) {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
			}
			userRepo.save(userVO);
			userService.createUserAction(userVO.getUserName(), userVO.getId(),
					UserConstants.USER_ACTION_TYPE_RESET_PASSWORD);
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	}

	@Override
	public RefreshTokenDTO getRefreshToken(String userName, String tokenId) throws ApplicationException {
		UserVO userVO = userRepo.findByUserName(userName);
		RefreshTokenDTO refreshTokenDTO = null;
		if (ObjectUtils.isEmpty(userVO)) {
			throw new ApplicationException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
		}
		TokenVO tokenVO = tokenRepo.findById(tokenId).orElseThrow(() -> new ApplicationException("Invalid Token Id."));
		if (tokenVO.getExpDate().compareTo(new Date()) > 0) {
			tokenVO = tokenProvider.createRefreshToken(tokenVO, userVO);
			refreshTokenDTO = RefreshTokenDTO.builder().token(tokenVO.getToken()).tokenId(tokenVO.getId()).build();
		} else {
			tokenRepo.delete(tokenVO);
			throw new ApplicationException(AuthConstant.REFRESH_TOKEN_EXPIRED_MESSAGE);
		}
		return refreshTokenDTO;
	}

	/**
	 * @param userVO
	 * @param request
	 */
	private void updateUserLoginInformation(UserVO userVO, HttpServletRequest request) {
		try {
			userVO.setLoginStatus(true);
			userRepo.save(userVO);
			userService.createUserLoginAction(userVO.getUserName(), userVO.getId(),
					UserConstants.USER_ACTION_TYPE_LOGIN, request);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_UPDATE_USER_INFORMATION);
		}
	}

	private void updateUserLogOutInformation(UserVO userVO) {
		try {
			userVO.setLoginStatus(false);
			userRepo.save(userVO);
			userService.createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_TYPE_LOGOUT);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_UPDATE_USER_INFORMATION);
		}
	}

	public static UserResponseDTO mapUserVOToDTO(UserVO userVO) {
		UserResponseDTO userDTO = new UserResponseDTO();
		userDTO.setUsersId(userVO.getId());
		userDTO.setEmployeeCode(userVO.getEmployeeCode());
		userDTO.setActive(userVO.isActive());
		userDTO.setUserType(userVO.getUserType());
		userDTO.setNickName(userVO.getNickName());
		userDTO.setEmail(userVO.getEmail());
		userDTO.setUserName(userVO.getUserName());
		userDTO.setLoginStatus(userVO.isLoginStatus());
		userDTO.setCommonDate(userVO.getCommonDate());
		return userDTO;
	}

	

	@Override
	public UserVO getUserById(Long usersId) {
		String methodName = "getUserById()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (ObjectUtils.isEmpty(usersId)) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_ID);
		}
		UserVO userVO = userRepo.getUserById(usersId);
		if (ObjectUtils.isEmpty(userVO)) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return userVO;
	}

	@Override
	public UserVO getUserByUserName(String userName) {
		String methodName = "getUserByUserName()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (StringUtils.isNotEmpty(userName)) {
			UserVO userVO = userRepo.findByUserName(userName);
			if (ObjectUtils.isEmpty(userVO)) {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
			}
			LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
			return userVO;
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_NAME);
		}
	}
}
