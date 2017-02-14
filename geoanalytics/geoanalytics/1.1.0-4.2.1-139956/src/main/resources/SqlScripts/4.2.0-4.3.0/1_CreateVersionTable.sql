CREATE TABLE public."Version"
(
  "VER_ID" uuid NOT NULL,
  "VER_Version" character varying(30) NOT NULL,
  "VER_Upgradetime" timestamp without time zone NOT NULL,
  CONSTRAINT version_pkey PRIMARY KEY ("VER_ID")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Version"
  OWNER TO geopolis;