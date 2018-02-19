CREATE OR REPLACE FUNCTION get_quarter_of_year_id(timestamp with time zone) RETURNS integer as $$
	SELECT to_char($1, 'YYYYQ')::integer;
$$ LANGUAGE SQL;