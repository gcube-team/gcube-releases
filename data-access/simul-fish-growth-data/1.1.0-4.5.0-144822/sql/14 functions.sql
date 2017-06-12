CREATE FUNCTION GetSimilarSimulmodelids(bigint, integer) RETURNS SETOF bigint AS
$BODY$
BEGIN
  RETURN QUERY 
    select sm.id
    from  simulmodel sm
    where sm.siteid in (
      select ss.similarid 
      from
        similarsite ss 
        inner join simulmodel smorig on (smorig.siteid=ss.siteid) and (smorig.speciesid=sm.speciesid)
      where 
        smorig.id = $1
        and
        ss.grade <= $2
      );
  RETURN;       

END
$BODY$
LANGUAGE plpgsql;

