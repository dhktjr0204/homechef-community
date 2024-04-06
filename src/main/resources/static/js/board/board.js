let currentUserId;

// 객체를 사용하여 각 게시물의 슬라이드 인덱스 관리
const boardSlideIndex = {};

//slide 사용하는 곳이 여러 곳이기 때문에 새로고침될 때마다 0으로 초기화 필요하다.(board, main)
document.addEventListener('DOMContentLoaded', function () {
    for (let key in boardSlideIndex) {
        delete boardSlideIndex[key];
    }
});

function prevSlide(boardId, button) {
    let currentIndex = boardSlideIndex[boardId] || 0; // 해당 게시물의 슬라이드 인덱스를 가져오거나, 없으면 0으로 초기화
    const imageItem = button.closest('.image-container').querySelectorAll('.image-item').length; //게시물에 등록된 사진의 개수
    // 음수가 되지 않게 imageItem.length를 더해주었다.
    currentIndex = (currentIndex - 1 + imageItem) % imageItem;
    moveSlide(boardId, currentIndex, button);
}

function nextSlide(boardId, button) {
    let currentIndex = boardSlideIndex[boardId] || 0; // 해당 게시물의 슬라이드 인덱스를 가져오거나, 없으면 0으로 초기화
    const imageItem = button.closest('.image-container').querySelectorAll('.image-item').length;//게시물에 등록된 사진의 개수
    // 현재 위치에서 1칸 이동, 만약 뒤에 더 이미지가 없다면 처음으로 이동
    currentIndex = (currentIndex + 1) % imageItem;
    moveSlide(boardId, currentIndex, button);
}

function moveSlide(boardId, newIndex, button) {
    const imageList = button.closest('.image-container').querySelector('.image-list');
    const imageWidth = imageList.querySelector('.image-item').offsetWidth;
    const newPosition = -newIndex * imageWidth;
    imageList.style.transform = `translateX(${newPosition}px)`;
    boardSlideIndex[boardId] = newIndex; // 해당 게시물의 슬라이드 인덱스 업데이트
}
//화면 넘기기 기능 끝

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
    const userId = boardArticle.querySelector('.username').getAttribute("value");
    if (confirmation) {
        fetch("/board/delete/" + boardId + "?userId=" + userId, {
            method: "DELETE",
        }).then(response => {
            if (!response.ok) {
                return response.text().then(msg => {
                    if (response.status === 401) {
                        alert(msg);
                    } else if (response.status === 404) {
                        alert("해당 게시물을 찾을 수 없습니다.");
                        throw new Error("해당 게시물을 찾을 수 없습니다.");
                    }
                });
            } else {
                return response.text();
            }
        }).then(url => {
            if (url) {
                window.location.replace(url);
            } else {
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

