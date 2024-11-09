package com.invoice.approval.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.entity.UserActionVO;
import com.invoice.approval.entity.UserVO;
import com.invoice.approval.repo.TokenRepo;
import com.invoice.approval.repo.UserActionRepo;
import com.invoice.approval.repo.UserRepo;
import com.invoice.approval.security.TokenProvider;

@Service
public class UserServiceImpl implements UserService {
	public static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

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

	public void createUserAction(String userName, Long usersId, String actionType) {
		try {
			UserActionVO userActionVO = new UserActionVO();
			userActionVO.setUserName(userName);
			userActionVO.setUserId(usersId);
			userActionVO.setActionType(actionType);
			userActionRepo.save(userActionVO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	

	@Override
	public void removeUser(String userName) {
		String methodName = "removeUser()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (StringUtils.isNotEmpty(userName)) {
			UserVO userVO = userRepo.findByUserName(userName);
			if (ObjectUtils.isEmpty(userVO)) {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
			}
//			userVO.setActive(false);
			userRepo.save(userVO);
			createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_REMOVE_ACCOUNT);
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_NAME);
		}
	}



	@Override
	public void createUserLoginAction(String userName, Long userId, String actionType, HttpServletRequest request) {
		try {
			UserActionVO userActionVO = new UserActionVO();
			userActionVO.setUserName(userName);
			userActionVO.setUserId(userId);
			userActionVO.setActionType(actionType);
			String clientIp = request.getHeader("X-Forwarded-For");
			if (clientIp == null || clientIp.isEmpty()) {
				clientIp = request.getRemoteAddr();
			}
			// Validate and ensure it's an IPv4 address
			if (isValidIPv4(clientIp)) {
				userActionVO.setLoginIp(clientIp);
			} 
			userActionRepo.save(userActionVO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
	}



	private boolean isValidIPv4(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress.getHostAddress().equals(ip) && ip.indexOf(':') == -1; // Check for no colons
        } catch (UnknownHostException e) {
            return false;
        }
    }

	

}