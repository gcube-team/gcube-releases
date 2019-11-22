CREATE OR REPLACE FUNCTION to_iso_quarter_of_year(text) RETURNS timestamp with time zone as $$
	SELECT (substring($1, '(\d{4})') || '-' || ((substring($1, '.*-Q(.)')::int)*3)-2 || '-01')::timestamp with time zone;
$$ LANGUAGE SQL;
