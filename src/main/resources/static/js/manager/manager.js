document.addEventListener('DOMContentLoaded', function () {
    // 블랙리스트에 추가하는 버튼에 대한 이벤트 리스너 추가
    document.querySelectorAll('.add-to-blacklist, .remove-from-blacklist').forEach(function (button) {
        button.addEventListener('click', function () {
            const userId = this.getAttribute('data-id');
            const action = this.classList.contains('add-to-blacklist') ? 'add' : 'remove';

            if (action === 'add') {
                console.log("블랙리스트 추가 버튼 클릭:", userId);
                addToBlacklist(userId);
            } else {
                console.log("블랙리스트 제거 버튼 클릭:", userId);
                removeFromBlacklist(userId);
            }
        });
    });
});

function addToBlacklist(userId) {
    updateBlacklistStatus(`/manager/blacklist/add/${userId}`, '블랙리스트 추가 중 오류가 발생했습니다.');
}

function removeFromBlacklist(userId) {
    updateBlacklistStatus(`/manager/blacklist/remove/${userId}`, '블랙리스트 제거 중 오류가 발생했습니다.');
}

function updateBlacklistStatus(url, errorMessage) {
    fetch(url, { method: 'POST' })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.text();
        })
        .then(data => {
            alert(data);
            window.location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert(errorMessage);
        });
}
