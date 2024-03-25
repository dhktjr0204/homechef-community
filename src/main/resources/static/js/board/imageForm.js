const input = document.querySelector('.hash-tag-input');
const list = document.querySelector('.hash-tag-list');

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

function openFileUploader() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/**';
    input.multiple = 'multiple';
    input.onchange = handleFileUpload;
    input.click();
}

function handleFileUpload(event) {
    const files = event.target.files;

    if (files.length > 0) {
        const imageList = document.querySelector('.image-list');
        const existingImages = imageList.querySelectorAll('.image-item');

        // 이미지 리스트에 이미 있는 이미지의 개수
        const existingImageCount = existingImages.length;

        for (let i = 0; i < files.length; i++) {
            if(existingImageCount+i>10){
                alert("사진 업로드는 최대 10개까지 가능합니다.");
                prevSlide();
                return;
            }

            let imageItem = createImageItem(imageList,existingImageCount, i, files[i]);

            const render = new FileReader();
            // 파일 읽기 작업이 성공적으로 완료되었을 때 호출되는 콜백함수
            // imageItem을 매개 변수로 받는다.
            // e는 파일의 내용
            // result에는 파일의 데이터 url이 포함된다.
            render.onload = (function (aImg) {
                return function (e) {
                    aImg.src = e.target.result;
                }
            })(imageItem);

            //FileReader객체를 사용하여 파일을 읽는다.
            render.readAsDataURL(files[i]);
        }

        prevSlide();
    }
}
function createImageItem(imageList, existingImageCount, i, file){
    const li = document.createElement('li');
    const imageItem = document.createElement('img');
    const indexLabel = document.createElement('span'); // 순서를 나타내는 레이블
    const deleteButton = document.createElement('button'); // 삭제 버튼

    li.classList.add('image-preview');

    imageItem.classList.add('image-item');
    imageItem.file = file;

    indexLabel.classList.add('image-index');
    indexLabel.textContent = `${existingImageCount + i}`;

    deleteButton.classList.add('image-delete-button');
    deleteButton.textContent = 'x';

    li.appendChild(deleteButton);
    li.appendChild(imageItem);
    li.appendChild(indexLabel);

    imageList.appendChild(li);

    clickDeleteImage(deleteButton, file, li);

    return imageItem;
}

function clickDeleteImage(deleteButton, files, li) {
    const imageList = document.querySelector('.image-list');

    deleteButton.addEventListener('click', function () {
        const confirmation = confirm("삭제하시겠습니까?");
        if (confirmation) {
            // 삭제 버튼을 클릭했을 때 해당 이미지와 파일을 삭제
            imageList.removeChild(li);
            delete files;

            // 삭제된 이미지 이후의 이미지들의 인덱스를 업데이트
            const imageItems = imageList.querySelectorAll('.image-preview');
            imageItems.forEach((image, index) => {
                const indexLabel = image.querySelector('span');
                indexLabel.textContent = `${index+1}`;
            });
        }
        prevSlide();
    });
}


let slideIndex = 0;

function prevSlide() {
    const imageList = document.querySelector('.image-list');
    const imageItem = document.querySelectorAll('.image-item');
    // 음수가 되지 않게 imageItem.length를 더해주었다.
    slideIndex = (slideIndex - 1 + imageItem.length) % imageItem.length;
    //왼쪽방향으로 이동
    const newPosition = -slideIndex * imageItem[0].offsetWidth;
    imageList.style.transform = `translateX(${newPosition}px)`;
}

function nextSlide() {
    const imageList = document.querySelector('.image-list');
    const imageItem = document.querySelectorAll('.image-item');
    // 현재 위치에서 1칸 이동, 만약 뒤에 더 이미지가 없다면 처음으로 이동
    slideIndex = (slideIndex + 1) % imageItem.length;
    //imageWidth만큼 왼쪽 방향으로 이동
    const newPosition = -slideIndex * imageItem[0].offsetWidth;
    imageList.style.transform = `translateX(${newPosition}px)`;
}