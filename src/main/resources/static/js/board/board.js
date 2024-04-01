let boardSlideIndex;

//slide 사용하는 곳이 여러 곳이기 때문에 새로고침될 때마다 0으로 초기화 필요하다.
document.addEventListener('DOMContentLoaded', function () {
    boardSlideIndex = 0;
});

function prevSlide(button) {
    //현재 버튼이 속한 container 찾기
    const imageContainer = button.closest('.image-container');
    const imageList = imageContainer.querySelector('.image-list');
    const imageItem = imageContainer.querySelectorAll('.image-item');
    // 음수가 되지 않게 imageItem.length를 더해주었다.
    boardSlideIndex = (boardSlideIndex - 1 + imageItem.length) % imageItem.length;
    //왼쪽방향으로 이동
    const newPosition = -boardSlideIndex * imageItem[0].offsetWidth;
    imageList.style.transform = `translateX(${newPosition}px)`;
}

function nextSlide(button) {
    //현재 버튼이 속한 container 찾기
    const imageContainer = button.closest('.image-container');
    const imageList = imageContainer.querySelector('.image-list');
    const imageItem = imageContainer.querySelectorAll('.image-item');
    // 현재 위치에서 1칸 이동, 만약 뒤에 더 이미지가 없다면 처음으로 이동
    boardSlideIndex = (boardSlideIndex + 1) % imageItem.length;
    //imageWidth만큼 왼쪽 방향으로 이동
    const newPosition = -boardSlideIndex * imageItem[0].offsetWidth;
    imageList.style.transform = `translateX(${newPosition}px)`;
}

function clickUpdateBoard(button) {
    // 수정 버튼이 속한 게시물의 부모 요소인 article을 찾음.
    const boardArticle = button.closest('.board-container');
    const boardId = boardArticle.querySelector('.board-id').value;
    location.href = "/board/edit/" + boardId;
}

function clickDeleteBoard(button) {
    const confirmation = confirm("정말 삭제하시겠습니까?");
    const boardArticle = button.closest('.board-container');

    const boardId = boardArticle.querySelector('.board-id').value;
    const userId= boardArticle.querySelector('.username').getAttribute("value");
    if (confirmation) {
        fetch("/board/delete/" + boardId + "?userId="+userId, {
            method: "DELETE",
        }).then(response => {
            if (!response.ok) {
                return response.text().then(msg => {
                    if (response.status === 401) {
                        alert(msg);
                    }
                });
            } else {
                return response.text();
            }
        }).then(url => {
            if (url) {
                window.location.replace(url);
            }else{
                window.location.replace("/");
            }
        }).catch(error => {
            console.error(error);
        });
    }
}

function clickGoToBoard(button) {
    const boardArticle = button.closest('.board-container');

    const boardId = boardArticle.querySelector('.board-id').value;
    location.href = "/board/" + boardId;
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
        return;
    }

    const commentData = {
        userId: 1, // 실제 애플리케이션에서는 인증된 사용자의 ID 사용
        content: commentContent,
        parentCommentId: activeReplyBox || null, // 답글인 경우 부모 댓글의 ID를 함께 전송
    };

    // 서버에 댓글 또는 답글 전송
    fetch(`/board/${boardId}/comments`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(commentData)
    })
        .then(response => response.json())
        .then(data => {
            console.log('Comment or reply submitted:', data);
            fetchComments(0); // 댓글 목록 새로고침
        })
        .catch(error => console.error('Error:', error))
        .finally(() => {
            commentInput.value = ''; // 입력창 초기화
            commentInput.placeholder = '댓글을 입력하세요...'; // 플레이스홀더 초기화
            // activeReplyBox = null; // 답글 대상 초기화
        });
});

// 답글을 전송하는 함수
function submitReply(content, parentCommentId) {
    const boardId = document.getElementById('boardId').value; // 현재 게시판의 ID
    // 답글 데이터 구조를 서버가 요구하는 형식에 맞춰서 작성합니다.
    const replyData = {
        boardId: boardId,
        userId: 1,
        content: content,
        parentCommentId: parentCommentId
    };

    // 서버에 답글을 전송하는 POST 요청을 보냅니다.
    fetch(`/board/${boardId}/comments`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(replyData)
    })
        .then(response => response.json())
        .then(data => {
            console.log('Reply submitted:', data);
            // 답글을 페이지에 추가하는 함수를 호출하거나 페이지를 새로고침합니다.
            addReplyToPage(data, parentCommentId); // 'data.reply'는 수정된 답글 데이터를 포함해야 합니다.
        })
        .catch(error => console.error('Error:', error));
}

// 이 함수는 대댓글을 페이지에 추가할 때 호출됩니다.
function addReplyToPage(reply, parentCommentId) {
    // 부모 댓글 찾기
    const parentComment = document.querySelector(`.comment[data-comment-id="${parentCommentId}"]`);
    // 부모 댓글 바로 아래에 대댓글 컨테이너가 있는지 확인하고, 없으면 생성.
    let repliesContainer = parentComment.querySelector('.replies-container');
    if (!repliesContainer) {
        repliesContainer = createRepliesContainer(parentComment);
        parentComment.insertAdjacentElement('afterend', repliesContainer);
    }

    // 대댓글 HTML 마크업 생성
    const replyHtml = `
        <div class="comment reply" data-comment-id="${reply.id}">
            <div class="reply-icon">ㄴ</div>
            <div class="comment-profile"><img src="/img/main/profile.jpg" alt="Profile Image"></div>
            <div class="comment-username">${reply.userName}</div>
            <div class="comment-content">${reply.content}</div>
            <div class="comment-metadata">
                <span class="comment-date">${new Date(reply.createdAt).toLocaleDateString('ko-KR')}</span>
                <button class="more-options">⋮</button>
                    <div class="options-menu" style="display:none;">
                        <ul>
                            <li><button class="report-button">신고</button></li>
                            <li><button class="edit-comment-button">수정</button></li>
                            <li><button class="delete-comment-button">삭제</button></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    `;

    // 대댓글을 repliesContainer에 추가합니다.
    // 여기서 repliesContainer는 parentComment 아래의 대댓글들을 담는 컨테이너입니다.
    // 이 컨테이너가 이미 있다면 그 안에, 없다면 새로 생성하여 추가합니다.
    parentComment.insertAdjacentHTML('afterend', replyHtml);
}

// 대댓글이 들어갈 컨테이너를 생성하는 함수입니다.
function createRepliesContainer(parentComment) {
    const container = document.createElement('div');
    container.className = 'replies-container';
    parentComment.appendChild(container);
    return container;
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

// 댓글을 전송하는 함수
function submitComment(content) {
    const boardId = document.getElementById('boardId').value; // 현재 게시판의 ID
    // 댓글 데이터 구조를 서버가 요구하는 형식에 맞춰서 작성합니다.
    const commentData = {
        boardId: boardId,
        userId: 1,
        content: content
    };

    // 서버에 댓글을 전송하는 POST 요청을 보냅니다.
    fetch(`/board/${boardId}/comments`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(commentData)
    })
        .then(response => response.json())
        .then(data => {
            console.log('Comment submitted:', data);
            // 댓글을 페이지에 추가하는 함수를 호출하거나 페이지를 새로고침합니다.
            // addCommentToPage(data);
        })
        .catch(error => console.error('Error:', error));
}

// 답글 입력창을 원래 위치로 돌려놓고 상태를 초기화하는 함수
function resetReplyBox() {
    const originalLocation = document.querySelector('.comments-container'); // 원래 댓글 입력창이 위치해야 할 곳
    originalLocation.appendChild(document.getElementById('commentForm'));

    // 입력창의 플레이스홀더를 원래대로 돌립니다.
    document.getElementById('commentInput').placeholder = '댓글을 입력하세요...';

    // 전역 변수 activeReplyBox를 초기화합니다.
    activeReplyBox = null;
}

function addCommentToPage(comment) {
    const commentsContainer = document.querySelector('.comments-display');
    const profileImageSrc = comment.profileImage ? `/img/main/${comment.profileImage}` : '/img/main/profile.jpg';

    // 댓글 날짜 포맷 변경 (예: '2024.03.22')
    const commentDate = new Date(comment.createdAt).toLocaleDateString('ko-KR', {
        year: 'numeric', month: '2-digit', day: '2-digit'
    });

    // 댓글 HTML 마크업에 신고, 수정, 삭제 버튼과 답글 버튼, 생성 날짜 추가
    const newCommentHtml = `
        <div class="comment" data-comment-id="${comment.id}">
            <div class="main-comment">
            <div class="comment-profile"><img src="${profileImageSrc}" alt="Profile Image"></div>
            <div class="comment-username">${comment.userName}</div>
            <div class="comment-content">${comment.content}</div>
            <button class="more-options">⋮</button>
                    <div class="options-menu" style="display:none;">
                        <ul>
                            <li><button class="report-button">신고</button></li>
                            <li><button class="edit-comment-button">수정</button></li>
                            <li><button class="delete-comment-button">삭제</button></li>
                        </ul>
                    </div>
            </div>
            <div class="comment-metadata">
                <span class="comment-date">${commentDate}</span>
                <div class="comment-actions">
                    <button class="reply-button">답글 달기</button>
                </div>
            </div>
        </div>
    `;
// 새 댓글을 commentsContainer의 시작 부분에 추가합니다.
    commentsContainer.insertAdjacentHTML('afterbegin', newCommentHtml);

    // 새로 추가된 댓글의 옵션 버튼 이벤트 리스너를 바인딩합니다.
    bindCommentOptions();
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
            // 답글 입력창 위치 조정 로직 개선
            if (activeReplyBox !== commentId) {
                // 다른 댓글에 답글을 달고 있지 않은 경우, 또는 다른 댓글에 답글을 달고 있는 경우 위치 이동
                const replyBox = document.getElementById('commentForm');
                this.closest('.comment').after(replyBox); // 댓글 바로 아래가 아닌 댓글 바로 뒤에 위치하도록 수정
                activeReplyBox = commentId;
                document.getElementById('commentInput').placeholder = '답글을 입력하세요...';
                document.getElementById('commentInput').focus();
            } else {
                // 같은 댓글에 이미 답글 입력창이 있는 경우
                resetReplyBox(); // 답글 입력창을 원래 위치로 이동
            }
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
    bindCommentOptions(); // 초기화 시 댓글 옵션 버튼에 이벤트 리스너를 설정
    const commentsDisplayContainer = document.querySelector('.comments-display');
    // 이벤트 위임을 사용하여 댓글 삭제 버튼 클릭 이벤트 처리
    commentsDisplayContainer.addEventListener('click', function (event) {
        if (event.target.classList.contains('delete-comment-button')) {
            const isConfirmed = confirm('댓글을 삭제하시겠습니까?');
            if (isConfirmed) {
                // 삭제 로직 실행
                const commentId = event.target.closest('.comment').dataset.commentId;
                deleteComment(commentId);
            }
        }
    });

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

// 서버로부터 댓글 데이터를 가져오는 함수
function fetchComments(page) {
    const boardId = document.getElementById('boardId').value;
    fetch(`/board/${boardId}/comments?page=${page}&limit=${commentsPerPage}`)
        .then(response => response.json())
        .then(data => { // 'data' 대신 'comments'로 받아옴
            const commentsContainer = document.querySelector('.comments-display');
            commentsContainer.innerHTML = ''; // 댓글 목록 초기화

            data.content.forEach(comment => {
                // 여기에서 comment가 대댓글인지 확인하여 적절한 위치에 추가합니다.
                // 예를 들면, comment.parentCommentId를 체크하여 대댓글인 경우
                // 해당하는 부모 댓글 아래에 추가하는 로직을 구현할 수 있습니다.
                if (comment.parentCommentId) {
                    addReplyToPage(comment, comment.parentCommentId);
                } else {
                    addCommentToPage(comment);
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
    console.log(`Creating button: ${text}, Active: ${isActive}`); // 버튼 생성 로깅
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
            console.log('Comment deleted');
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
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({content: updatedContent})
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