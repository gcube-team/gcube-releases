CREATE TABLE kingdom (kingdom VARCHAR(50) NOT NULL,PRIMARY KEY (kingdom));
CREATE TABLE phylum (kingdom VARCHAR(50) NOT NULL, phylum VARCHAR(50) NOT NULL,PRIMARY KEY (phylum));
CREATE TABLE class_table (classcolumn VARCHAR(50) NOT NULL,kingdom VARCHAR(50) NOT NULL, phylum VARCHAR(50) NOT NULL,PRIMARY KEY (classcolumn));
CREATE TABLE order_table (classcolumn VARCHAR(50) NOT NULL,kingdom VARCHAR(50) NOT NULL,ordercolumn VARCHAR(50) NOT NULL, phylum VARCHAR(50) NOT NULL,PRIMARY KEY (ordercolumn));
CREATE TABLE family_table (classcolumn VARCHAR(50) NOT NULL,familycolumn VARCHAR(50) NOT NULL,kingdom VARCHAR(50) NOT NULL,ordercolumn VARCHAR(50) NOT NULL, phylum VARCHAR(50) NOT NULL,PRIMARY KEY (familycolumn));
CREATE TABLE MAPS (requestid VARCHAR(100) NOT NULL,
image_count INTEGER not null default 0,
gis smallint default 0 ,
custom smallint default 0,
algorithm varchar(50),
creation_date BIGINT,
fileset_id VARCHAR(100),
layer_id VARCHAR(100),
image_list CLOB,
thumbnail CLOB,
title VARCHAR(200), 
type_field VARCHAR(70),
species_list CLOB,
author VARCHAR(100),
coverage_field VARCHAR(200),
resource_id INTEGER,
data_generation_time BIGINT,
layer_url CLOB,
layer_preview CLOB);