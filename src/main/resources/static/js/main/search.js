function submitForm() {
    let keyword = document.querySelector(".searchInput").value.trim();
    if (keyword === "") {
        return false; // 빈 키워드일 경우 폼 제출 방지
    } else {
        return true; // 검색어가 있을 경우 폼 제출
    }
}