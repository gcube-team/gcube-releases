CREATE OR REPLACE FUNCTION get_reference_id(valueToMatch ANYELEMENT, refTable TEXT, refColumn TEXT) RETURNS INTEGER AS $$
	
DECLARE
	refId INTEGER; 
BEGIN
	EXECUTE 'SELECT id FROM ' || quote_ident(refTable) || ' WHERE ' || quote_ident(refColumn) || ' = ' || quote_literal(valueToMatch) INTO refId;
	IF refId IS NULL THEN
		RAISE EXCEPTION 'Unable to find a valid reference for value %', quote_literal(valueToMatch);
	END IF;
	return refId;
END;
$$ LANGUAGE plpgsql;