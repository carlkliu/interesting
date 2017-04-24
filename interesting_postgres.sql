drop   table   if   exists licenseinfo;

CREATE TABLE licenseinfo (
    id bigint NOT NULL,
    signature character varying(2048) NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enable bigint default 1,
    flag bigint default 0
);
--
-- Name:licenseinfo_id; Type: CONSTRAINT; Schema: public; Owner: uqdm; Tablespace: 
--

ALTER TABLE ONLY licenseinfo ADD CONSTRAINT licenseinfo_id PRIMARY KEY (id);

ALTER TABLE public.licenseinfo OWNER TO uqdm;

--
-- Name: licenseinfo_id_seq; Type: SEQUENCE; Schema: public; Owner: uqdm
--

CREATE SEQUENCE licenseinfo_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.licenseinfo_id_seq OWNER TO uqdm;

--
-- Name: licenseinfo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: uqdm
--

ALTER SEQUENCE licenseinfo_id_seq OWNED BY licenseinfo.id;


--
-- Name: licenseinfo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: uqdm
--

SELECT pg_catalog.setval('licenseinfo_id_seq', 1000, true);
--
-- Name: id; Type: DEFAULT; Schema: public; Owner: uqdm
--

ALTER TABLE ONLY licenseinfo ALTER COLUMN id SET DEFAULT nextval('licenseinfo_id_seq'::regclass);
