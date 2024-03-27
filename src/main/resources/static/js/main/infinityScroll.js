let pageNum = 1;

window.onbeforeunload = function () {
    window.scrollTo(0, 0); // 페이지의 맨 위로 스크롤
}

async function fetchImages(pageNum) {
    // const orderType= document.querySelector('.dropbtn').getAttribute("value");
    try {
        if (pageNum >= totalPage) {
            alert("더 이상 콘텐츠가 없습니다.");
            window.removeEventListener('scroll', throttleTracker);
            return;
        }

        const response = await fetch(`?page=${pageNum}&id=${lastBoardId}`, {
            method: "GET",
        });

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