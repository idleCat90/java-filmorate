# java-filmorate
Template repository for Filmorate project.

# java-filmorate
**[Ссылка на ER diagram проекта](/filmorate-ER-diagram.png)**
![](/filmorate-ER-diagram.png)

Примеры запросов:

Получение пользователей:

SELECT *
FROM users;

Получение фильмов:

SELECT *
FROM films;

Получение списка друзей пользователя:

SELECT *
FROM friends
WHERE accepted = true
    AND user_id = %ID%;

Получение списка общих друзей:

SELECT DISTINCT friend_id
FROM (SELECT *
FROM friends
WHERE accepted = true
    AND user_id IN (%ID1%, %ID2%));

Получение списка самых популярных фильмов:

SELECT film_id,
    COUNT(user_id) AS likes_number
FROM likes
GROUP BY film_id
ORDER BY likes_number DESC
LIMIT 10;
