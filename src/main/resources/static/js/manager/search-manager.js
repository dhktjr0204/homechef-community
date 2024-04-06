function formatDate(dateString) {
    const options = {year: 'numeric', month: '2-digit', day: '2-digit'};
    return new Intl.DateTimeFormat('ko-KR', options).format(new Date(dateString));
}

// 페이지네이션 버튼 클릭 시 해당 페이지로 검색 조건을 유지하며 데이터를 불러오는 함수
function fetchBoards(page = 0, category = 'content', term = '') {
    const url = `/manager/boards/search?category=${encodeURIComponent(category)}&term=${encodeURIComponent(term)}&page=${page}&size=5`; // size를 5로 설정

    fetch(url, {
        method: 'GET',
        headers: {'Accept': 'application/json'}
    })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            updateBoardTableWithSearchResults(data.content);
            updateBoardPagination(data.totalPages, page, category, term); // 페이지네이션 업데이트 시 현재 검색 조건을 전달
        })
        .catch(error => console.error('Error:', error));
}

// 페이지네이션 버튼 클릭 시 해당 페이지로 검색 조건을 유지하며 데이터를 불러오는 함수
function fetchComments(page = 0, category = 'content', term = '') {
    const url = `/manager/comments/search?category=${encodeURIComponent(category)}&term=${encodeURIComponent(term)}&page=${page}&size=5`; // size를 5로 설정

    fetch(url, {
        method: 'GET',
        headers: {'Accept': 'application/json'}
    })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            updateCommentTableWithSearchResults(data.content);
            updateCommentPagination(data.totalPages, page, category, term); // 페이지네이션 업데이트 시 현재 검색 조건을 전달
        })
        .catch(error => console.error('Error:', error));
}

// 페이지네이션 버튼 클릭 시 해당 페이지로 검색 조건을 유지하며 데이터를 불러오는 함수
function fetchReports(page = 0, term = '') {
    const url = `/manager/reports/search?term=${encodeURIComponent(term)}&page=${page}&size=5`; // size를 5로 설정
    fetch(url, {
        method: 'GET',
        headers: {'Accept': 'application/json'}
    })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            updateReportTableWithSearchResults(data.content);
            updateReportPagination(data.totalPages, page, term); // 페이지네이션 업데이트 시 현재 검색 조건을 전달
        })
        .catch(error => console.error('Error:', error));
}

// 유저 정보 가져오기 및 업데이트 함수
function fetchUsers(page = 0, category = 'nickname', term = '') {
    const url = `/manager/users/search?category=${encodeURIComponent(category)}&term=${encodeURIComponent(term)}&page=${page}&size=5`;

    fetch(url, {
        method: 'GET',
        headers: {'Accept': 'application/json'}
         })
        .then(response => {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.json();
        })
        .then(data => {
            updateUsersTableWithSearchResults(data.content);
            updateUsersPagination(data.totalPages, page, category, term);
        })
        .catch(error => console.error('Error:', error));
}

// 페이지네이션 업데이트 함수에 검색 조건을 포함하여 수정
function updateBoardPagination(totalPages, currentPage, category, term) {
    const boardPagination = document.getElementById('board-pagination'); // 페이지네이션을 위한 요소의 ID를 boardPagination로 가정
    boardPagination.innerHTML = ''; // 기존 페이지네이션 초기화

    for (let i = 0; i < totalPages; i++) {
        const button = document.createElement('button');
        button.innerText = i + 1;
        // 검색 조건을 유지하며 해당 페이지를 불러오는 함수를 버튼의 클릭 이벤트에 연결
        button.onclick = function () {
            fetchBoards(i, category, term);
        };
        if (i === currentPage) {
            button.classList.add('active');
        }
        boardPagination.appendChild(button);
    }
}

// 페이지네이션 업데이트 함수에 검색 조건을 포함하여 수정
function updateCommentPagination(totalPages, currentPage, category, term) {
    const commentPagination = document.getElementById('comment-pagination');
    commentPagination.innerHTML = ''; // 기존 페이지네이션 초기화

    for (let i = 0; i < totalPages; i++) {
        const button = document.createElement('button');
        button.innerText = i + 1;
        // 검색 조건을 유지하며 해당 페이지를 불러오는 함수를 버튼의 클릭 이벤트에 연결
        button.onclick = function () {
            fetchComments(i, category, term);
        };
        if (i === currentPage) {
            button.classList.add('active');
        }
        commentPagination.appendChild(button);
    }
}

// 페이지네이션 업데이트 함수에 검색 조건을 포함하여 수정
function updateReportPagination(totalPages, currentPage, term) {
    const reportPagination = document.getElementById('report-pagination');
    reportPagination.innerHTML = ''; // 기존 페이지네이션 초기화

    for (let i = 0; i < totalPages; i++) {
        const button = document.createElement('button');
        button.innerText = i + 1;
        // 검색 조건을 유지하며 해당 페이지를 불러오는 함수를 버튼의 클릭 이벤트에 연결
        button.onclick = function () {
            fetchReports(i, term);
        };
        if (i === currentPage) {
            button.classList.add('active');
        }
        reportPagination.appendChild(button);
    }
}

// 유저 페이지네이션 업데이트 함수
function updateUsersPagination(totalPages, currentPage, category, term) {
    const usersPagination = document.getElementById('users-pagination');
    usersPagination.innerHTML = '';

    for (let i = 0; i < totalPages; i++) {
        const button = document.createElement('button');
        button.innerText = i + 1;
        button.addEventListener('click', function () {
            fetchUsers(i, category, term);
        });
        if (i === currentPage) {
            button.classList.add('active');
        }
        usersPagination.appendChild(button);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    // 현재 페이지 확인
    if (document.getElementById('board-management-page')) {
        setupBoardSearch();
        fetchBoards(0); // 초기 페이지 로드
    } else if (document.getElementById('comment-management-page')) {
        setupCommentSearch();
        fetchComments(0); // 초기 페이지 로드
    } else if (document.getElementById('report-management-page')) {
        setupReportSearch();
        fetchReports(0); // 초기 페이지 로드
    } else {document.getElementById('users-management-page')
        setupUserSearch();
        fetchUsers(0); // 초기 유저 목록 로드
    }
});
// 게시글 관리 페이지 검색 설정
function setupBoardSearch() {
    const searchBox = document.querySelector('.board-search-box');
    const searchSelect = document.querySelector('.board-search-select');
    searchBox.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            const searchTerm = searchBox.value;
            const searchCategory = searchSelect.value;
            fetchBoards(0, searchCategory, searchTerm); // 검색 실행
        }
    });
}

// 댓글 검색 설정 함수
function setupCommentSearch() {
    const searchBox = document.querySelector('.comment-search-box');
    const searchSelect = document.querySelector('.comment-search-select');
    searchBox.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            const searchTerm = searchBox.value;
            const searchCategory = searchSelect.value;
            fetchComments(0, searchCategory, searchTerm); // 검색 실행
        }
    });
}

// 댓글 검색 설정 함수
function setupReportSearch() {
    const searchBox = document.querySelector('.report-search-box');
    searchBox.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            const searchTerm = searchBox.value;
            fetchReports(0, searchTerm); // 검색 실행
        }
    });
}

// 유저 검색 설정 함수
function setupUserSearch() {
    const searchBox = document.querySelector('.users-search-box');
    const searchSelect = document.querySelector('.users-search-select');
    searchBox.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            const searchTerm = searchBox.value;
            const searchCategory = searchSelect.value;
            fetchUsers(0, searchCategory, searchTerm);
        }
    });
}

function updateBoardTableWithSearchResults(data) {
    const tableBody = document.querySelector('.content-table tbody');
    tableBody.innerHTML = ''; // 테이블 초기화
    data.forEach(item => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
                <td>${item.id}</td>
                <td>${item.content}</td>
                <td>${item.userNickname}</td>
                <td>${formatDate(item.createdAt)}</td>
                <td><button class="board-delete-btn" data-id="${item.id}">삭제</button></td>
            `;
        tableBody.appendChild(tr);
    });
}

// 댓글 테이블 업데이트 함수
function updateCommentTableWithSearchResults(comments) {
    const tableBody = document.querySelector('.content-table tbody');
    tableBody.innerHTML = ''; // 기존 내용 초기화

    comments.forEach(comment => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${comment.id}</td>
            <td>${comment.content}</td>
            <td>${comment.userName}</td>
            <td>${formatDate(comment.createdAt)}</td>
            <td><button class="comment-delete-btn" data-id="${comment.id}">삭제</button></td>
        `;
        tableBody.appendChild(tr);
    });
}

function updateReportTableWithSearchResults(data) {
    const tableBody = document.querySelector('.content-table tbody');
    tableBody.innerHTML = '';
    data.forEach(info => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><a href="/manager/userPosts/${info.userId}">${info.userNickname}</a></td>
            <td>${info.reportCount}</td>
            <td>${info.isBlacklisted ? '블랙 리스트' : '일반 유저'}</td>
            <td><button class="block-btn ${info.isBlacklisted ? 'remove-from-blacklist' : 'add-to-blacklist'}" data-id="${info.userId}">${info.isBlacklisted ? '해제' : '등록'}</button></td>
        `;
        console.log(info.isBlacklisted);
        tableBody.appendChild(tr);
    });
}

// 유저 테이블 업데이트 함수
function updateUsersTableWithSearchResults(users) {
    const tableBody = document.querySelector('.content-table tbody');
    tableBody.innerHTML = '';
    users.forEach((user, index) => {
        const tr = document.createElement('tr');

        // 사용자 역할에 따른 텍스트 설정
        let roleDescription = '';
        switch(user.role) {
            case 'USER':
                roleDescription = '미식 초보';
                break;
            case 'USER2':
                roleDescription = '요리 연습생';
                break;
            case 'USER3':
                roleDescription = '요리 전문가';
                break;
            case 'BLACK':
                roleDescription = '블랙 리스트';
                break;
            default:
                roleDescription = '관리자';
        }

        // 역할 설명과 변경 버튼 포함
        const roleCellHtml = `
            <div>${roleDescription}</div>
            <button class="update-btn" onclick="openRoleManagerWindow(${user.idx})">변경</button>
        `;

        tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${user.nickname}</td>
            <td>${user.email}</td>
            <td>${roleCellHtml}</td>
            <td>${user.reportCount}</td>
            <td>${user.isDeleted ? '탈퇴회원' : '일반회원'}</td>
        `;
        tableBody.appendChild(tr);
    });
}

function openRoleManagerWindow(userIdx) {
// 새 창의 위치를 화면 중앙으로 조정하기 위한 계산
    const width = 485;
    const height = 180;
    const left = (window.screen.width / 2) - (width / 2);
    const top = (window.screen.height / 2) - (height / 2);

// window.open을 사용하여 새 창 열기
    const roleManagerWindow = window.open('/manager/role-manager/' +userIdx, 'Role Manager', `width=${width},height=${height},top=${top},left=${left}`);
// 팝업 창이 닫히는 것을 감지하고 새로고침
    const checkClosed = setInterval(function() {
        if (roleManagerWindow.closed) {
            clearInterval(checkClosed);
            window.location.reload(); // 현재 페이지 새로고침
        }
    }, 1000);
}

document.body.addEventListener('click', function(event) {
    // 클릭된 요소가 게시글 삭제 버튼인지 확인
    if (event.target.classList.contains('board-delete-btn')) {
        const boardId = event.target.getAttribute('data-id');
        if (confirm('정말 삭제하시겠습니까?')) {
            fetch(`/manager/board/delete/${boardId}`, {
                method: 'DELETE',
            }).then(response => {
                if (response.ok) {
                    alert('게시글이 삭제되었습니다.');
                    window.location.reload(); // 페이지 새로고침
                } else {
                    alert('게시글 삭제에 실패했습니다.');
                }
            }).catch(error => console.error('Error:', error));
        }
    }
    // 클릭된 요소가 댓글 삭제 버튼인지 확인
    if (event.target.classList.contains('comment-delete-btn')) {
        const commentId = event.target.getAttribute('data-id');
        if (confirm('댓글을 삭제하시겠습니까?')) {
            fetch(`/manager/comment/delete/${commentId}`, {
                method: 'DELETE',
            }).then(response => {
                if (response.ok) {
                    alert('댓글이 삭제되었습니다.');
                    window.location.reload(); // 페이지 새로고침
                } else {
                    alert('댓글 삭제에 실패했습니다.');
                }
            }).catch(error => console.error('Error:', error));
        }
    }
});

document.body.addEventListener('click', function(event) {
    // 클릭된 요소가 블랙리스트 추가 버튼인지 확인
    if (event.target.classList.contains('add-to-blacklist')) {
        const userId = event.target.getAttribute('data-id');
        console.log("블랙리스트 추가 버튼 클릭:", userId);
        addToBlacklist(userId);
    }
    // 클릭된 요소가 블랙리스트 제거 버튼인지 확인
    else if (event.target.classList.contains('remove-from-blacklist')) {
        const userId = event.target.getAttribute('data-id');
        console.log("블랙리스트 제거 버튼 클릭:", userId);
        removeFromBlacklist(userId);
    }
});

function addToBlacklist(userId) {
    updateBlacklistStatus(`/manager/blacklist/add/${userId}`, '블랙리스트 추가 중 오류가 발생했습니다.');
}

function removeFromBlacklist(userId) {
    updateBlacklistStatus(`/manager/blacklist/remove/${userId}`, '블랙리스트 제거 중 오류가 발생했습니다.');
}

function updateBlacklistStatus(url, errorMessage) {
    fetch(url, { method: 'POST' })
        .then(response => {
            console.log('Response:', response);
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            console.log('Response data:', data);
            if (data.isBlacklisted !== undefined) {
                console.log('isBlacklisted:', data.isBlacklisted);
                document.getElementById('blacklist-status-' + data.userId).textContent =
                    data.isBlacklisted ? '블랙 리스트' : '일반 유저';
                alert('상태가 업데이트 되었습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert(errorMessage);
        });
}