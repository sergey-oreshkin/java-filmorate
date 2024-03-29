# java-filmorate
##### Yandex practicum course project

### Правила репозитория
(могут дополняться)

#### Стиль кода
- Стараемся соблюдать стиль описанный [здесь](https://habr.com/ru/post/112042/),
кроме пункта про поля на букву "m" или "s", это НЕ используем.   
- Названия без сокращений, на английском языке, без грамматических ошибок
- Язык комметариев и доков - **русский**
- Если пишем новый класс **обязательно javadoc** класса с описанием 
назначения и автором
- Если пишем новый публичный метод в уже существующем классе **обязательно javadoc**
метода с описанием назначения и автором
- Аннотации упорядочиваем по количеству символов, сначала короткие
- Внутри класса сначала идут объявления констант, потом полей, 
потом публичные методы, потом все остальные
- Не допускается оставлять закомментированный код или неиспользуемые импорты

#### Разработка
- Разрабатывать свою часть всегда в отдельной ветке
- Язык названий веток - **английский**
- Название ветки должно отражать суть работы в ней
- Если для вашей части требуется изменение структуры БД, то сразу создаем пулл реквест
с изменениями schema.sql, и только после его одобрения, продолжаем работу над кодом
- Внесение изменений в существующие классы требует обсуждения.
Нельзя просто так взять и поменять что то в уже написанном коде

#### Работа с гитхабом
- Каждый пулл реквест требует одобрения 2-х ревьюеров. Один из которых всегда тимлид.
- Язык комментария к коммиту - **русский**
- Комментарий к коммиту должен отражать внесенные изменения
- Перед коммитом проверяйте командой "git status" какие файлы вы изменили,
чтобы не допускать случайных измений в уже существующих файлах
- При ревью также обращайте внимание на соблюдение стиля кода,
принятого в этом репозитории
- 
##### Важно
Как начать работу над проектом. 
- Клонируете себе этот репозиторий.
- Переключаетесь в ветку develop
- Из нее создаете ветку указанную в карточке с вашей фичей (которая в notion)
- Делаете **git push origin [название новой ветки]**
- Из этой новой ветки создаете еще одну у которой к названию новой ветки через дефис
добавляете какую-нибудь букву
Пример. Вы взяли фичу про отзывы. Склонировали, переключились в develop, 
создали как указано ветку add-reviews. Из нее создаете еще одну ветку,
например add-reviews-a. И пишете свой код в ней
Это позволит в дальнейшем делится своим кодом с другими не задевая рабочую ветку фичи,
а также разделить или делегировать свою задачу другому,
а также создавать промежуточный ПР из ветки разработки в рабочую ветку фичи 