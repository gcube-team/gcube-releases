CREATE OR REPLACE FUNCTION get_month_id(timestamp with time zone) RETURNS integer as $$
	SELECT to_char($1, 'YYYYMM')::integer;
$$ LANGUAGE SQL;
