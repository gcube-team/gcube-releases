create table asfis (ISSCAAP varchar(10), TAXOCODE varchar(13), threeA_CODE varchar(27), Scientific_name varchar(37), English_name varchar(30), French_name varchar(30), Spanish_name varchar(50), Arabic_name varchar(50), Chinese_name varchar(50), Russian_name varchar(60), Author varchar(55), Family varchar(20), Order_rank varchar(30), Stats_data varchar(5), rank varchar(20), id serial PRIMARY KEY);

ALTER TABLE asfis  ADD COLUMN parent_id serial;
ALTER TABLE asfis ADD CONSTRAINT FK_parent_id 
  FOREIGN KEY (parent_id) 
  REFERENCES asfis(id) ;
  
ISSCAAP, TAXOCODE, threeA_CODE, Scientific_name, English_name, French_name, Spanish_name, Author, Family, Order_rank