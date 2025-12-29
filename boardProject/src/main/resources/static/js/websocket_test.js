// websocket_test.js
// 웹소켓 테스트

// 1. SockJS 라이브러리 추가
// -> common.html 추가해둠

// 2. SockJS 객체 생성
const testSock = new SockJS("/testSock");
// - WebSocket 통신을 할 수 있게끔 해주는
// 객체를 생성함과 동시에 자동으로
// (localhost)/test으로 연결 요청을 보냄
// => 서버에게 보내는 요청 -> http://localhost/testSock으로 연결 요청을 보냄
// => 추후 http://는 ws://로 바뀜

// 3. 생성된 SockJS 객체를 이용해서 서버에 메시지 전달
const sendMessageFn = (name, str) => {
  // JSON을 이용해서 데이터를 TEXT 형태로 전달
  const obj = {
    name: name,
    str: str,
  };
  testSock.send(JSON.stringify(obj));
};

// sendMessageFn("홍길동", "안녕하세요~");

// 4. 서버로부터 클라이언트에게
// 웹소켓을 이용한 메시지가 전달된 경우
testSock.addEventListener("message", (e) => {
  // e.data : 서버로부터 전달받은 message
  const msg = JSON.parse(e.data); // 서버로부터 JSON 형태로 받아온 것을 JS 객체 형태로 변환
  console.log(`${msg.name}의 메세지 : ${msg.str}`);
});
