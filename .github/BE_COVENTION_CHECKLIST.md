# 백엔드 컨벤션 리뷰 체크리스트

이 문서는 [BE 코드 컨벤션 Wiki](https://github.com/woowacourse-teams/2025-moitz/wiki/%5BBE%5D-%EC%BD%94%EB%93%9C-%EC%BB%A8%EB%B2%A4%EC%85%98)를 기반으로 작성된 코드 리뷰 체크리스트입니다.

## 📋 목차
- [패키지 구조](#패키지-구조)
- [코드 스타일](#코드-스타일)
- [클래스 구조](#클래스-구조)
- [메서드 작성](#메서드-작성)
- [테스트 코드](#테스트-코드)

---

## 패키지 구조

### ✅ 계층형 아키텍처 준수
- [ ] **infrastructure**: 영속성(persistence), 외부 클라이언트(client) 관련 코드
- [ ] **application**: 서비스(service), DTO 관련 코드
- [ ] **domain**: 모델(model), 리포지토리(repository) 인터페이스
- [ ] **ui**: 컨트롤러(controller) 관련 코드
- [ ] **common**: 설정(config), 인터셉터(interceptor), 예외 핸들러(exception handler)

---

## 코드 스타일

### ✅ 기본 규칙
- [ ] **Google Java Style Guide**를 기본으로 준수하고 있는가?

### ✅ Import 정리
- [ ] static import가 일반 import보다 먼저 위치하는가?
- [ ] 와일드카드(`*`) import를 사용하지 않았는가?
- [ ] import 개수 임계값이 999로 설정되어 있는가? (IntelliJ 설정)

### ✅ 중괄호 및 공백
- [ ] 클래스 선언 전후로 빈 줄이 있는가?
- [ ] 여는 중괄호(`{`)와 닫는 중괄호(`}`) 전후 공백이 적절한가?

---

## 클래스 구조

### ✅ 필드 순서
- [ ] 정적 상수(static constants)가 가장 먼저 선언되었는가?
- [ ] 인스턴스 변수가 정적 상수 다음에 선언되었는가?
- [ ] 정적 상수와 인스턴스 변수 사이에 빈 줄이 하나 있는가?

### ✅ final 키워드 사용
- [ ] 메서드 파라미터에 `final` 키워드를 사용했는가?
- [ ] 재할당되지 않는 지역 변수에 `final` 키워드를 사용했는가?
- [ ] 인터페이스 메서드 시그니처에는 `final`을 생략했는가?

### ✅ 메서드 순서
메서드가 다음 순서로 정렬되어 있는가?

1. [ ] 생성자(Constructor)
2. [ ] public 메서드 (호출 순서 또는 논리적 흐름 순)
3. [ ] private 헬퍼 메서드 (public 메서드를 지원)
4. [ ] Getter 메서드
5. [ ] `equals()` & `hashCode()`

---

## 메서드 작성

### ✅ 파라미터 줄바꿈
다음 경우에 파라미터를 줄바꿈 했는가?

- [ ] 파라미터가 4개 이상인 경우
- [ ] 파라미터가 2~3개이면서 줄 길이가 120자(공백 포함)를 초과하는 경우

**예시:**
```java
// 4개 이상 파라미터 - 줄바꿈
public void someMethod(
        String param1,
        String param2,
        String param3,
        String param4
) {
    // ...
}

// 2~3개 파라미터, 120자 초과 - 줄바꿈
public void anotherMethod(
        String veryLongParameterName1,
        String veryLongParameterName2
) {
    // ...
}
```

---

## 테스트 코드

### ✅ Given-When-Then 패턴
- [ ] 테스트 메서드가 **Given-When-Then** 패턴을 따르는가?
- [ ] 각 섹션에 주석(`// given`, `// when`, `// then`)이 명확하게 표시되어 있는가?

### ✅ 메서드 이름 및 DisplayName
- [ ] 테스트 메서드 이름은 간단한 영어로 작성되었는가?
- [ ] `@DisplayName` 어노테이션에 상세한 한글 설명이 포함되어 있는가?

**예시:**
```java
@DisplayName("유효한 사용자 정보로 회원가입 시 성공한다")
@Test
void registerUser() {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest("user@example.com", "password123");

    // when
    User user = userService.register(request);

    // then
    assertThat(user).isNotNull();
    assertThat(user.getEmail()).isEqualTo("user@example.com");
}
```

---

## 참고 자료
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Moitz BE 코드 컨벤션 Wiki](https://github.com/woowacourse-teams/2025-moitz/wiki/%5BBE%5D-%EC%BD%94%EB%93%9C-%EC%BB%A8%EB%B2%A4%EC%85%98)
