BEGIN TRANSACTION;

------------------------------------------PrincipalData------------------------------------------

ALTER TABLE ONLY "PrincipalData"
    ADD CONSTRAINT "PK_PRNCD_ID" PRIMARY KEY ("PRNCD_ID");

-------------------------------------------------------------------------------------------------

-----------------------------------------Principal-----------------------------------------------

ALTER TABLE ONLY "Principal"
    ADD CONSTRAINT "PK_PRCP_ID" PRIMARY KEY ("PRCP_ID");

ALTER TABLE ONLY "Principal"
    ADD CONSTRAINT "FK_Principal_Tenant" FOREIGN KEY ("PRCP_Tenant") REFERENCES "Tenant"("TEN_ID");

ALTER TABLE ONLY "Principal"
    ADD CONSTRAINT "FK_Principal_Principal" FOREIGN KEY ("PRCP_Creator") REFERENCES "Principal"("PRCP_ID");
    
ALTER TABLE ONLY "Principal"
	ADD CONSTRAINT "FK_Principal_PrincipalData" FOREIGN KEY ("PRCP_PrincipalData") REFERENCES "PrincipalData"("PRNCD_ID");
    
CREATE INDEX "FKI_Principal_Tenant"
ON "Principal"
USING btree ("PRCP_Tenant");

CREATE INDEX "FKI_Principal_Creator"
ON "Principal"
USING btree ("PRCP_Creator");

CREATE INDEX "FKI_Principal_PrincipalData"
ON "Principal"
USING btree ("PRCP_PrincipalData");

-------------------------------------------------------------------------------------------------

----------------------------------------AccessControl--------------------------------------------

ALTER TABLE ONLY "AccessControl"
    ADD CONSTRAINT "PK_ACCN_ID" PRIMARY KEY ("ACCN_ID");
    
ALTER TABLE ONLY "AccessControl"
    ADD CONSTRAINT "FK_AccessControl_Principal" FOREIGN KEY ("ACCN_Principal") REFERENCES "Principal"("PRCP_ID");

CREATE INDEX "FKI_AccessControl_Principal"
ON "AccessControl"
USING btree ("ACCN_Principal");

-------------------------------------------------------------------------------------------------

-----------------------------------------AccessRight---------------------------------------------

ALTER TABLE ONLY "AccessRight"
    ADD CONSTRAINT "PK_ACCR_ID" PRIMARY KEY ("ACCR_ID");
    
ALTER TABLE ONLY "AccessRight"
    ADD CONSTRAINT "FK_AccessRight_Principal" FOREIGN KEY ("ACCR_Principal") REFERENCES "Principal"("PRCP_ID");

CREATE INDEX "FKI_AccessRight_Principal"
ON "AccessRight"
USING btree ("ACCR_Principal");

-------------------------------------------------------------------------------------------------

----------------------------------------PrincipalMembership--------------------------------------

ALTER TABLE ONLY "PrincipalMembership"
    ADD CONSTRAINT "PK_PRCM_ID" PRIMARY KEY ("PRCM_ID");

ALTER TABLE ONLY "PrincipalMembership"
    ADD CONSTRAINT "FK_PrincipalMembership_Member" FOREIGN KEY ("PRCM_Member") REFERENCES "Principal"("PRCP_ID");

ALTER TABLE ONLY "PrincipalMembership"
    ADD CONSTRAINT "F_PrincipalMembership_Group" FOREIGN KEY ("PRCM_Group") REFERENCES "Principal"("PRCP_ID");

CREATE INDEX "FKI_PrincipalMembership_Member"
ON "PrincipalMembership"
USING btree ("PRCM_Member");

CREATE INDEX "FKI_PrincipalMembership"
ON "PrincipalMembership"
USING btree ("PRCM_Group");

-------------------------------------------------------------------------------------------------
    
-----------------------------------------------Tenant--------------------------------------------

ALTER TABLE ONLY "Tenant"
	DROP CONSTRAINT "fk27fbe3fe31cf6eca",
    ADD CONSTRAINT "FK_Tenant_Principal" FOREIGN KEY ("TEN_Creator") REFERENCES "Principal"("PRCP_ID");
    
ALTER INDEX "fki_customercreator" RENAME TO "FKI_Tenant_Creator";

-------------------------------------------------------------------------------------------------
    
-------------------------------------------TenantActivation--------------------------------------

ALTER TABLE ONLY "TenantActivation"
	RENAME CONSTRAINT "fk2021dbb4763bc6c4" TO "FK_TenantActivation_Shape";
	
ALTER TABLE ONLY "TenantActivation"
	DROP CONSTRAINT "fk2021dbb44e3443c9",
	DROP CONSTRAINT "fk2021dbb4b7c33779",
    ADD CONSTRAINT "FK_TenantActivation_Tenant" FOREIGN KEY ("TENA_Creator") REFERENCES "Principal"("PRCP_ID"),
    ADD CONSTRAINT "FK_TenantActivation_Principal" FOREIGN KEY ("TENA_Tenant") REFERENCES "Tenant"("TEN_ID");
    
ALTER INDEX "fki_customeractivationcreator" RENAME TO "FKI_TenantActivation_Creator";
ALTER INDEX "fki_customeractivationcustomer" RENAME TO "FKI_TenantActivation_Tenant";
ALTER INDEX "fki_customeractivationshape" RENAME TO "FKI_TenantActivation_Shape";

-------------------------------------------------------------------------------------------------

-----------------------------------------Accounting----------------------------------------------

ALTER TABLE ONLY "Accounting"
	DROP CONSTRAINT "fk46f01eb512edeb65",
	DROP CONSTRAINT "fk46f01eb57b7e7e58",
	DROP CONSTRAINT "fk46f01eb59695250a",
	ADD CONSTRAINT "FK_Accounting_Principal_Creator" FOREIGN KEY ("ACC_Creator") REFERENCES "Principal"("PRCP_ID"),
	ADD CONSTRAINT "FK_Accounting_Principal_Principal" FOREIGN KEY ("ACC_Principal") REFERENCES "Principal"("PRCP_ID"),
	ADD CONSTRAINT "FK_Accounting_Tenant" FOREIGN KEY ("ACC_Tenant") REFERENCES "Tenant"("TEN_ID");
	
ALTER INDEX "accounting_customer" RENAME TO "FKI_Accounting_Tenant";
ALTER INDEX "fki_accounting_user" RENAME TO "FKI_Accounting_Principal";
ALTER INDEX "fki_accounting_creator" RENAME TO "FKI_Accounting_Creator"; 
	
-------------------------------------------------------------------------------------------------

---------------------------------------Annotation------------------------------------------------

ALTER TABLE ONLY "Annotation"
	RENAME CONSTRAINT "fk1a21c74f53998d0f" TO "FK_Annotation_Annotation";
	
ALTER TABLE ONLY "Annotation"
	DROP CONSTRAINT "fk1a21c74fa24b4cb6",
	DROP CONSTRAINT "fk1a21c74fe68d4c2c",
	ADD CONSTRAINT "FK_Annotation_Principal" FOREIGN KEY ("AN_Creator") REFERENCES "Principal"("PRCP_ID"),
	ADD CONSTRAINT "FK_Annotation_Tenant" FOREIGN KEY ("AN_Tenant") REFERENCES "Tenant"("TEN_ID");
	
ALTER INDEX "fki_annotationcustomer" RENAME TO "FKI_Annotation_Tenant";
ALTER INDEX "fki_annotationcreator" RENAME TO "FKI_Annotation_Creator";
ALTER INDEX "fki_annotationprevious" RENAME TO "FKI_Annotation_InResponseTo";
	
-------------------------------------------------------------------------------------------------

-----------------------------------------Auditing------------------------------------------------

ALTER TABLE ONLY "Auditing"
	DROP CONSTRAINT "fk_auditing_creator",
	DROP CONSTRAINT "fk_auditing_customer",
	DROP CONSTRAINT "fk_auditing_user",
	ADD CONSTRAINT "FK_Auditing_Principal_creator" FOREIGN KEY ("AUD_Creator") REFERENCES "Principal"("PRCP_ID"),
	ADD CONSTRAINT "FK_Auditing_Principal_principal" FOREIGN KEY ("AUD_Principal") REFERENCES "Principal"("PRCP_ID"),
	ADD CONSTRAINT "FK_Auditing_Tenant" FOREIGN KEY ("AUD_Tenant") REFERENCES "Tenant"("TEN_ID");
	
ALTER INDEX "fki_auditing_customer" RENAME TO "FKI_Auditing_Tenant";
ALTER INDEX "fki_auditing_user" RENAME TO "FKI_Auditing_Principal";
ALTER INDEX "fki_auditing_creator" RENAME TO "FKI_Auditing_Creator";
	
---------------------------------------------------------------------------------------------------------

-----------------------------------------Document--------------------------------------------------------

ALTER TABLE ONLY "Document"
	DROP CONSTRAINT "fk3737353b8d4bcf81",
	DROP CONSTRAINT "fk3737353bc8b48ac1",
	ADD CONSTRAINT "FK_Document_Tenant" FOREIGN KEY ("DOC_Tenant") REFERENCES "Tenant"("TEN_ID"),
	ADD CONSTRAINT "FK_Dcoument_Principal" FOREIGN KEY ("DOC_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_documentcreator" RENAME TO "fki_Document_Creator";
ALTER INDEX "fki_documentcustomer" RENAME TO "fki_Document_Tenant";


-----------------------------------------Mimetype--------------------------------------------------------

ALTER TABLE ONLY "MimeType"
	DROP CONSTRAINT "fkb0e051ae8f573430",
	ADD CONSTRAINT "FK_mimetype_Principal" FOREIGN KEY ("MT_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_mimetypecreator" RENAME TO "FKI_MimeType_Creator";

---------------------------------------------------------------------------------------------------------

-------------------------------------------Project-------------------------------------------------------

ALTER TABLE "Project"
	DROP CONSTRAINT "fk50c8e2f9564fc011",
	DROP CONSTRAINT "fk50c8e2f9ae284831",
	ADD CONSTRAINT "FK_project_Tenant" FOREIGN KEY ("PRJ_Tenant") REFERENCES "Tenant"("TEN_ID"),
	ADD CONSTRAINT "FK_project_Principal" FOREIGN KEY ("PRJ_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_projectcreator" RENAME TO "FKI_Project_Creator";
ALTER INDEX "fki_projectcustomer" RENAME TO "FKI_Project_Tenant";

CREATE INDEX "FKI_Project_Shape"
ON "Project"
USING btree ("PRJ_Shape");

---------------------------------------------------------------------------------------------------------

----------------------------------------------projectdocument--------------------------------------------

ALTER TABLE ONLY "ProjectDocument"
	RENAME CONSTRAINT "fke08e0f3488fbc022" TO "FK_ProjectDocument_Document";
	
ALTER TABLE ONLY "ProjectDocument"
	RENAME CONSTRAINT "fke08e0f34e12918e0" TO "FK_ProjetcDocument_Project";
	
ALTER TABLE ONLY "ProjectDocument"
	RENAME CONSTRAINT "projectdocument_pd_document_key" TO "UQ_ProjectDocument_Document";

ALTER TABLE ONLY "ProjectDocument"
	DROP CONSTRAINT "fke08e0f3427ec977d",
	ADD CONSTRAINT "FK_ProjectDocument_Principal" FOREIGN KEY ("PD_Document") REFERENCES "Document"("DOC_ID");

ALTER INDEX "fki_projectdocumentcreator" RENAME TO "FKI_ProjectDocument_Creator";
ALTER INDEX "fki_projectdocumentdocument" RENAME TO "FKI_ProjectDocument_Document";

----------------------------------------------------------------------------------------------------------

-----------------------------------------------ProjectTerm------------------------------------------------

ALTER TABLE ONLY "ProjectTerm"
	RENAME CONSTRAINT "fk2b647705174755b8" TO "FK_ProjectTerm_Project";
	
ALTER TABLE ONLY "ProjectTerm"
	RENAME CONSTRAINT "fk2b64770535571a67" TO "FK_ProjectTerm_TaxonomyTerm";
	
ALTER TABLE ONLY "ProjectTerm"
	DROP CONSTRAINT "fk2b6477055e0ad455",
	ADD CONSTRAINT "FK_ProjectTerm_Principal" FOREIGN KEY ("PRJT_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_projecttermcreator" RENAME TO "FKI_ProjectTerm_Creator";
ALTER INDEX "fki_projecttermterm" RENAME TO "FKI_ProjectTerm_Term";

CREATE INDEX "FKI_ProjectTerm_Project"
ON "ProjectTerm"
USING btree ("PRJT_Project");

-------------------------------------------------------------------------------------------------------------

-------------------------------------------------Shape-------------------------------------------------------

ALTER TABLE ONLY "Shape"
	RENAME CONSTRAINT "fk4c25f81c2428d29" TO "FK_Shape_ShapeImport";

ALTER TABLE ONLY "Shape"
	DROP CONSTRAINT "fk4c25f815b928f44",
	ADD CONSTRAINT "FK_Shape_Principal" FOREIGN KEY ("SHP_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_shapecreator" RENAME TO "FKI_Shape_Creator";
ALTER INDEX "fki_shapeshapeimport" RENAME TO "FKI_Shape_ShapeImport";
ALTER INDEX "idx_shapegeography" RENAME TO "IDX_Shape_ShapeImport";

-------------------------------------------------------------------------------------------------------------

------------------------------------------------ShapeDocument------------------------------------------------

ALTER TABLE "ShapeDocument"
	RENAME CONSTRAINT "fk75a683bc47a9d865" TO "FK_ShapeDocument_Document";
	
ALTER TABLE "ShapeDocument"
	RENAME CONSTRAINT "fk_shapedocumentttshape" TO "FK_ShapeDocument_TaxonomyTermShape";
	
ALTER TABLE "ShapeDocument"
	RENAME CONSTRAINT "shapedocument_sd_ttshape_key" TO "UQ_ShapeDocument_TaxonomyTermShape";
	
ALTER TABLE "ShapeDocument"
	DROP CONSTRAINT "fk75a683bc4c8eada",
	ADD CONSTRAINT "FK_ShapeDocument_Creator" FOREIGN KEY ("SD_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_shapedocumentcreator" RENAME TO "FKI_ShapeDocument_Creator";
ALTER INDEX "fki_shapedocumentdocument" RENAME TO "FKI_ShapeDocument_Document";
ALTER INDEX "fki_shapedocumentttshape" RENAME TO "FKI_ShapeDocument_TaxonomyTermShape";

-------------------------------------------------ShapeImport-------------------------------------------------

ALTER TABLE ONLY "ShapeImport"
	RENAME CONSTRAINT "fk86095a86fef8aa97" TO "FK_ShapeImport_Creator";
	
ALTER INDEX "fki_shapeimpcreator" RENAME TO "FKI_ShapeImport_Creator";

-------------------------------------------------------------------------------------------------------------

------------------------------------------------ShapeTerm----------------------------------------------------

ALTER TABLE ONLY "ShapeTerm"
	RENAME CONSTRAINT "fk15bc6f8d20be0a3a" TO "FK_ShapeTerm_TaxonomyTerm";
	
ALTER TABLE ONLY "ShapeTerm"
	RENAME CONSTRAINT "fk15bc6f8de8cc14dd" TO "FK_ShapeTerm_Shape";
	
ALTER TABLE ONLY "ShapeTerm"
	DROP CONSTRAINT "fk15bc6f8d5de96fa2",
	ADD CONSTRAINT "FK_ShapeTerm_Creator" FOREIGN KEY ("SHPT_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_shapetermcreator" RENAME TO "FKI_ShapeTerm_Creator";
ALTER INDEX "fki_shapetermshape" RENAME TO "FKI_ShapeTerm_Shape";
ALTER INDEX "fki_shapetermterm" RENAME TO "FKI_ShapeTerm_Term";

-------------------------------------------------------------------------------------------------------------
	
------------------------------------------------SysConfig----------------------------------------------------

ALTER TABLE "SysConfig"
	DROP CONSTRAINT "fk67af404f456a42bf",
	ADD CONSTRAINT "FK_SysConfig_Creator" FOREIGN KEY ("SYSC_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_sysconfigcreator" RENAME TO "FKI_SysConfig_Creator";

-------------------------------------------------------------------------------------------------

-----------------------------------------------Taxonomy------------------------------------------

ALTER TABLE ONLY "Taxonomy"
	RENAME CONSTRAINT "fkf4349771afe995c0" TO "FK_Taxonomy_Class";
	
ALTER TABLE ONLY "Taxonomy"
	DROP CONSTRAINT "fkf4349771e498a234",
	ADD CONSTRAINT "FK_Taxonomy_Creator" FOREIGN KEY ("TAX_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_taxonomyclass" RENAME TO "FKI_Taxonomy_Class";
ALTER INDEX "idxtaxonomycreator" RENAME TO "FKI_Taxonomy_Creator";

-------------------------------------------------------------------------------------------------

-------------------------------------------TaxonomyTerm------------------------------------------

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME CONSTRAINT "fk10712f7d20d08283" TO "FK_TaxonomyTerm_Taxonomy";
	
ALTER TABLE ONLY "TaxonomyTerm"
	RENAME CONSTRAINT "fk10712f7d62151fe8" TO "FK_TaxonomyTerm_Parent";
	
ALTER TABLE ONLY "TaxonomyTerm"
	RENAME CONSTRAINT "fk10712f7d9340204a" TO "FK_TaxonomyTerm_Class";

ALTER TABLE ONLY "TaxonomyTerm"
	DROP CONSTRAINT "fk10712f7df5a5bab2",
	ADD CONSTRAINT "FK_TaxonomyTerm_Creator" FOREIGN KEY ("TAXT_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_taxonomytermclass" RENAME TO "FKI_TaxonomyTerm_Class";
ALTER INDEX "fki_taxonomytermcreator" RENAME TO "FKI_TaxonomyTerm_Creator";
ALTER INDEX "fki_taxonomytermparent" RENAME TO "FKI_TaxonomyTerm_Parent";
ALTER INDEX "fki_taxonomytermtaxonomy" RENAME TO "FKI_TaxonomyTerm_Taxonomy";
ALTER INDEX "idx_taxonomyterm" RENAME TO "IDX_TaxonomyTerm_Taxonomy_Name";
ALTER INDEX "idx_taxonomytermparent" RENAME TO "IDX_TaxonomyTerm_Taxonomy_Parent";

----------------------------------------------------------------------------------------
	
-----------------------------------------TaxonomyTermLink-------------------------------
	
ALTER TABLE ONLY "TaxonomyTermLink"
	RENAME CONSTRAINT "fkd474d177f7643d2" TO "FK_TaxonomyTerm_DestinationTerm";	
	
ALTER TABLE ONLY "TaxonomyTermLink"
	RENAME CONSTRAINT "fkd474d17d59961cb" TO "FK_TaxonomyTermLink_SourceTerm";
	
ALTER TABLE ONLY "TaxonomyTermLink"
	DROP CONSTRAINT "fkd474d17641839ec",
	ADD CONSTRAINT "FK_TaxonomyTermLink_Creator" FOREIGN KEY ("TAXTL_Creator") REFERENCES "Principal"("PRCP_ID");

	
ALTER INDEX "fki_taxonomytermdest" RENAME TO "FKI_TaxonomyTerm_DestinationTerm";
ALTER INDEX "fki_taxonomytermlinkcreator" RENAME TO "FKI_TaxonomyTermLink_Creator";
ALTER INDEX "idxtaxonomytermlink" RENAME TO "IDX_TaxonomyTermLink_SourceTerm_DestinationTerm_Verb";

----------------------------------------------------------------------------------------

------------------------------------------TaxonomyTermShape-----------------------------

ALTER TABLE ONLY "TaxonomyTermShape"
	RENAME CONSTRAINT "fk9c045624b52c6089" TO "FK_TaxonomyTermShape_Term";
	
ALTER TABLE ONLY "TaxonomyTermShape"
	RENAME CONSTRAINT "fk9c045624e228886e" TO "FK_TaxonomyTermShape_Shape";
	
ALTER TABLE ONLY "TaxonomyTermShape"
	DROP CONSTRAINT "fk9c04562471f742f3",
	ADD CONSTRAINT "FK_TaxonomyTermShape_Creator" FOREIGN KEY ("TAXTS_Creator") REFERENCES "Principal"("PRCP_ID");

ALTER INDEX "fki_taxonomytermshape" RENAME TO "FKI_TaxonomyTermShape_Shape";
ALTER INDEX "fki_taxonomytermshapecreator" RENAME TO "FKI_TaxonomyTermShape_Creator";
ALTER INDEX "idxtaxonomytermshape" RENAME TO "IDX_TaxonomyTermShape_Term_Shape";

-------------------------------------------------------------------------------------------------

---------------------------------------------Workflow--------------------------------------------

ALTER TABLE ONLY "Workflow"
	RENAME CONSTRAINT "fk5f63bdfb7b40ebb" TO "FK_Worklow_Project";
	
ALTER TABLE ONLY "Workflow"
	RENAME CONSTRAINT "fk5f63bdfde5e74fc" TO "FK_Worfklow_Template";
	
ALTER TABLE ONLY "Workflow"
	DROP CONSTRAINT "fk5f63bdffe778d58",
	ADD CONSTRAINT "FK_Workflow_Creator" FOREIGN KEY ("WF_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_workflowcreator" RENAME TO "FKI_Workflow_Creator";
ALTER INDEX "fki_workflowproject" RENAME TO "FKI_Workflow_Project";
ALTER INDEX "fki_workflowtemplate" RENAME TO "FKI_Workflow_Template";

-------------------------------------------------------------------------------------------------

--------------------------------------------WorkflowTask-----------------------------------------

ALTER TABLE ONLY "WorkflowTask"
	RENAME CONSTRAINT "fk66cede04bd89f4d5" TO "FK_WorkflowTask_Workflow";
	
ALTER TABLE ONLY "WorkflowTask"
	DROP CONSTRAINT "fk66cede0417a4340e",
	DROP CONSTRAINT "fk66cede04bbfcdae1",
	ADD CONSTRAINT "FK_WorkflowTask_Creator" FOREIGN KEY ("WFT_Creator") REFERENCES "Principal"("PRCP_ID"),
	ADD CONSTRAINT "FK_WorfklowTask_Principal" FOREIGN KEY ("WFT_Principal") REFERENCES "Principal"("PRCP_ID");

ALTER INDEX "fki_workflowtaskcreator" RENAME TO "FKI_WorkflowTask_Creator";
ALTER INDEX "fki_workflowtaskuser" RENAME TO "FKI_WorkflowTask_Principal";
ALTER INDEX "fki_workflowtaskworkflow" RENAME TO "FKI_WorkflowTask_Workflow";

-----------------------------------------WorkflowTaskDocument------------------------------------

ALTER TABLE ONLY "WorkflowTaskDocument"
	RENAME CONSTRAINT "workflowtaskdocument_fk_workflowtask" TO "FK_WorkflowTaskDocument_WorkflowTask";
	
ALTER TABLE ONLY "WorkflowTaskDocument"
	RENAME CONSTRAINT "workflowtaskdocument_ft_document" TO "FK_WorkflowTaskDocument_Document";
	
ALTER TABLE ONLY "WorkflowTaskDocument"
	DROP CONSTRAINT "workflowtaskdocument_ft_creator",
	ADD CONSTRAINT "FK_WorkflowTaskDocument_Creator" FOREIGN KEY ("WTD_Creator") REFERENCES "Principal"("PRCP_ID");
	
ALTER INDEX "fki_workflowtaskdocument_fk_workflowtask" RENAME TO "FKI_WorkflowTaskDocument_WorkflowTask";
ALTER INDEX "fki_workflowtaskdocument_ft_creator" RENAME TO "FKI_WorkflowTaskDocument_Creator";
ALTER INDEX "fki_workflowtaskdocument_ft_document" RENAME TO "FKI_WorkflowTaskDocument_Document";

-------------------------------------------------------------------------------------------------


COMMIT;