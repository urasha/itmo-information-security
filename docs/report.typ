#set document(title: "Разработка защищенного REST API с интеграцией в CI/CD", author: "Нестеров Владислав Алексеевич")
#set text(lang: "ru", size: 13pt)

#set par(justify: true, leading: 0.65em)
#set heading(numbering: "1.")

#align(center)[
  Федеральное государственное автономное образовательное учреждение #linebreak()
  высшего образования

  «Национальный исследовательский университет ИТМО»
]

#v(32mm)
#align(center)[
  #text(size: 15pt, weight: "bold")[Работа № 1]

  #text(size: 15pt, weight: "bold")[«Разработка защищенного REST API с интеграцией в CI/CD»]

  по дисциплине «Информационная безопасность»
]

#v(45mm)
#grid(
    columns: (2fr, 3fr),
    column-gutter: 1cm,

      [],
      [
        #align(right, [
        *Преподаватель:* \
        Маркина Татьяна Анатольевна \
        #v(2mm)
        *Выполнил:* \
        Нестеров Владислав Алексеевич \
        #v(2mm)
        *Группа:* \
        P3410
      ])
      ]
)


#v(54mm)
#align(center)[Санкт-Петербург 2026]

#pagebreak()

#set page(
  paper: "a4",
  margin: (top: 20mm, bottom: 20mm, left: 25mm, right: 15mm),
  numbering: "1",
)

= Ссылка на публичный репозиторий

#link("https://github.com/urasha/itmo-information-security")[https://github.com/urasha/itmo-information-security]

= Текст README.md

== Лабораторная работа № 1: защищённое REST API

Учебное REST API заметок для демонстрации базовых мер защиты backend-приложения. Проект реализован на Java 21 и Spring Boot 4.1.1, собирается Maven и использует файловую базу данных H2.

=== Запуск

Для запуска нужны JDK 21 и доступ к Maven Central. В PowerShell выполните:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:LAB1_JWT_SECRET = [guid]::NewGuid().ToString('N') + [guid]::NewGuid().ToString('N')
$env:LAB1_DEMO_ALICE_PASSWORD = 'AliceDemo2026!'
$env:LAB1_DEMO_BOB_PASSWORD = 'BobDemo2026!'
$env:SPRING_PROFILES_ACTIVE = 'demo'
.\mvnw.cmd spring-boot:run
```

Приложение будет доступно по адресу `http://127.0.0.1:8080`. Профиль `demo` создаёт пользователей `alice` и `bob`; их пароли задаются переменными окружения, а в H2 сохраняются только BCrypt-хэши.

=== API

#table(
  columns: (0.8fr, 1.1fr, 1.2fr, 2.5fr),
  inset: 6pt,
  stroke: 0.5pt + gray,
  table.header([*Метод*], [*Путь*], [*Доступ*], [*Назначение*]),
  [`POST`], [`/auth/login`], [открытый], [Принимает логин и пароль, возвращает JWT],
  [`GET`], [`/api/data`], [Bearer JWT], [Возвращает заметки текущего пользователя],
  [`POST`], [`/api/data`], [Bearer JWT], [Создаёт заметку текущего пользователя],
)

Для входа отправьте запрос:

```http
POST /auth/login
Content-Type: application/json

{"username":"alice","password":"AliceDemo2026!"}
```

Из ответа возьмите `accessToken` и передавайте его в защищённые запросы:

```http
Authorization: Bearer <accessToken>
```

Для создания заметки отправьте:

```http
POST /api/data
Authorization: Bearer <accessToken>
Content-Type: application/json

{"title":"Заголовок","content":"Текст заметки"}
```

`GET /api/data` возвращает массив заметок владельца токена. Без действительного JWT защищённые методы возвращают `401 Unauthorized`.

Готовые запросы находятся в #link("https://github.com/urasha/itmo-information-security/blob/main/postman/lab1.postman_collection.json")[`postman/lab1.postman_collection.json`]. Вместе с коллекцией можно импортировать окружение #link("https://github.com/urasha/itmo-information-security/blob/main/postman/local.postman_environment.json")[`postman/local.postman_environment.json`] и заполнить переменные `alicePassword` и `bobPassword`.

=== Реализованные меры защиты

==== Защита от SQL-инъекций

Доступ к данным реализован через Spring Data JPA и Hibernate. Методы `UserRepository.findByUsername` и `NoteRepository.findAllByOwnerIdOrderByCreatedAtDesc` передают пользовательские значения как связанные параметры. Строки из запроса не объединяются с SQL или JPQL, поэтому введённые данные не могут изменить структуру запроса.

==== Защита от XSS

Пользовательские поля `title` и `content` экранируются функцией `HtmlUtils.htmlEscape` при формировании response DTO. HTML-символы возвращаются как текст и не интерпретируются браузером как разметка или сценарий. Исходное значение хранится в базе без повторного экранирования.

==== Аутентификация и контроль доступа

Пароли хранятся только как BCrypt-хэши. После успешного входа сервер выдаёт JWT сроком действия 15 минут. Spring Security проверяет HS256-подпись, срок действия, издателя и аудиторию. Секрет подписи поступает из переменной окружения `LAB1_JWT_SECRET` и не хранится в репозитории.

Идентификатор владельца заметки берётся из проверенного JWT. Клиент не может указать владельца самостоятельно, а запрос списка фильтрует записи по текущему пользователю.

=== SAST и SCA в GitHub Actions

==== SAST: SpotBugs и Find Security Bugs

Проверка завершилась успешно, SpotBugs не обнаружил ошибок или предупреждений.

#figure(
  image("images/sast-report.png", width: 100%),
  caption: [Успешный SAST-анализ в GitHub Actions],
)

==== SCA: OWASP Dependency-Check

Проверка завершилась успешно, уязвимые компоненты не были обнаружены.

#figure(
  image("images/sca-report.png", width: 100%),
  caption: [Успешный SCA-анализ в GitHub Actions],
)

#figure(
  image("images/sca-report-details.png", width: 92%),
  caption: [Сводка отчёта OWASP Dependency-Check],
)

= Ссылка на последний успешный запуск pipeline

#link("https://github.com/urasha/itmo-information-security/actions/runs/34751476757")[https://github.com/urasha/itmo-information-security/actions/runs/34751476757]
