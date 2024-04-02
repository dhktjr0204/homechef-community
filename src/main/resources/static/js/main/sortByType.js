document.querySelector("#order-select").addEventListener("change", function() {
    let selectedValue = this.options[this.selectedIndex].value;
    if(selectedValue==="latest"){
        //index 초기화
        pageNum = 1;
        //boardSlideIndex 초기화 (board.js에서 사용하는 화면 넘기기 기능)
        deleteBoardSlideIndex();

        getBoardList("createdAt");
    }else if(selectedValue==="views"){
        pageNum = 1;
        deleteBoardSlideIndex();

        getBoardList("readCount");
    }else if(selectedValue==="famous"){
        pageNum = 1;
        deleteBoardSlideIndex();

        getBoardList("likesCount");
    }else if(selectedValue==="follow"){
        pageNum = 1;
        deleteBoardSlideIndex();

        getBoardListWithFollow();
    }
});

function deleteBoardSlideIndex(){
    // boardSlideIndex 객체의 모든 속성 삭제
    for (let key in boardSlideIndex) {
        delete boardSlideIndex[key];
    }
}

async function getBoardList(type) {
    try {
        const response = await fetch(`?page=0&sort=${type},DESC&id=${lastBoardId}`, {
            method: "GET",
        });

        if (!response.ok) {
            console.log("실패");
            return;
        }

        const html = await response.text();

        //main에 응답받은 html로 초기화
        const mainElement=document.querySelector('main');
        mainElement.innerHTML=html;

    } catch (error) {
        console.error(error);
    }
}

async function getBoardListWithFollow() {
    try {
        const response = await fetch(`/follow?page=0&id=${lastBoardId}`, {
            method: "GET",
        });

        if (!response.ok) {
            console.log("실패");
            return;
        }

        const html = await response.text();

        //main에 응답받은 html로 초기화
        const mainElement=document.querySelector('main');
        mainElement.innerHTML=html;

    } catch (error) {
        console.error(error);
    }
}