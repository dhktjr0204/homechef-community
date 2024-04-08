package com.cooklog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cooklog.dto.ReportedContentDTO;

public interface ReportService {
	void reportComment(Long commentId); // 댓글 신고 메서드 추가
	void reportBoard(Long boardId); // 게시글 신고 메서드 추가
	Page<ReportedContentDTO> searchReported(String term, Pageable pageable);
	Page<ReportedContentDTO> findReportedContentsPageable(Pageable pageable);
}