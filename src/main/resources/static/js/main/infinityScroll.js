window.onbeforeunload = function () {
    // 페이지를 새로 고침할 때만 실행할 작업을 여기에 작성
    window.scrollTo(0, 0); // 페이지의 맨 위로 스크롤
}

let pageNum = 1;

async function fetchImages(pageNum) {
    let selectedType="latest";

    const orderSelect= document.querySelector("#order-select");
    //만약 order-select태그가 있는 경우에만 value를 바꿔준다(main페이지일때만)
    if(orderSelect){
        selectedType=orderSelect.value;
    }

    const isExistSearchKeyword=document.querySelector(".search-keyword");

    try {
        if (pageNum >= totalPage) {
            alert("더 이상 콘텐츠가 없습니다.");
            return;
        }
        let response;
        if (selectedType === "latest") {
            //검색한 경우
            if(isExistSearchKeyword){
                const keyword=isExistSearchKeyword.textContent;
                response = await fetch(`?page=${pageNum}&id=${lastBoardId}&keyword=${keyword}`, {
                    method: "GET",
                });
            }else{
                //메인 페이지인 경우
                response = await fetch(`?page=${pageNum}&id=${lastBoardId}`, {
                    method: "GET",
                });
            }
        } else if (selectedType === "popularity") {
            response = await fetch(`?page=${pageNum}&sort=readCount,DESC&id=${lastBoardId}`, {
                method: "GET",
            });
        } else if (selectedType === "famous") {
            response = await fetch(`?page=${pageNum}&sort=likesCount,DESC&id=${lastBoardId}`, {
                method: "GET",
            });
        }else if(selectedType === "follow"){
            response = await fetch(`/follow?page=${pageNum}&id=${lastBoardId}`, {
                method: "GET",
            });
        }

        if (!response.ok) {
            console.log("실패");
            return;
        }

        const html = await response.text();

        //main 마지막 자식으로 HTML을 추가하기
        document.querySelector('main').insertAdjacentHTML("beforeend", html);

    } catch (error) {
        console.error(error);
    }
}

function throttling(callback, delay) {
    let timer = null;
    return () => {
        if (timer === null) {
            timer = setTimeout(() => {
                const scrollHeight = document.documentElement.scrollHeight;
                const scrollTop = document.documentElement.scrollTop;
                const clientHeight = document.documentElement.clientHeight;
                if (scrollHeight - scrollTop <= clientHeight + 10) {
                    callback(pageNum++);
                }
                timer = null;
            }, delay);
        }
    }
}


const throttleTracker = throttling(fetchImages, 500);
window.addEventListener('scroll', throttleTracker);