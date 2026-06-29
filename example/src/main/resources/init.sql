drop table if exists backend_user cascade;
drop table if exists open_caller cascade;
drop table if exists permission cascade;
drop table if exists principal_role cascade;
drop table if exists role cascade;
drop table if exists role_permission cascade;

create table backend_user (
                              deleted boolean not null,
                              disabled boolean not null default false,
                              locked boolean not null default false,
                              created_at timestamp(6) with time zone,
                              created_by bigint,
                              id bigint not null,
                              last_updated_at timestamp(6) with time zone,
                              last_updated_by bigint,
                              username varchar(32) not null unique,
                              nick_name varchar(32),
                              uid varchar(128) not null unique,
                              password varchar(512) not null,
                              primary key (id)
);
create table open_caller (
                             enabled boolean not null default false,
                             created_at timestamp(6) with time zone,
                             created_by bigint,
                             id bigint not null,
                             last_updated_at timestamp(6) with time zone,
                             last_updated_by bigint,
                             ak varchar(64) not null unique,
                             name varchar(128) not null,
                             sk varchar(128) not null,
                             primary key (id)
);

create table permission (
                            builtin boolean not null default false,
                            disabled boolean not null default false,
                            created_at timestamp(6) with time zone,
                            created_by bigint,
                            id bigint not null,
                            last_updated_at timestamp(6) with time zone,
                            last_updated_by bigint,
                            code varchar(64) not null unique,
                            name varchar(64) not null,
                            remark varchar(255),
                            primary key (id)
);

create table principal_role (
                                principal_type smallint not null,
                                created_at timestamp(6) with time zone,
                                created_by bigint,
                                id bigint not null,
                                last_updated_at timestamp(6) with time zone,
                                last_updated_by bigint,
                                principal_id bigint not null,
                                role_id bigint not null,
                                primary key (id)
);

create table role (
                      builtin boolean not null default false,
                      disabled boolean not null default false,
                      owner_type smallint not null,
                      sort integer,
                      created_at timestamp(6) with time zone,
                      created_by bigint,
                      id bigint not null,
                      last_updated_at timestamp(6) with time zone,
                      last_updated_by bigint,
                      parent_id bigint,
                      code varchar(32) not null,
                      name varchar(32) not null,
                      remark varchar(255),
                      primary key (id)
);

create table role_permission (
                                 created_at timestamp(6) with time zone,
                                 created_by bigint,
                                 id bigint not null,
                                 last_updated_at timestamp(6) with time zone,
                                 last_updated_by bigint,
                                 permission_id bigint not null,
                                 role_id bigint not null,
                                 primary key (id)
);
