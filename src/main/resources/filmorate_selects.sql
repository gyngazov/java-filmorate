-- 10 самых популярных фильмов
select f.name
from film f
inner join likes l on
l.film_id=f.id
group by f.name
order by count(l.id) desc
limit 10;

-- общие друзья у двух пользователей с id 7 и 17
select u.name
from friend f1
inner join friend f2 on
f1.friend_id=f2.friend_id
inner join users u on 
u.id=f1.friend_id
where f1.user_id=7 and f2.user_id=17
