CREATE OR REPLACE FUNCTION normalize_decade(timestamp with time zone) RETURNS text as $$
	SELECT substring(to_char($1,'YYYY') FROM '\d\d\d');
$$ LANGUAGE SQL;