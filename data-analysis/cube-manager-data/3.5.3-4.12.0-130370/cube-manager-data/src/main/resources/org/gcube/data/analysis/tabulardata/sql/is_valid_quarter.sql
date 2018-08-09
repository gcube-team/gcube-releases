CREATE OR REPLACE FUNCTION is_valid_quarter(anyelement) RETURNS boolean AS $$ 
	select $1::text~E'^[0-9]{1,4}-Q[1-4]$'
$$ LANGUAGE SQL


