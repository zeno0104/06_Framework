// 쿠키에 저장된 이메일 input 창에 뿌려놓기

// 쿠키에서 매개변수로 전달받은 key가 일치하는 value 얻어오는 함수
const getCookie = (key) => {
  const cookies = document.cookie; // "K=V; K=V; ....."

  //console.log(cookies); // saveId=user01@kh.or.kr; testKey=testValue

  // cookies 문자열을 배열 형태로 변환
  const cookieList = cookies
    .split("; ") // ["K=V", "K=V"...]
    .map((el) => el.split("=")); // ["K", "V"]..

  //console.log(cookieList);

  // [ ['saveId', 'user01@kh.or.kr'],
  //   ['testKey', 'testValue']  ]

  // 배열.map(함수) : 배열의 각 요소를 이용해 함수 수행 후
  //					결과 값으로 새로운 배열을 만들어서 반환

  // 배열 -> 객체로 변환 (그래야 다루기 쉽다)

  const obj = {}; // 비어있는 객체 선언

  for (let i = 0; i < cookieList.length; i++) {
    const k = cookieList[i][0]; // key 값
    const v = cookieList[i][1]; // value 값
    obj[k] = v; // 객체에 추가
    // obj["saveId"] = "user01@kh.or.kr";
    // obj["testKey"]  = "testValue";
  }

  //console.log(obj); // {saveId: 'user01@kh.or.kr', testKey: 'testValue'}

  return obj[key]; // 매개변수로 전달받은 key와
  // obj 객체에 저장된 key가 일치하는 요소의 value값 반환
};

// 이메일 작성 input 태그 요소
const loginEmail = document.querySelector(
  "#loginForm input[name='memberEmail']"
);

if (loginEmail != null) {
  // 로그인창의 이메일 input 태그가 화면상에 존재할 때
  // 즉, 로그인이 안되어있는 화면일 때

  // 쿠키 중 key 값이 "saveId"인 쿠키의 value 얻어오기
  const saveId = getCookie("saveId"); // 이메일 또는 undefined

  // savdId 값이 있을 경우
  if (saveId != undefined) {
    // 쿠키에서 얻어온 이메일 값을 input 요소의 value 세팅
    loginEmail.value = saveId;

    // 아이디 저장 체크박스에 체크해두기
    document.querySelector("input[name='saveId']").checked = true;
  }
}

// ---------------------------------main content-1---------------------------------
const selectMemberList = document.querySelector("#selectMemberList");
const memberList = document.querySelector("#memberList");
const getAllUser = () => {
  fetch("/main/getAllUser")
    .then((resp) => resp.json())
    .then((result) => {
      memberList.innerHTML = "";
      for (let member of result) {
        console.log(result);
        let tr = document.createElement("tr");

        let memberNo = document.createElement("td");
        let memberEmail = document.createElement("td");
        let memberNickname = document.createElement("td");
        let memberDelFl = document.createElement("td");

        memberNo.innerText = member.memberNo;
        memberEmail.innerText = member.memberEmail;
        memberNickname.innerText = member.memberNickname;
        memberDelFl.innerText = member.memberDelFl;

        tr.append(memberNo);
        tr.append(memberEmail);
        tr.append(memberNickname);
        tr.append(memberDelFl);

        memberList.append(tr);
      }
    });
};
selectMemberList.addEventListener("click", getAllUser);

const resetPw = document.querySelector("#resetPw");
const resetMemberNo = document.querySelector("#resetMemberNo");
const resetPwStatus = document.querySelector("#resetPwStatus");

resetPw.addEventListener("click", (e) => {
  if (resetMemberNo.value.trim() === "") {
    alert("회원번호를 입력해주세요!");
    return;
  }
  const memberNo = resetMemberNo.value;
  fetch("/main/resetPw", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(Number(memberNo)),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        resetPwStatus.innerText = "비밀번호 초기화 성공!";
        resetPwStatus.style.color = "green";
      } else {
        resetPwStatus.innerText = "비밀번호 초기화 실패..";
        resetPwStatus.style.color = "red";
      }
      resetMemberNo.value = "";
    });
});

const restorationMemberNo = document.querySelector("#restorationMemberNo");
const restorationBtn = document.querySelector("#restorationBtn");
const restorationStatus = document.querySelector("#restorationStatus");
restorationBtn.addEventListener("click", () => {
  if (restorationMemberNo.value.trim() === "") {
    alert("회원번호를 입력해주세요!");
    return;
  }
  const memberNo = Number(restorationMemberNo.value);
  fetch("/main/restorationMember", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(memberNo),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        restorationStatus.innerText = "탈퇴 복구 성공!";
        restorationStatus.style.color = "green";
        getAllUser();
      } else {
        restorationStatus.innerText = "탈퇴 복구 실패..";
        restorationStatus.style.color = "red";
      }
      restorationMemberNo.value = "";
    });
});
