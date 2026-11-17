// 고민을 자바 서버(/answer)에 보내고, 대훈이의 대답을 받아서 보여 준다.
const form = document.getElementById("form");
const input = document.getElementById("worry");
const card = document.getElementById("card");
const question = document.getElementById("question");
const answer = document.getElementById("answer");
let lastAnsweredAt = 0;

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const worry = input.value.trim();

  // 한글 입력기가 엔터를 두 번 보내면 빈 고민이 한 번 더 들어온다. 그건 무시한다.
  if (worry === "" && Date.now() - lastAnsweredAt < 500) return;
  if (worry !== "") lastAnsweredAt = Date.now();
  input.value = "";
  input.focus();

  let reply;
  try {
    const response = await fetch("/answer?worry=" + encodeURIComponent(worry));
    reply = await response.text();
  } catch {
    reply = "서버가 꺼져 있어. DaehuniServer를 먼저 실행해 줘!";
  }

  question.hidden = worry === "";
  question.textContent = "Q. " + worry;
  answer.textContent = "대훈이: " + reply;
  card.classList.remove("pop");
  void card.offsetWidth; // 애니메이션 다시 재생
  card.classList.add("pop");
});
