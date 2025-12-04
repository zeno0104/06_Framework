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
      tbody.innerHTML = "";
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
    .then((todoList) => {
      // MY_CODE
      // tbody.innerHTML = "";
      // result.map((todoList) => {
      //   // 매개변수 todoList :
      //   // 첫번쨰 then에서 resp.text() / resp.json() 했냐에 따라
      //   // 단순 텍스트이거나, JS Object 일 수 있음
      //   const tr = document.createElement("tr");
      //   const todoNo = document.createElement("td");
      //   todoNo.innerText = todoList.todoNo;

      //   const todoTitle = document.createElement("td");
      //   const title = document.createElement("a");
      //   title.innerText = todoList.todoTitle;
      //   title.href = `/ajax/getData?todoNo=${todoList.todoNo}`;
      //   // <a href="/ajax/detail?todoNo=1">테스트 1 제목</a>
      //   todoTitle.append(title);
      //   title.addEventListener("click", (e) => {
      //     e.preventDefault(); // 기본 이벤트 방지
      //     // 할 일 상세 조회 비동기 요청 함수 호출
      //     selectTodo(e.target.href);
      //   });
      //   const complete = document.createElement("td");
      //   complete.innerText = todoList.complete;

      //   const regDate = document.createElement("td");
      //   regDate.innerText = todoList.regDate;

      //   tr.append(todoNo, todoTitle, complete, regDate);

      //   tbody.append(tr);
      //   tbody.innerHTML = "";
      // tbody에 tr/td 요소를 생성해서 내용 추가
      tbody.innerHTML = "";
      for (let todo of todoList) {
        // 향상된 for문
        // tr 태그 생성
        const tr = document.createElement("tr"); // <tr></tr>
        // JS 객체에 존재하는 key 모음 배열 생성
        const arr = ["todoNo", "todoTitle", "complete", "regDate"];
        for (let key of arr) {
          const td = document.createElement("td"); // <td></td>
          // 제목인 경우
          if (key === "todoTitle") {
            const a = document.createElement("a"); // a태그 생성
            a.innerText = todo[key]; // todo["todoTitle"]
            a.href = "/ajax/detail?todoNo=" + todo.todoNo;
            td.append(a);
            tr.append(td);
            // a태그 클릭 시 페이지 이동 막기(비동기 요청 사용을 위해)
            a.addEventListener("click", (e) => {
              e.preventDefault(); // 기본 이벤트 방지
              // 할 일 상세 조회 비동기 요청 함수 호출
              selectTodo(e.target.href);
            });
            continue;
          }
          // 제목이 아닌 경우
          td.innerText = todo[key]; // todo['todoNo']
          tr.append(td); // tr의 마지막요소 현재 td 추가하기
        }
        // tbody 의 자식으로 tr 추가
        tbody.append(tr);
      }
    });
};

// 비동기로 할 일 상세 조회하는 함수

// popuplayer의 제목 클릭 시 popuplayer 보이게 하기

// popuplayer의 X 클릭 시 popuplayer 숨기기

const selectTodo = (url) => {
  // fetch() 요청 보내기
  fetch(url)
    .then((response) => response.json())
    .then((todo) => {
      // 만약 response.text()로 했다면
      // JSON.parse(result)
      // 이렇게 하면 json으로 바꿔줌
      // 하지만, 번거롭다면 response.json()
      // popup layer에 조회해온 값을 출력
      popupTodoNo.innerText = todo.todoNo;
      popupTodoTitle.innerText = todo.todoTitle;
      popupComplete.innerText = todo.complete;
      popupRegDate.innerText = todo.regDate;
      popupTodoContent.innerText = todo.todoContent;
      // popuplayer 보이게 하기
      popupLayer.classList.remove("popup-hidden");
      popupLayer.classList.add("popuplayer");
    });
};
// popuplayer의 X 클릭 시 popuplayer 숨기기
popupClose.addEventListener("click", () => {
  // display:none 처리 해주는 class 추가
  popupLayer.classList.add("popup-hidden");
});

// 삭제 버튼 클릭 시
deleteBtn.addEventListener("click", () => {
  // 취소 클릭 시 해당 함수 종료
  if (!confirm("정말로 삭제하시겠습니까?")) {
    return;
  }
  // 삭제할 할 일 번호 얻어오기
  const todoNo = popupTodoNo.innerText;

  // 확인 버튼 클릭 시 삭제 비동기 요청 (DELETE 방식)
  fetch("/ajax/delete", {
    method: "DELETE", // @DeleteMapping() 처리
    headers: { "Content-Type": "application/json" },
    body: todoNo, // 단일 값 하나는 JSON 형태로 자동 변환되어 전달됨
    // 원래는 body : JSON.stringify(todoNo)로 표현하는게 옳다
    // body : "todoNo=" + todoNo
    // headers에는 application/x-www-form-urlencoded 이렇게 작성해야함
    // 단일 값일 경우 body에 값 하나만 보내도 됌
  })
    .then((response) => response.text())
    .then((result) => {
      if (result > 0) {
        alert("삭제 성공!");
        // location.href = "/ajax/main";
        // 질문 -> 이렇게 구현하면 안되는지??

        // 상세 조회 팝업 레이어 닫기
        popupLayer.classList.add("popup-hidden");
        // 전체, 완료된 할 일 갯수 다시 조회
        // 할 일 목록 다시 조회
        getTotalCount();
        getCompleteCount();
        selectTodoList();
      } else {
        alert("삭제 실패...");
      }
    });
});
changeComplete.addEventListener("click", () => {
  // 현재 완료여부를 반대값으로 변경한 값, 변경할 할 일 번호
  const todoNo = popupTodoNo.innerText;
  const complete = popupComplete.innerText === "Y" ? "N" : "Y";

  // SQL 수행에 필요한 두 값을 JS 객체로 묶음
  const obj = {
    todoNo,
    complete,
  };

  // 비동기로 완료 여부 변경 요청(PUT 요청 방식)
  fetch("/ajax/changeComplete", {
    method: "PUT", // @PutMapping
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(obj),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        // update 된 DB 데이터를 다시 조회해서 화면에 출력
        // -> 서버 부하가 큼
        // selectTodo();

        // 상세 조회 팝업에서 Y/N 바꾸기
        popupComplete.innerText = complete;

        // getCompleteCount(); 재호출 가능하지만, 서버 부하가 걸린다
        // 따라서, 기존 완료된 것에 +1(Y일 때) or -1(N일 때) 하기
        const count = Number(completeCount.innerText);
        if (complete === "Y") {
          completeCount.innerText = count + 1;
        } else {
          completeCount.innerText = count - 1;
        }
        selectTodoList();
        // 서버 부하 줄이기 가능! 하지만, 코드가 복잡쓰..
        // 오히려 시간 비용이 증가할 수도 있음
      } else {
        alert("완료여부 수정 실패...");
      }
    });
});

// 상세조회 팝업에서 수정(#updateView) 버튼 클릭 시

updateView.addEventListener("click", () => {
  // 기존 상세 조회 팝업 레이어는 숨기고
  popupLayer.classList.add("popup-hidden");
  // 수정 팝업 레이어 보이게

  updateLayer.classList.remove("popup-hidden");
  // 상세 조회 팝업 레이어에 작성된 제목, 내용을 얻어와 세팅

  updateTitle.value = popupTodoTitle.innerText;
  updateContent.value = popupTodoContent.innerHTML.replaceAll("<br>", "\n");

  // HTML 화면에서 줄 바꿈이 <br>로 인식되고 있는데
  // textarea에서는 \n으로 바꿔줘야 실제 줄바꿈으로 인식되어
  // textarea 창에 출력된다!

  // 수정 레이어 -> 수정 버튼에 data-todo-no 속성 추가
  updateBtn.setAttribute("data-todo-no", popupTodoNo.innerText);
  // <button id="updateBtn" data-todo-no="3">수정</button>
});

// 수정 레이어에서 취소 버튼 클릭 시
updateCancel.addEventListener("click", () => {
  updateLayer.classList.add("popup-hidden");

  // 상세 팝업 레이어 보이기
  popupLayer.classList.remove("popup-hidden");
});

// 수정 레이어 -> 수정 버튼 클릭 시
updateBtn.addEventListener("click", (e) => {
  // 서버로 전달해야하는 값을 JS 객체로 묶음
  const obj = {
    todoNo: e.target.dataset.todoNo,
    todoTitle: updateTitle.value,
    todoContent: updateContent.value,
  };

  // 비동기 요청(PUT)
  fetch("/ajax/update", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(obj),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        alert("수정 성공!!");

        // 수정 레이어 숨기기
        updateLayer.classList.add("popup-hidden");

        // 상세 조회 레이어 보이기
        // -> 수정한 내용이 출력되도록
        popupTodoTitle.innerText = updateTitle.value;
        popupTodoContent.innerHTML = updateContent.value.replaceAll(
          "\n",
          "<br>"
        );
        popupLayer.classList.remove("popup-hidden");
        selectTodoList();

        updateTitle.value = "";
        updateContent.value = "";
        updateBtn.removeAttribute("data-todo-no"); // 속성 제거
      } else {
        alert("수정 실패...");
      }
    });
});

getTotalCount();
getCompleteCount();
selectTodoList();
