CREATE OR REPLACE FUNCTION is_valid_date(date_value anyelement) RETURNS boolean AS $$
DECLARE
	tmp date;
BEGIN
	
    tmp := CAST(date_value AS date);
  	return true;
  	
    EXCEPTION 
		WHEN OTHERS THEN RETURN false;
END;
$$ LANGUAGE plpgsql;
