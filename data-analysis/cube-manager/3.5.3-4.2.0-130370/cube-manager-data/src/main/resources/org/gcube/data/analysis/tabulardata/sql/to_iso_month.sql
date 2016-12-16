CREATE OR REPLACE FUNCTION to_iso_month(text) RETURNS timestamp with time zone as $$
	SELECT ($1::text || '-01')::timestamp with time zone;
$$ LANGUAGE SQL;
