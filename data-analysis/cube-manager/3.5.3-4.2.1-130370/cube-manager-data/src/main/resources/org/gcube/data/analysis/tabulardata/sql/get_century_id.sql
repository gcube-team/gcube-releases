CREATE OR REPLACE FUNCTION get_century_id(timestamp with time zone) RETURNS integer as $$
	SELECT to_char($1, 'CC')::integer;
$$ LANGUAGE SQL;