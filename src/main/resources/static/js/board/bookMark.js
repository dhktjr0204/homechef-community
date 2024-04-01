//북마크 버튼을 클릭했을때
function clickBookmarkBtn(markBtn) {
  let boardId = markBtn.value;

  if(markBtn.classList.contains('yellowbookmark')) {//이미 북마크 표시가 되어 있다면
    let url = `/api/bookmark/cancel?boardId=${boardId}`;
    cancelBookmark(url, markBtn);
  } else {
    let url =  `/api/bookmark/add?boardId=${boardId}`;
    addBookmark(url,markBtn);
  }
}


async function cancelBookmark(url,markBtn) {
  try {
    let response = await fetch(url, {
      method:'DELETE'
    })

    if(!response.ok) {
      console.log('네트워크 상태가 좋지 않습니다.');
    }

    markBtn.classList.remove('yellowbookmark');
    markBtn.classList.add('emptybookmark');

  } catch (error) {
    console.error(error);
  }
}

async function  addBookmark(url,markBtn) {
  try {
    let response = await fetch(url, {
      method:'POST',
    })

    if(!response.ok) {
      console.log('네트워크 상태가 좋지 않습니다.');
    }
    markBtn.classList.add('yellowbookmark');
    markBtn.classList.remove('emptybookmark');

  } catch (error) {
    console.error(error);
  }
}