# Users schema
 
# --- !Ups
 -- Table: fmuser

-- DROP TABLE fmuser;

CREATE TABLE fmuser
(
  id bigint NOT NULL,
  "admin" boolean NOT NULL,
  cellno character varying(255),
  email character varying(255),
  firstname character varying(255),
  lastname character varying(255),
  "password" character varying(255),
  CONSTRAINT fmuser_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE fmuser OWNER TO fishmarket;
 
# --- !Downs
DROP TABLE fmuser;