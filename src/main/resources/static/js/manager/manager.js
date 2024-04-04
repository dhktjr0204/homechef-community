document.addEventListener('DOMContentLoaded', function () {
    // 블랙리스트에 추가하는 버튼에 대한 이벤트 리스너 추가
    document.querySelectorAll('.add-to-blacklist').forEach(function (button) {
        button.addEventListener('click', function () {
            console.log("블랙리스트 추가 버튼 클릭:", this.getAttribute('data-id'));
            let userId = this.getAttribute('data-id');
            addToBlacklist(userId);
        });
    });

    // 블랙리스트에서 제거하는 버튼에 대한 이벤트 리스너 추가
    document.querySelectorAll('.remove-from-blacklist').forEach(function (button) {
        button.addEventListener('click', function () {
            console.log("블랙리스트 제거 버튼 클릭:", this.getAttribute('data-id'));
            let userId = this.getAttribute('data-id');
            removeFromBlacklist(userId);
        });
    });
});

function addToBlacklist(userId) {
    fetch(`/manager/blacklist/add/${userId}`, { method: 'POST' })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text(); // 텍스트 형식으로 응답을 받음
        })
        .then(data => {
            alert(data); // 서버로부터 받은 텍스트 메시지를 표시
            window.location.reload(); // 성공적으로 추가되면 페이지를 리로드
        })
        .catch(error => {
            console.error('Error:', error);
            alert('블랙리스트 추가 중 오류가 발생했습니다.');
        });
}

function removeFromBlacklist(userId) {
    fetch(`/manager/blacklist/remove/${userId}`, { method: 'POST' })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text(); // 텍스트 형식으로 응답을 받음
        })
        .then(data => {
            alert(data); // 서버로부터 받은 텍스트 메시지를 표시
            window.location.reload(); // 성공적으로 제거되면 페이지를 리로드
        })
        .catch(error => {
            console.error('Error:', error);
            alert('블랙리스트 제거 중 오류가 발생했습니다.');
        });
}