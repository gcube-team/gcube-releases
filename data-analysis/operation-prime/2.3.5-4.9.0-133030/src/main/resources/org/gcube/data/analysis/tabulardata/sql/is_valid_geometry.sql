CREATE OR REPLACE FUNCTION is_valid_geometry(anyelement) RETURNS boolean AS $$ 
	select $1::text~E'\\s*POINT\\s*\\(\\s*((-)?\\d+(\\.\\d+)?\\s+)?(-)?\\d+(\\.\\d+)?\\s*\\)\\s*$' OR $1::text~E'\\s*LINESTRING\\s*\\((\\s*((-)?\\d+(\\.\\d+)?\\s+)?(-)?\\d+(\\.\\d+)?\\s*,){1,}\\s*((-)?\\d+(\\.\\d+)?\\s*)?(-)?\\d+(\\.\\d+)?\\s*\\)\\s*$'
$$ LANGUAGE SQL



