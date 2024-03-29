function submitForm() {
    let keyword = document.querySelector(".search-input").value.trim();

    if (keyword === "") {
        alert("검색어를 입력해주세요");
        return false; // 빈 키워드일 경우 폼 제출 방지
    } else if(keyword.startsWith("#")){
        const tagsWithHash = keyword.split(" ");
        //연속으로 #이 있어도 지울 수 있다.
        const tags=tagsWithHash
            .map(tag=> tag.replace(/^#+/,""))
            .map(tag=>tag.trim())
            //아무것도 없는 값 제거
            .filter((tag)=>tag);

        if(tags.length===0){
            alert("해시태그를 입력해주세요.");
            return false;
        }

        location.href=`/hashtag_search?keyword=${tags.join(",")}`;

        return false;
    }
    else {
        return true; // 검색어가 있을 경우 폼 제출
    }
}