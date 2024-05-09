create table T_BUSI_ORDER
(
    ID          varchar(64)    not null comment '订单ID'
        primary key,
    USER_ID     varchar(64)    null comment '用户ID',
    SELLER_ID   varchar(64)    null comment '商家ID',
    SKU_ID      varchar(64)    null comment '商品ID',
    AMOUNT      int            null comment '购买数量',
    MONEY       decimal(10, 2) null comment '购买金额',
    PAY_TIME    datetime       null comment '支付时间',
    PAY_STATUS  varchar(2)     null comment '支持状态',
    DEL_FLAG    int default 0  null comment '删除标志',
    CREATE_BY   varchar(64)    null comment '创建人',
    CREATE_TIME datetime       null comment '创建时间',
    UPDATE_BY   varchar(64)    null comment '修改人',
    UPDATE_TIME datetime       null comment '修改时间'
)  comment '订单表';

create index T_BUSI_ORDER_SELLER_ID_DEL_FLAG_CREATE_TIME_index
    on T_BUSI_ORDER (SELLER_ID, DEL_FLAG, CREATE_TIME);

create index T_BUSI_ORDER_USER_ID_DEL_FLAG_CREATE_TIME_index
    on T_BUSI_ORDER (USER_ID, DEL_FLAG, CREATE_TIME);

create table T_STATS_ORDER
(
    ID          varchar(64) not null
        primary key,
    USER_ID     varchar(64)   null comment '买家ID',
    USER_NAME   varchar(200)   null comment '买家姓名',
    SELLER_ID   varchar(64)   null comment '卖家ID',
    SELLER_NAME varchar(200)   null comment '卖家姓名',
    COUNT       int         null comment '订单数',
    HOUR        int         null comment '时间段(预留,0~23)',
    STATS_DATE  datetime    null comment '统计日期，2024-05-01'
)  comment '订单统计表';

create index T_STATS_ORDER_SELLER_ID_STATS_DATE_index
    on T_STATS_ORDER (SELLER_ID, STATS_DATE);

create index T_STATS_ORDER_USER_ID_STATS_DATE_index
    on T_STATS_ORDER (USER_ID, STATS_DATE);