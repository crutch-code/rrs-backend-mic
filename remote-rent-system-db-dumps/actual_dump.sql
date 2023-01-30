toc.dat                                                                                             0000600 0004000 0002000 00000035150 14347604642 0014455 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        PGDMP       6    %                z            remote-rent-system    14.3    14.3 ,    :           0    0    ENCODING    ENCODING     !   SET client_encoding = 'WIN1251';
                      false         ;           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false         <           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false         =           1262    41353    remote-rent-system    DATABASE     q   CREATE DATABASE "remote-rent-system" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'Russian_Russia.1251';
 $   DROP DATABASE "remote-rent-system";
             	   vattghern    false         �            1255    41363    gen_id()    FUNCTION     �   CREATE FUNCTION public.gen_id() RETURNS bigint
    LANGUAGE plpgsql
    AS $$
    begin
        return (select to_char(current_timestamp + '3 hour', 'YYddMMHHmmssms') || nextval('users_sequence_identificator')::text);
    end;
$$;
    DROP FUNCTION public.gen_id();
       public       	   vattghern    false         �            1255    41443    updated_at_check()    FUNCTION     �   CREATE FUNCTION public.updated_at_check() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
        begin
            if new.updated_at is null then
                new.updated_at := current_timestamp;
            end if;
        end;
    $$;
 )   DROP FUNCTION public.updated_at_check();
       public       	   vattghern    false         �            1259    41368    base_entity    TABLE     H   CREATE TABLE public.base_entity (
    oid character varying NOT NULL
);
    DROP TABLE public.base_entity;
       public         heap 	   vattghern    false         �            1259    41416    contract    TABLE       CREATE TABLE public.contract (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    contract_date date DEFAULT CURRENT_DATE NOT NULL,
    contract_doc_path text,
    contract_creator_oid character varying NOT NULL,
    contract_signatory_oid character varying
);
    DROP TABLE public.contract;
       public         heap 	   vattghern    false    218         �            1259    41435    files    TABLE       CREATE TABLE public.files (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    file_path character varying NOT NULL,
    file_size double precision NOT NULL,
    updated_at timestamp without time zone,
    CONSTRAINT check_updated_at CHECK ((updated_at <= now()))
);
    DROP TABLE public.files;
       public         heap 	   vattghern    false    218         �            1259    41482    flat    TABLE       CREATE TABLE public.flat (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    flat_owner character varying NOT NULL,
    flat_address character varying(40) NOT NULL,
    flat_square numeric NOT NULL,
    CONSTRAINT check_square CHECK ((flat_square > 0.0))
);
    DROP TABLE public.flat;
       public         heap 	   vattghern    false    218         �            1259    41459    post    TABLE     �  CREATE TABLE public.post (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    post_status character varying(10) NOT NULL,
    price numeric NOT NULL,
    post_title character varying(15) NOT NULL,
    post_information text,
    security_ticket character varying,
    post_creator_oid character varying NOT NULL,
    post_moderator_oid character varying,
    post_flat_oid character varying NOT NULL
);
    DROP TABLE public.post;
       public         heap 	   vattghern    false    218         �            1259    41446    post_photos    TABLE     �   CREATE TABLE public.post_photos (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    post_oid character varying NOT NULL,
    file_oid character varying NOT NULL
);
    DROP TABLE public.post_photos;
       public         heap 	   vattghern    false    218         �            1259    41501    security_ticket    TABLE       CREATE TABLE public.security_ticket (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    security_ticket_last_update character varying,
    security_ticket_control_photos character varying,
    security_ticket_status character varying(15) NOT NULL
);
 #   DROP TABLE public.security_ticket;
       public         heap 	   vattghern    false    218         �            1259    41384    users    TABLE       CREATE TABLE public.users (
    user_name character varying NOT NULL,
    gender character varying NOT NULL,
    user_birthday date NOT NULL,
    user_reg_date date NOT NULL,
    contacts text,
    user_nickname character varying NOT NULL,
    user_password character varying NOT NULL,
    user_rating double precision,
    avatar_path text,
    CONSTRAINT users_gender_check CHECK ((((gender)::text = 'male'::text) OR ((gender)::text = 'female'::text))),
    CONSTRAINT users_user_birthday_check CHECK ((user_birthday < (now() - '18 years'::interval))),
    CONSTRAINT users_user_rating_check CHECK (((user_rating >= (0)::double precision) AND (user_rating <= (5.0)::double precision))),
    CONSTRAINT users_user_reg_date_check CHECK ((user_reg_date <= now()))
)
INHERITS (public.base_entity);
    DROP TABLE public.users;
       public         heap 	   vattghern    false    210         �            1259    41358    users_sequence_identificator    SEQUENCE     �   CREATE SEQUENCE public.users_sequence_identificator
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 20
    CACHE 1
    CYCLE;
 3   DROP SEQUENCE public.users_sequence_identificator;
       public       	   vattghern    false         z           2604    41398 	   users oid    DEFAULT     L   ALTER TABLE ONLY public.users ALTER COLUMN oid SET DEFAULT public.gen_id();
 8   ALTER TABLE public.users ALTER COLUMN oid DROP DEFAULT;
       public       	   vattghern    false    211    218         0          0    41368    base_entity 
   TABLE DATA           *   COPY public.base_entity (oid) FROM stdin;
    public       	   vattghern    false    210       3376.dat 2          0    41416    contract 
   TABLE DATA           w   COPY public.contract (oid, contract_date, contract_doc_path, contract_creator_oid, contract_signatory_oid) FROM stdin;
    public       	   vattghern    false    212       3378.dat 3          0    41435    files 
   TABLE DATA           F   COPY public.files (oid, file_path, file_size, updated_at) FROM stdin;
    public       	   vattghern    false    213       3379.dat 6          0    41482    flat 
   TABLE DATA           J   COPY public.flat (oid, flat_owner, flat_address, flat_square) FROM stdin;
    public       	   vattghern    false    216       3382.dat 5          0    41459    post 
   TABLE DATA           �   COPY public.post (oid, post_status, price, post_title, post_information, security_ticket, post_creator_oid, post_moderator_oid, post_flat_oid) FROM stdin;
    public       	   vattghern    false    215       3381.dat 4          0    41446    post_photos 
   TABLE DATA           >   COPY public.post_photos (oid, post_oid, file_oid) FROM stdin;
    public       	   vattghern    false    214       3380.dat 7          0    41501    security_ticket 
   TABLE DATA           �   COPY public.security_ticket (oid, security_ticket_last_update, security_ticket_control_photos, security_ticket_status) FROM stdin;
    public       	   vattghern    false    217       3383.dat 1          0    41384    users 
   TABLE DATA           �   COPY public.users (oid, user_name, gender, user_birthday, user_reg_date, contacts, user_nickname, user_password, user_rating, avatar_path) FROM stdin;
    public       	   vattghern    false    211       3377.dat >           0    0    users_sequence_identificator    SEQUENCE SET     J   SELECT pg_catalog.setval('public.users_sequence_identificator', 0, true);
          public       	   vattghern    false    209         �           2606    41374    base_entity base_entity_pkey 
   CONSTRAINT     [   ALTER TABLE ONLY public.base_entity
    ADD CONSTRAINT base_entity_pkey PRIMARY KEY (oid);
 F   ALTER TABLE ONLY public.base_entity DROP CONSTRAINT base_entity_pkey;
       public         	   vattghern    false    210         �           2606    41424    contract contract_pk 
   CONSTRAINT     S   ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_pk PRIMARY KEY (oid);
 >   ALTER TABLE ONLY public.contract DROP CONSTRAINT contract_pk;
       public         	   vattghern    false    212         �           2606    41442    files files_pk 
   CONSTRAINT     M   ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pk PRIMARY KEY (oid);
 8   ALTER TABLE ONLY public.files DROP CONSTRAINT files_pk;
       public         	   vattghern    false    213         �           2606    41490    flat flat_pk 
   CONSTRAINT     K   ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_pk PRIMARY KEY (oid);
 6   ALTER TABLE ONLY public.flat DROP CONSTRAINT flat_pk;
       public         	   vattghern    false    216         �           2606    41397 	   users oid 
   CONSTRAINT     H   ALTER TABLE ONLY public.users
    ADD CONSTRAINT oid PRIMARY KEY (oid);
 3   ALTER TABLE ONLY public.users DROP CONSTRAINT oid;
       public         	   vattghern    false    211         �           2606    41453    post_photos post_photos_pk 
   CONSTRAINT     Y   ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT post_photos_pk PRIMARY KEY (oid);
 D   ALTER TABLE ONLY public.post_photos DROP CONSTRAINT post_photos_pk;
       public         	   vattghern    false    214         �           2606    41466    post post_pk 
   CONSTRAINT     K   ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_pk PRIMARY KEY (oid);
 6   ALTER TABLE ONLY public.post DROP CONSTRAINT post_pk;
       public         	   vattghern    false    215         �           2606    41513 "   security_ticket security_ticket_pk 
   CONSTRAINT     a   ALTER TABLE ONLY public.security_ticket
    ADD CONSTRAINT security_ticket_pk PRIMARY KEY (oid);
 L   ALTER TABLE ONLY public.security_ticket DROP CONSTRAINT security_ticket_pk;
       public         	   vattghern    false    217         �           2606    41394    users users_user_nickname_key 
   CONSTRAINT     a   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_user_nickname_key UNIQUE (user_nickname);
 G   ALTER TABLE ONLY public.users DROP CONSTRAINT users_user_nickname_key;
       public         	   vattghern    false    211         �           2620    41444    files files_updated_at_checkup    TRIGGER        CREATE TRIGGER files_updated_at_checkup BEFORE INSERT ON public.files FOR EACH ROW EXECUTE FUNCTION public.updated_at_check();
 7   DROP TRIGGER files_updated_at_checkup ON public.files;
       public       	   vattghern    false    213    219         �           2606    41425 "   contract contract_users_creator_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_users_creator_fk FOREIGN KEY (contract_creator_oid) REFERENCES public.users(oid);
 L   ALTER TABLE ONLY public.contract DROP CONSTRAINT contract_users_creator_fk;
       public       	   vattghern    false    211    3211    212         �           2606    41430 $   contract contract_users_signatory_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_users_signatory_fk FOREIGN KEY (contract_signatory_oid) REFERENCES public.users(oid);
 N   ALTER TABLE ONLY public.contract DROP CONSTRAINT contract_users_signatory_fk;
       public       	   vattghern    false    211    212    3211         �           2606    41491    flat flat_users_null_fk    FK CONSTRAINT     z   ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_users_null_fk FOREIGN KEY (flat_owner) REFERENCES public.users(oid);
 A   ALTER TABLE ONLY public.flat DROP CONSTRAINT flat_users_null_fk;
       public       	   vattghern    false    216    3211    211         �           2606    41477    post_photos foreign_key_name    FK CONSTRAINT     |   ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT foreign_key_name FOREIGN KEY (post_oid) REFERENCES public.post(oid);
 F   ALTER TABLE ONLY public.post_photos DROP CONSTRAINT foreign_key_name;
       public       	   vattghern    false    215    214    3221         �           2606    41496    post post_foreign_key_flat    FK CONSTRAINT        ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_foreign_key_flat FOREIGN KEY (post_flat_oid) REFERENCES public.flat(oid);
 D   ALTER TABLE ONLY public.post DROP CONSTRAINT post_foreign_key_flat;
       public       	   vattghern    false    216    3223    215         �           2606    41454 %   post_photos post_photos_files_null_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT post_photos_files_null_fk FOREIGN KEY (file_oid) REFERENCES public.files(oid);
 O   ALTER TABLE ONLY public.post_photos DROP CONSTRAINT post_photos_files_null_fk;
       public       	   vattghern    false    213    3217    214         �           2606    41467    post post_users_creator_oid_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_users_creator_oid_fk FOREIGN KEY (post_creator_oid) REFERENCES public.users(oid);
 H   ALTER TABLE ONLY public.post DROP CONSTRAINT post_users_creator_oid_fk;
       public       	   vattghern    false    3211    211    215         �           2606    41472     post post_users_moderator_oid_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_users_moderator_oid_fk FOREIGN KEY (post_moderator_oid) REFERENCES public.users(oid);
 J   ALTER TABLE ONLY public.post DROP CONSTRAINT post_users_moderator_oid_fk;
       public       	   vattghern    false    215    3211    211         �           2606    41514 -   security_ticket security_ticket_files_null_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.security_ticket
    ADD CONSTRAINT security_ticket_files_null_fk FOREIGN KEY (security_ticket_control_photos) REFERENCES public.files(oid);
 W   ALTER TABLE ONLY public.security_ticket DROP CONSTRAINT security_ticket_files_null_fk;
       public       	   vattghern    false    217    213    3217                                                                                                                                                                                                                                                                                                                                                                                                                                3376.dat                                                                                            0000600 0004000 0002000 00000000005 14347604642 0014261 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           3378.dat                                                                                            0000600 0004000 0002000 00000000005 14347604642 0014263 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           3379.dat                                                                                            0000600 0004000 0002000 00000000005 14347604642 0014264 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           3382.dat                                                                                            0000600 0004000 0002000 00000000005 14347604642 0014256 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           3381.dat                                                                                            0000600 0004000 0002000 00000000005 14347604642 0014255 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           3380.dat                                                                                            0000600 0004000 0002000 00000000005 14347604642 0014254 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           3383.dat                                                                                            0000600 0004000 0002000 00000000005 14347604642 0014257 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           3377.dat                                                                                            0000600 0004000 0002000 00000000120 14347604642 0014260 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        2217120912271980	asd	male	2001-12-01	2022-12-15	asdasd	user_1	123123	\N	\N
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                restore.sql                                                                                         0000600 0004000 0002000 00000030742 14347604642 0015404 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        --
-- NOTE:
--
-- File paths need to be edited. Search for $$PATH$$ and
-- replace it with the path to the directory containing
-- the extracted data files.
--
--
-- PostgreSQL database dump
--

-- Dumped from database version 14.3
-- Dumped by pg_dump version 14.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'WIN1251';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE "remote-rent-system";
--
-- Name: remote-rent-system; Type: DATABASE; Schema: -; Owner: vattghern
--

CREATE DATABASE "remote-rent-system" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'Russian_Russia.1251';


ALTER DATABASE "remote-rent-system" OWNER TO vattghern;

\connect -reuse-previous=on "dbname='remote-rent-system'"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'WIN1251';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: gen_id(); Type: FUNCTION; Schema: public; Owner: vattghern
--

CREATE FUNCTION public.gen_id() RETURNS bigint
    LANGUAGE plpgsql
    AS $$
    begin
        return (select to_char(current_timestamp + '3 hour', 'YYddMMHHmmssms') || nextval('users_sequence_identificator')::text);
    end;
$$;


ALTER FUNCTION public.gen_id() OWNER TO vattghern;

--
-- Name: updated_at_check(); Type: FUNCTION; Schema: public; Owner: vattghern
--

CREATE FUNCTION public.updated_at_check() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
        begin
            if new.updated_at is null then
                new.updated_at := current_timestamp;
            end if;
        end;
    $$;


ALTER FUNCTION public.updated_at_check() OWNER TO vattghern;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: base_entity; Type: TABLE; Schema: public; Owner: vattghern
--

CREATE TABLE public.base_entity (
    oid character varying NOT NULL
);


ALTER TABLE public.base_entity OWNER TO vattghern;

--
-- Name: contract; Type: TABLE; Schema: public; Owner: vattghern
--

CREATE TABLE public.contract (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    contract_date date DEFAULT CURRENT_DATE NOT NULL,
    contract_doc_path text,
    contract_creator_oid character varying NOT NULL,
    contract_signatory_oid character varying
);


ALTER TABLE public.contract OWNER TO vattghern;

--
-- Name: files; Type: TABLE; Schema: public; Owner: vattghern
--

CREATE TABLE public.files (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    file_path character varying NOT NULL,
    file_size double precision NOT NULL,
    updated_at timestamp without time zone,
    CONSTRAINT check_updated_at CHECK ((updated_at <= now()))
);


ALTER TABLE public.files OWNER TO vattghern;

--
-- Name: flat; Type: TABLE; Schema: public; Owner: vattghern
--

CREATE TABLE public.flat (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    flat_owner character varying NOT NULL,
    flat_address character varying(40) NOT NULL,
    flat_square numeric NOT NULL,
    CONSTRAINT check_square CHECK ((flat_square > 0.0))
);


ALTER TABLE public.flat OWNER TO vattghern;

--
-- Name: post; Type: TABLE; Schema: public; Owner: vattghern
--

CREATE TABLE public.post (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    post_status character varying(10) NOT NULL,
    price numeric NOT NULL,
    post_title character varying(15) NOT NULL,
    post_information text,
    security_ticket character varying,
    post_creator_oid character varying NOT NULL,
    post_moderator_oid character varying,
    post_flat_oid character varying NOT NULL
);


ALTER TABLE public.post OWNER TO vattghern;

--
-- Name: post_photos; Type: TABLE; Schema: public; Owner: vattghern
--

CREATE TABLE public.post_photos (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    post_oid character varying NOT NULL,
    file_oid character varying NOT NULL
);


ALTER TABLE public.post_photos OWNER TO vattghern;

--
-- Name: security_ticket; Type: TABLE; Schema: public; Owner: vattghern
--

CREATE TABLE public.security_ticket (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    security_ticket_last_update character varying,
    security_ticket_control_photos character varying,
    security_ticket_status character varying(15) NOT NULL
);


ALTER TABLE public.security_ticket OWNER TO vattghern;

--
-- Name: users; Type: TABLE; Schema: public; Owner: vattghern
--

CREATE TABLE public.users (
    user_name character varying NOT NULL,
    gender character varying NOT NULL,
    user_birthday date NOT NULL,
    user_reg_date date NOT NULL,
    contacts text,
    user_nickname character varying NOT NULL,
    user_password character varying NOT NULL,
    user_rating double precision,
    avatar_path text,
    CONSTRAINT users_gender_check CHECK ((((gender)::text = 'male'::text) OR ((gender)::text = 'female'::text))),
    CONSTRAINT users_user_birthday_check CHECK ((user_birthday < (now() - '18 years'::interval))),
    CONSTRAINT users_user_rating_check CHECK (((user_rating >= (0)::double precision) AND (user_rating <= (5.0)::double precision))),
    CONSTRAINT users_user_reg_date_check CHECK ((user_reg_date <= now()))
)
INHERITS (public.base_entity);


ALTER TABLE public.users OWNER TO vattghern;

--
-- Name: users_sequence_identificator; Type: SEQUENCE; Schema: public; Owner: vattghern
--

CREATE SEQUENCE public.users_sequence_identificator
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 20
    CACHE 1
    CYCLE;


ALTER TABLE public.users_sequence_identificator OWNER TO vattghern;

--
-- Name: users oid; Type: DEFAULT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.users ALTER COLUMN oid SET DEFAULT public.gen_id();


--
-- Data for Name: base_entity; Type: TABLE DATA; Schema: public; Owner: vattghern
--

COPY public.base_entity (oid) FROM stdin;
\.
COPY public.base_entity (oid) FROM '$$PATH$$/3376.dat';

--
-- Data for Name: contract; Type: TABLE DATA; Schema: public; Owner: vattghern
--

COPY public.contract (oid, contract_date, contract_doc_path, contract_creator_oid, contract_signatory_oid) FROM stdin;
\.
COPY public.contract (oid, contract_date, contract_doc_path, contract_creator_oid, contract_signatory_oid) FROM '$$PATH$$/3378.dat';

--
-- Data for Name: files; Type: TABLE DATA; Schema: public; Owner: vattghern
--

COPY public.files (oid, file_path, file_size, updated_at) FROM stdin;
\.
COPY public.files (oid, file_path, file_size, updated_at) FROM '$$PATH$$/3379.dat';

--
-- Data for Name: flat; Type: TABLE DATA; Schema: public; Owner: vattghern
--

COPY public.flat (oid, flat_owner, flat_address, flat_square) FROM stdin;
\.
COPY public.flat (oid, flat_owner, flat_address, flat_square) FROM '$$PATH$$/3382.dat';

--
-- Data for Name: post; Type: TABLE DATA; Schema: public; Owner: vattghern
--

COPY public.post (oid, post_status, price, post_title, post_information, security_ticket, post_creator_oid, post_moderator_oid, post_flat_oid) FROM stdin;
\.
COPY public.post (oid, post_status, price, post_title, post_information, security_ticket, post_creator_oid, post_moderator_oid, post_flat_oid) FROM '$$PATH$$/3381.dat';

--
-- Data for Name: post_photos; Type: TABLE DATA; Schema: public; Owner: vattghern
--

COPY public.post_photos (oid, post_oid, file_oid) FROM stdin;
\.
COPY public.post_photos (oid, post_oid, file_oid) FROM '$$PATH$$/3380.dat';

--
-- Data for Name: security_ticket; Type: TABLE DATA; Schema: public; Owner: vattghern
--

COPY public.security_ticket (oid, security_ticket_last_update, security_ticket_control_photos, security_ticket_status) FROM stdin;
\.
COPY public.security_ticket (oid, security_ticket_last_update, security_ticket_control_photos, security_ticket_status) FROM '$$PATH$$/3383.dat';

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: vattghern
--

COPY public.users (oid, user_name, gender, user_birthday, user_reg_date, contacts, user_nickname, user_password, user_rating, avatar_path) FROM stdin;
\.
COPY public.users (oid, user_name, gender, user_birthday, user_reg_date, contacts, user_nickname, user_password, user_rating, avatar_path) FROM '$$PATH$$/3377.dat';

--
-- Name: users_sequence_identificator; Type: SEQUENCE SET; Schema: public; Owner: vattghern
--

SELECT pg_catalog.setval('public.users_sequence_identificator', 0, true);


--
-- Name: base_entity base_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.base_entity
    ADD CONSTRAINT base_entity_pkey PRIMARY KEY (oid);


--
-- Name: contract contract_pk; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_pk PRIMARY KEY (oid);


--
-- Name: files files_pk; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pk PRIMARY KEY (oid);


--
-- Name: flat flat_pk; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_pk PRIMARY KEY (oid);


--
-- Name: users oid; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT oid PRIMARY KEY (oid);


--
-- Name: post_photos post_photos_pk; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT post_photos_pk PRIMARY KEY (oid);


--
-- Name: post post_pk; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_pk PRIMARY KEY (oid);


--
-- Name: security_ticket security_ticket_pk; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.security_ticket
    ADD CONSTRAINT security_ticket_pk PRIMARY KEY (oid);


--
-- Name: users users_user_nickname_key; Type: CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_user_nickname_key UNIQUE (user_nickname);


--
-- Name: files files_updated_at_checkup; Type: TRIGGER; Schema: public; Owner: vattghern
--

CREATE TRIGGER files_updated_at_checkup BEFORE INSERT ON public.files FOR EACH ROW EXECUTE FUNCTION public.updated_at_check();


--
-- Name: contract contract_users_creator_fk; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_users_creator_fk FOREIGN KEY (contract_creator_oid) REFERENCES public.users(oid);


--
-- Name: contract contract_users_signatory_fk; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_users_signatory_fk FOREIGN KEY (contract_signatory_oid) REFERENCES public.users(oid);


--
-- Name: flat flat_users_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_users_null_fk FOREIGN KEY (flat_owner) REFERENCES public.users(oid);


--
-- Name: post_photos foreign_key_name; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT foreign_key_name FOREIGN KEY (post_oid) REFERENCES public.post(oid);


--
-- Name: post post_foreign_key_flat; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_foreign_key_flat FOREIGN KEY (post_flat_oid) REFERENCES public.flat(oid);


--
-- Name: post_photos post_photos_files_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT post_photos_files_null_fk FOREIGN KEY (file_oid) REFERENCES public.files(oid);


--
-- Name: post post_users_creator_oid_fk; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_users_creator_oid_fk FOREIGN KEY (post_creator_oid) REFERENCES public.users(oid);


--
-- Name: post post_users_moderator_oid_fk; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_users_moderator_oid_fk FOREIGN KEY (post_moderator_oid) REFERENCES public.users(oid);


--
-- Name: security_ticket security_ticket_files_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: vattghern
--

ALTER TABLE ONLY public.security_ticket
    ADD CONSTRAINT security_ticket_files_null_fk FOREIGN KEY (security_ticket_control_photos) REFERENCES public.files(oid);


--
-- PostgreSQL database dump complete
--

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              