CREATE OR REPLACE FUNCTION is_valid_integer(anyelement) RETURNS boolean AS $$ 
	select $1::text~E'^(-)?\\d+$' 
$$ LANGUAGE SQL
