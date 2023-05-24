
create table if not exists ratings (
  id int auto_increment primary key,
  name varchar(8) unique not null
);
create table if not exists films (
    id int auto_increment primary key,
    name varchar(64) not null,
    description varchar(200),
    release_date date not null,
    duration int not null,
    rating_id int references ratings(id) not null,
    check(duration > 0),
    unique(name, description, release_date, duration, rating_id) -- без повторов
);
create table if not exists users (
    id int auto_increment primary key,
    email varchar(32) unique not null,
    login varchar(16) not null,
    name varchar(64) not null,
    birthday date not null,
    check(birthday <= cast(now() as date)) -- исключаем не родившихся пользователей
);
create table if not exists likes (
    id int auto_increment primary key,
    film_id int references films(id) not null,
    user_id int references users(id) not null,
    unique(film_id, user_id) -- исключаем повторные лайки
);
create table if not exists friends (
    id int auto_increment primary key,
    user_id int references users(id) not null,
    friend_id int references users(id) not null,
    is_accepted boolean default false,  -- true - дружба подтверждена
                                        -- false - дружба отклонена
                                        -- по умолчанию дружба не подтверждена
    unique(user_id, friend_id) -- исключаем повторы
);
create table if not exists genres (
  id int auto_increment primary key,
  name varchar(32) unique not null
);
create table if not exists film_genres (
    id int auto_increment primary key,
    film_id int references films(id) not null,
    genre_id int references genres(id) not null,
    unique(film_id, genre_id) -- исключаем повторы
);
