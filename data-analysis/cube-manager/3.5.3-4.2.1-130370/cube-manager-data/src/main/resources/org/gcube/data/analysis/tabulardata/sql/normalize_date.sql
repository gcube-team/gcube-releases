CREATE OR REPLACE FUNCTION normalize_day(timestamp with time zone) RETURNS text as $$
	SELECT to_char($1, 'YYYY-MM-DD');
$$ LANGUAGE SQL;
