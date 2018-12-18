CREATE OR REPLACE FUNCTION normalize_year(timestamp with time zone) RETURNS text as $$
	SELECT to_char($1, 'YYYY');
$$ LANGUAGE SQL;
