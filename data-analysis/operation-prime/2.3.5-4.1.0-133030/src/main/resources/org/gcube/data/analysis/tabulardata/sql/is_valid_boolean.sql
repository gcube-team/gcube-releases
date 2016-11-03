CREATE OR REPLACE FUNCTION is_valid_boolean(value anyelement) RETURNS boolean AS $$
DECLARE
	tmp boolean;
BEGIN
    tmp := CAST(value AS boolean);
    return true;
  
    EXCEPTION 
		WHEN OTHERS THEN RETURN false;
END;
$$ LANGUAGE plpgsql;