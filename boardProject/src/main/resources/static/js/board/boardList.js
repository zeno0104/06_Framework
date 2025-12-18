/* 글쓰기 버튼 클릭 시 */
const insertBtn = document.querySelector("#insertBtn");

// 글쓰기 버튼이 존재할 때 (로그인 상태인 경우)
// if(insertBtn == null)일 수 있기 때문에 조건문 작성
// 조건문을 작성하지 않으면 에러 발생
if (insertBtn != null) {
  insertBtn.addEventListener("click", () => {
    // get 방식 요청
    // /editBoard/1/insert

    // <script th:inline="javascript">
    //    const boardCode = /*[[${boardCode}]]*/ "게시판 코드 번호";
    // </script>
    // html 파일에서 타임리프 문법으로 boardCode 작성

    // html : 타임리프
    // "${java에서 가지고온 값 사용 가능}"

    // JavaScript : ""로 안되고 백틱으로만 사용 가능
    // `${JS변수 사용 가능}`

    // 동기식 요청 : location을 이용
    // 비동기식 요청 : fetch() API을 이용
    location.href = `/editBoard/${boardCode}/insert`;
  });
}
// /editBoard/1/insert
