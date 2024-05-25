drop database if exists wins_pool_test;
create database wins_pool_test;
use wins_pool_test;

-- create tables and relationships

create table user (
	user_id int primary key auto_increment,
    first_name varchar(25) not null,
    last_name varchar(50) not null,
    email varchar(250) not null unique,
    `password` varchar(250) not null unique,
    is_deleted bit not null default 0,
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

create table team (
	team_id int primary key auto_increment,
	location varchar(100) not null,
    team_name varchar(50) not null,
    color varchar(15) not null,
    alt_color varchar(15) not null
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
