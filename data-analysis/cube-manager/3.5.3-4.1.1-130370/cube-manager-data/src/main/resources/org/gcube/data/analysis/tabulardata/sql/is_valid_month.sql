CREATE OR REPLACE FUNCTION is_valid_month(anyelement) RETURNS boolean AS $$ 
	select $1::text~E'^\\d{4}-(0?[1-9]|1[012])$'
$$ LANGUAGE SQL

