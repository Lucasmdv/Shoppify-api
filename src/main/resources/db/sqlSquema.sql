create table public.categories
(
    id      serial
        primary key,
    img_url varchar(200),
    name    varchar(50) not null
        unique
);

alter table public.categories
    owner to postgres;

create table public.users
(
    user_date_of_registration date,
    user_dni                  varchar(8)  not null,
    user_id                   bigserial
        primary key,
    user_first_name           varchar(20) not null,
    user_last_name            varchar(20) not null,
    user_phone                varchar(20) not null,
    user_img                  varchar(255)
);

alter table public.users
    owner to postgres;

create table public.credentials
(
    id       bigserial
        primary key,
    user_id  bigint
        unique
        constraint fkiqlblk63xq1ylv3hhuoj6mdrq
            references public.users,
    email    varchar(255) not null
        unique,
    password varchar(100) not null,
    username varchar(50) not null
);

alter table public.credentials
    owner to postgres;

create table public.permits
(
    id     bigserial
        primary key,
    code   varchar(100) not null
        unique,
    permit varchar(255) not null
        constraint permits_permit_check
            check ((permit)::text = ANY
        ((ARRAY ['READ'::character varying, 'WRITE'::character varying, 'DELETE'::character varying, 'ADMIN'::character varying, 'USER'::character varying, 'MANAGE_USERS'::character varying, 'MANAGE_ROLES'::character varying, 'GENERATE_REPORTS'::character varying])::text[]))
    );

alter table public.permits
    owner to postgres;

create table public.products
(
    discount_percentage numeric(5, 2)  default 0,
    price               numeric(10, 2) default 0 not null,
    unit_price          numeric(10, 2) default 0 not null,
    id                  bigserial
        primary key,
    sold_quantity       bigint         default 0,
    stock_quantity      bigint         default 0 not null,
    barcode             varchar(12) not null
        unique,
    brand               varchar(50) not null,
    img_url             varchar(255),
    name                varchar(50) not null
        unique,
    sku                 varchar(12)   default '-1'::integer not null,
    description         varchar(500)
);

alter table public.products
    owner to postgres;

create table public.products_categories
(
    category_id integer not null
        constraint fkqt6m2o5dly3luqcm00f5t4h2p
            references public.categories,
    product_id  bigint  not null
        constraint fktj1vdea8qwerbjqie4xldl1el
            references public.products,
    primary key (category_id, product_id)
);

alter table public.products_categories
    owner to postgres;

create table public.providers
(
    active        boolean default true not null,
    provider_id   bigserial
        primary key,
    tax_id        varchar(100)         not null
        unique,
    business_name varchar(255)         not null
        unique,
    contact_name  varchar(100)         not null,
    email         varchar(255)         not null
        unique,
    phone         varchar(255)
        unique,
    tax_address   varchar(255)         not null
);

alter table public.providers
    owner to postgres;

create table public.products_providers
(
    product_id  bigint not null
        constraint fk4fdscy4q1eetuait5y2108x1p
            references public.products,
    provider_id bigint not null
        constraint fk11eser9xhq5umo43p4mfj7f06
            references public.providers,
    primary key (product_id, provider_id)
);

alter table public.products_providers
    owner to postgres;

create table public.revinfo
(
    rev      integer not null
        primary key,
    revtstmp bigint
);

alter table public.revinfo
    owner to postgres;

create table public.details_transactions_aud
(
    rev        integer not null
        constraint fk2a31tsmloyyqxcttv3b8cqli
            references public.revinfo,
    revtype    smallint,
    id         bigint  not null,
    product_id bigint,
    primary key (rev, id)
);

alter table public.details_transactions_aud
    owner to postgres;

create table public.purchases_aud
(
    rev            integer not null
        constraint fkblj12nvk1f77j0e81a8q99jyt
            references public.revinfo,
    revtype        smallint,
    unit_price     numeric(19, 2),
    id             bigint  not null,
    provider_id    bigint,
    transaction_id bigint,
    primary key (rev, id)
);

alter table public.purchases_aud
    owner to postgres;

create table public.roles
(
    id          bigserial
        primary key,
    name        varchar(50) not null
        unique,
    description varchar(255)
);

alter table public.roles
    owner to postgres;

create table public.credentials_roles
(
    credential_id bigint not null
        constraint fks82q2vdge7lkyhinaaixrfrg0
            references public.credentials,
    role_id       bigint not null
        constraint fkrklwpb8s4ra4q6gnk0nnpl3rh
            references public.roles,
    primary key (credential_id, role_id)
);

alter table public.credentials_roles
    owner to postgres;

create table public.role_permits
(
    permit_id bigint not null
        constraint fkdnv3lj1j2e2brklx3e2t32mk3
            references public.permits,
    role_id   bigint not null
        constraint fk2rptwphd2txfpsqudgsv8mdd1
            references public.roles,
    primary key (permit_id, role_id)
);

alter table public.role_permits
    owner to postgres;

create table public.sales_aud
(
    rev            integer not null
        constraint fkhmuff9a34p7hfmyuq6sf2hpcd
            references public.revinfo,
    revtype        smallint,
    user_id        bigint,
    id             bigint  not null,
    transaction_id bigint,
    primary key (rev, id)
);

alter table public.sales_aud
    owner to postgres;

create table public.stores
(
    id         bigserial
        primary key,
    city       varchar(100) not null,
    phone      varchar(20) not null,
    store_name varchar(15) not null,
    address    varchar(100) not null,
    postal_code varchar(20) not null,
    facebook   varchar(100),
    instagram  varchar(100),
    twitter    varchar(100),
    shipping_cost_small numeric(10, 2),
    shipping_cost_medium numeric(10, 2),
    shipping_cost_large numeric(10, 2)
);

alter table public.stores
    owner to postgres;

create table public.store_home_carousel
(
    id       bigserial
        primary key,
    store_id bigint not null
        constraint fkntg2hqr8h1nqlx9fclre7et6l
            references public.stores,
    href     varchar(512),
    url      varchar(512),
    title    varchar(80)
);

alter table public.store_home_carousel
    owner to postgres;

create table public.transactions
(
    total          numeric(38, 2),
    date_time      timestamp(6),
    id             bigserial
        primary key,
    store_id       bigint
        constraint fk76d23hljajshvpfgctye587jp
            references public.stores,
    description    varchar(255),
    payment_method varchar(255)
        constraint transactions_payment_method_check
            check ((payment_method)::text = ANY
        ((ARRAY ['CREDIT'::character varying, 'DEBIT'::character varying, 'CASH'::character varying, 'DIGITAL'::character varying])::text[])),
    type           varchar(255)
        constraint transactions_type_check
            check ((type)::text = ANY
                   ((ARRAY ['PURCHASE'::character varying, 'SALE'::character varying, 'OTHER'::character varying])::text[]))
);

alter table public.transactions
    owner to postgres;

create table public.details_transactions
(
    subtotal       numeric(20, 2),
    id             bigserial
        primary key,
    product_id     bigint
        constraint fkh0l0gaa9gekam8bubgaihlrd7
            references public.products,
    quantity       bigint not null,
    transaction_id bigint not null
        constraint fkgj88y15hxg0sp8d6l5r4nfr2j
            references public.transactions
);

alter table public.details_transactions
    owner to postgres;

create table public.purchases
(
    unit_price     numeric(19, 2) not null,
    id             bigserial
        primary key,
    provider_id    bigint         not null
        constraint fkbn2k5burari3lcgietiifq9ho
            references public.providers,
    transaction_id bigint         not null
        unique
        constraint fknuwq8dcjsu0uiq1rwrex2uoi8
            references public.transactions
);

alter table public.purchases
    owner to postgres;

create table public.sales
(
    user_id        bigint
        constraint fkbbif9cb3ecyusyms54yvwlhd5
            references public.users,
    id             bigserial
        primary key,
    transaction_id bigint not null
        unique
        constraint fknxayievw4djoj9suy6tiwa8lm
            references public.transactions
);

alter table public.sales
    owner to postgres;

create table public.transactions_aud
(
    rev            integer not null
        constraint fks0a2j7849lgmcshqip6rcp7kx
            references public.revinfo,
    revtype        smallint,
    total          numeric(38, 2),
    date_time      timestamp(6),
    id             bigint  not null,
    store_id       bigint,
    description    varchar(255),
    payment_method varchar(255)
        constraint transactions_aud_payment_method_check
            check ((payment_method)::text = ANY
        ((ARRAY ['CREDIT'::character varying, 'DEBIT'::character varying, 'CASH'::character varying, 'DIGITAL'::character varying])::text[])),
    type           varchar(255)
        constraint transactions_aud_type_check
            check ((type)::text = ANY
                   ((ARRAY ['PURCHASE'::character varying, 'SALE'::character varying, 'OTHER'::character varying])::text[])),
    primary key (rev, id)
);

alter table public.transactions_aud
    owner to postgres;

