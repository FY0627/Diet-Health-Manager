create table if not exists sys_user (
    id bigint unsigned auto_increment comment '主键id',
    username varchar(50) not null comment '登录用户名',
    password_hash varchar(255) not null comment '密码哈希值',
    birthday date default null comment '出生日期',
    gender tinyint not null default 0 comment '性别(0:未知,1:男,2:女)',

    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '是否删除(0:未删除,1:已删除)',

    primary key (id),
    unique key uk_username (username)
) engine=innodb
  default charset=utf8mb4
  comment '用户表';

create table if not exists food (
    id bigint unsigned auto_increment comment '主键id',
    food_name varchar(100) not null comment '食物名称',
    calories decimal(6,2) not null comment '卡路里',
    protein decimal(6,2) not null comment '蛋白质',
    fat decimal(6,2) not null comment '脂肪',
    carbohydrate decimal(6,2) not null comment '碳水化合物',

    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '是否删除(0:未删除,1:已删除)',

    primary key (id),
    unique key uk_food_name (food_name)
) engine=innodb
  default charset=utf8mb4
  comment '食物表';

create table if not exists nutrition_goal (
    id bigint unsigned auto_increment comment '主键id',
    user_id bigint unsigned not null comment '用户id',
    target_calories decimal(6,2) not null comment '目标卡路里',
    target_protein decimal(6,2) not null comment '目标蛋白质',
    target_carbohydrate decimal(6,2) not null comment '目标碳水化合物',
    target_fat decimal(6,2) not null comment '目标脂肪',

    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '是否删除(0:未删除,1:已删除)',

    primary key (id),
    unique key uk_user_id (user_id),
    key idx_user_id (user_id)
) engine=innodb
  default charset=utf8mb4
  comment '营养目标表';

create table if not exists health_data (
    id bigint unsigned auto_increment comment '主键id',
    user_id bigint unsigned not null comment '用户id',
    height decimal(5,2) not null comment '身高(cm)',
    weight decimal(5,2) not null comment '体重(kg)',
    bmi decimal(6,2) comment 'BMI',
    bmr decimal(6,2) comment '基础代谢率',

    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '是否删除(0:未删除,1:已删除)', 
    
    primary key (id),
    unique key uk_user_id (user_id),
    key idx_user_id (user_id)
) engine=innodb
  default charset=utf8mb4
  comment '健康数据表';

create table if not exists diet_record (
    id bigint unsigned auto_increment comment '主键id',
    user_id bigint unsigned not null comment '用户id',
    food_id bigint unsigned comment '食物id',
    food_name_snapshot varchar(100) not null comment '食物名称快照',
    meal_type tinyint not null comment '餐别(0:早餐,1:午餐,2:晚餐,3:加餐)',
    quantity decimal(6,2) not null comment '摄入量',
    record_date date not null comment '记录日期',

    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '是否删除(0:未删除,1:已删除)',

    primary key (id),
    key idx_user_id (user_id),
    key idx_food_id (food_id),
    key idx_record_date (record_date),
    key idx_user_date_meal (user_id, record_date, meal_type)
) engine=innodb
  default charset=utf8mb4
  comment '饮食记录表';
