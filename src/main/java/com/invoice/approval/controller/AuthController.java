package com.invoice.approval.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.invoice.approval.dto.ResponsibilityDTO;
import com.invoice.approval.dto.RolesDTO;
import com.invoice.approval.dto.ScreenNamesDTO;
import com.invoice.approval.dto.SignUpFormDTO;
import com.invoice.approval.dto.UserResponseDTO;
import com.invoice.approval.entity.ResponsibilityVO;
import com.invoice.approval.entity.RolesVO;
import com.invoice.approval.entity.ScreenNamesVO;
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
	
	@PutMapping("/createUpdateResponsibility")
	public ResponseEntity<ResponseDTO> createUpdateResponsibility(@RequestBody ResponsibilityDTO responsibilityDTO) {
		String methodName = "createUpdateResponsibility()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> responsibile = authService.createUpdateResponsibilities(responsibilityDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, responsibile.get("message") );
			responseObjectsMap.put("responsibilityVO", responsibile.get("responsibilityVO"));
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap,errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getResponsibilityForRoles")
	public ResponseEntity<ResponseDTO> getResponsibilityForRoles() {
		String methodName = "getResponsibilityForRoles()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> responsibilityVO = new ArrayList<>();
		try {
			responsibilityVO = authService.getResponsibilityForRoles();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Responsibility Founded");
			responseObjectsMap.put("responsibilityVO", responsibilityVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			errorMsg = "Responsibility not found";
			responseDTO = createServiceResponseError(responseObjectsMap, "Responsibility not found", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@PutMapping("/createUpdateRoles")
	public ResponseEntity<ResponseDTO> createUpdateRoles(@RequestBody RolesDTO rolesDTO) {
		String methodName = "createUpdateResponsibility()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> roles = authService.createUpdateRoles(rolesDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, roles.get("message") );
			responseObjectsMap.put("rolesVO", roles.get("rolesVO"));
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap,errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/allRoles")
	public ResponseEntity<ResponseDTO> getAllRoles() {
		String methodName = "getAllRoles()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<RolesVO> rolesVO = new ArrayList<>();
		try {
			rolesVO = authService.getAllRoles();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Roles information get successfully");
			responseObjectsMap.put("rolesVO", rolesVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Roles information receive failed", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/allActiveRoles")
	public ResponseEntity<ResponseDTO> getAllActiveRoles() {
		String methodName = "getAllActiveRoles()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<RolesVO> rolesVO = new ArrayList<>();
		try {
			rolesVO = authService.getAllActiveRoles();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Roles information get successfully");
			responseObjectsMap.put("rolesVO", rolesVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Roles information receive failed", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/rolesById")
	public ResponseEntity<ResponseDTO> getRolesById(@RequestParam Long id) {
		String methodName = "getRolesById()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		RolesVO rolesVO = new RolesVO();
		try {
			rolesVO = authService.getRolesById(id);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Roles information get successfully");
			responseObjectsMap.put("rolesVO", rolesVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Roles information receive failed", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/allActiveResponsibility")
	public ResponseEntity<ResponseDTO> getAllActiveResponsibility() {
		String methodName = "getAllActiveResponsibility()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<ResponsibilityVO> resposResponsibilityVO = new ArrayList<>();
		try {
			resposResponsibilityVO = authService.getAllActiveResponsibility();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Responsibility information get successfully");
			responseObjectsMap.put("resposResponsibilityVO", resposResponsibilityVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Responsibility information receive failed", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/allResponsibility")
	public ResponseEntity<ResponseDTO> getAllResponsibility() {
		String methodName = "getAllResponsibility()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<ResponsibilityVO> responsibilityVO = new ArrayList<>();
		try {
			responsibilityVO = authService.getAllResponsibility();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Responsibility information get successfully");
			responseObjectsMap.put("responsibilityVO", responsibilityVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Responsibility information receive failed", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@GetMapping("/responsibilityById")
	public ResponseEntity<ResponseDTO> getResponsibilityById(@RequestParam Long id) {
		String methodName = "getResponsibilityById()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		ResponsibilityVO responsibilityVO = new ResponsibilityVO();
		try {
			responsibilityVO = authService.getResponsibilityById(id);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Responsibility information get successfully");
			responseObjectsMap.put("responsibilityVO", responsibilityVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Responsibility information receive failed", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/allUsers")
	public ResponseEntity<ResponseDTO> getAllUsers() {
		String methodName = "getAllUsers()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<UserVO> userVO = new ArrayList<>();
		try {
			userVO = authService.getAllUsers();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Users information get successfully");
			responseObjectsMap.put("userVO", userVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Users information receive failed", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	// Screen Names
	@PutMapping("/createUpdateScreenNames")
	public ResponseEntity<ResponseDTO> createUpdateScreenNames(@RequestBody ScreenNamesDTO screenNamesDTO) {
		String methodName = "createUpdateScreenNames()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> screenNamesVO = authService.createUpdateScreenNames(screenNamesDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, screenNamesVO.get("message"));
			responseObjectsMap.put("screenNamesVO", screenNamesVO.get("screenNamesVO"));
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@GetMapping("/getAllScreenNames")
	public ResponseEntity<ResponseDTO> getAllScreenNames() {
		String methodName = "getAllScreenNames()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<ScreenNamesVO> screenNamesVO = new ArrayList<>();
		try {
			screenNamesVO = authService.getAllScreenNames();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "screenNames information get successfully");
			responseObjectsMap.put("screenNamesVO", screenNamesVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "screenNames information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@GetMapping("/screenNamesById")
	public ResponseEntity<ResponseDTO> getScreenNamesById(@RequestParam Long id) {
		String methodName = "getScreenNamesById()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		ScreenNamesVO screenNamesVO = new ScreenNamesVO();
		try {
			screenNamesVO = authService.getScreenNamesById(id);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "screenNames information get successfully");
			responseObjectsMap.put("screenNamesVO", screenNamesVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "screenNames information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	

}