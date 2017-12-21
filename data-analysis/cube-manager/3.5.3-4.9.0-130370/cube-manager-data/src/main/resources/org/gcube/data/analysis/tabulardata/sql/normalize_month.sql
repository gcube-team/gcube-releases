CREATE OR REPLACE FUNCTION normalize_month(timestamp with time zone) RETURNS text as $$
	SELECT to_char($1, 'YYYY-MM');
$$ LANGUAGE SQL;
