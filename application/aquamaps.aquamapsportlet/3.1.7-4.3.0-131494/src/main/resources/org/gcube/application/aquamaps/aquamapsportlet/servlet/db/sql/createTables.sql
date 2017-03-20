CREATE TABLE kingdom (kingdom VARCHAR(50) NOT NULL,PRIMARY KEY (kingdom));
CREATE TABLE phylum (kingdom VARCHAR(50) NOT NULL, phylum VARCHAR(50) NOT NULL,PRIMARY KEY (phylum));
CREATE TABLE class_table (classcolumn VARCHAR(50) NOT NULL,kingdom VARCHAR(50) NOT NULL, phylum VARCHAR(50) NOT NULL,PRIMARY KEY (classcolumn));
CREATE TABLE order_table (classcolumn VARCHAR(50) NOT NULL,kingdom VARCHAR(50) NOT NULL,ordercolumn VARCHAR(50) NOT NULL, phylum VARCHAR(50) NOT NULL,PRIMARY KEY (ordercolumn));
CREATE TABLE family_table (classcolumn VARCHAR(50) NOT NULL,familycolumn VARCHAR(50) NOT NULL,kingdom VARCHAR(50) NOT NULL,ordercolumn VARCHAR(50) NOT NULL, phylum VARCHAR(50) NOT NULL,PRIMARY KEY (familycolumn));
CREATE TABLE Area (code VARCHAR(4) NOT NULL,name VARCHAR(50) NOT NULL,type VARCHAR(5));
CREATE TABLE Species(
	speciesid VARCHAR(50), 
	genus VARCHAR(50), 
	species VARCHAR(50), 
	speccode INTEGER, 
	fbname VARCHAR(40),
	scientific_name VARCHAR(100),
	occurrecs INTEGER, 
	occurcells INTEGER, 
	classcolumn VARCHAR(50),
	familycolumn VARCHAR(50),
	kingdom VARCHAR(50),
	ordercolumn VARCHAR(50), 
	phylum VARCHAR(50), 
	map_beforeafter DOUBLE, 
	map_seasonal DOUBLE, 
	with_gte_5 SMALLINT, 
	with_gte_6 SMALLINT, 
	with_gt_66 SMALLINT, 
	no_of_cells_3 INTEGER,
	no_of_cells_5 INTEGER, 
	no_of_cells_0 INTEGER, 
	database_id INTEGER, 
	picname CLOB, 
	authname VARCHAR(75), 
	entered INTEGER, 
	total_native_csc_cnt INTEGER, 
	deepwater SMALLINT, 
	m_mammals SMALLINT, 
	angling SMALLINT, 
	diving SMALLINT, 
	dangerous SMALLINT, 
	m_invertebrates SMALLINT, 
	algae SMALLINT, 
	seabirds SMALLINT, 
	timestampcolumn TIMESTAMP, 
	pic_source_url CLOB, 
	freshwater SMALLINT,
	PRIMARY KEY (speciesid));
CREATE TABLE Basket(speciesid VARCHAR(50), userid VARCHAR(50), customized SMALLINT DEFAULT 0, perturbations CLOB, PRIMARY KEY (speciesid, userid));
CREATE TABLE Objects(userid VARCHAR(50), title VARCHAR(50), type VARCHAR(50),bbox VARCHAR(50), species VARCHAR(50), threshold float, gis SMALLINT DEFAULT 0, PRIMARY KEY (userid, title));
CREATE TABLE Objects_Basket(userid VARCHAR(50), title VARCHAR(50), speciesid VARCHAR(50), customized SMALLINT, PRIMARY KEY (speciesid, userid, title));
CREATE TABLE fetchedBasket(objectid INTEGER, speciesid VARCHAR(50), PRIMARY KEY (speciesid, objectid));
CREATE TABLE AreaSelections(userid VARCHAR(50)NOT NULL, code VARCHAR(4) NOT NULL,name VARCHAR(50) NOT NULL,type VARCHAR(5) NOT NULL);
