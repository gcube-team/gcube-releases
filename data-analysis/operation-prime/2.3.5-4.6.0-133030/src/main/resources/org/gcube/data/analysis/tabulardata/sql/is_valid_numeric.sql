CREATE OR REPLACE FUNCTION is_valid_numeric(anyelement) RETURNS boolean AS $$ 
	select $1::text~E'^(-)?\\d+(\\.\\d+)?$' 
$$ LANGUAGE SQL
