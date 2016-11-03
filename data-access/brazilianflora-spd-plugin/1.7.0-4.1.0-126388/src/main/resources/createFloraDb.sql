create table updates (id serial NOT NULL PRIMARY KEY, date date);
create table flora(id varchar(15) NOT NULL PRIMARY KEY, name varchar(100), scientific_name varchar(100), rank varchar(100), status character varying(50), id_parent varchar(15), citation varchar(100), acceptednameusageid varchar(15), path varchar(200), qualifier varchar(100));
