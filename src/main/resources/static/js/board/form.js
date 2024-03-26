const limit = 500;

function wordCount(textarea) {
    const textCount = document.querySelector('.text-count');
    countBytes(textarea, textCount, limit);
}

const submitButton = document.querySelector('.submit-button');

submitButton.addEventListener('click', () => {
    handleSubmit("dd", "dd")
});

function handleSubmit(url, method) {
    const tags = document.querySelectorAll('.hash-tag-list .hash-tag-button');
    const tagValues = Array.from(tags).map(tag => tag.textContent);

    const formData = new FormData(document.querySelector('.board-form'));
    formData.append('tags', tagValues.join(','));

    const imageList = document.querySelectorAll('.image-item');

    for (let i = 1; i < imageList.length; i++) {
        const file = imageList[i].file;
        if (file) {
            formData.append('images', imageList[i].file);
        }
    }

    fetch('/board/write', {
        method: 'POST',
        body: formData,
    }).then(response => {
        if (!response.ok) {
            console.log("실패");
        } else {
            window.location.replace("/user/profile");
        }
    }).catch(error => {
        console.error(error);
    })
}