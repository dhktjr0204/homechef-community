function clickEditProfileButton(){

    const userId=document.querySelector('.user-name').getAttribute("value");
    location.href="/myPage/edit/"+userId;
}
function clickLogoutButton() {
    const confirmation = confirm("로그아웃 하시겠습니까?");

    if (confirmation) {
        alert("로그아웃 되었습니다.");
        location.href = "/logout";
    } else {
        // 아무 처리도 안함
    }
}
function clickUserQuitButton(){
    const confirmation = confirm("회원 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.");

    if (confirmation) {
        alert("회원 탈퇴가 완료되었습니다.");
        location.href = "/quit";
    } else {
        // 아무 처리도 안함
    }
}


function clickFollowButton(event) {
    let button = event.target; // 현재 클릭된 버튼
    let userInfo = button.closest('.follow-button-container'); // 버튼의 상위 컨테이너 찾기

    let currentUserIdx = button.getAttribute('data-currentid');
    let userIdx = button.getAttribute('data-userid');
    console.log(currentUserIdx);
    console.log(userIdx);

    fetch(`/api/follow/${userIdx}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(response => {
        if (response.ok) {
            console.log('Follow successful');

            userInfo.querySelector('.follow-button').classList.add('hidden');
            userInfo.querySelector('.un-follow-button').classList.remove('hidden');
        }
    }).catch(error => {
        console.error('Error:', error);
    });

}

function clickUnFollowButton(event) {
    let button = event.target; // 현재 클릭된 버튼
    let userInfo = button.closest('.follow-button-container'); // 버튼의 상위 컨테이너 찾기

    let currentUserIdx = button.getAttribute('data-currentid');
    let userIdx = button.getAttribute('data-userid');
    console.log(currentUserIdx);
    console.log(userIdx);

    fetch(`/api/unfollow/${userIdx}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(response => {
        if (response.ok) {
            console.log('Unfollow successful');

            userInfo.querySelector('.un-follow-button').classList.add('hidden');
            userInfo.querySelector('.follow-button').classList.remove('hidden');
        }
    }).catch(error => {
        console.error('Error:', error);
    });
}


function clickButton(event) {
    const button = event.target;
    const userIdx = button.getAttribute('data-userid');
    const isFollow = button.classList.contains('un-follow-button');

    const url = isFollow ? `/api/unfollow/${userIdx}` : `/api/follow/${userIdx}`;
    const method = isFollow ? 'DELETE' : 'POST';

    fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
    })
    .then(response => {
        if (response.ok) {
            if (isFollow) {
                button.classList.remove('un-follow-button');
                button.classList.add('follow-button');
                button.textContent = '팔로우';
            } else {
                button.classList.remove('follow-button');
                button.classList.add('un-follow-button');
                button.textContent = '언팔로우';
            }
        } else {
            response.text().then(text => {
                alert(text);
            });
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
}



function clickBoardImage(button){
    const boardId=button.getAttribute("value");
    location.href="/board/"+boardId;
}

function clickBookmarkImage(boardId) {
    location.href="/board/"+boardId;
}

//탭 버튼을 바꾸는 함수
function switchTab(tabName) {
    // 모든 탭 컨텐츠를 숨긴다
    const allTabsContent = document.querySelectorAll('.board-list-container');
    allTabsContent.forEach(tabContent => {
        tabContent.style.display = 'none';
    });

    // 모든 탭 버튼의 active 클래스를 숨긴다
    const allTabButtons = document.querySelectorAll('.tab-button');
    allTabButtons.forEach(tabButton => {
        tabButton.classList.remove('active');
    });

    // 선택된 탭과 관련된 내용을 표시
    if (tabName === 'Posts') {
        document.querySelector('.board-list-container:not(#bookmarkContainer)').style.display = 'block';
        document.querySelector('.tab-button:first-child').classList.add('active');
    } else if (tabName === 'Bookmarks') {
        document.getElementById('bookmarkContainer').style.display = 'block';
        document.querySelector('.tab-button:nth-child(2)').classList.add('active');
    }
}


function loadMyBookmarks() {

    fetch('/myPage/myBookmarks')
    .then(response => response.json()) // 응답을 JSON 형태로 파싱
    .then(data => {
        const bookmarkList = document.getElementById('bookmarkList');
        const noContentContainer = document.getElementById('noContentContainer');
        bookmarkList.innerHTML = '';

        if(data.length > 0) {
            noContentContainer.style.display = 'none';
            data.forEach(bookmark => {
                const listItem = document.createElement('li');
                const image = document.createElement('img');
                image.src = bookmark.imageUrl;
                image.className = 'image-item';
                image.setAttribute('onclick',`clickBookmarkImage(${bookmark.id})`)
                listItem.appendChild(image);
                bookmarkList.appendChild(listItem);
            });
        } else {
            bookmarkList.innerHTML = '';
            noContentContainer.style.display = 'block';
        }

    })
    .catch(error => {
        console.error('Error loading bookmarks:', error);
    });
}




