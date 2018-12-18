CREATE OR REPLACE FUNCTION to_iso_day(text) RETURNS timestamp with time zone as $$
	SELECT $1::timestamp with time zone;
$$ LANGUAGE SQL;
