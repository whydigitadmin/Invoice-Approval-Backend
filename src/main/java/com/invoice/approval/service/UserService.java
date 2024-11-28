package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.invoice.approval.entity.UserVO;

@Service
public interface UserService {

	public void createUserAction(String userName, Long userId, String actionType);
	
	public void createUserLoginAction(String userName, Long userId, String actionType, HttpServletRequest request);

	public void removeUser(String userName);
	
	List<Map<String, Object>> getBranchCodeByUser(String userName);
	
}
