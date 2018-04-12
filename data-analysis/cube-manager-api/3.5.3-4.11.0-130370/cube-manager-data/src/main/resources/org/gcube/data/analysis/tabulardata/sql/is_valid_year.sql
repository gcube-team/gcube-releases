CREATE OR REPLACE FUNCTION is_valid_year(anyelement) RETURNS boolean AS $$ 
	select $1::text~E'^\\d{4}$'
$$ LANGUAGE SQL
