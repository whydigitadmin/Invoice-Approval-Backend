package com.invoice.approval.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.invoice.approval.entity.UserVO;

@Service
public interface UserService {

	public void createUserAction(String userName, Long userId, String actionType);
	
	public void createUserLoginAction(String userName, Long userId, String actionType, HttpServletRequest request);

	public void removeUser(String userName);
	
	public List<UserVO> getBranchCodeByUser(String userName);
}
