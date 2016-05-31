CREATE OR REPLACE FUNCTION normalize_century(timestamp with time zone) RETURNS text as $$
	SELECT to_char($1, 'CC');
$$ LANGUAGE SQL;