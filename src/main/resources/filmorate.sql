
create table ratings (
  id smallserial primary key,
  name varchar(8) unique not null
);
create table films (
    id serial primary key,
    name varchar(64) not null,
    description varchar(200),
    release_date date not null,
    duration smallint not null,
    rating_id smallint references rating(id),
    check(duration > 0)
);
create table users (
    id serial primary key,
    email varchar(32) unique not null,
    login varchar(16) not null,
    name varchar(16) not null,
    birthday date not null,
    check(birthday <= cast(now() as date)) -- исключаем не родившихся пользователей
);
create table likes (
    id serial primary key,
    film_id int references film(id) not null,
    user_id int references users(id) not null,
    unique(film_id, user_id) -- исключаем повторные лайки
);
create table friends (
    id serial primary key,
    user_id int references users(id) not null,
    friend_id int references users(id) not null,
    is_accepted boolean default false,  -- true - дружба подтверждена
                                        -- false - дружба отклонена
                                        -- по умолчанию дружба не подтверждена
    unique(user_id, friend_id) -- исключаем повторы
);
create table genres (
  id smallserial primary key,
  name varchar(32) unique not null
);
create table film_genres (
    id smallserial primary key,
    film_id int references film(id) not null,
    genre_id smallint references genre(id) not null,
    unique(film_id, genre_id) -- исключаем повторы
);
