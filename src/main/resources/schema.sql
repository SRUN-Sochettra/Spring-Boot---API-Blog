CREATE TABLE IF NOT EXISTS users (
    user_id       serial        primary key,
    user_name     varchar(100)  not null,
    email         varchar(100)  not null unique,
    password      varchar(255)  not null,  -- FIXED: 100 too short for bcrypt
    token_version int           not null default 0,  -- FIXED: added default
    created_at    timestamp     not null default current_timestamp  -- FIXED: added default
);

CREATE TABLE IF NOT EXISTS posts (
    post_id     serial        primary key,
    title       varchar(100)  not null,
    description varchar(255)  not null,
    user_id     int           not null,
    created_at  timestamp     not null default current_timestamp,
    constraint fk_post_user foreign key (user_id)
        references users(user_id) on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS post_image (
    image_id  serial        primary key,
    image_url varchar(255),
    post_id   int           not null,
    constraint fk_image_post foreign key (post_id)
        references posts(post_id) on update cascade on delete cascade
);
CREATE TABLE IF NOT EXISTS comments (
    comment_id  serial        primary key,
    content     varchar(500)  not null,
    post_id     int           not null,
    user_id     int           not null,
    created_at  timestamp     not null default current_timestamp,
    constraint fk_comment_post foreign key (post_id)
        references posts(post_id) on update cascade on delete cascade,
    constraint fk_comment_user foreign key (user_id)
        references users(user_id) on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS post_likes (
    post_id     int           not null,
    user_id     int           not null,
    created_at  timestamp     not null default current_timestamp,
    primary key (post_id, user_id),
    constraint fk_like_post foreign key (post_id)
        references posts(post_id) on update cascade on delete cascade,
    constraint fk_like_user foreign key (user_id)
        references users(user_id) on update cascade on delete cascade
);
