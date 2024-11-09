package com.invoice.approval.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.common.AuthConstant;
import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.ChangePasswordFormDTO;
import com.invoice.approval.dto.LoginFormDTO;
import com.invoice.approval.dto.RefreshTokenDTO;
import com.invoice.approval.dto.ResetPasswordFormDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.dto.SignUpFormDTO;
import com.invoice.approval.dto.UserResponseDTO;
import com.invoice.approval.entity.UserVO;
import com.invoice.approval.service.AuthService;

@RestController
@RequestMapping("api/auth")
public class AuthController extends BaseController {
	public static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	AuthService authService;

	@PutMapping("/signup")
	public ResponseEntity<ResponseDTO> signup(@Valid @RequestBody SignUpFormDTO signUpRequest) {
		String methodName = "signup()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			authService.signup(signUpRequest);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName, signUpRequest.getEmail(),
					errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.SIGNUP_REGISTERED_SUCCESS_MESSAGE);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, UserConstants.SIGNUP_REGISTERED_FAILED_MESSAGE,
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginFormDTO loginRequest,HttpServletRequest request) {
		String methodName = "login()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		UserResponseDTO userResponseDTO = null;
		try {
			userResponseDTO = authService.login(loginRequest,request);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName, loginRequest.getUserName(),
					errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.USER_LOGIN_SUCCESS_MESSAGE);
			responseObjectsMap.put(UserConstants.KEY_USER_VO, userResponseDTO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, UserConstants.USER_LOGIN_FAILED_MESSAGE,
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@GetMapping("/logout")
	public ResponseEntity<ResponseDTO> logout(@RequestParam String userName) {
		String methodName = "logout()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			authService.logout(userName);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName, userName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.USER_LOGOUT_SUCCESS_MESSAGE);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, UserConstants.USER_LOGOUT_FAILED_MESSAGE,
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@PostMapping("/changePassword")
	public ResponseEntity<ResponseDTO> changePassword(@Valid @RequestBody ChangePasswordFormDTO changePasswordRequest) {
		String methodName = "changePassword()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			authService.changePassword(changePasswordRequest);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName,
					changePasswordRequest.getUserName(), errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.CHANGE_PASSWORD_SUCCESS_MESSAGE);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, UserConstants.CHANGE_PASSWORD_FAILED_MESSAGE,
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<ResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordFormDTO resetPasswordRequest) {
		String methodName = "resetPassword()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			authService.resetPassword(resetPasswordRequest);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName,
					resetPasswordRequest.getUserName(), errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.RESET_PASSWORD_SUCCESS_MESSAGE);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, UserConstants.RESET_PASSWORD_FAILED_MESSAGE,
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@GetMapping("/getRefreshToken")
	public ResponseEntity<ResponseDTO> getRefreshToken(@RequestParam String userName, @RequestParam String tokenId) {
		String methodName = "getRefreshToken()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
		try {
			refreshTokenDTO = authService.getRefreshToken(userName, tokenId);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName, userName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, AuthConstant.REFRESH_TOKEN_SUCCESS_MESSAGE);
			responseObjectsMap.put(CommonConstant.REFRESH_TOKEN, refreshTokenDTO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, AuthConstant.REFRESH_TOKEN_FAILED_MESSAGE,
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	
	@GetMapping("/getUserById")
	public ResponseEntity<ResponseDTO> getUserById(@RequestParam Long userId) {
		String methodName = "getUserById()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		UserVO userVO = null;
		try {
			userVO = authService.getUserById(userId);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_ID, methodName, userId, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.GET_USER_INFORMATION_SUCCESS_MESSAGE);
			responseObjectsMap.put(UserConstants.KEY_USER_VO, userVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					UserConstants.GET_USER_INFORMATION_FAILED_MESSAGE, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getUserByUserName")
	public ResponseEntity<ResponseDTO> getUserByUserName(@RequestParam String userName) {
		String methodName = "getUserByUserName()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		UserVO userVO = null;
		try {
			userVO = authService.getUserByUserName(userName);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_ID, methodName, userName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.GET_USER_INFORMATION_SUCCESS_MESSAGE);
			responseObjectsMap.put(UserConstants.KEY_USER_VO, userVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					UserConstants.GET_USER_INFORMATION_FAILED_MESSAGE, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
}