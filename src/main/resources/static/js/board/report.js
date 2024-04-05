function bindMainReportEvent(button) {
    const boardArticle = button.closest('.board-container');

    const boardId = boardArticle.querySelector('.board-id').value;

    const isConfirmed = confirm('이 게시글을 신고하시겠습니까?');

    if (isConfirmed) {
        fetch(`/board/reportBoard/${boardId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // 필요한 경우 CSRF 토큰 등의 보안 헤더를 추가하세요.
            },
            // body를 사용하지 않는 POST 요청이라면 body를 생략할 수 있습니다.
        }).then(response => {
            if (response.ok) {
                alert('게시글이 신고되었습니다.');
            } else {
                throw new Error('Failed to report board');
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('신고 처리 중 오류가 발생했습니다.');
        });
    }
}
