drop database if exists wins_pool_test;
create database wins_pool_test;
use wins_pool_test;

-- create tables and relationships

create table team (
	team_id int primary key auto_increment,
	location varchar(100) not null,
    team_name varchar(50) not null,
    color varchar(15) not null,
    alt_color varchar(15) not null,
    league varchar(15) null
);

create table user (
	user_id int primary key auto_increment,
    first_name varchar(25) not null,
    last_name varchar(50) not null,
    email varchar(250) not null unique,
    `password` varchar(250) not null unique,
    is_deleted bit not null default 1,
    is_admin bit not null default 1,
    money_owed decimal(10,2) not null,
    team_id int not null,
    constraint fk_user_team_id
        foreign key (team_id)
        references team(team_id)
);

create table year (
	year_id int primary key auto_increment,
    year_number int not null unique
);


create table draft_pick(
	draft_pick_id int primary key auto_increment,
    pick_number int not null
);

create table draft (
	draft_id int primary key auto_increment,
    user_id int not null,
    year_id int not null,
    draft_pick_id int not null,
    team_id int null,
    constraint fk_draft_user_id
		foreign key (user_id)
        references user(user_id),
	constraint fk_draft_year_id
		foreign key (year_id)
        references year(year_id),
	constraint fk_draft_draft_pick_id
		foreign key (draft_pick_id)
        references draft_pick(draft_pick_id),
	constraint fk_draft_team_id
		foreign key (team_id)
        references team(team_id)
);

delimiter //
create procedure set_known_good_state()
begin

    delete from draft;
    alter table draft auto_increment = 1;
    delete from user;
    alter table user auto_increment = 1;
    delete from year;
    alter table year auto_increment = 1;
    delete from team;
    alter table team auto_increment = 1;
    delete from draft_pick;
    alter table draft_pick auto_increment = 1;

    insert into year(year_id, year_number) values
        (1, 2023),
        (2, 2024),
        (3, 2025);

    insert into team (team_id, location, team_name, color, alt_color, league) values
        (1, 'San Francisco', '49ers', 'red', 'gold', 'NFC'),
        (2, 'New England', 'Patriots', 'blue', 'white', 'AFC'),
        (3, 'Chicago', 'Bears', 'blue', 'gold', 'NFC'),
        (4, 'Denver', 'Broncos', 'blue', 'orange', null);

    insert into draft_pick (draft_pick_id, pick_number) values
        (1,1),
        (2,2),
        (3,3),
        (4,4),
        (5,5);

    insert into user (user_id, first_name, last_name, email, password, is_deleted, is_admin, money_owed, team_id ) values
        (1, 'Luke', 'Skywalker', 'lastjedi@lightside.com', 'yodayomama', 1, 0, 0.00, 1),
        (2, 'Cal', 'Kestis', 'survivor@lightside.com', 'redheadjedhead', 1, 1, 0.00, 2),
        (3, 'Anakin', 'Skywalker', 'darthvader@sith.com', 'iamyourfather', 0, 1, 0.00, 3);

    insert into draft (draft_id, user_id, year_id, draft_pick_id, team_id) values
        (1, 1, 1, 1, 2),
        (2, 2, 1, 2, 1),
        (3, 2, 1, 3, 3),
        (4, 1, 2, 1, 3),
        (5, 3, 2, 2, 2);
end //

delimiter ;
