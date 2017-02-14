--PRJ_Client to extent
ALTER TABLE public."Project" RENAME COLUMN "PRJ_Client" TO "PRJ_Extent";

ALTER TABLE public."PrincipalProject" ADD COLUMN "PRCP_PRJ_LastUpdate" timestamp without time zone;
ALTER TABLE public."PrincipalProject" ADD COLUMN "PRCP_PRJ_CreationDate" timestamp without time zone;
UPDATE public."PrincipalProject" SET "PRCP_PRJ_LastUpdate"= "Project"."PRJ_CreationDate","PRCP_PRJ_CreationDate"= "Project"."PRJ_CreationDate" FROM "Project" WHERE "Project_ID"="Project"."PRJ_ID";
ALTER TABLE public."PrincipalProject" ALTER COLUMN "PRCP_PRJ_LastUpdate" SET NOT NULL;
ALTER TABLE public."PrincipalProject" ALTER COLUMN "PRCP_PRJ_CreationDate" SET NOT NULL;

--Update AccessControl table for projects created before Adding Access Control logic.
CREATE extension "uuid-ossp";

UPDATE public."PrincipalProject" SET "PRCP_PRJ_LastUpdate"= "Project"."PRJ_CreationDate","PRCP_PRJ_CreationDate"= "Project"."PRJ_CreationDate" FROM "Project" WHERE "Project_ID"="Project"."PRJ_ID";

INSERT INTO "AccessControl"
 SELECT uuid_generate_v1mc(), "PrincipalProject"."Principal_ID", 0, "PrincipalProject"."Project_ID", 1, 1, 1, "PrincipalProject"."PRCP_PRJ_CreationDate", "PrincipalProject"."PRCP_PRJ_CreationDate"
 FROM "PrincipalProject"
 WHERE "PrincipalProject"."Principal_ID" NOT IN (SELECT "AccessControl"."ACCN_Principal" FROM "AccessControl" WHERE "AccessControl"."ACCN_Entity"="PrincipalProject"."Project_ID" AND "AccessControl"."ACCN_Principal"="PrincipalProject"."Principal_ID");
 
INSERT INTO "AccessControl"
 SELECT uuid_generate_v1mc(), "Project"."PRJ_Creator", 0, "Project"."PRJ_ID", 1, 1, 1, "Project"."PRJ_CreationDate", "Project"."PRJ_CreationDate"
 FROM "Project"
 WHERE "Project"."PRJ_Creator" NOT IN (SELECT "AccessControl"."ACCN_Principal" FROM "AccessControl" WHERE "AccessControl"."ACCN_Entity"="Project"."PRJ_ID" AND "AccessControl"."ACCN_Principal"="Project"."PRJ_Creator");