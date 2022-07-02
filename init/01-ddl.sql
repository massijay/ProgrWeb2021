create table trip_recorder.users
(
    id       int auto_increment
        primary key,
    username varchar(255) not null,
    password varchar(255) not null,
    email    varchar(255) not null,
    constraint users_email_uindex
        unique (email),
    constraint users_id_uindex
        unique (id),
    constraint users_username_uindex
        unique (username)
);

create table trip_recorder.sessions
(
    token     varchar(255) not null
        primary key,
    user_id   int          not null,
    expire_at timestamp    not null,
    constraint sessions_token_uindex
        unique (token),
    constraint sessions_users_id_fk
        foreign key (user_id) references trip_recorder.users (id)
            on delete cascade
);

create table trip_recorder.trips
(
    id             int auto_increment
        primary key,
    user_id        int          not null,
    trip_name      varchar(255) not null,
    trip_date      timestamp    not null,
    transport_type varchar(255) not null,
    notes          text         null,
    constraint trips_users_id_fk
        foreign key (user_id) references trip_recorder.users (id)
            on delete cascade
);

create table trip_recorder.geopoints
(
    id          int auto_increment
        primary key,
    trip_id     int             not null,
    latitude    double          not null,
    longitude   double          not null,
    recorded_at timestamp       not null,
    label       varchar(255)    null,
    constraint geopoints_trips_id_fk
        foreign key (trip_id) references trip_recorder.trips (id)
            on delete cascade
);