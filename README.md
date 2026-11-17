# 빵빵이의 고민 해결소

고민을 적으면 빵빵이가 해결책 하나를 랜덤으로 알려 주는 자바 콘솔 프로그램이에요.
고민 내용과 상관없이 110개의 답변 중 하나가 나오고, 같은 답은 연달아 나오지 않아요.

```
=== 빵빵이의 고민 해결소 ===
고민을 적어 주면 빵빵이가 해결책을 알려 줄게!

고민을 적어 줘 (끝내려면 '끝') > 내가 연애를 할 수 있을까요?
빵빵이: "될 놈은 된다. 그리고 너는 될 놈이다."
```

## 실행 방법

JDK 17 이상이 필요해요.

```bash
javac -d out src/*.java
java -cp out Main
```

`끝`을 입력하면 프로그램이 끝나요.

## 테스트

```bash
javac -d out src/*.java test/*.java
java -cp out BbangBbangiTest
```

## 파일 구조

| 파일 | 하는 일 |
|------|---------|
| `src/Main.java` | 고민을 입력받고 빵빵이 답을 출력하는 반복 |
| `src/BbangBbangi.java` | 답변 목록과 랜덤 뽑기 |
| `test/BbangBbangiTest.java` | JUnit 없이 돌아가는 테스트 |
