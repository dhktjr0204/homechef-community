const nameInput = document.querySelector('.user-name-input');
const introductionInput = document.querySelector('.profile-introduction-input');

const nameLimit=20;
const introductionLimit=300;

//새로고침되면서 글자수 세기 작동(수정폼일 경우 필요)
document.addEventListener('DOMContentLoaded', function () {
    const nameTextCount = document.querySelector('.name-text-count');
    const introductionTextCount = document.querySelector('.introduction-text-count');
    countBytes(nameInput, nameTextCount, nameLimit);
    countBytes(introductionInput, introductionTextCount, introductionLimit);
});

function nameWordCount(textarea) {
    const textCount = document.querySelector('.name-text-count');
    countBytes(textarea, textCount, nameLimit);
}
function introductionWordCount(textarea) {
    const textCount = document.querySelector('.introduction-text-count');
    countBytes(textarea, textCount, introductionLimit);
}
// 글자 수 세기 끝