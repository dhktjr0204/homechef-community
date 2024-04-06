package com.cooklog.service;

import java.util.List;

import com.cooklog.dto.ReportedContentDTO;

public interface ReportService {
	void reportComment(Long commentId); // 댓글 신고 메서드 추가
	void reportBoard(Long boardId); // 게시글 신고 메서드 추가
	List<ReportedContentDTO> findReportedContents();
	List<ReportedContentDTO> searchReported(String term);
}