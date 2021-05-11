create table if not exists `master_txn_core`
(
    merchant_reference varchar(40) not null primary key,
    primary_merchant_reference varchar(40) not null,
    rloc varchar(8) not null,
    rloc_create_date varchar(7) null,

    member_id varchar(50) null,
    psp_internet_ref_number varchar(10) null,
    psp_media_source varchar(10) null,

    tp_app_code varchar(35) not null,
    tp_app_ref varchar(40) not null,
    application_payment_code varchar(3) null,
    brand varchar(2) null,
    market varchar(2) null,
    locale varchar(5) null,
    sales_channel varchar(10) null,
    sales_port varchar(3) null,
    sales_reference varchar(15) null,
    sales_type varchar(10) null,
    payment_option varchar(3) null,

    auth_amount decimal(20,9) null,
    auth_currency varchar(3) null,

    auth_miles decimal(20,0) null,
    cash_equivalent_amount decimal(20, 9) null,
    capture_miles decimal(20,0) null,
    refund_miles decimal(20,0) null,

    payment_status varchar(30) null,
    is_captured bit default b'0' null,
    is_rollbacked bit default b'0' null,

    psp_type varchar(3) null,
    psp_amadeus_card_type_code varchar(10) null,

    psp_auth_unexpected_error_code varchar(100) null,
    psp_auth_unexpected_error_message varchar(100) null,
    psp_capture_unexpected_error_code varchar(100) null,
    psp_capture_unexpected_error_message varchar(100) null,
    psp_rollback_unexpected_error_code varchar(100) null,
    psp_rollback_unexpected_error_message varchar(100) null,
    psp_refund_unexpected_error_code varchar(100) null,
    psp_refund_unexpected_error_message varchar(100) null,

    additional_sales_data varchar(2000) null,
    delivery_date datetime null,
    phone_number varchar(15) null,
    remark_field1 varchar(100) null,
    remark_field2 varchar(100) null,
    shopper_email varchar(150) null,
    shopper_ip varchar(150) null,
    shopper_member_tier_level varchar(2) null,
    shopper_reference varchar(150) null,

    modified_merchant_reference varchar(100) null,
    payment_modification_ref varchar(50) null,

    pre_prod bit default b'0' null,
    is_ignore_discrepant bit default b'0' null,

    create_date_time datetime null,
    created_by varchar(50) null,
    update_date_time datetime null,
    updated_by varchar(50) null
);

create table if not exists `product_txn_core`
(
    merchant_reference varchar(40) not null,
    sequence_number int(3) not null,
    primary_merchant_reference varchar(40) not null,
    rloc varchar(8) not null,
    rloc_create_date varchar(7) null,

    payment_status varchar(30) null,

    product_type varchar(3) null,
    product_code varchar(3) null,
    origin_country varchar(2) null,
    destination_country varchar(2) null,
    cabin_class varchar(1) null,

    trip_type varchar(2) null,
    origin_port varchar(3) null,
    destination_port varchar(3) null,
    flight_carrier varchar(3) null,
    flight_number varchar(4) null,
    flight_date varchar(20) null,

    psp_product_type varchar(10) null,
    psp_trip_type varchar(20) null,

    passenger_surname varchar(40) null,
    passenger_firstname varchar(25) null,
    passenger_type varchar(3) null,

    psp_claim_status varchar(10) null,
    psp_claim_number int(8) null,
    psp_seq_number int(3) null,

    auth_miles decimal(20,0) null,
    product_currency varchar(3) null,
    cash_equivalent_amount decimal(20, 9) null,
    cash_amount decimal(20,9) null,

    mce_product_rate_id varchar(40) null,
    mce_fx_rate_id varchar(40) null,
    miles_to_ticketing_currency_rate decimal(12,12) null,
    usd_to_ticketing_currency_rate decimal(20,12) null,
    psp_miles_conversion_rate decimal(12,12) null,
    psp_fx_rate decimal(20,12) null,

    emd_number varchar(14) null,
    ticket_number varchar(14) null,

    create_date_time datetime null,
    created_by varchar(50) null,
    update_date_time datetime null,
    updated_by varchar(50) null,

    PRIMARY KEY(merchant_reference, sequence_number)
);

create table if not exists `flight_detail`
(
    id varchar(36) not null primary key,
    merchant_reference varchar(40) not null,

    origin varchar(3) not null,
    destination varchar(3) not null,
    trip_type varchar(10) not null,
    marketing_carrier varchar(2) not null,
    operating_carrier varchar(2) null,
    flight_number varchar(4) not null,
    flight_date varchar(20) null,

    create_date_time datetime null,
	created_by varchar(50) null,
	update_date_time datetime null,
	updated_by varchar(50) null
);

create table if not exists `txn_rollback`
(
    payment_modification_ref varchar(52) not null primary key,

    merchant_reference varchar(40) not null,
    primary_merchant_reference varchar(40) not null,
    rloc varchar(8) not null,
    rloc_create_date varchar(7) null,

    rollback_status varchar(30) null,
    psp_claim_status varchar(10) null,

    psp_rollback_unexpected_error_code varchar(100) null,
    psp_rollback_unexpected_error_message varchar(100) null,

    create_date_time datetime null,
    created_by varchar(50) null,
    update_date_time datetime null,
    updated_by varchar(50) null
);

create table if not exists `product_txn_capture`
(
    payment_modification_ref varchar(52) not null primary key,

    merchant_reference varchar(40) not null,
    sequence_number int(3) not null,
    primary_merchant_reference varchar(40) not null,
    rloc varchar(8) not null,
    rloc_create_date varchar(7) null,

    psp_claim_status varchar(10) null,
    psp_claim_number int(8) not null,
    psp_seq_number int(3) not null,

    product_type varchar(3) null,
    product_code varchar(3) null,
    origin_country varchar(2) null,
    destination_country varchar(2) null,
    cabin_class varchar(1) null,

    trip_type varchar(2) null,
    origin_port varchar(3) null,
    destination_port varchar(3) null,
    flight_carrier varchar(3) null,
    flight_number varchar(4) null,
    flight_date varchar(20) null,

    psp_product_type varchar(10) null,
    psp_trip_type varchar(20) null,

    passenger_surname varchar(40) null,
    passenger_firstname varchar(25) null,
    passenger_type varchar(3) null,

    capture_miles decimal(20,0) null,
    product_currency varchar(3) null,
    cash_equivalent_amount decimal(20, 9) null,

    mce_product_rate_id varchar(40) null,
    mce_fx_rate_id varchar(40) null,
    miles_to_ticketing_currency_rate decimal(12,12) null,
    usd_to_ticketing_currency_rate decimal(20,12) null,
    psp_miles_conversion_rate decimal(12,12) null,
    psp_fx_rate decimal(20,12) null,

    emd_number varchar(14) null,
    ticket_number varchar(14) null,

    psp_capture_unexpected_error_code varchar(100) null,
    psp_capture_unexpected_error_message varchar(100) null,

    create_date_time datetime null,
    created_by varchar(50) null,
    update_date_time datetime null,
    updated_by varchar(50) null
);

create table if not exists `product_txn_refund`
(
    payment_modification_ref varchar(52) not null primary key,

    merchant_reference varchar(40) not null,
    sequence_number int(3) not null,
    primary_merchant_reference varchar(40) not null,
    rloc varchar(8) not null,
    rloc_create_date varchar(7) null,

    psp_refund_internet_ref_number varchar(10) null,

    psp_claim_status varchar(10) null,
    psp_claim_number int(8) not null,
    psp_seq_number int(3) not null,

    refund_status varchar(10) null,

    product_type varchar(3) null,
    product_code varchar(3) null,
    origin_country varchar(2) null,
    destination_country varchar(2) null,
    cabin_class varchar(1) null,

    trip_type varchar(2) null,
    origin_port varchar(3) null,
    destination_port varchar(3) null,
    flight_carrier varchar(3) null,
    flight_number varchar(4) null,
    flight_date varchar(20) null,

    psp_product_type varchar(10) null,
    psp_trip_type varchar(20) null,

    passenger_surname varchar(40) null,
    passenger_firstname varchar(25) null,
    passenger_type varchar(3) null,

    refund_miles decimal(20,0) null,
    product_currency varchar(3) null,
    cash_equivalent_amount decimal(20, 9) null,

    mce_product_rate_id varchar(40) null,
    mce_fx_rate_id varchar(40) null,
    miles_to_ticketing_currency_rate decimal(12,12) null,
    usd_to_ticketing_currency_rate decimal(20,12) null,
    psp_miles_conversion_rate decimal(12,12) null,
    psp_fx_rate decimal(20,12) null,

    emd_number varchar(14) null,
    ticket_number varchar(14) null,

    psp_refund_unexpected_error_code varchar(100) null,
    psp_refund_unexpected_error_message varchar(100) null,

    create_date_time datetime null,
    created_by varchar(50) null,
    update_date_time datetime null,
    updated_by varchar(50) null
);
