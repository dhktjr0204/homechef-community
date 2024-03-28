package com.cooklog.service;

public interface BlacklistService {
	void addToBlacklist(Long userId);
	void removeFromBlacklist(Long userId);
}

