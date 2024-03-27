let boardSlideIndex;

//slide 사용하는 곳이 여러 곳이기 때문에 새로고침될 때마다 0으로 초기화 필요하다.
document.addEventListener('DOMContentLoaded', function (){
    boardSlideIndex=0;
});

function prevSlide(button) {
    //현재 버튼이 속한 container 찾기
    const imageContainer=button.closest('.image-container');
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
    const imageContainer=button.closest('.image-container');
    const imageList = imageContainer.querySelector('.image-list');
    const imageItem = imageContainer.querySelectorAll('.image-item');
    // 현재 위치에서 1칸 이동, 만약 뒤에 더 이미지가 없다면 처음으로 이동
    boardSlideIndex = (boardSlideIndex + 1) % imageItem.length;
    //imageWidth만큼 왼쪽 방향으로 이동
    const newPosition = -boardSlideIndex * imageItem[0].offsetWidth;
    imageList.style.transform = `translateX(${newPosition}px)`;
}

function clickUpdateBoard(button){
    // 수정 버튼이 속한 게시물의 부모 요소인 article을 찾음.
    const boardArticle = button.closest('.board-container');
    const boardId=boardArticle.querySelector('.board-id').value;
    location.href="/board/edit/"+boardId;
}

function clickDeleteBoard(button){
    const confirmation = confirm("정말 삭제하시겠습니까?");
    const boardArticle = button.closest('.board-container');

    const boardId=boardArticle.querySelector('.board-id').value;
    if(confirmation){
        fetch("/board/delete/"+boardId, {
            method: "DELETE",
        }).then(response => {
            if (!response.ok) {
                console.log("실패");
            } else {
                return response.text();
            }
        }).then(url => {
            window.location.replace(url);
        }).catch(error => {
            console.error(error);
        });
    }
}

function clickGoToBoard(button){
    const boardArticle = button.closest('.board-container');

    const boardId=boardArticle.querySelector('.board-id').value;
    location.href="/board/"+boardId;
}