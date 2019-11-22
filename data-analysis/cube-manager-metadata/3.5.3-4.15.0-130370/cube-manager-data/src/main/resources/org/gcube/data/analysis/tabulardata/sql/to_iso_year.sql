CREATE OR REPLACE FUNCTION to_iso_year(anyelement) RETURNS timestamp with time zone as $$
	SELECT ($1::text || '-01-01')::timestamp with time zone;
$$ LANGUAGE SQL;
