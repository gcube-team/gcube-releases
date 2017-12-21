CREATE OR REPLACE FUNCTION is_valid_regexp(anyelement, text) RETURNS boolean AS $$ 
	select $1::text~$2
$$ LANGUAGE SQL
