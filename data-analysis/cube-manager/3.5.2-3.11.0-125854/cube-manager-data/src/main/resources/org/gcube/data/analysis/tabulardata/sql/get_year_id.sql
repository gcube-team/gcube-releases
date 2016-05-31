CREATE OR REPLACE FUNCTION get_year_id(timestamp with time zone) RETURNS integer as $$
	SELECT to_char($1, 'YYYY')::integer;
$$ LANGUAGE SQL;
