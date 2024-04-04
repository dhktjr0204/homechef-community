function openFileUploader() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/**';
    input.onchange = handleFileUpload;
    input.click();
}

function handleFileUpload(event) {
    const file = event.target.files[0];

    if (file) {
        const reader = new FileReader();

        reader.onload = function (e) {
            const newImageUrl = e.target.result;
            const profileImage = document.querySelector('.profile-image img');

            profileImage.src = newImageUrl;
            profileImage.file = file;
        };

        // FileReader를 사용하여 파일을 읽습니다.
        reader.readAsDataURL(file);
    }
}

function clickEditBasicProfileButton() {
    fetch("/myPage/basicProfile", {
        method: "GET",
    }).then(response => {
        if (!response.ok) {
            console.log("요청에 실패하였습니다");
        } else {
            return response.text();
        }
    }).then(profileImageUrl => {
        const profileImage = document.querySelector('.profile-image img');
        const profileImageName = "images/db181dbe-7139-4f6c-912f-a53f12de6789_기본프로필.png";
        profileImage.src = profileImageUrl;
        profileImage.setAttribute("value", profileImageName);
    }).catch(error => {
        console.error(error);
    });
}

function clickCancelButton() {
    window.location.replace("/");
}

function clickSubmitButton() {
    const userId = document.querySelector('.user-name-input').getAttribute("value");

    const nickname=document.querySelector('.user-name-input').value.trim();

    if(nickname.length===0){
        alert("ID를 입력해주세요.");
        return;
    } else if (nickname.length > 20) {
        alert("ID가 길이를 초과하였습니다. 20자 이하로 입력해 주세요.");
        return;
    }

    const formData = new FormData(document.querySelector('.profile-edit-form'));

    const profileImage = document.querySelector('.profile-image img');

    const file = profileImage.file;
    if (file) {
        formData.append('newImage', file);
    } else {
        formData.append('originalImage', profileImage.getAttribute("value"));
    }


    fetch("/myPage/edit/" + userId, {
        method: "PUT",
        body: formData,
    }).then(response => {
        if (!response.ok) {
            return response.text().then(msg => {
                if (response.status === 400) {
                    alert(msg);
                    throw new Error(msg);
                }
            });
        } else {
            return response.text();
        }
    }).then(url => {
        window.location.replace(url);
    }).catch(error => {
        console.error(error);
    });
}