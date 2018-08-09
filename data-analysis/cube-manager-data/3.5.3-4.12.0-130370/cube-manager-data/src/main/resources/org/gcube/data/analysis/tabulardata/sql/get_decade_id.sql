CREATE OR REPLACE FUNCTION get_decade_id(timestamp with time zone) RETURNS integer as $$
	SELECT substring(to_char($1,'YYYY') FROM '\d\d\d')::integer;
$$ LANGUAGE SQL;


