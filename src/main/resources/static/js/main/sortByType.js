document.querySelector("#order-select").addEventListener("change", function() {
    let selectedValue = this.options[this.selectedIndex].value;
    if(selectedValue==="latest"){
        pageNum = 1;
        getBoardList("createdAt");
    }else if(selectedValue==="views"){
        pageNum = 1;
        getBoardList("readCount");
    }else if(selectedValue==="famous"){
        pageNum = 1;
        getBoardList("likesCount");
    }else if(selectedValue="follow"){
        pageNum = 1;
        getBoardListWithFollow();
    }
});

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