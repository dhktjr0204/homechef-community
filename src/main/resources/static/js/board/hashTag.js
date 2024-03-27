const input = document.querySelector('.hash-tag-input');
const list = document.querySelector('.hash-tag-list');

//모든 hash-tag-button에 대해 이벤트 처리
document.querySelectorAll('.hash-tag-button').forEach(button=>{
    button.addEventListener('click',()=>{
        button.remove();
    });
});

//새로 만든 hash-tag-button에 대해 이벤트 처리
input.addEventListener('keyup', (e) => {
    if (e.keyCode === 13 || e.keyCode == 32) {
        const tag = e.target.value.trim();
        if (tag) {
            // 10개 이상의 해시태그 추가 제한
            const list = document.querySelector('.hash-tag-list');
            if (list.children.length >= 10) {
                alert('최대 10개까지 해시태그를 추가할 수 있습니다.');
                return;
            }

            //해시태그 생성 코드
            const button = document.createElement('button');
            button.classList.add('hash-tag-button');
            button.textContent = `${tag}`;
            button.addEventListener('click', () => {
                button.remove();
            });
            list.appendChild(button);
            e.target.value = '';
        }
    }
});