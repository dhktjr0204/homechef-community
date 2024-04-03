function clickEditProfileButton(){
    const userId=document.querySelector('.user-name').getAttribute("value");
    location.href="/myPage/edit/"+userId;
}