--
-- PostgreSQL database dump
--

-- Dumped from database version 14.3
-- Dumped by pg_dump version 14.3

-- Started on 2023-01-25 14:18:21

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
-- TOC entry 218 (class 1255 OID 41363)
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
-- TOC entry 220 (class 1255 OID 67640)
-- Name: register_new_user(character varying, character varying, character varying, character varying, date, character varying, double precision); Type: PROCEDURE; Schema: public; Owner: -
--

CREATE PROCEDURE public.register_new_user(IN v_user_name character varying, IN v_user_email character varying, IN v_user_phone_number character varying, IN v_user_password character varying, IN v_user_birthday date, IN v_files_path character varying, IN v_file_size double precision)
    LANGUAGE plpgsql
    AS $$
        begin


        end;
    $$;


--
-- TOC entry 219 (class 1255 OID 41443)
-- Name: updated_at_check(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.updated_at_check() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
        begin
            if new.updated_at is null or new.updated_at < current_timestamp + '3 hours'::interval 
                then
                new.updated_at := current_timestamp + '3 hours'::interval;
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
    contract_doc_path character varying,
    contract_creator_oid character varying NOT NULL,
    contract_signatory_oid character varying,
    contract_status character varying(10)
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
    CONSTRAINT check_square CHECK ((flat_square > 0.0))
);


--
-- TOC entry 215 (class 1259 OID 41459)
-- Name: post; Type: TABLE; Schema: public; Owner: -
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
-- TOC entry 217 (class 1259 OID 41501)
-- Name: security_ticket; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.security_ticket (
    oid character varying DEFAULT public.gen_id() NOT NULL,
    security_ticket_last_update character varying,
    security_ticket_control_photos character varying,
    security_ticket_status character varying(15) NOT NULL,
    security_ticket_description text
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
    user_rating double precision,
    avatar_path text DEFAULT '2303010801231333'::bigint,
    user_phone_number text NOT NULL,
    user_is_confirm boolean DEFAULT false,
    CONSTRAINT users_user_birthday_check CHECK ((user_birthday < (now() - '18 years'::interval))),
    CONSTRAINT users_user_rating_check CHECK (((user_rating >= (0)::double precision) AND (user_rating <= (5.0)::double precision))),
    CONSTRAINT users_user_reg_date_check CHECK ((user_reg_date <= now()))
)
INHERITS (public.base_entity);


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
-- TOC entry 3195 (class 2604 OID 41398)
-- Name: users oid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN oid SET DEFAULT public.gen_id();


--
-- TOC entry 3386 (class 0 OID 41368)
-- Dependencies: 210
-- Data for Name: base_entity; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3388 (class 0 OID 41416)
-- Dependencies: 212
-- Data for Name: contract; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3389 (class 0 OID 41435)
-- Dependencies: 213
-- Data for Name: files; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('2303010801316294', '213', 123, '2023-01-03 08:13:23');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('2303010801231333', '/some/default/path/avatar/path/to/file', 0, '2023-01-03 05:12:23.133645');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('2313010401137245', '/try/check/', 23123, '2023-01-13 16:00:13.724');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('2313010501432076', '/sswdsd/ssss/ssss', 23322, '2023-01-13 17:30:43.207692');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('2313010501067367', 'sd', 222, '2023-01-13 17:35:06.736662');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('23230104013663210', '/W:/rrs-backend/storage/files/avatars/a13ecf6b332a1c1daca785b762686691.png', 162725, '2023-01-23 16:14:36.747213');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('23230104010356511', '/W:/rrs-backend/storage/files/avatars/652fdab640f5160454d8afbac8ef2e30.png', 162725, '2023-01-23 16:17:03.672539');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('23230104014912412', '/W:/rrs-backend/storage/files/avatars/b1261c0400d70d1a1b0c4f123fecceb4.png', 162725, '2023-01-23 16:18:49.129339');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('23230104015166813', '/W:/rrs-backend/storage/files/avatars/a9f15ac14f313ea0d45742b3e828d2b6.png', 162725, '2023-01-23 16:19:51.774756');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('23230104014920514', '/W:/rrs-backend/storage/files/avatars/3d777ec3220576990928f4246b8bd41a.png', 162725, '2023-01-23 16:20:49.321497');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('23230104014893115', '/W:/rrs-backend/storage/files/avatars/16022bf97ff913fc8b0979e4ffeb962a.png', 162725, '2023-01-23 16:21:49.043055');
INSERT INTO public.files (oid, file_path, file_size, updated_at) VALUES ('23230106013416816', '/W:/rrs-backend/storage/files/avatars/dbf122557223e6acc8942ac8cf8ac76d.png', 162725, '2023-01-23 18:32:34.287152');


--
-- TOC entry 3392 (class 0 OID 41482)
-- Dependencies: 216
-- Data for Name: flat; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3391 (class 0 OID 41459)
-- Dependencies: 215
-- Data for Name: post; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3390 (class 0 OID 41446)
-- Dependencies: 214
-- Data for Name: post_photos; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3393 (class 0 OID 41501)
-- Dependencies: 217
-- Data for Name: security_ticket; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3387 (class 0 OID 41384)
-- Dependencies: 211
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.users (oid, user_name, user_birthday, user_reg_date, user_email, user_password, user_rating, avatar_path, user_phone_number, user_is_confirm) VALUES ('2217120912271980', 'asd', '2001-12-01', '2022-12-15', 'asdasd', '123123', NULL, '2313010501432076', '+79991112233', false);


--
-- TOC entry 3399 (class 0 OID 0)
-- Dependencies: 209
-- Name: users_sequence_identificator; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_sequence_identificator', 17, true);


--
-- TOC entry 3214 (class 2606 OID 41374)
-- Name: base_entity base_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.base_entity
    ADD CONSTRAINT base_entity_pkey PRIMARY KEY (oid);


--
-- TOC entry 3223 (class 2606 OID 41424)
-- Name: contract contract_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_pk PRIMARY KEY (oid);


--
-- TOC entry 3225 (class 2606 OID 41442)
-- Name: files files_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pk PRIMARY KEY (oid);


--
-- TOC entry 3231 (class 2606 OID 41490)
-- Name: flat flat_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_pk PRIMARY KEY (oid);


--
-- TOC entry 3216 (class 2606 OID 41397)
-- Name: users oid; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT oid PRIMARY KEY (oid);


--
-- TOC entry 3227 (class 2606 OID 41453)
-- Name: post_photos post_photos_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT post_photos_pk PRIMARY KEY (oid);


--
-- TOC entry 3229 (class 2606 OID 41466)
-- Name: post post_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_pk PRIMARY KEY (oid);


--
-- TOC entry 3233 (class 2606 OID 41513)
-- Name: security_ticket security_ticket_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.security_ticket
    ADD CONSTRAINT security_ticket_pk PRIMARY KEY (oid);


--
-- TOC entry 3218 (class 2606 OID 67636)
-- Name: users users_email_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_unique UNIQUE (user_email);


--
-- TOC entry 3221 (class 2606 OID 67638)
-- Name: users users_phone_number_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_number_unique UNIQUE (user_phone_number);


--
-- TOC entry 3219 (class 1259 OID 67639)
-- Name: users_name_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX users_name_index ON public.users USING btree (user_name);


--
-- TOC entry 3245 (class 2620 OID 41444)
-- Name: files files_updated_at_checkup; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER files_updated_at_checkup BEFORE INSERT ON public.files FOR EACH ROW EXECUTE FUNCTION public.updated_at_check();


--
-- TOC entry 3237 (class 2606 OID 41763)
-- Name: contract contract_files_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_files_null_fk FOREIGN KEY (contract_doc_path) REFERENCES public.files(oid);


--
-- TOC entry 3235 (class 2606 OID 41425)
-- Name: contract contract_users_creator_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_users_creator_fk FOREIGN KEY (contract_renter_oid) REFERENCES public.users(oid);


--
-- TOC entry 3236 (class 2606 OID 41430)
-- Name: contract contract_users_signatory_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_users_signatory_fk FOREIGN KEY (contract_owner_oid) REFERENCES public.users(oid);


--
-- TOC entry 3243 (class 2606 OID 41491)
-- Name: flat flat_users_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_users_null_fk FOREIGN KEY (flat_owner) REFERENCES public.users(oid);


--
-- TOC entry 3239 (class 2606 OID 41477)
-- Name: post_photos foreign_key_name; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT foreign_key_name FOREIGN KEY (post_oid) REFERENCES public.post(oid);


--
-- TOC entry 3240 (class 2606 OID 41496)
-- Name: post post_foreign_key_flat; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_foreign_key_flat FOREIGN KEY (post_flat_oid) REFERENCES public.flat(oid);


--
-- TOC entry 3238 (class 2606 OID 41454)
-- Name: post_photos post_photos_files_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post_photos
    ADD CONSTRAINT post_photos_files_null_fk FOREIGN KEY (file_oid) REFERENCES public.files(oid);


--
-- TOC entry 3241 (class 2606 OID 41467)
-- Name: post post_users_creator_oid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_users_creator_oid_fk FOREIGN KEY (post_creator_oid) REFERENCES public.users(oid);


--
-- TOC entry 3242 (class 2606 OID 41472)
-- Name: post post_users_moderator_oid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_users_moderator_oid_fk FOREIGN KEY (post_moderator_oid) REFERENCES public.users(oid);


--
-- TOC entry 3244 (class 2606 OID 41514)
-- Name: security_ticket security_ticket_files_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.security_ticket
    ADD CONSTRAINT security_ticket_files_null_fk FOREIGN KEY (security_ticket_control_photos) REFERENCES public.files(oid);


--
-- TOC entry 3234 (class 2606 OID 59078)
-- Name: users users_files_null_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_files_null_fk FOREIGN KEY (avatar_path) REFERENCES public.files(oid);


-- Completed on 2023-01-25 14:18:21

--
-- PostgreSQL database dump complete
--

