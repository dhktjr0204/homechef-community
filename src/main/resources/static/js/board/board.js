let boardSlideIndex = 0;

function prevSlide() {
    const imageList = document.querySelector('.image-list');
    const imageItem = document.querySelectorAll('.image-item');
    // 음수가 되지 않게 imageItem.length를 더해주었다.
    boardSlideIndex = (boardSlideIndex - 1 + imageItem.length) % imageItem.length;
    //왼쪽방향으로 이동
    const newPosition = -boardSlideIndex * imageItem[0].offsetWidth;
    imageList.style.transform = `translateX(${newPosition}px)`;
}

function nextSlide() {
    const imageList = document.querySelector('.image-list');
    const imageItem = document.querySelectorAll('.image-item');
    // 현재 위치에서 1칸 이동, 만약 뒤에 더 이미지가 없다면 처음으로 이동
    boardSlideIndex = (boardSlideIndex + 1) % imageItem.length;
    //imageWidth만큼 왼쪽 방향으로 이동
    const newPosition = -boardSlideIndex * imageItem[0].offsetWidth;
    imageList.style.transform = `translateX(${newPosition}px)`;
}

function clickUpdateBoard(){
    const boardId=document.querySelector('.board-id').value;
    location.href="/board/edit/"+boardId;
}

function clickDeleteBoard(){
    const confirmation = confirm("정말 삭제하시겠습니까?");

    const boardId=document.querySelector('.board-id').value;
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