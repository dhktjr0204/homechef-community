# 🖥 프로젝트 명 'CookLog'

CookLog는 자취생들을 위한 집밥 소개 SNS입니다! <br>
꼭 자취생이 아니더라도 자신이 만든 요리라면 공유할 수 있습니다!<br>
CookLog를 통해 자신의 요리를 업로드하고, 다른 사용자들의 요리를 탐색하며 소통 할 수 있습니다.<br>
신선한 요리 아이디어와 다양한 레시피를 공유하고 발견할 수 있는 CookLog로 요리하는 즐거움을 함께 누려보세요!
<br><br>
[클릭하여 페이지를 방문하세요](http://ec2-43-202-107-97.ap-northeast-2.compute.amazonaws.com:8080/)
<br>

# 👩‍👧‍👦 개발 인원 및 역할

<table style="width: 100%;">
 <tr>
    <td align="center" style="width: 25%;"><a href="https://github.com/HuiGyun-kim"><img src="src/main/resources/static/img/readMe/kimhuigyun.png" width="130px" alt=""></a></td>
    <td align="center" style="width: 25%;"><a href="https://github.com/dhktjr0204"><img src="src/main/resources/static/img/readMe/jeongjiwon.jpg" width="130px;" alt=""></a></td>
    <td align="center" style="width: 25%;"><a href="https://github.com/hongju1234"><img src="src/main/resources/static/img/readMe/namhongju.jpg" width="130px;" alt=""></a></td>
    <td align="center" style="width: 25%;"><a href="https://github.com/kanghanju"><img src="src/main/resources/static/img/readMe/kanghanju.png" width="130px;" alt=""></a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/HuiGyun-kim"><b>kimhuigyun</b></a></td>
    <td align="center"><a href="https://github.com/dhktjr0204"><b>jeongjiwon</b></a></td>
    <td align="center"><a href="https://github.com/hongju1234"><b>namhongju</b></a></td>
    <td align="center"><a href="https://github.com/kanghanju"><b>kanghanju</b></a></td>
  </tr>
  <tr> 
    <td align="center">Backend, Front</td>
    <td align="center">Backend, Front</td>
    <td align="center">Backend, Front</td>
    <td align="center">Backend, Front</td>
  </tr> 
  <tr> 
    <td align="center">ERD 설계<br>댓글 API 개발<br>관리자 API 개발<br>신고 API 개발<br>Spring Security<br>UI 개발(메인페이지, 게시물, 댓글, 관리자페이지)<br>JavaScript 개발(댓글, 신고)</td>
    <td align="center">화면 설계<br>ERD 설계<br>메인페이지 API 개발<br>게시물 API 개발<br>텍스트 및 태그 검색 API 개발<br>S3를 이용한 사진 업로드 및 사진 삭제<br>프로필 수정 API 개발<br>UI 개발(게시물)<br>JavaScript 개발(글자 수 세기,무한 스크롤, 검색, 게시물, 메인 페이지, 마이페이지 수정)<br>프로젝트 배포</td>
    <td align="center">요구사항 및 기능 명세<br>Spring Security<br>로그인 API 개발<br>마이페이지 API 개발<br>UI 개발(마이페이지)<br>JavaScript 개발(마이페이지)</td>
    <td align="center">ERD 설계<br>좋아요 API 개발<br>북마크 API 개발<br>팔로우 API 개발<br>메인페이지 API 개발<br>UI 개발(북마크, 팔로우)<br>JavaScript 개발(좋아요, 북마크, 팔로우, 메인 페이지)
    </td>
  </tr> 
</table>

<br>

# 📆 프로젝트 일정

2024/03/25(월) ~ 2024/04/05(금)

<br>

# 📚 기술스택

### [기술 - FE]

<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white"> <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">

### [기술 - BE]

<img  src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img alt="Spring Boot" src ="https://img.shields.io/badge/Spring Boot-6DB33F.svg?&style=for-the-badge&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">

### [기술 - DB]

<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"> <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=Amazon RDS&logoColor=white">

### [개발 환경]

<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white"> <img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=Discord&logoColor=white">

<br>

# 🛠 아키텍처 설계도

![Architecture](/src/main/resources/static/img/readMe/architecture.png)

<br>

# 🗂 DB 구성도

![ERD](/src/main/resources/static/img/readMe/CookLog_ERD.png)

# 🖼 UI

|                            로그인 화면                             |                             회원가입 화면                            |
|:-------------------------------------------------------------:|:--------------------------------------------------------------:|
|   ![로그인 이미지](/src/main/resources/static/img/readMe/로그인.png)   |   ![회원가입 이미지](/src/main/resources/static/img/readMe/회원가입.png)  |
|                          <b>마이페이지 화면                          |                          <b>프로필 수정 화면                          |
| ![마이페이지 이미지](/src/main/resources/static/img/readMe/마이페이지.png) | ![프로필 수정 이미지](/src/main/resources/static/img/readMe/프로필수정.png) |
|                           <b>북마크 화면                           |                          <b>팔로워,팔로잉 화면                         |
|   ![북마크 이미지](/src/main/resources/static/img/readMe/북마크.png)   |    ![팔로워 이미지](/src/main/resources/static/img/readMe/팔로워.png)   |
|                           <b>메인 화면                            |                            <b>게시글 화면                           |
|  ![메인 이미지](/src/main/resources/static/img/readMe/메인페이지.png)   |  ![게시글 이미지](/src/main/resources/static/img/readMe/게시글_페이지.png) |
|                           <b>검색 화면                            |                          <b>관리자 메인 화면                          |
|    ![검색 이미지](/src/main/resources/static/img/readMe/검색.png)    | ![관리자 메인 이미지](/src/main/resources/static/img/readMe/관리자_메인.png) |
|                          <b>글 관리 화면                           |                           <b>댓글 관리 화면                          |
|  ![글 관리 이미지](/src/main/resources/static/img/readMe/글_관리.png)  |  ![댓글 관리 이미지](/src/main/resources/static/img/readMe/댓글_관리.png) |
|                        <b>신고 유저 관리 화면                         |                           <b>                          |
| ![신고 관리 이미지](/src/main/resources/static/img/readMe/글_관리.png)  |      |


# 👉 주요 기능

### 1. 메인 페이지
![메인 이미지](/src/main/resources/static/img/readMe/메인페이지.gif)

- 현재 작성된 게시글 목록들을 보여줍니다.
- 무한 스크롤로 구현 되어 있어 스크롤을 내릴때마다 추가로 목록들이 보여집니다.
- 시간순, 조회순, 좋아요순, 팔로워만 보기 기능을 제공하여 정렬된 상태로 게시글 목록들을 볼 수 있습니다. 팔로워만 보기 기능은 현재 팔로잉된 유저들의 게시물을 보여줍니다.

### 2. 텍스트 검색과 태그 검색 기능
![검색 이미지](/src/main/resources/static/img/readMe/검색.gif)

### 3. 게시글 CRUD
![게시글 이미지](/src/main/resources/static/img/readMe/글CRUD.gif)

### 4. 댓글 CRUD
![댓글 이미지](/src/main/resources/static/img/readMe/댓글CRUD.gif)

### 5. 좋아요 기능과 북마크 기능
![좋아요 이미지](/src/main/resources/static/img/readMe/좋아요_북마크.gif)

### 6. 마이페이지 기능
![마이페이지 이미지](/src/main/resources/static/img/readMe/마이페이지.gif)

### 7. 신고 기능
![신고 이미지](/src/main/resources/static/img/readMe/신고.gif)
