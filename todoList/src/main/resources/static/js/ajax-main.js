// 할 일 개수 관련 요소
const totalCount = document.querySelector("#totalCount");
const completeCount = document.querySelector("#completeCount");
const reloadBtn = document.querySelector("#reloadBtn");
// 할 일 추가 관련 요소
const todoTitle = document.querySelector("#todoTitle");
const todoContent = document.querySelector("#todoContent");
const addBtn = document.querySelector("#addBtn");
// 할 일 목록 조회 관련 요소
const tbody = document.querySelector("#tbody");
// 할 일 상세 조회 관련 요소
const popupLayer = document.querySelector("#popupLayer");
const popupTodoNo = document.querySelector("#popupTodoNo");
const popupTodoTitle = document.querySelector("#popupTodoTitle");
const popupComplete = document.querySelector("#popupComplete");
const popupRegDate = document.querySelector("#popupRegDate");
const popupTodoContent = document.querySelector("#popupTodoContent");
const popupClose = document.querySelector("#popupClose");
// 상세 조회 팝업레이어 관련 버튼 요소
const changeComplete = document.querySelector("#changeComplete");
const updateView = document.querySelector("#updateView");
const deleteBtn = document.querySelector("#deleteBtn");
// 수정 레이어 관련 요소
const updateLayer = document.querySelector("#updateLayer");
const updateTitle = document.querySelector("#updateTitle");
const updateContent = document.querySelector("#updateContent");
const updateBtn = document.querySelector("#updateBtn");
const updateCancel = document.querySelector("#updateCancel");

// 전체 Todo 개수 조회 및 html 화면상에 출력하는 함수

/**
 * fetch() API
 * - 비동기 요청을 수행하는 최신 Javascript API 중 하나.
 *
 * - Promise는 비동기 작업의 결과를 처리하는 방법으로
 * - 어떤 결과가 올지는 모르지만 반드시 결과를 응답을 해주겠다는 약속임
 * -> 비동기 작업이 맞이할 완료 또는 실패와 그 결과값을 나타냄.
 * -> 비동기 작업이 완료되었을 때 실행할 콜백함수를 지정하고,
 * 해당 작업의 성공 또는 실패 여부를 처리할 수 있도록 함.
 *
 *
 * Promise 객체는 세가지 상태를 가짐
 * - Pending(대기 중) : 비동기 작업이 완료되지 않은 상태
 * - Fulfiled(이행됨) : 비동기 작업이 성공적으로 완료된 상태
 * - Rejected(거부됨) : 비동기 작업이 실패한 상태
 */
// 전체 Todo 개수 조회 및 html 화면상에 출력하는 함수
function getTotalCount() {
  // 비동기로 서버에 전체 Todo 개수를 조회하는 요청
  // fetch() API 로 코드 작성
  //   /ajax/totalCount
  fetch("/ajax/totalCount") // 서버로 "/ajax/totalCount" 로 GET 요청
    // 첫번 째 then (응답을 처리하는 역할)
    .then((response) => {
      // 서버에서 응답을 받으면,
      // 이 응답(response)을 텍스트 형식으로 변환하는 콜백함수로 바꾸기
      // => 상태값, 결과값이 들어가있음
      // 매개변수 response : 비동기 요청에 대한 응답이 담긴 객체

      // response.text() : 응답 데이터를 문자열/숫자 형태로 변환한 결과를 가지는 Promise 객체 반환
      return response.text();
    }) // 두번째 then (첫번째에서 return된 데이터를 활용하는 역할)
    .then((result) => {
      // 첫번쨰 콜백함수가 완료된 후 호출되는 콜백함수
      // result는 response.text() return된 결과를 의미
      // 매개변수로 전달되어진 데이터(result)를 받아서
      // 어떤식으로 처리할지 정의
      // -> #totalCount인 span 태그의 내용으로 result 값 대입
      totalCount.innerText = result;
    });
}
// 완료된 할 일 갯수 조회 및 html 화면상에 출력하는 함수
function getCompleteCount() {
  fetch("/ajax/completeCount")
    .then((response) => response.text())
    .then((result) => {
      // #completeCount 요소에 내용으로 result 값 출력
      completeCount.innerText = result;
    });
}

// 추가하기
addBtn.addEventListener("click", () => {
  if (
    todoTitle.value.trim().length === 0 ||
    todoContent.value.trim().length === 0
  ) {
    alert("제목이나 내용은 비어있을 수 없습니다!");
    return;
  }
  // POST 방식 fetch() 비동기 요청 보내기
  // - 요청 주소 : "/ajax/add"
  // - 데이터 전달방식 : POST
  // - 전달 데이터(파라미터) : todoTitle값, todoContent값

  // JS에서 전달하는 데이터를 -> Java로 보내야하고, Java에서 JavaScript로
  // 보내야하는 일이 발생함
  // 언어가 다르기 때문에, 호환될 수 없음.
  // 중간에서 호환될 수 있도록 하는 형식이 JSON
  // JSON(Javascript Object Notation) : 데이터를 표현하는 문법
  /**
   * {
   *   "name" : "홍길동",
   *   "age" : "20",
   *   "skill" : ["javascript", "java"]
   * }
   */

  // todoTitle과 todoContent를 저장한 JS 객체
  const param = {
    // key : value
    todoTitle: todoTitle.value,
    todoContent: todoContent.value,
  };

  fetch("/ajax/add", {
    // key:value
    method: "POST", // post 방식 요청
    headers: {
      // 요청 데이터의 형식을 JSON으로 지정
      "Content-Type": "application/json",
    },
    // 요청 본문, 즉 요청할 때 보낼 데이터를 가지고 있음
    // js 객체로 보내는 것이 아닌, JSON의 String으로 바꾸겠다는 것
    // param이라는 JS 객체를 JSON(string)으로 변환
    body: JSON.stringify(param),
  })
    .then((response) => response.text())
    .then((result) => {
      if (result > 0) {
        alert("추가 성공!");
        todoTitle.value = "";
        todoContent.value = "";

        getTotalCount();
        selectTodoList();
        // -> 전체 Todo 목록 조회하는 함수 재호출 예정
      } else {
        alert("추가 실패..");
      }
    });
});

// 비동기로 할 일 전체 목록을 조회 & html 화면에 출력까지 하는 함수
const selectTodoList = () => {
  fetch("/ajax/selectList")
    .then((response) => response.json()) // 응답 결과를 json으로 받음
    .then((result) => {
      console.log(result);
      // MY_CODE
      tbody.innerHTML = "";
      result.map((todoList) => {
        // 매개변수 todoList :
        // 첫번쨰 then에서 resp.text() / resp.json() 했냐에 따라
        // 단순 텍스트이거나, JS Object 일 수 있음
        const tr = document.createElement("tr");
        const todoNo = document.createElement("td");
        todoNo.innerText = todoList.todoNo;

        const todoTitle = document.createElement("td");
        const title = document.createElement("a");
        title.innerText = todoList.todoTitle;
        title.href = `/ajax/getData/todoNo=${todoList.todoNo}`;
        todoTitle.append(title);

        const complete = document.createElement("td");
        complete.innerText = todoList.complete;

        const regDate = document.createElement("td");
        regDate.innerText = todoList.regDate;

        tr.append(todoNo, todoTitle, complete, regDate);

        tbody.append(tr);
      });
    });
  // T_CODE
  //   // 기존에 출력되어 있던 할 일 목록을 모두 비우기
  //   tbody.innerHTML = "";
  //   // tbody에 tr/td 요소를 생성해서 내용 추가
  //   for (let todo of todoList) {
  //     // 향상된 for문
  //     // tr 태그 생성
  //     const tr = document.createElement("tr"); // <tr></tr>
  //     // JS 객체에 존재하는 key 모음 배열 생성
  //     const arr = ["todoNo", "todoTitle", "complete", "regDate"];
  //     for (let key of arr) {
  //       const td = document.createElement("td"); // <td></td>
  //       // 제목인 경우
  //       if (key === "todoTitle") {
  //         const a = document.createElement("a"); // a태그 생성
  //         a.innerText = todo[key]; // todo["todoTitle"]
  //         a.href = "/ajax/detail?todoNo=" + todo.todoNo;
  //         td.append(a);
  //         tr.append(td);
  //         // a태그 클릭 시 페이지 이동 막기(비동기 요청 사용을 위해)
  //         a.addEventListener("click", (e) => {
  //           e.preventDefault(); // 기본 이벤트 방지
  //           // 할 일 상세 조회 비동기 요청 함수 호출
  //           selectTodo(e.target.href);
  //         });
  //         continue;
  //       }
  //       // 제목이 아닌 경우
  //       td.innerText = todo[key]; // todo['todoNo']
  //       tr.append(td); // tr의 마지막요소 현재 td 추가하기
  //     }
  //     // tbody 의 자식으로 tr 추가
  //     tbody.append(tr);
  //   }
};

getTotalCount();
getCompleteCount();
selectTodoList();
