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

