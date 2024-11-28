package com.invoice.approval.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.invoice.approval.dto.ResponsibilityDTO;
import com.invoice.approval.dto.RolesDTO;
import com.invoice.approval.dto.RolesResponsibilityDTO;
import com.invoice.approval.dto.ScreenNamesDTO;
import com.invoice.approval.dto.ScreensDTO;
import com.invoice.approval.dto.SignUpFormDTO;
import com.invoice.approval.dto.UserLoginRoleAccessDTO;
import com.invoice.approval.dto.UserResponseDTO;
import com.invoice.approval.entity.ResponsibilityVO;
import com.invoice.approval.entity.RolesResponsibilityVO;
import com.invoice.approval.entity.RolesVO;
import com.invoice.approval.entity.ScreenNamesVO;
import com.invoice.approval.entity.ScreensVO;
import com.invoice.approval.entity.TokenVO;
import com.invoice.approval.entity.UserLoginRolesVO;
import com.invoice.approval.entity.UserVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.ResponsibilitiesRepo;
import com.invoice.approval.repo.RolesRepo;
import com.invoice.approval.repo.RolesResponsibilityRepo;
import com.invoice.approval.repo.ScreenNamesRepo;
import com.invoice.approval.repo.ScreensRepo;
import com.invoice.approval.repo.TokenRepo;
import com.invoice.approval.repo.UserActionRepo;
import com.invoice.approval.repo.UserLoginRolesRepo;
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

	@Autowired
	ScreensRepo screenRepo;

	@Autowired
	ResponsibilitiesRepo responsibilityRepo;

	@Autowired
	RolesRepo rolesRepo;

	@Autowired
	RolesResponsibilityRepo rolesResponsibilityRepo;

	@Autowired
	ScreenNamesRepo screenNamesRepo;

	@Autowired
	UserLoginRolesRepo loginRolesRepo;


//	@Override
//	public void signup(SignUpFormDTO signUpRequest) {
//		String methodName = "signup()";
//		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//		if (ObjectUtils.isEmpty(signUpRequest) || StringUtils.isBlank(signUpRequest.getEmail())
//				|| StringUtils.isBlank(signUpRequest.getUserName())) {
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_REGISTER_INFORMATION);
//		}
////		else if (userRepo.existsByUserNameOrEmail(signUpRequest.getUserName(), signUpRequest.getEmail())) 
////		{
////			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_ALREADY_REGISTERED);
////		}
//		UserVO userVO = getUserVOFromSignUpFormDTO(signUpRequest);
//		userRepo.save(userVO);
//		userService.createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_ADD_ACCOUNT);
//		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//	}
//
//	private UserVO getUserVOFromSignUpFormDTO(SignUpFormDTO signUpFormDTO) {
//
//		UserVO userVO = new UserVO();
//
////		userVO=userRepo.findByUserNameOrEmailOrMobileNo(signUpFormDTO.getUserName(), signUpFormDTO.getEmail(), signUpFormDTO.getEmail());
//		if (userRepo.existsByUserName(signUpFormDTO.getUserName())) {
//			userVO = userRepo.findByUserName(signUpFormDTO.getUserName());
//		}
//		userVO.setUserName(signUpFormDTO.getUserName());
//		if (ObjectUtils.isEmpty(userVO.getId())) {
//			try {
//				userVO.setPassword(encoder.encode(CryptoUtils.getDecrypt(signUpFormDTO.getPassword())));
//			} catch (Exception e) {
//				LOGGER.error(e.getMessage());
//				throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
//			}
//		}
//		userVO.setNickName(signUpFormDTO.getNickName());
//		userVO.setEmail(signUpFormDTO.getEmail());
//		userVO.setEmployeeCode(signUpFormDTO.getEmployeeCode());
//		userVO.setUserType(signUpFormDTO.getUserType());
//		userVO.setActive(signUpFormDTO.isActive());
//		return userVO;
//	}

	@Override
	public void signup(SignUpFormDTO signUpRequest) {
		String methodName = "signup()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (ObjectUtils.isEmpty(signUpRequest) || StringUtils.isBlank(signUpRequest.getEmail())
				|| StringUtils.isBlank(signUpRequest.getUserName())) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_REGISTER_INFORMATION);
		}
		UserVO userVO = getUserVOFromSignUpFormDTO(signUpRequest);
		userRepo.save(userVO);
		userService.createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_ADD_ACCOUNT);
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	}

	private UserVO getUserVOFromSignUpFormDTO(SignUpFormDTO signUpFormDTO) {

		UserVO userVO = new UserVO();

		if (userRepo.existsByUserNameOrEmail(signUpFormDTO.getUserName(), signUpFormDTO.getEmail())) {
			userVO = userRepo.findByUserNameOrEmail(signUpFormDTO.getUserName(), signUpFormDTO.getEmail());

			List<UserLoginRolesVO> roles = loginRolesRepo.findByUserVO(userVO);
			loginRolesRepo.deleteAll(roles);
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
		userVO.setEmployeeName(signUpFormDTO.getEmployeeName());

		List<UserLoginRolesVO> rolesVO = new ArrayList<>();
		if (signUpFormDTO.getRoleAccessDTO() != null) {
			for (UserLoginRoleAccessDTO accessDTO : signUpFormDTO.getRoleAccessDTO()) {

				UserLoginRolesVO loginRolesVO = new UserLoginRolesVO();
				loginRolesVO.setRole(accessDTO.getRole());
				loginRolesVO.setRoleId(accessDTO.getRoleId());
				loginRolesVO.setStartDate(accessDTO.getStartDate());
				loginRolesVO.setEndDate(accessDTO.getEndDate());
				loginRolesVO.setUserVO(userVO);
				rolesVO.add(loginRolesVO);
			}
		}

		userVO.setRoleAccessVO(rolesVO);
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
		UserVO userVO = userRepo.findByUserNameOrEmail(loginRequest.getUserName(), loginRequest.getUserName());

		if (ObjectUtils.isNotEmpty(userVO)) {
			if (userVO.getActive() == "In-Active") {
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

		List<Map<String, Object>> roleVOList = new ArrayList<>();

		// Iterate through UserLoginRolesVO to fetch roles and responsibilities
		for (UserLoginRolesVO loginRolesVO : userVO.getRoleAccessVO()) {
			Map<String, Object> roleMap = new HashMap<>();
			roleMap.put("role", loginRolesVO.getRole());
			roleMap.put("roleId", loginRolesVO.getRoleId());
			roleMap.put("startDate", loginRolesVO.getStartDate());
			roleMap.put("endDate", loginRolesVO.getEndDate());
			// Initialize the list for responsibilities under this role
			List<Map<String, Object>> responsibilityVOList = new ArrayList<>();

			// Fetch the RolesVO using loginRolesVO.getRoleId()
			RolesVO rolesVO = rolesRepo.findById(loginRolesVO.getRoleId()).orElse(null);
			if (rolesVO != null && rolesVO.getRolesReposibilitiesVO() != null) {
				for (RolesResponsibilityVO rolesResponsibilityVO : rolesVO.getRolesReposibilitiesVO()) {
					Map<String, Object> responsibilityMap = new HashMap<>();
					responsibilityMap.put("responsibilityName", rolesResponsibilityVO.getResponsibility());

					ResponsibilityVO responsibilityVO = responsibilityRepo
							.findById(rolesResponsibilityVO.getResponsibilityId()).orElse(null);
					if (loginRolesVO.getEndDate() == null || !loginRolesVO.getEndDate().isBefore(LocalDate.now())) {
						if (responsibilityVO != null && responsibilityVO.getScreensVO() != null) {
							List<String> screensList = new ArrayList<>();
							for (ScreensVO screenVO : responsibilityVO.getScreensVO()) {
								screensList.add(screenVO.getScreenName());
							}
							responsibilityMap.put("screensVO", screensList);
						}
						responsibilityVOList.add(responsibilityMap);
					} else {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
						String endDateFormatted = loginRolesVO.getEndDate().format(formatter);
						responsibilityMap.put("screensVO", null);
						responsibilityMap.put("expiredMessage",
								"Your Role " + loginRolesVO.getRole() + " was expired on " + endDateFormatted);
						responsibilityVOList.add(responsibilityMap);
					}
				}
			}

			// Add the responsibilities list to the role map
			roleMap.put("responsibilityVO", responsibilityVOList);

			// Add the role map to the roleVOList
			roleVOList.add(roleMap);
		}

		userResponseDTO.setRoleVO(roleVOList);

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

	@Override
	public List<Map<String, Object>> getResponsibilityForRoles() {
		Set<Object[]> activeResponsibility = responsibilityRepo.findActive();
		return getActiveResponsibile(activeResponsibility);
	}

	private List<Map<String, Object>> getActiveResponsibile(Set<Object[]> activeResponsibility) {
		List<Map<String, Object>> getResponse = new ArrayList<>();
		for (Object[] response : activeResponsibility) {
			Map<String, Object> res = new HashMap<>();
			res.put("responsibilityId", response[0] != null ? response[0].toString() : "");
			res.put("responsibility", response[1] != null ? response[1].toString() : "");
			getResponse.add(res);
		}
		return getResponse;
	}

	@Override
	public Map<String, Object> createUpdateRoles(RolesDTO rolesDTO) throws ApplicationException {
		RolesVO rolesVO = new RolesVO();
		String message;

		// Check if the rolesDTO ID is empty (indicating a new entry)
		if (ObjectUtils.isEmpty(rolesDTO.getId())) {

			// Validate if role already exists
			if (rolesRepo.existsByRole(rolesDTO.getRole())) {
				throw new ApplicationException("Role already exists");
			}

			rolesVO.setCreatedBy(rolesDTO.getCreatedBy());
			rolesVO.setUpdatedBy(rolesDTO.getCreatedBy());
			// Set the values from rolesDTO to rolesVO
			mapRolesDtoToRolesVo(rolesDTO, rolesVO);
			message = "Roles Created successfully";

		} else {

			// Retrieve the existing RolesVO from the repository
			rolesVO = rolesRepo.findById(rolesDTO.getId())
					.orElseThrow(() -> new ApplicationException("Role not found"));

			// Validate and update unique fields if changed
			if (!rolesVO.getRole().equalsIgnoreCase(rolesDTO.getRole())) {
				if (rolesRepo.existsByRole(rolesDTO.getRole())) {
					throw new ApplicationException("Role already exists");
				}
				rolesVO.setRole(rolesDTO.getRole().toUpperCase());
			}

			List<RolesResponsibilityVO> rolesResponsibilityVOs = rolesResponsibilityRepo.findByRolesVO(rolesVO);
			rolesResponsibilityRepo.deleteAll(rolesResponsibilityVOs);

			rolesVO.setUpdatedBy(rolesDTO.getCreatedBy());
			// Update the remaining fields from rolesDTO to rolesVO
			mapRolesDtoToRolesVo(rolesDTO, rolesVO);
			message = "Roles Updated successfully";
		}

		rolesRepo.save(rolesVO);
		Map<String, Object> response = new HashMap<>();
		response.put("rolesVO", rolesVO);
		response.put("message", message);
		return response;
	}

	// Helper method to map RolesDTO to RolesVO
	private void mapRolesDtoToRolesVo(RolesDTO rolesDTO, RolesVO rolesVO) {
		rolesVO.setRole(rolesDTO.getRole().toUpperCase());
		rolesVO.setActive(rolesDTO.isActive());
		if (rolesDTO.getRolesResponsibilityDTO() != null) {
			List<RolesResponsibilityVO> rolesResponsibilityVOList = new ArrayList<>();
			for (RolesResponsibilityDTO rolesResponsibilityDTO : rolesDTO.getRolesResponsibilityDTO()) {
				RolesResponsibilityVO rolesResponsibilityVO = new RolesResponsibilityVO();
				rolesResponsibilityVO.setResponsibility(rolesResponsibilityDTO.getResponsibility().toUpperCase());
				rolesResponsibilityVO.setResponsibilityId(rolesResponsibilityDTO.getResponsibilityId());
				rolesResponsibilityVO.setRolesVO(rolesVO);
				rolesResponsibilityVOList.add(rolesResponsibilityVO);
			}
			rolesVO.setRolesReposibilitiesVO(rolesResponsibilityVOList);
		}
	}

	@Override
	public List<RolesVO> getAllRoles() {

		return rolesRepo.findAll();
	}

	@Override
	public List<RolesVO> getAllActiveRoles() {

		return rolesRepo.findAllActiveRoles();
	}

	@Override
	public RolesVO getRolesById(Long id) throws ApplicationException {

		if (ObjectUtils.isEmpty(id)) {
			throw new ApplicationException("Invalid Roles Id");
		}

		RolesVO rolesVO = rolesRepo.findById(id)
				.orElseThrow(() -> new ApplicationException("Role not found for Id: " + id));

		return rolesVO;
	}

	@Override
	public ResponsibilityVO getResponsibilityById(Long id) throws ApplicationException {

		if (ObjectUtils.isEmpty(id)) {
			throw new ApplicationException("Invalid Responsibility Id");
		}

		ResponsibilityVO responsibilityVO = responsibilityRepo.findById(id)
				.orElseThrow(() -> new ApplicationException("Responsibility not found for Id: " + id));

		return responsibilityVO;
	}

	@Override
	public List<ResponsibilityVO> getAllResponsibility() {

		return responsibilityRepo.findAllResponsibility();
	}

	@Override
	public List<ResponsibilityVO> getAllActiveResponsibility() {
		// TODO Auto-generated method stub
		return responsibilityRepo.findAllActiveResponsibility();
	}

	@Override
	public List<UserVO> getAllUsers() {
		// TODO Auto-generated method stub
		return userRepo.findAll();
	}

	@Override
	public Map<String, Object> createUpdateResponsibilities(ResponsibilityDTO responsibilityDTO)
			throws ApplicationException {

		ResponsibilityVO responsibilityVO = new ResponsibilityVO();
		String message;
		// Check if the responsibilityDTO ID is empty (indicating a new entry)
		if (ObjectUtils.isEmpty(responsibilityDTO.getId())) {

			// Validate if responsibility already exists by responsibility name
			if (responsibilityRepo.existsByResponsibility(responsibilityDTO.getResponsibility())) {
				throw new ApplicationException("Responsibility Name already exists");
			}

			responsibilityVO.setCreatedBy(responsibilityDTO.getCreatedBy());
			responsibilityVO.setUpdatedBy(responsibilityDTO.getCreatedBy());
			// Set the values from responsibilityDTO to responsibilityVO
			mapResponsibilityDtoToResponsibilityVo(responsibilityDTO, responsibilityVO);
			message = "Responsibilites Created successfully";

		} else {

			// Retrieve the existing ResponsibilityVO from the repository
			responsibilityVO = responsibilityRepo.findById(responsibilityDTO.getId())
					.orElseThrow(() -> new ApplicationException("Responsibility not found"));

			// Validate and update unique fields if changed
			if (!responsibilityVO.getResponsibility().equalsIgnoreCase(responsibilityDTO.getResponsibility())) {
				if (responsibilityRepo.existsByResponsibility(responsibilityDTO.getResponsibility())) {
					throw new ApplicationException("Responsibility Name already exists");
				}
				responsibilityVO.setResponsibility(responsibilityDTO.getResponsibility());
			}

			List<ScreensVO> screensVOs = screenRepo.findByResponsibilityVO(responsibilityVO);
			screenRepo.deleteAll(screensVOs);

			responsibilityVO.setUpdatedBy(responsibilityDTO.getCreatedBy());
			// Update the remaining fields from responsibilityDTO to responsibilityVO
			mapResponsibilityDtoToResponsibilityVo(responsibilityDTO, responsibilityVO);
			message = "Responsibilites Updated successfully";
		}

		responsibilityRepo.save(responsibilityVO);
		Map<String, Object> response = new HashMap<>();
		response.put("responsibilityVO", responsibilityVO);
		response.put("message", message);
		return response;
	}

	// Helper method to map ResponsibilityDTO to ResponsibilityVO
	private void mapResponsibilityDtoToResponsibilityVo(ResponsibilityDTO responsibilityDTO,
			ResponsibilityVO responsibilityVO) {
		responsibilityVO.setResponsibility(responsibilityDTO.getResponsibility().toUpperCase());
		responsibilityVO.setActive(responsibilityDTO.isActive());
		if (responsibilityDTO.getScreensDTO() != null) {
			List<ScreensVO> screensVOList = new ArrayList<>();
			for (ScreensDTO screensDTO : responsibilityDTO.getScreensDTO()) {
				ScreensVO screensVO = new ScreensVO();
				screensVO.setScreenName(screensDTO.getScreenName().toUpperCase());
				screensVO.setResponsibilityVO(responsibilityVO);
				screensVOList.add(screensVO);
			}
			responsibilityVO.setScreensVO(screensVOList);
		}
	}

	@Override
	public Map<String, Object> createUpdateScreenNames(ScreenNamesDTO screenNamesDTO) throws ApplicationException {
		ScreenNamesVO screenNamesVO = new ScreenNamesVO();
		String message = null;

		if (ObjectUtils.isEmpty(screenNamesDTO.getId())) {

			// Validate if responsibility already exists by responsibility name
			if (screenNamesRepo.existsByScreenName(screenNamesDTO.getScreenName())) {
				throw new ApplicationException("Screen Name already exists");
			}
			if (screenNamesRepo.existsByScreenCode(screenNamesDTO.getScreenCode())) {
				throw new ApplicationException("Screen Code already exists");
			}

			screenNamesVO.setCreatedBy(screenNamesDTO.getCreatedBy());
			screenNamesVO.setUpdatedBy(screenNamesDTO.getCreatedBy());
			screenNamesVO.setActive(screenNamesDTO.isActive());
			screenNamesVO.setScreenCode(screenNamesDTO.getScreenCode());
			screenNamesVO.setScreenName(screenNamesDTO.getScreenName());
			// Set the values from screenNamesDTO to responsibilityVO
			message = "ScreenName Created successfully";

		} else {

			// Retrieve the existing ResponsibilityVO from the repository
			screenNamesVO = screenNamesRepo.findById(screenNamesDTO.getId())
					.orElseThrow(() -> new ApplicationException("Screen Name not found"));

			// Validate and update unique fields if changed
			if (!screenNamesVO.getScreenName().equalsIgnoreCase(screenNamesDTO.getScreenName())) {
				if (screenNamesRepo.existsByScreenName(screenNamesDTO.getScreenName())) {
					throw new ApplicationException("Screen Name already exists");
				}
				screenNamesVO.setScreenName(screenNamesDTO.getScreenName());
			}
			if (!screenNamesVO.getScreenCode().equalsIgnoreCase(screenNamesDTO.getScreenCode())) {
				if (screenNamesRepo.existsByScreenCode(screenNamesDTO.getScreenCode())) {
					throw new ApplicationException("Screen Code already exists");
				}
				screenNamesVO.setScreenCode(screenNamesDTO.getScreenCode());
			}
			screenNamesVO.setActive(screenNamesDTO.isActive());
			screenNamesVO.setUpdatedBy(screenNamesDTO.getCreatedBy());
			// Update the remaining fields from screenNamesDTO to responsibilityVO
			message = "ScreenName Updated successfully";
		}

		screenNamesRepo.save(screenNamesVO);
		Map<String, Object> response = new HashMap<>();
		response.put("screenNamesVO", screenNamesVO);
		response.put("message", message);
		return response;
	}

	@Override
	public List<ScreenNamesVO> getAllScreenNames() {

		return screenNamesRepo.findAll();
	}

	@Override
	public ScreenNamesVO getScreenNamesById(Long id) throws ApplicationException {

		if (ObjectUtils.isEmpty(id)) {
			throw new ApplicationException("Invalid Id");
		}

		ScreenNamesVO screenNamesVO = screenNamesRepo.findById(id)
				.orElseThrow(() -> new ApplicationException("Screen Name not found for Id: " + id));

		return screenNamesVO;
	}
}
