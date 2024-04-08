function bindMainReportEvent(button) {
    const boardArticle = button.closest('.board-container');

    const boardId = boardArticle.querySelector('.board-id').value;
    const isConfirmed = confirm('이 게시글을 신고하시겠습니까?');
    if (isConfirmed) {
        fetch(`/board/reportBoard/${boardId}`, {
            method: 'POST', headers: {
                'Content-Type': 'application/json',
            }, // body를 사용하지 않는 POST 요청이라면 body를 생략할 수 있습니다.
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

//댓글 추가 API
let isEditing = false; // 전역 상태로 수정 모드 관리
let activeReplyBox = null; // 전역 변수로 현재 답글을 다는 대상의 댓글 ID를 관리.
document.getElementById('commentForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const boardId = document.getElementById('boardId').value; // 게시글 ID
    const commentInput = document.getElementById('commentInput');
    const commentContent = commentInput.value.trim();

    // 입력 내용이 비어있는 경우 전송하지 않음
    if (!commentContent) {
        alert("댓글 내용을 입력해주세요.");
        return;
    }

    const commentData = {
        userId: currentUserId, // 실제 애플리케이션에서는 인증된 사용자의 ID 사용
        content: commentContent, parentCommentId: activeReplyBox || null, // 답글인 경우 부모 댓글의 ID를 함께 전송
    };

    // 서버에 댓글 또는 답글 전송
    fetch(`/board/${boardId}/comments`, {
        method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(commentData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 처리 중 오류 발생');
            }
            return response.json();
        })
        .then(data => {
            resetReplyBox(); // 답글 입력창을 초기 상태로 복원하고 관련 상태를 초기화
            fetchComments(0); // 댓글 목록 새로고침
        })
        .catch(error => {
            console.error('Error:', error);
            alert('댓글 추가 중 오류가 발생했습니다.');
        });
});

function addCommentOrReplyToPage(commentData, parentCommentId = null) {
    const userPageLink = `<a href="/myPage/main/${commentData.userId}" class="comment-username">${commentData.userName}</a>`;

    let optionsHTML; // 수정, 삭제 또는 신고 버튼을 포함할 HTML
    if (Number(currentUserId) === commentData.userId) {
        // 현재 로그인한 사용자가 댓글을 작성한 사용자와 같은 경우
        optionsHTML = `
            <ul>
                <li><button class="edit-comment-button">수정</button></li>
                <li><button class="delete-comment-button">삭제</button></li>
            </ul>`;
    } else {
        // 다른 사용자의 댓글인 경우
        optionsHTML = `
            <ul>
                <li><button class="report-button">신고</button></li>
            </ul>`;
    }

    // parentCommentId가 있는 경우와 없는 경우로 나누어 처리
    if (parentCommentId) {
        // 답글인 경우의 처리...
        const parentCommentElement = document.querySelector(`.comment[data-comment-id="${parentCommentId}"]`);
        // 답글 처리 로직은 이전과 동일하며, optionsHTML을 사용하여 올바른 버튼을 표시합니다.
    } else {
        // 댓글인 경우의 HTML
        const commentHTML = `
            <div class="comment" data-comment-id="${commentData.id}">
                <div class="main-comment">
                    <div class="comment-profile"><img src="${commentData.profileImage ? `${commentData.profileImage}` : '/img/main/profile1.jpg'}" alt="Profile Image"></div>
                    ${userPageLink}
                    <div class="comment-content">${commentData.content}</div>
                    <button class="more-options">⋮</button>
                    <div class="options-menu" style="display:none;">
                        ${optionsHTML}
                    </div>
                </div>
                <div class="replies-container"></div>
                <div class="comment-metadata">
                    <span class="comment-date">${new Date(commentData.createdAt).toLocaleDateString('ko-KR', {
            year: 'numeric', month: '2-digit', day: '2-digit'
        })}</span>
                </div>
            </div>`;
        const commentsContainer = document.querySelector('.comments-display');
        commentsContainer.insertAdjacentHTML('afterbegin', commentHTML);
    }

    bindCommentOptions(); // 이 함수는 새로운 댓글 요소에 이벤트 리스너를 바인딩합니다.
}


// 대댓글이 들어갈 컨테이너를 생성하는 함수입니다.
function createRepliesContainer(parentComment) {
    const container = document.createElement('div');
    container.className = 'replies-container';
    parentComment.appendChild(container);
    return container;

    bindCommentOptions();

}

// '답글 달기' 버튼 이벤트 리스너
document.querySelectorAll('.reply-button').forEach(button => {
    button.addEventListener('click', function () {
        const parentCommentId = this.closest('.comment').dataset.commentId;

        // 현재 답글 대상의 댓글 ID를 설정합니다.
        activeReplyBox = parentCommentId;

        // '답글을 입력하세요...' 플레이스홀더를 설정하고 포커스를 맞춥니다.
        const commentInput = document.getElementById('commentInput');
        commentInput.placeholder = '답글을 입력하세요...';
        commentInput.focus();

        // 기존에 다른 댓글에 답글을 달고 있는 경우 그 상태를 초기화합니다.
        // 여기서는 위치를 변경하지 않고, 포커스와 플레이스홀더만 조정합니다.
        if (activeReplyBox && activeReplyBox !== parentCommentId) {
            resetReplyBox();
        }
    });
});

function resetReplyBox() {
    // 입력창 초기화 및 placeholder 설정
    const commentInput = document.getElementById('commentInput');
    commentInput.value = ''; // 입력창 내용 초기화
    commentInput.placeholder = '댓글을 입력하세요...'; // Placeholder 초기화

    // 전역 변수를 이용한 상태 관리 초기화
    activeReplyBox = null; // 답글 대상 초기화
    isEditing = false; // 수정 모드 해제
}

function bindCommentOptions() {
    // 댓글의 더 보기 옵션 메뉴 토글
    document.querySelectorAll('.more-options').forEach(button => {
        button.removeEventListener('click', toggleOptionsMenu); // 기존 이벤트 리스너 제거
        button.addEventListener('click', toggleOptionsMenu); // 새 이벤트 리스너 추가
    });


    // 댓글 수정 버튼 이벤트 리스너
    document.querySelectorAll('.edit-comment-button').forEach(button => {
        button.addEventListener('click', function () {
            if (isEditing) return; // 이미 수정 모드인 경우 함수 실행 중지
            isEditing = true; // 수정 모드 활성화

            const commentElement = this.closest('.comment');
            const commentId = commentElement.dataset.commentId;
            const originalContent = commentElement.querySelector('.comment-content').textContent;
            // 옵션 메뉴를 숨깁니다.
            const optionsMenu = commentElement.querySelector('.options-menu');
            optionsMenu.style.display = 'none';

            // '댓글 달기' 입력란으로 현재 내용을 이동합니다.
            const commentForm = document.getElementById('commentForm');
            const commentInput = document.getElementById('commentInput');
            commentInput.value = originalContent;
            commentInput.focus();

            // '수정'과 '취소' 버튼을 생성합니다.
            const saveButton = document.createElement('button');
            saveButton.textContent = '수정';
            saveButton.className = 'save-edit-button';
            saveButton.type = 'button';

            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'cancel-edit-button';
            cancelButton.type = 'button';

            // 버튼에 이벤트 리스너를 설정합니다.
            saveButton.addEventListener('click', function () {
                updateComment(commentId, commentInput.value);
                isEditing = false; // 수정 모드 비활성화
            });

            cancelButton.addEventListener('click', function () {
                resetEditState(commentInput, saveButton, cancelButton, originalContent);
                isEditing = false; // 수정 모드 비활성화
            });

            // '수정'과 '취소' 버튼을 댓글 폼에 추가합니다.
            commentForm.appendChild(saveButton);
            commentForm.appendChild(cancelButton);

            // 수정 중 상태를 관리하는 플래그를 설정합니다.
            commentForm.dataset.isEditing = true;
            commentForm.dataset.editingCommentId = commentId;

            // 입력란에 엔터키 이벤트를 설정합니다.
            commentInput.addEventListener('keypress', function (e) {
                if (e.key === 'Enter' && commentForm.dataset.isEditing === 'true') {
                    e.preventDefault(); // 기본 이벤트를 취소합니다.
                    saveButton.click(); // '수정' 버튼의 클릭 이벤트를 강제로 실행합니다.
                }
            }, {once: true}); // 이벤트는 한 번만 실행되고 제거됩니다.
        });
    });
    // '답글 달기' 버튼 이벤트 리스너
    document.querySelectorAll('.reply-button').forEach(button => {
        button.addEventListener('click', function () {
            const commentId = this.closest('.comment').dataset.commentId;
            activeReplyBox = commentId;
            commentInput.placeholder = '답글을 입력하세요...'; // 플레이스홀더 변경
            commentInput.focus(); // 입력창에 포커스를 맞춥니다.
        });
    });
    // 신고 버튼에 대한 이벤트 리스너 추가
    document.querySelectorAll('.report-button').forEach(button => {
        button.addEventListener('click', function () {
            const optionsMenu = this.closest('.options-menu');
            if (optionsMenu) {
                optionsMenu.style.display = 'none';
            }
            const commentElement = this.closest('.comment');
            const commentId = commentElement.dataset.commentId;
            reportComment(commentId);
        });
    });
}

// 옵션 메뉴를 토글하는 함수
function toggleOptionsMenu(event) {
    const optionsMenu = this.nextElementSibling;
    optionsMenu.style.display = optionsMenu.style.display === 'block' ? 'none' : 'block';
    // 다른 옵션 메뉴가 열려있을 경우 닫습니다.
    document.querySelectorAll('.options-menu').forEach(menu => {
        if (menu !== optionsMenu) {
            menu.style.display = 'none';
        }
    });
}

let currentPage = 0;
const commentsPerPage = 5; // 한 페이지당 보여줄 댓글의 수

// 페이지 로드 시 실행되는 초기화 함수에 bindCommentOptions 호출을 추가
document.addEventListener('DOMContentLoaded', function () {
    currentUserId = document.getElementById('currentUserId').value;

    bindCommentOptions(); // 초기화 시 댓글 옵션 버튼에 이벤트 리스너를 설정
    bindDeleteEvent(); // 댓글 삭제 이벤트 리스너 설정
    bindReportEvent();
    resetReplyBox();
    bindBoardReportEvent();
    //댓글 수정 상태라면 엔터키 입력시 수정한 댓글로 변경 가능.
    document.getElementById('commentInput').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault(); // 기본 이벤트를 취소합니다.
            const commentForm = document.getElementById('commentForm');
            if (commentForm.dataset.isEditing === 'true') {
                document.querySelector('.save-edit-button').click();
            } else {
                commentForm.dispatchEvent(new Event('submit'));
            }
        }
    });

    // 첫 페이지의 댓글을 로드.
    fetchComments(currentPage);
});

// 댓글 삭제 기능을 위한 이벤트 리스너를 설정하는 함수
function bindDeleteEvent() {
    const commentsDisplayContainer = document.querySelector('.comments-display');
    commentsDisplayContainer.addEventListener('click', function (event) {
        if (event.target.classList.contains('delete-comment-button')) {
            // 옵션 메뉴를 찾기 위해 event.target을 사용합니다.
            const optionsMenu = event.target.closest('.comment').querySelector('.options-menu');
            if (optionsMenu) {
                optionsMenu.style.display = 'none';
            }

            const isConfirmed = confirm('댓글을 삭제하시겠습니까?');
            if (isConfirmed) {
                const commentElement = event.target.closest('.comment');
                const commentId = commentElement.dataset.commentId;
                fetch(`/board/comments/${commentId}`, {method: 'DELETE'})
                    .then(response => {
                        if (!response.ok) throw new Error('Failed to delete comment');
                        commentElement.remove();
                    })
                    .catch(error => console.error('Error:', error));
            }
        }
    });
}

function bindReportEvent() {
    const commentsDisplayContainer = document.querySelector('.comments-display');
    commentsDisplayContainer.addEventListener('click', function(event) {
        // 이벤트 타겟이 신고 버튼인지 확인
        if (event.target.classList.contains('report-button')) {
            const commentElement = event.target.closest('.comment');
            const commentId = commentElement.dataset.commentId;
            const isConfirmed = confirm('이 댓글을 신고하시겠습니까?');
            if (isConfirmed) {
                // 신고 요청을 보내는 코드
                fetch(`/manager/comment/${commentId}`, {
                    method: 'POST',
                    // 필요한 경우 추가적인 헤더나 요청 본문을 설정합니다.
                }).then(response => {
                    if (response.ok) {
                        alert('댓글이 신고되었습니다.');
                    } else {
                        throw new Error('Failed to report comment');
                    }
                }).catch(error => {
                    console.error('Error:', error);
                    alert('신고 처리 중 오류가 발생했습니다.');
                });
            }
        }
    });
}
function bindBoardReportEvent() {
    document.querySelectorAll('.report-button').forEach(button => {
        button.addEventListener('click', function() {
            const boardId = this.dataset.boardId; // dataset을 사용하여 boardId를 가져옵니다.
            const isConfirmed = confirm('이 게시글을 신고하시겠습니까?');
            if (isConfirmed) {
                fetch(`/board/reportBoard/${boardId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
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
        });
    });
}

// 서버로부터 댓글 데이터를 가져오는 함수
function fetchComments(page) {
    const boardId = document.getElementById('boardId').value;
    fetch(`/board/${boardId}/comments?page=${page}&limit=${commentsPerPage}`)
        .then(response => response.json())
        .then(data => { // 'data'를 받아옴
            const commentsContainer = document.querySelector('.comments-display');
            commentsContainer.innerHTML = ''; // 댓글 목록 초기화

            data.content.forEach(comment => {
                if (comment.parentCommentId) {
                    addCommentOrReplyToPage(comment, comment.parentCommentId);
                } else {
                    addCommentOrReplyToPage(comment);
                }
            });

            // 페이지네이션 설정 부분에서도 totalPages와 currentPage 정보를 정확하게 사용하도록 수정합니다.
            setupPagination(data.totalPages, data.number); // data.number는 현재 페이지 번호입니다.
        })
        .catch(error => console.error('Error:', error));
}

// 페이지네이션 설정 함수
function setupPagination(totalPages, currentPage, windowSize = 1) {
    const paginationContainer = document.getElementById('pagination');
    paginationContainer.innerHTML = ''; // 기존의 페이지네이션 버튼들을 초기화

    // '이전' 버튼 생성 및 추가
    if (currentPage > 0) {
        paginationContainer.appendChild(createPaginationButton('<', () => fetchComments(currentPage - 1)));
    }

    // 페이지 번호 버튼 생성 및 추가 (윈도우 범위 계산)
    const startPage = Math.max(currentPage - windowSize, 0);
    const endPage = Math.min(currentPage + windowSize + 1, totalPages);

    for (let i = startPage; i < endPage; i++) {
        paginationContainer.appendChild(createPaginationButton(i + 1, () => fetchComments(i), currentPage === i));
    }

    // '다음' 버튼 생성 및 추가
    if (currentPage < totalPages - 1) {
        paginationContainer.appendChild(createPaginationButton('>', () => fetchComments(currentPage + 1)));
    }
}

// 페이지네이션 버튼 생성 함수
function createPaginationButton(text, clickHandler, isActive = false) {
    const button = document.createElement('button');
    button.textContent = text;
    button.disabled = isActive; // 현재 페이지에 해당하는 버튼은 비활성화
    button.addEventListener('click', clickHandler);
    return button;
}

// 댓글 삭제 로직
function deleteComment(commentId) {
    fetch(`/board/comments/${commentId}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete comment');
            }
            // 페이지에서 댓글 요소를 제거
            removeCommentElement(commentId);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// 댓글 요소를 DOM에서 제거하는 함수
function removeCommentElement(commentId) {
    const commentElement = document.querySelector(`.comment[data-comment-id="${commentId}"]`);
    if (commentElement) {
        commentElement.remove();
    }
}


//수정 요소 시작
// 수정된 내용을 서버로 전송하고 페이지에서 댓글을 업데이트하는 함수
function updateComment(commentId, updatedContent) {
    // 서버로 수정된 댓글 내용을 전송하는 코드
    fetch(`/board/comments/${commentId}`, {
        method: 'PUT', headers: {
            'Content-Type': 'application/json'
        }, body: JSON.stringify({content: updatedContent})
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok.');
            }
            return response.json(); // Parse JSON response
        })
        .then(updatedComment => {
            // 페이지에서 댓글 내용을 업데이트하는 로직
            updateCommentElement(commentId, updatedComment.content);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// '수정'과 '취소' 버튼을 제거하고 입력란을 리셋하는 함수
function resetEditState(commentInput, saveButton, cancelButton, originalContent) {
    // 입력란을 원래 상태로 리셋합니다.
    commentInput.value = originalContent;
    commentInput.blur();

    // '수정'과 '취소' 버튼을 제거합니다.
    if (saveButton) saveButton.remove();
    if (cancelButton) cancelButton.remove();

    // 수정 중 상태를 관리하는 플래그를 리셋합니다.
    const commentForm = document.getElementById('commentForm');
    delete commentForm.dataset.isEditing;
    delete commentForm.dataset.editingCommentId;
}

// 페이지에서 댓글 내용을 업데이트하는 함수
function updateCommentElement(commentId, updatedContent) {
    const commentElement = document.querySelector(`.comment[data-comment-id="${commentId}"] .comment-content`);
    if (commentElement) {
        commentElement.textContent = updatedContent;
    }
    // 수정 완료 후 수정/취소 버튼을 제거하고, 댓글 입력란을 초기화합니다.
    resetEditState(document.getElementById('commentInput'), document.querySelector('.save-edit-button'), document.querySelector('.cancel-edit-button'), '');
}
