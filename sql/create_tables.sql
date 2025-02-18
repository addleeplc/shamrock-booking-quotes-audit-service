CREATE TABLE public.quotation
(   id uuid NOT NULL,
    create_ts timestamp,
    update_ts timestamp,
    date timestamp,
    booking_channel varchar,
    event_date timestamptz,
    market varchar,
    asap bool,
    customer_code varchar,
    client_id uuid,
    client_grade varchar,
    pickup_address text,
    pickup_postcode varchar,
    pickup_location_lat float8,
    pickup_location_lon float8,
    drop_address text,
    drop_postcode varchar,
    drop_location_lat float8,
    drop_location_lon float8,
    transaction_id varchar,
    CONSTRAINT quotation_pkey PRIMARY KEY (id)
);
CREATE INDEX quotation_date_idx ON public.quotation USING btree (date);
CREATE INDEX quotation_event_date_idx ON public.quotation USING btree (event_date);

CREATE TABLE public.product_quotation
(   id uuid NOT NULL,
    quotation_id uuid NOT NULL,
    lead_time interval,
    lead_time_source varchar,
    restriction_code varchar,
    restriction_message text,
	product_id uuid NULL,
	product_code varchar NULL,
	price float8 NULL,
	currency_code varchar NULL,
    public_event_id varchar,
    CONSTRAINT booking_restriction_pkey PRIMARY KEY (id),
	constraint fk_product_quotation_on_quotation foreign key (quotation_id) references quotation(id) on delete cascade
);
CREATE INDEX product_quotation_quotation_id_idx ON public.product_quotation USING btree (quotation_id);

CREATE TABLE public.booking
(   id uuid NOT NULL,
    quotation_id uuid NOT NULL,
    booking_number character varying,
    lead_time interval,
    lead_time_source varchar,
    restriction_code varchar,
    restriction_message text,
	product_id uuid NULL,
	product_code varchar NULL,
	price float8 NULL,
	currency_code varchar NULL,
    public_event_id varchar,
    CONSTRAINT booking_pkey PRIMARY KEY (id),
    constraint fk_booking_on_quotation foreign key (quotation_id) references quotation(id) on delete cascade
);
CREATE INDEX booking_quotation_id_idx ON public.booking USING btree (quotation_id);