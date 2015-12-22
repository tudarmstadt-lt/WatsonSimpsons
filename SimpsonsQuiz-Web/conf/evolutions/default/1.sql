# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table squiz_question (
  id                        varchar(255) not null,
  squiz_question_list_id    varchar(255) not null,
  correct_answer            varchar(255),
  false_answer1             varchar(255),
  false_answer2             varchar(255),
  false_answer3             varchar(255),
  question_id               integer,
  question                  varchar(255),
  constraint pk_squiz_question primary key (id))
;

create table squiz_question_list (
  id                        varchar(255) not null,
  question_iterator         integer,
  constraint pk_squiz_question_list primary key (id))
;

create table user (
  id                        integer not null,
  username                  varchar(255),
  api_key                   varchar(255),
  constraint pk_user primary key (id))
;

create sequence squiz_question_seq;

create sequence squiz_question_list_seq;

create sequence user_seq;

alter table squiz_question add constraint fk_squiz_question_squiz_questi_1 foreign key (squiz_question_list_id) references squiz_question_list (id) on delete restrict on update restrict;
create index ix_squiz_question_squiz_questi_1 on squiz_question (squiz_question_list_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists squiz_question;

drop table if exists squiz_question_list;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists squiz_question_seq;

drop sequence if exists squiz_question_list_seq;

drop sequence if exists user_seq;

