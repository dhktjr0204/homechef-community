//하트 버튼을 눌렀을때
function clickLikeBtn(likeBtn) {
  let boardId = likeBtn.value;

  if(likeBtn.classList.contains('redheart')) {//이미 빨간 하트가 표시되어 있다면
    let url = `/api/likes/cancel?boardId=${boardId}`;
    cancelLike(url, likeBtn);
  } else {
    let url =  `/api/likes/add?boardId=${boardId}`;
    addLike(url,likeBtn);
  }
}


async function cancelLike(url,likeBtn) {
  try {
    let response = await fetch(url, {
      method:'DELETE'
    })

    if(!response.ok) {
      if (response.status === 400) {
        let errorMessage = await response.text(); // 서버로부터 받은 에러 메시지
        alert(errorMessage); // 에러 메시지를 alert로 표시
      } else {
        alert("네트워크 상태가 좋지 않습니다.")
        console.log('네트워크 상태가 좋지 않습니다.');
      }
      return;
    }

    let responseData = await response.text();
    likeBtn.classList.remove('redheart');
    likeBtn.classList.add('emptyheart');
    likeBtn.nextElementSibling.textContent = responseData;

  } catch (error) {
    console.error(error);
  }
}

async function  addLike(url,likeBtn) {
  try {
    let response = await fetch(url, {
      method:'POST',
    })

    if(!response.ok) {
      if (response.status === 400) {
        let errorMessage = await response.text(); // 서버로부터 받은 에러 메시지
        alert(errorMessage); // 에러 메시지를 alert로 표시
      } else {
        alert("네트워크 상태가 좋지 않습니다.")
        console.log('네트워크 상태가 좋지 않습니다.');
      }
      return;
    }

    let responseData = await response.text();
    likeBtn.classList.add('redheart');
    likeBtn.classList.remove('emptyheart');
    likeBtn.nextElementSibling.textContent = responseData;

  } catch (error) {
    console.error(error);
  }
}