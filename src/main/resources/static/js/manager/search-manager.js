function formatDate(dateString) {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
    return new Intl.DateTimeFormat('ko-KR', options).format(new Date(dateString));
}

// 페이지네이션 버튼 클릭 시 해당 페이지로 검색 조건을 유지하며 데이터를 불러오는 함수
function fetchBoards(page = 0, category = 'content', term = '') {
    const url = `/manager/boards/search?category=${encodeURIComponent(category)}&term=${encodeURIComponent(term)}&page=${page}&size=5`; // size를 5로 설정

    fetch(url, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
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
        headers: { 'Accept': 'application/json' }
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

// 페이지네이션 업데이트 함수에 검색 조건을 포함하여 수정
function updateBoardPagination(totalPages, currentPage, category, term) {
    const boardPagination = document.getElementById('board-pagination'); // 페이지네이션을 위한 요소의 ID를 boardPagination로 가정
    boardPagination.innerHTML = ''; // 기존 페이지네이션 초기화

    for (let i = 0; i < totalPages; i++) {
        const button = document.createElement('button');
        button.innerText = i + 1;
        // 검색 조건을 유지하며 해당 페이지를 불러오는 함수를 버튼의 클릭 이벤트에 연결
        button.onclick = function() { fetchBoards(i, category, term); };
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
        button.onclick = function() { fetchComments(i, category, term); };
        if (i === currentPage) {
            button.classList.add('active');
        }
        commentPagination.appendChild(button);
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
// 신고 유저 관리 페이지 검색 설정
function setupReportSearch() {
    const searchBox = document.querySelector('.report-search-box');
    searchBox.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            performReportSearch(searchBox);
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
                <td><button class="delete-btn" data-id="${item.id}">삭제</button></td>
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
            <td><button class="delete-btn" data-id="${comment.id}">삭제</button></td>
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
        tableBody.appendChild(tr);
    });
}