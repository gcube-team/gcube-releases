CREATE TABLE public."PrincipalProject"
(
  "Principal_ID" uuid NOT NULL,
  "Project_ID" uuid NOT NULL,
  CONSTRAINT "PRCP_PROJ_ID" PRIMARY KEY ("Principal_ID", "Project_ID"),
  CONSTRAINT "FK_PRincipalProject_Project" FOREIGN KEY ("Project_ID")
      REFERENCES public."Project" ("PRJ_ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "FK_Project_Participant" FOREIGN KEY ("Principal_ID")
      REFERENCES public."Principal" ("PRCP_ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE INDEX "FKI_PRincipalProject_Project"
  ON public."PrincipalProject"
  USING btree
  ("Project_ID");

CREATE INDEX "FKI_Project_Participant"
  ON public."PrincipalProject"
  USING btree
  ("Principal_ID");

