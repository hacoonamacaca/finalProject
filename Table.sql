--create database back
use back
go

--1-1�޲z��
create table admin (
id int identity(1,1) primary key,
account varchar(50) not null UNIQUE,
password varchar(100) not null,
name nvarchar(50) not null
);
go
--1-2�Ȥ�
create table [user](
id int identity(1,1) primary key,
email varchar(50) not null UNIQUE,
password varchar(100) not null,
name nvarchar(50) not null,
phone char(10),
sigup_date datetime,
last_login datetime,
is_active bit,
is_verify bit,
hide_until datetime
);
go
--1-3�\�U�֦���
create table owner(
id int identity(1,1) primary key,
email varchar(50) not null UNIQUE,
password varchar(100) not null,
name nvarchar(50) not null,
phone char(10),
sigup_date datetime,
last_login datetime,
is_active bit,
is_verify bit
);
go

--1-6�\�U
create table store(
id int identity(1,1) primary key,
owner_id int,
name nvarchar(50),
address nvarchar(50),
store_coords geography,
lng numeric(10,7),  --�g��
lat numeric(10,7),  --�n��
store_intro varchar(max),
photo varchar(max),  --�Ӥ��x�s�榡
is_open bit,
score float,
created_time datetime,
updated_time datetime,
is_active bit
foreign key (owner_id) references owner(id)
);
go

--2-1����
create table food(
id int primary key identity(1,1),
store_id int,
name nvarchar(100),
price int,
description nvarchar(100),
create_time datetime,
update_time datetime,
score float,
is_active bit,
stock int,
img_resouce    varchar(500),
foreign key (store_id) references store(id),
);
go

--3-1�\�U���O
create table category(
id int primary key identity(1,1),
name nvarchar(50)
);
go
--3-2��������
create table tag(
id int  primary key identity(1,1),
name nvarchar(50)
);
go

--4-1 �q�\���
create table [plan](
id int primary key identity(1,1),
name varchar(50),
price int,
valid_months int,
created_time datetime,
update_time datetime
);
go

--4-2 �u�f����
create table promotion(
id int primary key identity(1,1),
plan_id int,
store_id int,
tag_id int,
title nvarchar,
description nvarchar,
discount_type varchar,
discount_value varchar,
min_spend int,
start_time datetime,
end_time datetime,
created_time datetime,
updated_time datetime,
code varchar,
max_usage int,
user_usage_limit int,
status varchar(20),
foreign key (plan_id) references [plan](id),
foreign key (store_id) references store(id),
foreign key (tag_id) references tag(id),
);
go

--5-1 ���
create table tables(
id int primary key identity(1,1),
store_id int,
quantity int,
seats int,
is_active bit,
foreign key (store_id) references store(id)
);
go

--5-2�ɶ��q
create table time_slots(
id int primary key identity(1,1),
store_id int,
day Date,
start_time time ,
end_time time,
is_active bit ,
foreign key (store_id) references store(id)
);
go

--5-3�\�U�ɶ�����]�w
create table time_setting(
id int primary key identity(1,1),
store_id int,
interval int, --�w�����j�ɶ�
meal_time int default 90, -- ���\�ɶ�
foreign key (store_id) references store(id)
);
go

--5-4�q��
create table reservation(
id int primary key identity(1,1),
user_id int,
store_id int,
--���\�������\�ɶ�
reserved_date date,
reserved_time datetime,
guests int,--�H��
duration int,--���\�ɶ�
status nvarchar(50), --���A
content nvarchar(50),
created_at datetime,
updated_at datetime,
foreign key (user_id) references [user](id),
foreign key (store_id) references store(id)
);
go

--5-5�q��&���
create table reservation_tables(
reservation_id int,
tables_id int,
foreign key (reservation_id) references reservation(id),
foreign key (tables_id) references tables(id)
);
go

--6-1�q��
create table [order](
id int primary key identity(1,1),
user_id int,
store_id int,
promotion_id int,
total int,
status nvarchar(10),
create_time datetime,
content nvarchar(50),
pickup_time datetime,
foreign key (user_id) references [user](id),
foreign key (store_id) references store(id),
foreign key (promotion_id) references promotion(id)
);
go

--6-2�q�����
create table order_detail(
id int primary key identity(1,1),
order_id int,
food_id int,
name nvarchar(100),
quantity int,
price int,
sub_total int,
total int,
foreign key (order_id) references [order](id),
foreign key (food_id) references food(id)
);
go

--2-3�s�ճW�� (�ū�)
create table spec_group(
id int primary key identity(1,1),
store_id int ,
name nvarchar(100),
sort int,
foreign key (store_id) references store(id)
);
go

--2-4�W�� (�֦B�B�h�B)
create table spec(
id int primary key identity(1,1),
spec_group_id int,
name nvarchar(100),
price int,
min_select int,
max_select int,
sort int,
is_active bit default 1 ,
foreign key (spec_group_id) references spec_group(id)
);
go

--2-5����&�W��s��
create table food_spec_group(
food_id int,
spec_group_id int,
store_id int,
primary key(food_id,spec_group_id,store_id),
foreign key (food_id) references food(id),
foreign key (spec_group_id) references spec_group(id),
foreign key (store_id) references store(id)
);
go

--6-3 �q��Pspec
create table order_detail_spec(
id int primary key identity(1,1),
spec_id int
foreign key (spec_id) references spec(id)
);
go

--6-5 ����
create table comment(
id int primary key identity(1,1),
order_id int,
user_id int,
store_id int,
content nvarchar(500),
score int,
create_time datetime,
reply nvarchar(500),
reply_update_time datetime,
is_hidden bit default 0,
foreign key (order_id) references [order](id),
foreign key (user_id) references [user](id),
foreign key (store_id) references store(id)
)
go

--6-6���׷Ӥ�
create table comment_img(
id int primary key identity(1,1),
comment_id int,
resource varchar(500),
sort int,
foreign key (comment_id) references comment(id)
)
go

--6-7���|����
create table report_type(
id int primary key identity(1,1),
type nvarchar(100),
description nvarchar(100)
);
go

--6-8���|
create table report(
id int primary key identity(1,1),
report_type_id int,
comment_id int,
submitter_id int,
submitter_type varchar(50),
status varchar(50),
repoart_date datetime,
foreign key (report_type_id) references report_type(id),
foreign key (comment_id) references comment(id)
);
go

--1-4�q�l�l������ user
create table user_token(
id int identity(1,1) primary key,
user_id int,
email varchar(50) not null UNIQUE,
verify_code varchar(50) not null,
expiration datetime not null,
is_used bit default 0 --�w�]false
foreign key (user_id) references [user](id)
);
go

--1-5�q�l�l������ owner
create table owner_token(
id int identity(1,1) primary key,
owner_id int,
email varchar(50) not null UNIQUE,
verify_code varchar(50) not null,
expiration datetime not null,
is_used bit default 0, --�w�]false
foreign key (owner_id) references owner(id)
);
go

--1-7��~�ɶ�
create table open_hour(
id int identity(1,1) primary key,
store_id int,
day tinyint,
open_time time,
close_time  time,
foreign key (store_id) references store(id)
);
go

--�S��𰲤�ɶ�
create table special_hours(
id int identity(1,1) primary key,
store_id int,
date date,
open_time time,
close_time time,
is_close bit,
foreign key (store_id) references store(id)
);
go

--1-9
create table store_img(
id int primary key identity(1,1),
store_id int ,
resource varchar(500),
display_sort int,
alt_text nvarchar(100)
foreign key (store_id) references store(id),
);
go

--2-6 �������g
create table liked_food(
user_id	int,
food_id	int,
order_detail_id	int,
is_liked   bit,
updated_time	datetime,
foreign key (user_id) references [user](id),
foreign key (food_id) references food(id),
foreign key (order_detail_id) references order_detail(id)
)
go

--3-6���òM��(�\�U)
create table favorite_store(
user_id int,
store_id int,
primary key( user_id,store_id),
foreign key (user_id) references [user](id),
foreign key (store_id) references store(id)
);
go

--3-7���òM��(����)
create table favorite_food(
user_id int,
food_id int,
primary key( user_id,food_id),
foreign key (user_id) references [user](id),
foreign key (food_id) references food(id)
);
go

--3-8�Ȥ�j�M����
create table category_searched(
user_id int,
store_id int,
counter int
primary key( user_id,store_id),
foreign key (user_id) references [user](id),
foreign key (store_id) references store(id)
);
go

--3-9��������
create table web_recom(
id int primary key identity(1,1),
tag nvarchar(50) not null
);
go

--3-10�d�߫�s�W���s
create table search_tag(
id int primary key identity(1,1),
user_id int,
tag nvarchar(50),
create_time datetime default getDate()
);
go

--4-3�u�f�T���q��
create table notification(
id int primary key identity(1,1),
user_id int,
promotion_id int,
created_time int,
is_read bit,
read_time datetime,
foreign key (user_id) references [user](id)
);
go

--4-4 �ϥΪ̭q�\����
create table sub_record(
id int primary key identity(1,1),
user_id int,
plan_id int,
start_time datetime,
end_time datetime,
amount int,
method int,
paid_time datetime,
foreign key (user_id) references [user](id),
foreign key (plan_id) references [plan](id)
);
go

--6-4 ���b����
create table payment(
id int primary key identity(1,1),
order_id int,
method nvarchar(50),
transaction_id varchar(50),
total int,
is_paid bit,
paid_time datetime
foreign key (order_id) references [order](id)
)
go

--6-9 �������O
create table food_class(
id int primary key identity(1,1),
store_id int,
name nvarchar(100) not null,
description nvarchar(100),
sort int
foreign key (store_id) references store(id)
);
go

--3-3�h��h �\�U&�\�U���O
create table store_category(
store_id int,
category_id int,
primary key( store_id,category_id),
foreign key (store_id) references store(id),
foreign key (category_id) references category(id)
);
go

--3-4�h��h ��������
create table food_tag(
food_id int,
tag_id int,
primary key( food_id,tag_id),
foreign key (food_id) references food(id),
foreign key (tag_id) references tag(id)
);
go

--3-5�h��h�Ȥ�ﭹ�����ߦn
create table user_tag(
user_id int,
tag_id int,
counter int default 0,
is_custom bit default 0,-- �D�Ȥ�w�] 
primary key( user_id,tag_id),
foreign key (user_id) references [user](id),
foreign key (tag_id) references tag(id)
);
go

--6-10�������O���p��
create table food_class_id(
food_id int ,
food_class_id int,
store_id int,
sort int,
foreign key (food_id) references food(id),
foreign key (food_class_id) references food_class(id),
foreign key (store_id) references store(id)
);
go
