--
-- PostgreSQL database dump
--

-- Dumped from database version 14.3
-- Dumped by pg_dump version 14.3

-- Started on 2023-06-14 14:12:20

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 223 (class 1255 OID 103116)
-- Name: chat_creation_date(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.chat_creation_date() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin
    if new.chat_creation_date is null
    then
        new.updated_at := current_timestamp + '3 hours'::interval;
    end if;
    return new;
end
$$;


--
-- TOC entry 236 (class 1255 OID 104964)
-- Name: delete_file(text, text, text); Type: PROCEDURE; Schema: public; Owner: -
--

CREATE PROCEDURE public.delete_file(IN v_file_oid text, IN v_path text, IN v_target_table_mm text)
    LANGUAGE plpgsql
    AS $$
        DECLARE
            selected_file_oid text ;
        begin
            if v_target_table_mm is  null
                then raise exception 'Имя служебной таблицы не может быть null';
            end if;

            if v_file_oid is not null then
                execute 'delete from ' || v_target_table_mm || ' as t where t.file_oid = ' || E'\'' || v_file_oid || E'\'';
                delete from files as t where t.oid = v_file_oid::text;
                return;
            end if;

            if v_path is not null then
                select into selected_file_oid t.oid from files as t where t.file_path = v_path;
                execute 'delete from ' || v_target_table_mm || ' as t where t.file_oid = ' || E'\'' || selected_file_oid || E'\'';
                delete from files as t where t.oid = selected_file_oid;
                return;
            end if;

            raise exception 'Ключи v_file_oid, v_path равны null. Хотя бы один должен быть равен не null';
        end
    $$;


--
-- TOC entry 221 (class 1255 OID 41363)
-- Name: gen_id(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.gen_id() RETURNS bigint
    LANGUAGE plpgsql
    AS $$
    begin
        return (select to_char(current_timestamp + '3 hour', 'YYddMMHHmmssms') || nextval('users_sequence_identificator')::text);
    end;
$$;


--
-- TOC entry 239 (class 1255 OID 104994)
-- Name: rating_d(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.rating_d(v_oid text) RETURNS numeric
    LANGUAGE plpgsql
    AS $$
        declare
            cnt integer;
            reg_date date;
        begin
            select into reg_date t.user_reg_date from users as t where  t.oid = v_oid;
            select into cnt extract(day from now()- '2022-03-06'::date) ;
            case
                when cnt > 90 and cnt<= 180 then return 1.02;
                when cnt > 180 then return 1.05;
                else return 1.0;
            end case;

        end;
    $$;


--
-- TOC entry 238 (class 1255 OID 104995)
-- Name: rating_v(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.rating_v(v_oid text) RETURNS numeric
    LANGUAGE plpgsql
    AS $$
        declare
            cnt integer;
        begin
            select into cnt count(t) from post as t
                where t.post_creator_oid = v_oid
                and t.post_status = 'active'
                group by t.post_flat_oid;
        case
            when cnt > 3 and cnt<= 7 then return 1.02;
            when cnt > 7 then return 1.05;
            else return 1.0;
            end case;

        end;
    $$;


--
-- TOC entry 237 (class 1255 OID 104991)
-- Name: recalc_rating(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.recalc_rating() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
        declare
            _avg decimal;
        begin
            select into _avg (avg(t.rating_score) + new.rating_score)/2 from rating as t where t.user_oid = new.user_oid;
            update users as t
                set user_rating = _avg * rating_D(new.user_oid)
                                      * rating_V(new.user_oid)
            where  t.oid = new.user_oid;
            return new;
        end
    $$;


--
-- TOC entry 222 (class 1255 OID 67640)
-- Name: register_new_user(character varying, character varying, character varying, character varying, date, character varying, double precision); Type: PROCEDURE; Schema: public; Owner: -
--

CREATE PROCEDURE public.register_new_user(IN v_user_name character varying, IN v_user_email character varying, IN v_user_phone_number character varying, IN v_user_password character varying, IN v_user_birthday date, IN v_files_path character varying, IN v_file_size double precision)
    LANGUAGE plpgsql
    AS $$
        begin


        end;
    $$;


--
-- TOC entry 235 (class 1255 OID 41443)
-- Name: updated_at_check(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.updated_at_check() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
        begin
            if new.updated_at is null or new.updated_at < current_timestamp
                then
                new.updated_at := current_timestamp;
            end if;
            return new;
        end
    $$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 210 (class 1259 OID 41368)
-- Name: base_entity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.base_entity (
    oid character varying NOT NULL
);


--
-- TOC entry 212 (class 1259 OID 41416)
-- Name: contract; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.contract (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    contract_date date DEFAULT CURRENT_DATE NOT NULL,
    contract_renter_oid character varying NOT NULL,
    contract_owner_oid character varying,
    contract_start_rent timestamp without time zone NOT NULL,
    contrcat_target_flat text NOT NULL,
    contract_target_post text NOT NULL,
    contract_total_cost numeric NOT NULL,
    contract_total_cost_flat text,
    contract_end_rent timestamp without time zone NOT NULL,
    contract_document text
);


--
-- TOC entry 213 (class 1259 OID 41435)
-- Name: files; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.files (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    file_path character varying NOT NULL,
    file_size double precision NOT NULL,
    updated_at timestamp without time zone,
    CONSTRAINT check_updated_at CHECK ((updated_at <= (now() + '03:00:00'::interval)))
);


--
-- TOC entry 216 (class 1259 OID 41482)
-- Name: flat; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flat (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    flat_owner character varying NOT NULL,
    flat_address character varying(40) NOT NULL,
    flat_square numeric NOT NULL,
    flat_rooms_count integer DEFAULT 1 NOT NULL,
    flat_x_map_cord numeric DEFAULT 0.0 NOT NULL,
    flat_y_map_cord numeric DEFAULT 0.0 NOT NULL,
    flat_type text NOT NULL,
    CONSTRAINT check_square CHECK ((flat_square > 0.0)),
    CONSTRAINT check_type CHECK (((flat_type = 'flat'::text) OR (flat_type = 'townhouse'::text) OR (flat_type = 'uninhabited'::text)))
);


--
-- TOC entry 215 (class 1259 OID 41459)
-- Name: post; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.post (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    post_status character varying(10) NOT NULL,
    price numeric NOT NULL,
    post_title character varying(25) NOT NULL,
    post_information text,
    post_creator_oid character varying NOT NULL,
    post_flat_oid character varying NOT NULL,
    post_creation_date timestamp without time zone NOT NULL
);


--
-- TOC entry 214 (class 1259 OID 41446)
-- Name: post_photos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.post_photos (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    post_oid character varying NOT NULL,
    file_oid character varying NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 104976)
-- Name: rating; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rating (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    user_oid character varying NOT NULL,
    rating_score numeric NOT NULL,
    CONSTRAINT check_rating_bigger CHECK ((rating_score > 0.0)),
    CONSTRAINT check_rating_less CHECK ((rating_score <= 5.0))
);


--
-- TOC entry 219 (class 1259 OID 105101)
-- Name: rent_offer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rent_offer (
    oid text DEFAULT public.gen_id() NOT NULL,
    rent_offer_resolve boolean,
    rent_offer_start timestamp without time zone NOT NULL,
    rent_offer_end timestamp without time zone NOT NULL,
    rent_offer_renter text NOT NULL,
    rent_offer_post text,
    rent_offer_need_rating boolean DEFAULT false NOT NULL
);


--
-- TOC entry 220 (class 1259 OID 154604)
-- Name: some_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.some_table (
    oid text DEFAULT public.gen_id() NOT NULL,
    some_table_attribute text
);


--
-- TOC entry 211 (class 1259 OID 41384)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    user_name character varying NOT NULL,
    user_birthday date NOT NULL,
    user_reg_date date NOT NULL,
    user_email text NOT NULL,
    user_password character varying NOT NULL,
    user_phone_number text NOT NULL,
    user_is_confirm boolean DEFAULT false,
    user_telegram_link text,
    user_whatsup_link text,
    user_rating numeric,
    user_is_admin boolean DEFAULT false NOT NULL,
    user_is_banned boolean DEFAULT false NOT NULL,
    CONSTRAINT users_user_birthday_check CHECK ((user_birthday < (now() - '18 years'::interval))),
    CONSTRAINT users_user_reg_date_check CHECK ((user_reg_date <= now()))
)
INHERITS (public.base_entity);


--
-- TOC entry 217 (class 1259 OID 92213)
-- Name: users_avatar_file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users_avatar_file (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    user_oid character varying NOT NULL,
    file_oid character varying NOT NULL
);


--
-- TOC entry 209 (class 1259 OID 41358)
-- Name: users_sequence_identificator; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_sequence_identificator
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 20
    CACHE 1
    CYCLE;


--
-- TOC entry 3212 (class 2604 OID 41398)
-- Name: users oid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN oid SET DEFAULT public.gen_id();


--
-- TOC entry 3430 (class 0 OID 41368)
-- Dependencies: 210
-- Data for Name: base_entity; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.base_entity (oid) FROM stdin;
\.


--
-- TOC entry 3432 (class 0 OID 41416)
-- Dependencies: 212
-- Data for Name: contract; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.contract (oid, contract_date, contract_renter_oid, contract_owner_oid, contract_start_rent, contrcat_target_flat, contract_target_post, contract_total_cost, contract_total_cost_flat, contract_end_rent, contract_document) FROM stdin;
23080611060384719	2023-06-08	23160308030344414	23160502054496120	2023-06-09 12:00:00	2306060106324727	23060602061143114	21000	двадцать одна тысяча	2023-06-15 12:00:00	23080611060409520
23230511050318220	2023-05-23	2316050305542140	2316030203364944	2023-05-25 12:00:00	2327030103296278	23230512051959916	1	один	2023-05-26 12:00:00	2323051105032940
23110606062656413	2023-06-11	23260108010714212	23160502054496120	2023-06-10 12:00:00	2329051005384542	2329051005413896	21000	двадцать одна тысяча	2023-06-16 12:00:00	23110606062671914
23080609064989514	2023-06-08	2316050305542140	23260108010714212	2023-06-09 12:00:00	2305060106445610	2305060106499221	30000	тридцать тысяч	2023-06-15 12:00:00	23080609065362415
23120610060585716	2023-06-12	2316050305542140	23160502054496120	2023-06-14 12:00:00	2306060106324727	23060602061143114	10500	десять тысяч пятьсот	2023-06-17 12:00:00	23120610060875417
23130609061797816	2023-06-13	2217120912271980	23160502054496120	2023-07-06 12:00:00	2329051005384542	2329051005413896	7000	семь тысяч	2023-07-08 12:00:00	23130609062087117
2312061106497570	2023-06-12	23120611063359611	23160502054496120	2023-08-11 12:00:00	2306060106324727	23060602061143114	17500	семнадцать тысяч пятьсот	2023-08-16 12:00:00	2312061106534381
23080609065641112	2023-06-08	2316050305542140	23260108010714212	2023-06-09 12:00:00	2305060106445610	2305060106499221	30000	тридцать тысяч	2023-06-15 12:00:00	23080609065995413
23230511054966018	2023-05-23	2316050305542140	2316030203364944	2023-05-25 12:00:00	2327030103296278	23230512051959916	1	один	2023-05-26 12:00:00	23230511055259019
2313061206561815	2023-06-12	2217120912271980	23160502054496120	2023-06-28 12:00:00	2329051005384542	2329051005413896	14000	четырнадцать тысяч	2023-07-02 12:00:00	2313061206564136
2311060606033519	2023-06-11	2316050305542140	23160502054496120	2023-06-10 12:00:00	2306060106324727	23060602061143114	14000	четырнадцать тысяч	2023-06-14 12:00:00	23110606060699910
23110606064785811	2023-06-11	2316050305542140	23160502054496120	2023-08-16 12:00:00	2306060106324727	23060602061143114	10500	десять тысяч пятьсот	2023-08-19 12:00:00	23110606064805712
23290509050200411	2023-05-29	23160308030344414	23160502054496120	2023-05-30 12:00:00	2325051105156870	2325051105124951	6000	шесть тысяч	2023-05-31 12:00:00	23290509050514812
2312061006509893	2023-06-12	2316050305542140	23160502054496120	2023-06-16 12:00:00	2306060106324727	23060602061143114	77000	семьдесят семь тысяч	2023-07-08 12:00:00	2312061006511184
23080609062115216	2023-06-08	2316050305542140	23260108010714212	2023-06-09 12:00:00	2305060106445610	2305060106499221	30000	тридцать тысяч	2023-06-15 12:00:00	23080609062396017
23130609062387219	2023-06-13	2217120912271980	23160502054496120	2023-08-25 12:00:00	2329051005384542	2329051005413896	3500	три тысячи пятьсот	2023-08-26 12:00:00	23130609062665720
2313060906358721	2023-06-13	2217120912271980	23160502054496120	2023-08-22 12:00:00	2325051105156870	2329051005068763	2900	две тысячи девятьсот	2023-08-23 12:00:00	2313060906360092
\.


--
-- TOC entry 3433 (class 0 OID 41435)
-- Dependencies: 213
-- Data for Name: files; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.files (oid, file_path, file_size, updated_at) FROM stdin;
23230502054703920	/rrs-backend/storage/files/post-photos/89aa4cd10be33584642e866e9226d7cc.jpg	57794	2023-05-23 11:55:47.042223
2323050305561991	/rrs-backend/storage/files/post-photos/96b6c0b003958eca2b46ca8102e17b71.jpg	71372	2023-05-23 12:33:56.222616
2323050305492603	/rrs-backend/storage/files/post-photos/ba5c91fb9ee5af1463cdc6e0b944b374.jpg	71372	2023-05-23 12:35:49.286261
2323050305400665	/rrs-backend/storage/files/post-photos/97db79d2976079e5f3bf9e9e31d1ab87.jpg	71372	2023-05-23 12:58:40.091479
2323050305400946	/rrs-backend/storage/files/post-photos/48c9846f072280af9faf3dc69a1b5e20.jpg	119474	2023-05-23 12:58:40.095121
2323050305400967	/rrs-backend/storage/files/post-photos/f33734a3062cfb2a0c1c50cd49641a0d.jpg	173270	2023-05-23 12:58:40.09744
2323050305400988	/rrs-backend/storage/files/post-photos/c91d6849ec2b74651562bae8079c1abc.jpg	1062941	2023-05-23 12:58:40.099695
23280407044346510	/rrs-backend/storage/files/post-photos/2e52efb45f4b88881900fb47f4428954.jpg	93115	2023-04-28 19:41:43.467351
23280407044463313	/rrs-backend/storage/files/post-photos/82bdef866e3de11daf5570cff033d51f.jpg	93115	2023-04-28 19:43:44.635619
23280407041804614	/rrs-backend/storage/files/post-photos/5eda85e8135d5268a07f6e9d936222bd.jpg	93115	2023-04-28 19:54:18.290371
23280408040160717	/rrs-backend/storage/files/post-photos/7ee2808f5ffa522d7c672983a2f5f3ad.jpg	93115	2023-04-28 20:00:01.614881
23280408042975920	/rrs-backend/storage/files/post-photos/46cdcf5113d179096d1ad418907b531a.jpg	93115	2023-04-28 20:01:43.896475
2328040804276492	/rrs-backend/storage/files/post-photos/dacffdb9807692c82e6882a54ff8cae1.jpg	93115	2023-04-28 20:07:50.684291
2328040804110155	/rrs-backend/storage/files/post-photos/91df27e4d6ed7dcfb8f72295b5f0f713.jpg	93115	2023-04-28 20:10:11.022277
2328040804224666	/rrs-backend/storage/files/post-photos/b6786c7efc423a105c8a75d676f27c8a.jpg	93115	2023-04-28 20:13:22.511227
23280408041729212	/rrs-backend/storage/files/post-photos/37544e472c95129db51d5e9f36c0a4fa.jpg	57794	2023-04-28 17:31:17.334838
23280408042370814	/rrs-backend/storage/files/post-photos/32d63029cbcf9993a0141e1291fc168f.jpg	57794	2023-04-28 17:31:23.710548
23280408042508018	/rrs-backend/storage/files/post-photos/82eb2e02555ab5a0fc6b1a5e235e80cf.jpg	57794	2023-04-28 17:31:25.083255
23230502051834116	/rrs-backend/storage/files/post-photos/d4e4c7984ac034b13a9dfb21ec6f34ff.jpg	57794	2023-05-23 11:52:18.361355
23230502054285118	/rrs-backend/storage/files/post-photos/45a01819c7e56969fb4ae6147cb9999e.jpg	57794	2023-05-23 11:54:42.934561
23230510052198716	/rrs-backend/storage/files/documents/9d7fdf29eb3da6dc17dd0adf76fe7b65.pdf	103953	2023-05-23 19:17:21.989564
23230511055259019	/rrs-backend/storage/files/documents/fb52338688c62ee0b256ca3fac6ae0b5.pdf	103286	2023-05-23 20:02:52.592007
2323051105032940	/rrs-backend/storage/files/documents/63325195791abd4a5bb34a87f3d59d48.pdf	103286	2023-05-23 20:03:03.296116
2324051205482003	/rrs-backend/storage/files/post-photos/c3116840fcee6ba5c89f4ffb52be069c.jpg	1062941	2023-05-23 21:22:48.203878
2324051205482104	/rrs-backend/storage/files/post-photos/d50104d8d3a98aff3bbaaa089fb78d05.jpg	119474	2023-05-23 21:22:48.212606
2324051205482145	/rrs-backend/storage/files/post-photos/d7bc7425ad46f6adb4d5c9af834af4cb.jpg	71372	2023-05-23 21:22:48.216506
23240512051581710	/rrs-backend/storage/files/post-photos/3675c10db19237f06fb67142a143cd87.jpg	173270	2023-05-23 21:26:15.81874
23240512051582311	/rrs-backend/storage/files/post-photos/00919141a8c2ba2986efb8d0bf4f1036.jpg	119474	2023-05-23 21:26:15.824757
23240504054301515	/rrs-backend/storage/files/post-photos/ee9e2ba21093532a70e2e252ad270fd6.jpg	119474	2023-05-24 13:44:43.016668
23240505051815718	/rrs-backend/storage/files/post-photos/99ee21b9ce2820d71bef559caac50060.jpg	468773	2023-05-24 14:54:18.15958
23240505051816319	/rrs-backend/storage/files/post-photos/d7c71f863197f2734f45050e65b4ee72.jpg	71372	2023-05-24 14:54:18.16521
2324051005225282	/rrs-backend/storage/files/post-photos/a67bbd47fb9223a99416fd8f43285e46.jpg	1066321	2023-05-24 19:10:22.531428
2324051105051375	/rrs-backend/storage/files/post-photos/855d8ffdaaec77546967c3fab0f9ad32.jpg	173270	2023-05-24 20:40:05.138967
2324051105468128	/rrs-backend/storage/files/post-photos/c2cc497fa0a375ae6318e52dddc13f6f.jpg	119474	2023-05-24 20:43:46.814273
23250512053312210	/rrs-backend/storage/files/post-photos/3ab55254fc864454d7376418022b7819.jpg	196717	2023-05-25 09:58:33.147362
23250512053315111	/rrs-backend/storage/files/post-photos/cf0ca4fe08d0f1537dadb57f32959bc2.jpg	97125	2023-05-25 09:58:33.153402
23250501052574815	/rrs-backend/storage/files/post-photos/59f42d895486e024ca421e1ff061b6fc.jpg	137343	2023-05-25 10:02:25.749988
23250501051865019	/rrs-backend/storage/files/post-photos/8687862b1f7e4445794f37028c13c9a8.jpg	118670	2023-05-25 10:04:18.653383
2325051105153542	/rrs-backend/storage/files/post-photos/32021369b9b424e7f6a3d43b9b00c268.jpg	2333438	2023-05-25 20:48:15.356555
2325051105153684	/rrs-backend/storage/files/post-photos/4a47c6ccd0ccd76736ef7d48524b0c36.jpg	3251751	2023-05-25 20:48:15.371415
2325051105153755	/rrs-backend/storage/files/post-photos/8b514ec0fbd944e73cbe9b5c6d60e382.jpg	4058785	2023-05-25 20:48:15.377074
23290509050514812	/rrs-backend/storage/files/documents/fb03209366b838acebd9f17d4a15b4ad.pdf	103715	2023-05-29 18:19:05.150214
23290510050487911	/rrs-backend/storage/files/post-photos/69f23b5e0954896dbf3265703f6e8313.jpg	1065913	2023-05-29 19:31:04.894999
2329051005415371	/rrs-backend/storage/files/post-photos/75661b48ffe0fa5df6058dc2ce7f7ac6.jpg	4855670	2023-05-29 19:36:41.538817
2329051005085544	/rrs-backend/storage/files/post-photos/31f598d5db3fb4e0e698110e28e93cf6.jpg	3085532	2023-05-29 19:37:08.555931
2329051005457207	/rrs-backend/storage/files/post-photos/429a66b0f1a83e2d0ef7db967e98bec5.jpg	3892634	2023-05-29 19:37:45.721352
2329051005457288	/rrs-backend/storage/files/post-photos/25376ba1787a80b8e6d177e52c4c5cc5.jpg	3755279	2023-05-29 19:37:45.729534
23030603062938817	/rrs-backend/storage/files/avatars/4b949da8f697435595ed7431b197722b.jpeg	575783	2023-06-03 12:07:29.409228
23030603060179718	/rrs-backend/storage/files/avatars/f2aac66137355d7439fb81f0b72bb75e.jpeg	575783	2023-06-03 12:13:01.818637
23030603060859519	/rrs-backend/storage/files/avatars/cd4c00af15f56baeda5b8817d21c91b2.jpeg	575783	2023-06-03 12:17:08.617765
23030604061913011	/rrs-backend/storage/files/post-photos/814dcb7140e6e4e5d15af1e4eb82c0b7.jpg	210707	2023-06-03 13:20:19.135391
23030604061914312	/rrs-backend/storage/files/post-photos/db3ff964a120f94605191bb21d4c3cb9.jpg	242718	2023-06-03 13:20:19.144908
23030606065735919	/rrs-backend/storage/files/avatars/cf850bb662ea67e088bd4609330d976a.jpg	749261	2023-06-03 15:12:57.361174
2305060106505712	/rrs-backend/storage/files/post-photos/7126346d86dacc963cd05d91c3dacdd7.jpg	2233089	2023-06-04 22:07:50.572783
23060602061189815	/rrs-backend/storage/files/post-photos/19aee339b2687b470682bcf6a177c4db.jpg	401765	2023-06-06 11:42:11.900847
23060602061191416	/rrs-backend/storage/files/post-photos/eacc0b37293e3ff901b0ab5145438f16.jpg	325668	2023-06-06 11:42:11.915911
23080609065995413	/rrs-backend/storage/files/documents/2ae0ea439e24ec8c17b0df21eabf9d01.pdf	103491	2023-06-08 18:17:59.956786
23080609065362415	/rrs-backend/storage/files/documents/574cdcd8f4d90e602459e7882dcb66d4.pdf	102547	2023-06-08 18:21:53.627685
23080609062396017	/rrs-backend/storage/files/documents/ce9b67df0fe74bc9131ad6afac7450f0.pdf	102554	2023-06-08 18:25:23.963231
23080611060409520	/rrs-backend/storage/files/documents/c5cbb86a6dcfa4d367769b82245bb919.pdf	103026	2023-06-08 20:38:04.097322
23110606060699910	/rrs-backend/storage/files/documents/fe7bcf65ab60725406673a1d82e947bd.pdf	101955	2023-06-11 03:22:07.001243
23110606064805712	/rrs-backend/storage/files/documents/35aabcb353b8c2c2c3c5ed636801e415.pdf	102337	2023-06-11 03:23:48.058808
23110606062671914	/rrs-backend/storage/files/documents/b5b5fd322744b027939219c713215565.pdf	102639	2023-06-11 03:24:26.720455
23120610060875417	/rrs-backend/storage/files/documents/d0a3333d82d82d6c2f1bbeb51a99d202.pdf	101972	2023-06-12 19:11:08.756291
2312061006380900	/rrs-backend/storage/files/post-photos/832e06cf3ced1e9d62245cfc26a7ce70.jpg	128177	2023-06-12 19:29:38.091707
2312061006511184	/rrs-backend/storage/files/documents/25180dd78cf0cdf149d8305d18bc0035.pdf	101995	2023-06-12 19:55:51.119799
23120611061417314	/rrs-backend/storage/files/post-photos/8a93bbac7d1835733edc696deb2ef846.jpg	38285	2023-06-12 20:25:14.175893
23120611061417715	/rrs-backend/storage/files/post-photos/5c039a6ef9f436a0c610d3916798b138.jpg	32308	2023-06-12 20:25:14.178798
23120611061113818	/rrs-backend/storage/files/avatars/dfb57ebf5cb83dff92d4ac4a1cdec58f.jpg	128177	2023-06-12 20:26:11.139534
2312061106534381	/rrs-backend/storage/files/documents/cde7a3a44fed012611343dbd5a04895f.pdf	101979	2023-06-12 20:28:53.439793
2313061206564136	/rrs-backend/storage/files/documents/b13a56df5f0879ca8cbfd1a7a69f035c.pdf	102336	2023-06-12 21:20:56.414464
23130609060098810	/rrs-backend/storage/files/post-photos/e6964c15c4f03b4609203912199ad2f1.jpg	166743	2023-06-13 18:40:00.98962
23130609061665713	/rrs-backend/storage/files/post-photos/292dbbbc8403d9e0231176b8bce78d06.jpg	49675	2023-06-13 18:42:16.658687
23130609062087117	/rrs-backend/storage/files/documents/90f1972aa25c810837e272e43f6f692d.pdf	102324	2023-06-13 18:43:20.872688
23130609062665720	/rrs-backend/storage/files/documents/62847714c7c913d417900bc8bfaeff32.pdf	102315	2023-06-13 18:50:26.659643
2313060906360092	/rrs-backend/storage/files/documents/5c54bb7d93f8d0b2f5a1932f9813121a.pdf	102641	2023-06-13 18:54:36.010512
2313060906464293	/rrs-backend/storage/files/post-photos/eac4257a7d72c5c0fc75ceb816e4270a.jpg	247438	2023-06-13 18:55:46.431614
\.


--
-- TOC entry 3436 (class 0 OID 41482)
-- Dependencies: 216
-- Data for Name: flat; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.flat (oid, flat_owner, flat_address, flat_square, flat_rooms_count, flat_x_map_cord, flat_y_map_cord, flat_type) FROM stdin;
2327030103296126	2316030203364944	ул. Пушкина д.1 кв.1	65	1	0	0	flat
2327030103296278	2316030203364944	ул. Пушкина д.1 кв.3	132	4	0	0	flat
2327030103296247	2316030203364944	ул. Пушкина д.1 кв.2	65	2	0	0	flat
2319050805318128	23160501053520617	Test	100	2	0	0	flat
23200504051731512	2316050305542140	ул. Ленина 123, кв. 45	120	5	0	0	flat
23200504051886713	2316050305542140	ул. Сталина 124	354	12	0	0	townhouse
23220512050520615	23160501053520617	testik	350	6	0	0	townhouse
2325051105156870	23160502054496120	Калатушкина 1	55	2	0	0	flat
2329051005384542	23160502054496120	Проспект Победы 11	75	3	0	0	flat
2305060106445610	23260108010714212	Тестовая 12	60	2	0	0	flat
2306060106324727	23160502054496120	Печушкина 12	88	3	0	0	flat
23120610060788918	2217120912271980	Тестовая 1	55	3	0	0	flat
23120611065833112	23120611063359611	Тестовая улица 12	60	2	0	0	flat
2313060906279618	23160502054496120	тестовая квартира	150	4	0	0	flat
2313060906454289	23160502054496120	тестовый коттедж	230	5	0	0	townhouse
23140604061875615	2316050305542140	Ленина 200	2589	6	0	0	uninhabited
\.


--
-- TOC entry 3435 (class 0 OID 41459)
-- Dependencies: 215
-- Data for Name: post; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.post (oid, post_status, price, post_title, post_information, post_creator_oid, post_flat_oid, post_creation_date) FROM stdin;
23270302033460211	active	3400	Пиздатая хата	Квартира по улице пушкина, дом колотушкина	2316030203364944	2327030103296126	2023-03-26 23:28:42.063176
23230512053981919	deny	1	Новый пост 4	Новый пост 4	2316030203364944	2327030103296278	2023-05-22 21:51:39.820755
2323050105398903	deny	1	Новый пост 8	Новый пост 8	2316030203364944	2327030103296126	2023-05-22 22:05:39.892865
2323050105431374	deny	1	Новый пост 8	Новый пост 8	2316030203364944	2327030103296126	2023-05-22 22:05:43.13981
2323050105513395	deny	1	Новый пост 9	Новый пост 9	2316030203364944	2327030103296126	2023-05-22 22:05:51.339239
2323050105565146	deny	1	Новый пост 10	Новый пост 10	2316030203364944	2327030103296126	2023-05-22 22:05:56.514746
2323050105002617	deny	1	Новый пост 11	Новый пост 11	2316030203364944	2327030103296126	2023-05-22 22:06:00.26095
2325040504353775	active	0	Тест поста	Тест поста 1	2316030203364944	2327030103296247	2023-04-25 14:16:38.922112
2325040504290366	active	0	Тест поста	Тест поста 2	2316030203364944	2327030103296278	2023-04-25 14:19:31.918563
23230509054219113	archive	3500	Test name	Test descr	2316050305542140	23200504051886713	2023-05-23 06:11:42.19264
2323051205512020	deny	1	Новый пост 6	Новый пост 6	2316030203364944	2327030103296278	2023-05-22 21:51:51.204728
23230512054406220	deny	1	Новый пост 5	Новый пост 5	2316030203364944	2327030103296278	2023-05-22 21:51:44.062656
2323051205580331	active	1	Новый пост 7	Новый пост 7	2316030203364944	2327030103296278	2023-05-22 21:51:58.033723
23230512052788217	deny	1	Новый пост 2	Новый пост 2	2316030203364944	2327030103296278	2023-05-22 21:51:27.884167
2323050805184788	moderation	10000	Post title	Post Description	2316050305542140	23200504051731512	2023-05-23 05:38:18.486819
23230512051959916	active	1	Новый пост 1	Новый пост 1	2316030203364944	2327030103296278	2023-05-22 21:51:19.612042
2323051205047692	deny	1	Новый пост 8	Новый пост 8	2316030203364944	2327030103296278	2023-05-22 21:52:04.770916
2324051205469392	moderation	2000	Тестовое	Использовать без ключа подписи и печати на графике и на сайте не нашел в интернете прочитал а ваше письмо требует ответа не получил от вас было получено письмо с запросом на подтверждение прочтения и на сайте в разделе о нас и в две строки не у всех есть новости на сайт пуш-уведомления не могу найти в интернете и откройте её свойства и применение на уровне пользователя не уу не у компьютера и мобильных телефонов не у компьютера и мобильных телефонов не у компьютера и мобильных телефонов не у компьютера и мобильных телефонов не у компьютера не знаю почему не отвечаете не знаю почему не отвечаете в этом месяце	2316050305542140	23200504051731512	2023-05-23 21:22:46.94344
23230509054098412	archive	10000	Post title	Post Description	2316050305542140	23200504051731512	2023-05-23 06:08:40.990295
23230512053386518	deny	1	Новый пост 3	Новый пост 3	2316030203364944	2327030103296278	2023-05-22 21:51:33.867915
23230510055802715	archive	10000	Название	Описание объявления	2316050305542140	23200504051731512	2023-05-23 07:51:58.032329
23240505051733017	moderation	2000	Название 8	Иисуса Христа и не зн не знаю что делать с этим делать с этим делать с этим делать с этим делать с этой задачи и на какой срок поставки и цену за которую я заказывал на новый год наверное не стоит не нужно только в течении часа посмотрю что есть на данный момент когда я буду ещё звонить на мой ящик и в каком городе находитесь и щас на в процессе работы для детей с задержкой но я думаю это можно было сделать не сможем вам предложить не у кого не секрет то и три часа ночи в этом случае нужно для начала я тогда приступаю в этом году мы будем надеяться на графике список литературы по то и три часа позвоню как дела как работа в компании которая нарисована в этом году не у кого нет сейчас 	2316050305542140	23200504051731512	2023-05-24 14:54:17.334908
23290510050446619	archive	4500	\nТестируемся	Фильтры квартиры\nХолодильник\nФен\nМикроволновка\nПерловка\nРецепт плова в подарок	23160502054496120	2325051105156870	2023-05-29 19:00:04.466855
23240504054231414	archive	5000	Навание	Test 200	2316050305542140	23200504051731512	2023-05-24 13:44:42.329721
2324051005213571	archive	2000	Квартира	Тверь Тольятти Томск Тула Тюмень Улан-Удэ Ульяновск и ульяновская область Ульяновск и ульяновская область Ульяновск и ульяновская область и на этом случае за ответ укгшшш	2316050305542140	23200504051886713	2023-05-24 19:10:21.363923
23250501052561514	archive	2500	Темт темт	Ррррииииррррррррррррооо	2316050305542140	23200504051886713	2023-05-25 10:02:25.616822
2325051105124951	active	6000	Гасом	Мосагагага	23160502054496120	2325051105156870	2023-05-25 20:48:12.501422
2329051005522179	deny	2500	Тест еще с фото	Тест	23160502054496120	2329051005384542	2023-05-29 19:06:52.218062
2329051005426718	deny	2500	Тест еще с фото	Тест	23160502054496120	2329051005384542	2023-05-29 19:06:42.671583
2329051005280727	deny	2500	Тест еще с фото	Тест	23160502054496120	2329051005384542	2023-05-29 19:06:28.072166
2329051005468545	deny	3000	Тест с фото	Тест с добавление фото ошибка "не удалось создать объект"	23160502054496120	2329051005384542	2023-05-29 19:05:46.854718
2329051005308594	deny	3000	Тест с фото	Тест с добавление фото ошибка "не удалось создать объект"	23160502054496120	2329051005384542	2023-05-29 19:05:30.858893
2329051005215573	deny	3000	Тест с фото	Тест с добавление фото ошибка "не удалось создать объект"	23160502054496120	2329051005384542	2023-05-29 19:05:21.559276
2329051005092711	deny	3000	Еио о	Мнмнио	23160502054496120	2325051105156870	2023-05-29 19:04:09.271551
2329051005555580	deny	3000	Еио о	Мнмнио	23160502054496120	2325051105156870	2023-05-29 19:03:55.5611
23290510054401820	deny	3000	Еио о	Мнмнио	23160502054496120	2325051105156870	2023-05-29 19:03:44.020583
23290509055590018	deny	4500	\nТестируемся	Фильтры квартиры\nХолодильник\nФен\nМикроволновка\nПерловка\nРецепт плова в подарок	23160502054496120	2325051105156870	2023-05-29 18:59:55.900622
23290509054507917	deny	4500	\nТестируемся	Фильтры квартиры\nХолодильник\nФен\nМикроволновка\nПерловка\nРецепт плова в подарок	23160502054496120	2325051105156870	2023-05-29 18:59:45.079943
2329051005413896	active	3500	Тест 2х фото	2 фото	23160502054496120	2329051005384542	2023-05-29 19:37:41.391706
2305060106499221	active	5000	Тест	Тестоаая	23260108010714212	2305060106445610	2023-06-04 22:07:49.923147
23060602061143114	active	3500	Новая квартира	Видовая квартира в центре города с отличным ремонтом. Все удобства включены. Заселение без животных. Без шумных вечеринок и компаний	23160502054496120	2306060106324727	2023-06-06 11:42:11.432123
2329051005068763	active	2900	Тест	Тест фото большого размера\nпроверка редактирования	23160502054496120	2325051105156870	2023-05-29 19:37:06.875788
23290510051115710	moderation	2005	Тест	Тесттт	2316050305542140	23200504051731512	2023-05-29 19:08:11.157919
2329051005557446	archive	3000	Тест с фото	Тест с добавление фото ошибка "не удалось создать объект"	23160502054496120	2329051005384542	2023-05-29 19:05:55.746024
2329051005391770	active	2700	Тест111	Тест фото с редактированием	23160502054496120	2325051105156870	2023-05-29 19:36:39.18082
2324051205150389	archive	2000	Объявление	См вложение с уважением Александр и на сайте не нашел в интернете прочитал а ваше письмо требует ответа не получил от чего отталкиваться и на сайте в личном кабинете на е ещё не оплачивали на не готов выполнить в этом случае мы можем не знаю почему так получилось завершить отладку в этом случае нужно для начала нужно ли нам на сайт вы про меня не будет в Москве и Московской консерватории имени сообщества в сети Интернет не для того чтобы исправить на графике и на сайте не нашел в интернете прочитал а заплатил я все время в пути и способы их устранения не у компьютера и 	2316050305542140	23200504051731512	2023-05-23 21:26:15.039085
23120610063801420	moderation	3500	test	testtest	2217120912271980	23120610060788918	2023-06-12 19:29:38.019065
23120611061409113	active	4500	Тестовое объяв	Тесттест фильтр 	23120611063359611	23120611065833112	2023-06-12 20:25:14.09267
23130609061652212	archive	8000	Тест создания	Описание	2217120912271980	23120610060788918	2023-06-13 18:42:16.525031
\.


--
-- TOC entry 3434 (class 0 OID 41446)
-- Dependencies: 214
-- Data for Name: post_photos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.post_photos (oid, post_oid, file_oid) FROM stdin;
23280408041734913	23270302033460211	23280408041729212
23280408042371615	23270302033460211	23280408042370814
23280408042508719	23270302033460211	23280408042508018
23230502051837417	23230512051959916	23230502051834116
23230502054296119	23230512051959916	23230502054285118
2323050205470430	23230512053981919	23230502054703920
2323050305562302	2323050805184788	2323050305561991
2323050305492994	2323050805184788	2323050305492603
2323050305401059	2323050805184788	2323050305400665
23230503054010510	2323050805184788	2323050305400988
23230503054010511	2323050805184788	2323050305400967
23230503054010512	2323050805184788	2323050305400946
2324051205482246	2323050805184788	2324051205482145
2324051205482247	2323050805184788	2324051205482003
2324051205482248	2323050805184788	2324051205482104
23240512051582512	2323050805184788	23240512051581710
23240512051582513	2323050805184788	23240512051582311
23240504054302716	2323050805184788	23240504054301515
23240505051816620	23240505051733017	23240505051815718
2324050505181660	23240505051733017	23240505051816319
2324051005225423	2324051005213571	2324051005225282
2324051105051407	23240504054231414	2324051105051375
2324051105468159	23240504054231414	2324051105468128
23250512053315412	2324051205469392	23250512053315111
23250512053315413	2324051205469392	23250512053312210
23250501052600117	23250501052561514	23250501052574815
23250501051865420	23250501052561514	23250501051865019
2325051105153836	2325051105124951	2325051105153684
2325051105153837	2325051105124951	2325051105153542
2325051105153839	2325051105124951	2325051105153755
23290510050489812	23290510051115710	23290510050487911
2329051005415412	2329051005391770	2329051005415371
2329051005085585	2329051005068763	2329051005085544
2329051005457309	2329051005413896	2329051005457207
23290510054573010	2329051005413896	2329051005457288
23030604061914613	23290510051115710	23030604061913011
23030604061914614	23290510051115710	23030604061914312
2305060106505743	2305060106499221	2305060106505712
23060602061192717	23060602061143114	23060602061191416
23060602061192718	23060602061143114	23060602061189815
2312061006380931	23120610063801420	2312061006380900
23120611061418816	23120611061409113	23120611061417314
23120611061418817	23120611061409113	23120611061417715
23130609060099111	2329051005391770	23130609060098810
23130609061666014	23130609061652212	23130609061665713
2313060906464354	2329051005391770	2313060906464293
\.


--
-- TOC entry 3438 (class 0 OID 104976)
-- Dependencies: 218
-- Data for Name: rating; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.rating (oid, user_oid, rating_score) FROM stdin;
23250403042978216	2316030203364944	5
2325040604515273	2316030203364944	5
2325040604544824	2316030203364944	5
2325040604044375	2316030203364944	4
2325040604071926	2316030203364944	4
2325040604077207	2316030203364944	4
2325040604106288	2316030203364944	3
2325040604121099	2316030203364944	3
23250406041270110	2316030203364944	3
23250406041333011	2316030203364944	3
23250406043153612	2316030203364944	5
23250406043293313	2316030203364944	5
23250406043333114	2316030203364944	5
23250406043351415	2316030203364944	5
23250406043378116	2316030203364944	5
23250406044007717	2316030203364944	4
23250406044068218	2316030203364944	4
23250406044083819	2316030203364944	4
23250406044102920	2316030203364944	4
23080611062164119	23160502054496120	4
23080611062164120	23160502054496120	5
2308061106216410	23160502054496120	5
2308061106216411	23160502054496120	4
2308061106216412	23160502054496120	4
2308061106216413	23160502054496120	5
2308061106216414	23160502054496120	4
2308061106147565	23160308030344414	5
2308061106147566	23160308030344414	5
2308061106147567	23160308030344414	4
2308061106147568	23160308030344414	4
2308061106147569	23160308030344414	5
23080611061475610	23160308030344414	4
\.


--
-- TOC entry 3439 (class 0 OID 105101)
-- Dependencies: 219
-- Data for Name: rent_offer; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.rent_offer (oid, rent_offer_resolve, rent_offer_start, rent_offer_end, rent_offer_renter, rent_offer_post, rent_offer_need_rating) FROM stdin;
23050404040848319	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
2305040404441470	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
2306040404135421	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
2306040404117512	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
2306040404475883	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
2306040404487024	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
2306040404406155	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
2306040404107117	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
2306040404392508	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
23060404045194810	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
23060405042916711	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
23060405043757412	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
23060405040566713	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
23060405043245714	\N	2023-04-05 12:00:00	2023-04-05 12:00:00	23160304030761711	23270302033460211	f
23230510052209813	t	2023-05-24 12:00:00	2023-05-24 12:00:00	23160304030761711	23230512051959916	f
23230510053577017	t	2023-05-25 12:00:00	2023-05-26 12:00:00	2316050305542140	23230512051959916	f
23290509055769710	t	2023-05-30 12:00:00	2023-05-31 12:00:00	23160308030344414	2325051105124951	f
23080609061059311	t	2023-06-09 12:00:00	2023-06-15 12:00:00	2316050305542140	2305060106499221	f
23080611063997718	t	2023-06-09 12:00:00	2023-06-15 12:00:00	23160308030344414	23060602061143114	f
2310061206163354	\N	2023-06-30 12:00:00	2023-07-09 12:00:00	2316050305542140	2305060106499221	f
2311060206219627	\N	2023-06-10 12:00:00	2023-06-14 12:00:00	23160502054496120	2323051205580331	f
2310060306447770	t	2023-06-10 12:00:00	2023-06-14 12:00:00	2316050305542140	23060602061143114	f
2310060706024341	f	2023-09-22 12:00:00	2023-10-07 12:00:00	2316050305542140	23060602061143114	f
2310060906030672	f	2023-08-16 12:00:00	2023-08-19 12:00:00	2316050305542140	23060602061143114	f
2310060906203113	t	2023-08-16 12:00:00	2023-08-19 12:00:00	2316050305542140	23060602061143114	f
2310060606347005	f	2023-06-14 12:00:00	2023-06-16 12:00:00	2316050305542140	23060602061143114	f
2310061006093196	t	2023-06-10 12:00:00	2023-06-16 12:00:00	23260108010714212	2329051005413896	f
2311060206250798	t	2023-06-14 12:00:00	2023-06-17 12:00:00	2316050305542140	23060602061143114	f
23120610060976419	\N	2023-06-15 12:00:00	2023-06-24 12:00:00	2217120912271980	2305060106499221	f
23120601063515615	t	2023-06-16 12:00:00	2023-07-08 12:00:00	2316050305542140	23060602061143114	f
2312061006263772	f	2023-07-01 12:00:00	2023-07-05 12:00:00	2217120912271980	23060602061143114	f
23120611060103020	t	2023-08-11 12:00:00	2023-08-16 12:00:00	23120611063359611	23060602061143114	f
2312061106527223	\N	2023-06-29 12:00:00	2023-07-02 12:00:00	2316050305542140	23120611061409113	f
2312061106307952	f	2023-09-12 12:00:00	2023-09-16 12:00:00	2316050305542140	23060602061143114	f
2313061206269184	t	2023-06-28 12:00:00	2023-07-02 12:00:00	2217120912271980	2329051005413896	f
2313061206349067	f	2023-07-05 12:00:00	2023-07-07 12:00:00	2217120912271980	2329051005413896	f
23130609065937315	t	2023-07-06 12:00:00	2023-07-08 12:00:00	2217120912271980	2329051005413896	f
23130609060479018	t	2023-08-25 12:00:00	2023-08-26 12:00:00	2217120912271980	2329051005413896	f
2313060906178930	t	2023-08-22 12:00:00	2023-08-23 12:00:00	2217120912271980	2329051005068763	f
\.


--
-- TOC entry 3440 (class 0 OID 154604)
-- Dependencies: 220
-- Data for Name: some_table; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.some_table (oid, some_table_attribute) FROM stdin;
2326041104332640	фывафыва
2326041104332641	фывафыва
2326041104332642	фывафыва
2326041104332643	фывафыва
2326041104332644	фываыфва
2327040204409795	asdfasdfasdf
\.


--
-- TOC entry 3431 (class 0 OID 41384)
-- Dependencies: 211
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (oid, user_name, user_birthday, user_reg_date, user_email, user_password, user_phone_number, user_is_confirm, user_telegram_link, user_whatsup_link, user_rating, user_is_admin, user_is_banned) FROM stdin;
2316050105220620	Andrey Andreev	2003-05-16	2023-05-15	specialnij1@yandex.ru	khjhjh1141	+78541235796	f	\N	\N	\N	f	f
23160308034803215	Курдюков Илья ЮрьевичTEST12	2001-08-02	2023-03-16	ilya.kurduykoff@yandex.ru	123453	123643545	t	\N	\N	\N	f	f
2316041204203194	Ступин Борис Игоревич	2001-10-28	2023-04-15	boris.stupin.01@mail.ru	123123	1235678123	f	\N	\N	\N	f	f
2316050105217435	Andrey Andreev	2003-05-16	2023-05-15	specialnij2@asd.ru	khjhjh1141	+78541235798	f	\N	\N	\N	f	f
2316050105087736	Anton	2003-05-16	2023-05-15	qwe@qwe.qwe	qwerty	+71235874526	f	\N	\N	\N	f	f
2316050105482407	Anton	2003-05-16	2023-05-15	qaz@qaz.qaz	qazqaz	+79524852638	f	\N	\N	\N	f	f
2316050105111068	Anton	2003-05-16	2023-05-15	qasqas	qqqqqq	+79628453625	f	\N	\N	\N	f	f
23160501050608713	Andrey Andreev	2003-05-16	2023-05-15	specialnij3@asd.ru	khjhjh1141	+78541235791	f	\N	\N	\N	f	f
23160501051172216	Anton	2003-05-16	2023-05-15	as@as.as	zxcvb	+78542563633	f	\N	\N	\N	f	f
23160502054364919	Ancghnnjbj	2003-05-16	2023-05-16	asccgg@as.as	asdfgh	+79658745623	f	\N	\N	\N	f	f
23160501053520617	Andrey Andreev	2003-05-16	2023-05-15	specialnij@yandex.ru	khjhjh1141	+79627804587	t	\N	\N	\N	f	f
23260108010714212	Ступин Борис Игоревич	2001-10-28	2023-01-26	test	123123	+79525960683	f	@hackkraken	\N	\N	f	f
2316050305542140	Иван Иванов	2003-05-16	2023-05-16	zxc@zxc.zxc	zxczxc	9654258949	f	@asdfghbb	+79999999988	\N	t	f
23160304030761711	Бабак Ольга Григорьевна	2002-02-24	2023-03-16	olga.babak48@gmail.com	123123	123	t	\N	\N	\N	f	f
2316050305473441	Илюха Тестик	2003-05-13	2023-05-16	notvalid@gmail.com	1111	+79464646496	f	\N	\N	\N	f	f
23120611063359611	ИльяТест	2003-06-01	2023-06-12	testdiplom111@gmail.com	1234	+79515510088	f	\N	\N	\N	f	f
23060602061193611	Iluyshka	1992-06-24	2023-06-06	ilyatest@ilya.test	112233	+79510051005	f	\N	\N	\N	f	f
23120611060939710	ИльяЮзер	2003-06-01	2023-06-12	testdiplomai19@gmail.com	1234	+79515551111	f	\N	\N	\N	f	t
23160501052889215	Anton	2003-05-16	2023-05-15	specialnij5@yandex.ru	qwerty	+79622584536	f	\N	\N	\N	f	f
2311041204489260	Травка Михаил	2003-01-24	2023-04-11	mihailwhoss@gmail.com	123123	4555637483	f	\N	\N	\N	f	f
2217120912271980	asd	2001-12-01	2022-12-15	asdasd	123123	+79991112233	f	\N	\N	\N	f	f
23160308030344414	Курдюков Илья ЮрьевичTEST1	2001-08-02	2023-03-16	ilya.kurdyukoff@gmail.com	123453	1236545	t	\N	\N	4.4625000000000000000	t	f
2316030203364944	Cтупин Борис	2001-10-28	2022-03-16	boris.stupin01@gmail.com	123123	79999999999	t	\N	\N	4.4	t	f
23160502054496120	Илюшка Тест	2003-05-31	2023-05-16	gimmigrimmi@gmail.com	112233	+79935850751	t	@aylivokyudruk	+79935850751	4.57	f	f
\.


--
-- TOC entry 3437 (class 0 OID 92213)
-- Dependencies: 217
-- Data for Name: users_avatar_file; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users_avatar_file (oid, user_oid, file_oid) FROM stdin;
23030606065736620	2316050305542140	23030606065735919
23120611061114219	23120611063359611	23120611061113818
\.


--
-- TOC entry 3446 (class 0 OID 0)
-- Dependencies: 209
-- Name: users_sequence_identificator; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_sequence_identificator', 15, true);


--
-- TOC entry 3238 (class 2606 OID 41374)
-- Name: base_entity base_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.base_entity
    ADD CONSTRAINT base_entity_pkey PRIMARY KEY (oid);


--
-- TOC entry 3251 (class 2606 OID 129838)
-- Name: contract contract_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_pk PRIMARY KEY (oid);


--
-- TOC entry 3254 (class 2606 OID 41442)
-- Name: files files_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pk PRIMARY KEY (oid);


--
-- TOC entry 3261 (class 2606 OID 41490)
-- Name: flat flat_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_pk PRIMARY KEY (oid);


--
-- TOC entry 3240 (class 2606 OID 41397)
-- Name: users oid; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT oid PRIMARY KEY (oid);


--
-- TOC entry 3256 (class 2606 OID 41453)
-- Name: post_photos post_photos_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT post_photos_pk PRIMARY KEY (oid);


--
-- TOC entry 3258 (class 2606 OID 41466)
-- Name: post post_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_pk PRIMARY KEY (oid);


--
-- TOC entry 3268 (class 2606 OID 129816)
-- Name: rating rating_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rating
    ADD CONSTRAINT rating_pk PRIMARY KEY (oid);


--
-- TOC entry 3270 (class 2606 OID 105108)
-- Name: rent_offer rent_offer_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rent_offer
    ADD CONSTRAINT rent_offer_pk PRIMARY KEY (oid);


--
-- TOC entry 3272 (class 2606 OID 154611)
-- Name: some_table some-table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.some_table
    ADD CONSTRAINT "some-table_pkey" PRIMARY KEY (oid);


--
-- TOC entry 3263 (class 2606 OID 129822)
-- Name: users_avatar_file user_avatar_file_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_avatar_file
    ADD CONSTRAINT user_avatar_file_pk PRIMARY KEY (oid);


--
-- TOC entry 3266 (class 2606 OID 129830)
-- Name: users_avatar_file users_avatar_file_unique_file_oid; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_avatar_file
    ADD CONSTRAINT users_avatar_file_unique_file_oid UNIQUE (file_oid);


--
-- TOC entry 3242 (class 2606 OID 67636)
-- Name: users users_email_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_unique UNIQUE (user_email);


--
-- TOC entry 3245 (class 2606 OID 67638)
-- Name: users users_phone_number_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_number_unique UNIQUE (user_phone_number);


--
-- TOC entry 3247 (class 2606 OID 104973)
-- Name: users users_telegram_link_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_telegram_link_unique UNIQUE (user_telegram_link);


--
-- TOC entry 3249 (class 2606 OID 104975)
-- Name: users users_whatsup_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_whatsup_unique UNIQUE (user_whatsup_link);


--
-- TOC entry 3252 (class 1259 OID 100413)
-- Name: files_file_path_uindex; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX files_file_path_uindex ON public.files USING btree (file_path);


--
-- TOC entry 3259 (class 1259 OID 104963)
-- Name: flat_flat_address_uindex; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX flat_flat_address_uindex ON public.flat USING btree (flat_address);


--
-- TOC entry 3264 (class 1259 OID 129823)
-- Name: user_avatar_file_user_uindex; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX user_avatar_file_user_uindex ON public.users_avatar_file USING btree (user_oid);


--
-- TOC entry 3243 (class 1259 OID 138016)
-- Name: users_name_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX users_name_index ON public.users USING btree (user_name);


--
-- TOC entry 3288 (class 2620 OID 41444)
-- Name: files files_updated_at_checkup; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER files_updated_at_checkup BEFORE INSERT ON public.files FOR EACH ROW EXECUTE FUNCTION public.updated_at_check();


--
-- TOC entry 3289 (class 2620 OID 104992)
-- Name: rating recalculate_rating_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER recalculate_rating_trigger AFTER INSERT ON public.rating FOR EACH ROW EXECUTE FUNCTION public.recalc_rating();


--
-- TOC entry 3277 (class 2606 OID 105184)
-- Name: contract contract_files_oid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_files_oid_fk FOREIGN KEY (contract_document) REFERENCES public.files(oid);


--
-- TOC entry 3275 (class 2606 OID 103197)
-- Name: contract contract_flat_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_flat_null_fk FOREIGN KEY (contrcat_target_flat) REFERENCES public.flat(oid);


--
-- TOC entry 3276 (class 2606 OID 103202)
-- Name: contract contract_post_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_post_null_fk FOREIGN KEY (contract_target_post) REFERENCES public.post(oid);


--
-- TOC entry 3273 (class 2606 OID 41425)
-- Name: contract contract_users_creator_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_users_creator_fk FOREIGN KEY (contract_renter_oid) REFERENCES public.users(oid);


--
-- TOC entry 3274 (class 2606 OID 41430)
-- Name: contract contract_users_signatory_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_users_signatory_fk FOREIGN KEY (contract_owner_oid) REFERENCES public.users(oid);


--
-- TOC entry 3282 (class 2606 OID 41491)
-- Name: flat flat_users_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_users_null_fk FOREIGN KEY (flat_owner) REFERENCES public.users(oid);


--
-- TOC entry 3279 (class 2606 OID 41477)
-- Name: post_photos foreign_key_name; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT foreign_key_name FOREIGN KEY (post_oid) REFERENCES public.post(oid);


--
-- TOC entry 3280 (class 2606 OID 41496)
-- Name: post post_foreign_key_flat; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_foreign_key_flat FOREIGN KEY (post_flat_oid) REFERENCES public.flat(oid);


--
-- TOC entry 3278 (class 2606 OID 41454)
-- Name: post_photos post_photos_files_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT post_photos_files_null_fk FOREIGN KEY (file_oid) REFERENCES public.files(oid);


--
-- TOC entry 3281 (class 2606 OID 41467)
-- Name: post post_users_creator_oid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_users_creator_oid_fk FOREIGN KEY (post_creator_oid) REFERENCES public.users(oid);


--
-- TOC entry 3285 (class 2606 OID 129839)
-- Name: rating rating_users_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rating
    ADD CONSTRAINT rating_users_null_fk FOREIGN KEY (user_oid) REFERENCES public.users(oid);


--
-- TOC entry 3287 (class 2606 OID 105114)
-- Name: rent_offer rent_offer_post_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rent_offer
    ADD CONSTRAINT rent_offer_post_null_fk FOREIGN KEY (rent_offer_post) REFERENCES public.post(oid);


--
-- TOC entry 3286 (class 2606 OID 105109)
-- Name: rent_offer rent_offer_users_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rent_offer
    ADD CONSTRAINT rent_offer_users_null_fk FOREIGN KEY (rent_offer_renter) REFERENCES public.users(oid);


--
-- TOC entry 3283 (class 2606 OID 174016)
-- Name: users_avatar_file user_avatar_file_file_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_avatar_file
    ADD CONSTRAINT user_avatar_file_file_fk FOREIGN KEY (file_oid) REFERENCES public.files(oid) ON DELETE CASCADE;


--
-- TOC entry 3284 (class 2606 OID 174021)
-- Name: users_avatar_file user_avatar_file_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_avatar_file
    ADD CONSTRAINT user_avatar_file_user_fk FOREIGN KEY (user_oid) REFERENCES public.users(oid) ON DELETE CASCADE;


-- Completed on 2023-06-14 14:12:20

--
-- PostgreSQL database dump complete
--

