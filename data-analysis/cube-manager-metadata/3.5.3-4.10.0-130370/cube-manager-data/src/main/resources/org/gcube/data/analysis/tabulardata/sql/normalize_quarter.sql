CREATE OR REPLACE FUNCTION normalize_quarter_of_year(timestamp with time zone) RETURNS text as $$
	SELECT to_char($1, 'YYYY-"Q"Q');
$$ LANGUAGE SQL;
