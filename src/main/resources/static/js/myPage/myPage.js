function clickEditProfileButton(){
    location.href="/myPage/edit";
}
function clickLogoutButton() {
    const confirmation = confirm("로그아웃 하시겠습니까?");

    if (confirmation) {
        alert("로그아웃 되었습니다.");
        location.href = "/logout";
    } else {
        // 아무 처리도 안함
    }
}
function clickUserQuitButton(){
    const confirmation = confirm("회원 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.");

    if (confirmation) {
        alert("회원 탈퇴가 완료되었습니다.");
        location.href = "/quit";
    } else {
        // 아무 처리도 안함
    }
}