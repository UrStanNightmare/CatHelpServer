# Cat help server
____

### Небольшой бэкенд для хранения и получения заметок и событий.

Написал для себя, чтобы можно было иметь постоянный доступ к истории лечения кота, необходимым процедурам и событиям.

### Возможности:
* Сохранение описания события, его даты и статуса готовности (сделано/ не сделано)
* Получение событий по дате (выдаются события на месяц) для отображения на каком-нибудь календаре на любом клиенте.
* Изменение статуса выполнения задачи
* Удаление задачи

В данный момент используется бд h2 для хранения данных. Логи записываются в папку logs.

Кодировка UTF-8.

### Запуск:
Имеется docker файл для сборки образа и запуска контейнера.

Стоит указать переменные:
* DB_URL - путь к бд. Пока не особо на что-то повлияет т.к используется H2
* DB_USER - пользователь бд
* DB_PWD - пароль пользователя
* SERVER_PORT - порт сервера. Стандартный - 8080
* AVAILABLE_ORIGIN - origin с которого можно будет обращаться к api. Для локального использования можно не менять.

### Клиенты:
* Небольшой web клиент для взаимодействия - [Cat-help-client](https://github.com/UrStanNightmare/cat-task-manager)

### Планы:
* Переход на PostgreSQL для возможности добавления удобных функций поиска данных
* Подключения Spring Security для блокировки неавторизированного доступа. (Пока советую закрывать используя base auth на прокси сервере)
* Добавление возможности загрузки файлов, таких как картинки и pdf