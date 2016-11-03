BEGIN TRANSACTION;

------------------------------------------PrincipalData------------------------------------------

CREATE TABLE "PrincipalData" (
  "PRNCD_ID" uuid NOT NULL,
  "PRNCD_Credential" text,
  "PRNCD_Email" character varying(250),
  "PRNCD_ExpirationDate" timestamp without time zone NOT NULL,
  "PRNCD_FullName" character varying(250) NOT NULL,
  "PRNCD_Initials" character varying(10) NOT NULL,
  "PRNCD_IsActive" smallint NOT NULL,
  "PRNCD_LastUpdate" timestamp without time zone NOT NULL,
  "PRNCD_CreationDate" timestamp without time zone NOT NULL
);

-------------------------------------------------------------------------------------------------

------------------------------------------AccessControl------------------------------------------

CREATE TABLE "AccessControl" (
    "ACCN_ID" uuid NOT NULL,
    "ACCN_Principal" uuid NOT NULL,
    "ACCN_EntityType" smallint NOT NULL,
    "ACCN_Entity" uuid NOT NULL,
    "ACCN_Read" smallint NOT NULL,
    "ACCN_Edit" smallint NOT NULL,
    "ACCN_Delete" smallint NOT NULL,
    "ACCN_CreationDate" timestamp without time zone NOT NULL,
    "ACCN_LastUpdate" timestamp without time zone NOT NULL
);

------------------------------------------AccessRight--------------------------------------------

CREATE TABLE "AccessRight" (
    "ACCR_ID" uuid NOT NULL,
    "ACCR_Principal" uuid NOT NULL,
    "ACCR_Right" uuid NOT NULL,
    "ACCR_Value" smallint NOT NULL,
    "ACCR_CreationDate" timestamp without time zone NOT NULL,
    "ACCR_LastUpdate" timestamp without time zone NOT NULL
);

------------------------------------------PrincipalMembership------------------------------------

CREATE TABLE "PrincipalMembership" (
    "PRCM_ID" uuid NOT NULL,
    "PRCM_Member" uuid NOT NULL,
    "PRCM_Group" uuid NOT NULL,
    "PRCM_CreationDate" timestamp without time zone NOT NULL,
    "PRCM_LastUpdate" timestamp without time zone NOT NULL
);

-------------------------------------------------------------------------------------------------

----------------------------------------Principal------------------------------------------------

CREATE TABLE "Principal" (
    "PRCP_ID" uuid NOT NULL,
    "PRCP_Class" uuid NOT NULL,
    "PRCP_Name" character varying(250) NOT NULL,
    "PRCP_URI" character varying(250),
    "PRCP_DataURL" character varying(500),
    "PRCP_Metadata" xml,
    "PRCP_IsActive" smallint NOT NULL,
    "PRCP_ProviderDefinition" xml,
    "PRCP_CreationDate" timestamp without time zone NOT NULL,
    "PRCP_LastUpdate" timestamp without time zone NOT NULL,
    "PRCP_Tenant" uuid,
	"PRCP_Creator" uuid NOT NULL,
	"PRCP_PrincipalData" uuid
);

-------------------------------------------------------------------------------------------------
    
-----------------------------------------------Tenant--------------------------------------------

ALTER TABLE "customer" RENAME TO "Tenant";
    
ALTER TABLE ONLY "Tenant"
    RENAME COLUMN "cus_id" TO "TEN_ID";
    
ALTER TABLE ONLY "Tenant"	
    RENAME COLUMN "cus_code" TO "TEN_Code";
    
ALTER TABLE ONLY "Tenant"
    RENAME COLUMN "cus_creation" TO "TEN_CreationDate";
    
ALTER TABLE ONLY "Tenant"
    RENAME COLUMN "cus_emailaddress" TO "TEN_EmailAddress";
    
ALTER TABLE ONLY "Tenant"
    RENAME COLUMN "cus_lastupdate" TO "TEN_LastUpdate";
    
ALTER TABLE ONLY "Tenant"
    RENAME COLUMN "cus_name" TO "TEN_Name";
    
ALTER TABLE ONLY "Tenant"
    RENAME COLUMN "cus_creator" TO "TEN_Creator";

-------------------------------------------------------------------------------------------------
    
-------------------------------------------TenantActivation--------------------------------------

ALTER TABLE ONLY "customeractivation" RENAME TO "TenantActivation";
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_id" TO "TENA_ID";  
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_activationconfig" TO "TENA_ActivationConfig";   
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_creation" TO "TENA_CreationDate";   
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_end" TO "TENA_End";    
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_isactive" TO "TENA_IsActive";  
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_lastupdate" TO "TENA_LastUpdate";    
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_start" TO "TENA_Start";
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_creator" TO "TENA_Creator";    
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_customer" TO "TENA_Tenant";  
    
ALTER TABLE ONLY "TenantActivation"
    RENAME COLUMN "cusa_shape" TO "TENA_Shape";

-------------------------------------------------------------------------------------------------

-----------------------------------------Accounting----------------------------------------------

ALTER TABLE "accounting" RENAME TO "Accounting";
    
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_id" TO "ACC_ID";
    
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_creation" TO "ACC_CreationDate";
    
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_date" TO "ACC_Date";
    
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_isvalid" TO "ACC_IsValid";
	
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_lastupdate" TO "ACC_LastUpdate";
    
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_referencedata" TO "ACC_ReferenceData";
    
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_type" TO "ACC_Type";
    
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_units" TO "ACC_Units";
    
ALTER TABLE ONLY "Accounting"
	RENAME COLUMN "acc_creator" TO "ACC_Creator";
    
ALTER TABLE ONLY "Accounting"
    RENAME COLUMN "acc_customer" TO "ACC_Tenant";
    
ALTER TABLE ONLY "Accounting"
    RENAME COLUMN "acc_user" TO "ACC_Principal";
	
-------------------------------------------------------------------------------------------------

---------------------------------------Annotation------------------------------------------------

ALTER TABLE ONLY "annotation" RENAME TO "Annotation";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_id" TO "AN_ID";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_body" TO "AN_Body";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_creation" TO "AN_CreationDate";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_isshared" TO "AN_IsShared";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_lastupdate" TO "AN_LastUpdate";
	
ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_date" TO "AN_Date";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_target" TO "AN_Target";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_title" TO "AN_Title";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_creator" TO "AN_Creator";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_inresponseto" TO "AN_InResponseTo";

ALTER TABLE ONLY "Annotation"
	RENAME COLUMN "an_customer" TO "AN_Tenant";
	
-------------------------------------------------------------------------------------------------

-----------------------------------------Auditing------------------------------------------------

ALTER TABLE ONLY "auditing" RENAME TO "Auditing";

ALTER TABLE ONLY "Auditing"
	RENAME COLUMN "aud_id" TO "AUD_ID";

ALTER TABLE ONLY "Auditing"
	RENAME COLUMN "aud_creation" TO "AUD_CreationDate";

ALTER TABLE ONLY "Auditing"
	RENAME COLUMN "aud_date" TO "AUD_Date";

ALTER TABLE ONLY "Auditing"
	RENAME COLUMN "aud_lastupdate" TO "AUD_LastUpdate";

ALTER TABLE ONLY "Auditing"
	RENAME COLUMN "aud_data" TO "AUD_Data";

ALTER TABLE ONLY "Auditing"
	RENAME COLUMN "aud_type" TO "AUD_Type";

ALTER TABLE ONLY "Auditing"
	RENAME COLUMN "aud_creator" TO "AUD_Creator";

ALTER TABLE ONLY "Auditing"
    RENAME COLUMN "aud_customer" TO "AUD_Tenant";

ALTER TABLE ONLY "Auditing"
    RENAME COLUMN "aud_user" TO "AUD_Principal";
	
---------------------------------------------------------------------------------------------------------

-----------------------------------------Document--------------------------------------------------------

ALTER TABLE ONLY "document" RENAME TO "Document";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_id" TO "DOC_ID";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_creation" TO "DOC_CreationDate";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_description" TO "DOC_Description";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_lastupdate" TO "DOC_LastUpdate";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_mimesubtype" TO "DOC_MimeSubType";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_mimetype" TO "DOC_MimeType";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_name" TO "DOC_Name";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_size" TO "DOC_Size";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_url" TO "DOC_Url";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_creator" TO "DOC_Creator";

ALTER TABLE ONLY "Document" 
	RENAME COLUMN "doc_customer" TO "DOC_Tenant";

-----------------------------------------Mimetype--------------------------------------------------------

ALTER TABLE ONLY "mimetype" RENAME TO "MimeType";

ALTER TABLE ONLY "MimeType"
	RENAME COLUMN "mt_id" TO "MT_ID";

ALTER TABLE ONLY "MimeType"
	RENAME COLUMN "mt_creation" TO "MT_CreationDate";

ALTER TABLE ONLY "MimeType"
	RENAME COLUMN "mt_lastupdate" TO "MT_LastUpdate";

ALTER TABLE ONLY "MimeType"
	RENAME COLUMN "mt_filenameextension" TO "MT_FileNameExtension";

ALTER TABLE ONLY "MimeType"
	RENAME COLUMN "mt_mimesubtype" TO "MT_MimeSubtype";

ALTER TABLE ONLY "MimeType"
	RENAME COLUMN "mt_mimetype" TO "MT_MimeType";

ALTER TABLE ONLY "MimeType"
	RENAME COLUMN "mt_creator" TO "MT_Creator";

---------------------------------------------------------------------------------------------------------

-------------------------------------------Project-------------------------------------------------------

ALTER TABLE ONLY "project" RENAME TO "Project";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_id" TO "PRJ_ID";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_creation" To "PRJ_CreationDate";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_description" TO "PRJ_Description";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_istemplate" TO "PRJ_IsTemplate";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_lastupdate" TO "PRJ_LastUpdate";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_name" TO "PRJ_Name";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_shape" TO "PRJ_Shape";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_status" TO "PRJ_Status";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_creator" TO "PRJ_Creator";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_client" TO "PRJ_Client";

ALTER TABLE ONLY "Project"
	RENAME COLUMN "prj_customer" TO "PRJ_Tenant";

---------------------------------------------------------------------------------------------------------

----------------------------------------------projectdocument--------------------------------------------

ALTER TABLE ONLY "projectdocument" RENAME TO "ProjectDocument";

ALTER TABLE ONLY "ProjectDocument"
	RENAME COLUMN "pd_document" TO "PD_Document";

ALTER TABLE ONLY "ProjectDocument"
	RENAME COLUMN "pd_project" TO "PD_Project";

ALTER TABLE ONLY "ProjectDocument"
	RENAME COLUMN "pd_creation" TO "PD_CreationDate";

ALTER TABLE ONLY "ProjectDocument"
	RENAME COLUMN "pd_lastupdate" TO "PD_LastUpdate";

ALTER TABLE ONLY "ProjectDocument"
	RENAME COLUMN "pd_creator" TO "PD_Creator";

----------------------------------------------------------------------------------------------------------

-----------------------------------------------ProjectTerm------------------------------------------------

ALTER TABLE ONLY "projectterm" RENAME TO "ProjectTerm";

ALTER TABLE ONLY "ProjectTerm"
	RENAME COLUMN "prjt_project" TO "PRJT_Project";

ALTER TABLE ONLY "ProjectTerm"
	RENAME COLUMN "prjt_term" TO "PRJT_Term";

ALTER TABLE ONLY "ProjectTerm"
	RENAME COLUMN "prjt_creation" TO "PRJT_CreationDate";

ALTER TABLE ONLY "ProjectTerm"
	RENAME COLUMN "prjt_lastupdate" TO "PRJT_LastUpdate";

ALTER TABLE ONLY "ProjectTerm"
	RENAME COLUMN "prjt_creator" TO "PRJT_Creator";

ALTER TABLE ONLY "ProjectTerm"
	RENAME COLUMN "prjt_id" TO "PRJT_ID";

-------------------------------------------------------------------------------------------------------------

-------------------------------------------------Shape-------------------------------------------------------

ALTER TABLE ONLY "shape" RENAME TO "Shape";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_id" TO "SHP_ID";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_code" TO "SHP_Code";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_creation" TO "SHP_CreationDate";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_extradata" TO "SHP_ExtraData";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_geography" TO "SHP_Geography";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_lastupdate" TO "SHP_LastUpdate";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_name" TO "SHP_Name";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_class" TO "SHP_Class";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_creator" TO "SHP_Creator";

ALTER TABLE ONLY "Shape"
	RENAME COLUMN "shp_shapeimport" TO "SHP_ShapeImport";

-------------------------------------------------------------------------------------------------------------

------------------------------------------------ShapeDocument------------------------------------------------

ALTER TABLE ONLY "shapedocument" RENAME TO "ShapeDocument";

ALTER TABLE "ShapeDocument"
	RENAME COLUMN "sd_document" TO "SD_Document";

ALTER TABLE "ShapeDocument"
	RENAME COLUMN "sd_creation" TO "SD_CreationDate";

ALTER TABLE "ShapeDocument"
	RENAME COLUMN "sd_lastupdate" TO "SD_LastUpdate";

ALTER TABLE "ShapeDocument"
	RENAME COLUMN "sd_creator" TO "SD_Creator";

ALTER TABLE "ShapeDocument"
	RENAME COLUMN "sd_ttshape" TO "SD_TaxonomyTermShape";

ALTER TABLE "ShapeDocument"
	RENAME COLUMN "sd_id" TO "SD_ID";

-------------------------------------------------ShapeImport-------------------------------------------------

ALTER TABLE ONLY "shapeimport" RENAME TO "ShapeImport";

ALTER TABLE "ShapeImport"
	RENAME COLUMN "shpi_id" TO "SHPI_ID";

ALTER TABLE "ShapeImport"
	RENAME COLUMN "shpi_creation" TO "SHPI_CreationDate";

ALTER TABLE "ShapeImport"
	RENAME COLUMN "shpi_data" TO "SHPI_Data";

ALTER TABLE "ShapeImport"
	RENAME COLUMN "shpi_geography" TO "SHPI_Geography";

ALTER TABLE "ShapeImport"
	RENAME COLUMN "shpi_lastupdate" TO "SHPI_LastUpdate";

ALTER TABLE "ShapeImport"
	RENAME COLUMN "shpi_shapeidentity" TO "SHPI_ShapeIdentity";

ALTER TABLE "ShapeImport"
	RENAME COLUMN "shpi_import" TO "SHPI_Import";

ALTER TABLE "ShapeImport"
	RENAME COLUMN "shpi_creator" TO "SHPI_Creator";

-------------------------------------------------------------------------------------------------------------

------------------------------------------------ShapeTerm----------------------------------------------------

ALTER TABLE ONLY "shapeterm" RENAME TO "ShapeTerm";

ALTER TABLE ONLY "ShapeTerm"
	RENAME COLUMN "shpt_shape" TO "SHPT_Shape";

ALTER TABLE ONLY "ShapeTerm"
	RENAME COLUMN "shpt_term" TO "SHPT_Term";

ALTER TABLE ONLY "ShapeTerm"
	RENAME COLUMN "shpt_creation" TO "SHPT_CreationDate";

ALTER TABLE ONLY "ShapeTerm"
	RENAME COLUMN "shpt_lastupdate" TO "SHPT_LastUpdate";

ALTER TABLE ONLY "ShapeTerm"
	RENAME COLUMN "shpt_creator" TO "SHPT_Creator";

-------------------------------------------------------------------------------------------------------------
	
------------------------------------------------SysConfig----------------------------------------------------

ALTER TABLE ONLY "sysconfig" RENAME TO "SysConfig";

ALTER TABLE ONLY "SysConfig"
	RENAME COLUMN "sysc_id" TO "SYSC_ID";

ALTER TABLE ONLY "SysConfig"
	RENAME COLUMN "sysc_config" TO "SYSC_Config";

ALTER TABLE ONLY "SysConfig"
	RENAME COLUMN "sysc_class" TO "SYSC_Class";
	
ALTER TABLE ONLY "SysConfig"
	RENAME COLUMN "sysc_creation" TO "SYSC_CreationDate";

ALTER TABLE ONLY "SysConfig"
	RENAME COLUMN "sysc_lastupdate" TO "SYSC_LastUpdate";

ALTER TABLE ONLY "SysConfig"
	RENAME COLUMN "sysc_creator" TO "SYSC_Creator";

-------------------------------------------------------------------------------------------------

-----------------------------------------------Taxonomy------------------------------------------

ALTER TABLE ONLY "taxonomy" RENAME TO "Taxonomy";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_id" TO "TAX_ID";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_creation" TO "TAX_CreationDate";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_isactive" TO "TAX_IsActive";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_isusertaxonomy" TO "TAX_IsUserTaxonomy";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_lastupdate" TO "TAX_LastUpdate";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_name" TO "TAX_Name";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_creator" TO "TAX_Creator";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_class" TO "TAX_Class";

ALTER TABLE ONLY "Taxonomy"
	RENAME COLUMN "tax_extradata" TO "TAX_ExtraData";

-------------------------------------------------------------------------------------------------

-------------------------------------------TaxonomyTerm------------------------------------------

ALTER TABLE ONLY "taxonomyterm" RENAME TO "TaxonomyTerm";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_id" TO "TAXT_ID";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_creation" TO "TAXT_CreationDate";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_extradata" TO "TAXT_ExtraData";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_isactive" TO "TAXT_IsActive";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_lastupdate" TO "TAXT_LastUpdate";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_name" TO "TAXT_Name";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_order" TO "TAXT_Order";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_refclassschema" TO "TAXT_RefClassSchema";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_creator" TO "TAXT_Creator";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_parent" TO "TAXT_Parent";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_taxonomy" TO "TAXT_Taxonomy";

ALTER TABLE ONLY "TaxonomyTerm"
	RENAME COLUMN "taxt_class" TO "TAXT_Class";

----------------------------------------------------------------------------------------
	
-----------------------------------------TaxonomyTermLink-------------------------------
	
ALTER TABLE ONLY "taxonomytermlink" RENAME TO "TaxonomyTermLink";

ALTER TABLE ONLY "TaxonomyTermLink"
	RENAME COLUMN "taxtl_destterm" TO "TAXTL_DestinationTerm";

ALTER TABLE ONLY "TaxonomyTermLink"
	RENAME COLUMN "taxtl_sourceterm" TO "TAXTL_SourceTerm";

ALTER TABLE ONLY "TaxonomyTermLink"
	RENAME COLUMN "taxtl_creation" TO "TAXTL_CreationDate";

ALTER TABLE ONLY "TaxonomyTermLink"
	RENAME COLUMN "taxtl_lastupdate" TO "TAXTL_LastUpdate";

ALTER TABLE ONLY "TaxonomyTermLink"
	RENAME COLUMN "taxtl_verb" TO "TAXTL_Verb";

ALTER TABLE ONLY "TaxonomyTermLink"
	RENAME COLUMN "taxtl_creator" TO "TAXTL_Creator";

----------------------------------------------------------------------------------------

------------------------------------------TaxonomyTermShape-----------------------------

ALTER TABLE ONLY "taxonomytermshape" RENAME TO "TaxonomyTermShape";

ALTER TABLE ONLY "TaxonomyTermShape"
	RENAME COLUMN "taxts_id" TO "TAXTS_ID";

ALTER TABLE ONLY "TaxonomyTermShape"
	RENAME COLUMN "taxts_creation" TO "TAXTS_CreationDate";

ALTER TABLE ONLY "TaxonomyTermShape"
	RENAME COLUMN "taxts_lastupdate" TO "TAXTS_LastUpdate";

ALTER TABLE ONLY "TaxonomyTermShape"
	RENAME COLUMN "taxts_creator" TO "TAXTS_Creator";

ALTER TABLE ONLY "TaxonomyTermShape"
	RENAME COLUMN "taxts_shape" TO "TAXTS_Shape";

ALTER TABLE ONLY "TaxonomyTermShape"
	RENAME COLUMN "taxts_term" TO "TAXTS_Term";

-------------------------------------------------------------------------------------------------

---------------------------------------------Workflow--------------------------------------------

ALTER TABLE ONLY "workflow" RENAME TO "Workflow";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_id" TO "WF_ID";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_creation" TO "WF_CreationDate";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_description" TO "WF_Description";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_enddate" TO "WF_EndDate";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_extradata" TO "WF_ExtraData";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_lastupdate" TO "WF_LastUpdate";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_name" TO "WF_Name";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_reminderdate" TO "WF_ReminderDate";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_startdate" TO "WF_StartDate";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_status" TO "WF_Status";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_statusdate" TO "WF_StatusDate";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_creator" TO "WF_Creator";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_project" TO "WF_Project";

ALTER TABLE ONLY "Workflow"
	RENAME COLUMN "wf_template" TO "WF_Template";
	
-------------------------------------------------------------------------------------------------

--------------------------------------------WorkflowTask-----------------------------------------

ALTER TABLE ONLY "workflowtask" RENAME TO "WorkflowTask";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_id" TO "WFT_ID";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_creation" TO "WFT_CreationDate";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_critical" TO "WFT_Critical";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_enddate" TO "WFT_EndDate";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_extradata" TO "WFT_ExtraData";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_lastupdate" TO "WFT_LastUpdate";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_name" TO "WFT_Name";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_startdate" TO "WFT_StartDate";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_status" TO "WFT_Status";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_statusdate" TO "WFT_StatusDate";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_creator" TO "WFT_Creator";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_user" TO "WFT_Principal";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_workflow" TO "WFT_Workflow";

ALTER TABLE ONLY "WorkflowTask"
	RENAME COLUMN "wft_reminderdate" TO "WFT_ReminderDate";

-----------------------------------------WorkflowTaskDocument------------------------------------

ALTER TABLE ONLY "workflowtaskdocument" RENAME TO "WorkflowTaskDocument";

ALTER TABLE ONLY "WorkflowTaskDocument"
	RENAME COLUMN "wtd_workflowtask" TO "WTD_WorkflowTask";

ALTER TABLE ONLY "WorkflowTaskDocument"
	RENAME COLUMN "wtd_document" TO "WTD_Document";

ALTER TABLE ONLY "WorkflowTaskDocument"
	RENAME COLUMN "wtd_creation" TO "WTD_CreationDate";

ALTER TABLE ONLY "WorkflowTaskDocument"
	RENAME COLUMN "wtd_lastupdate" TO "WTD_LastUpdate";

ALTER TABLE ONLY "WorkflowTaskDocument"
	RENAME COLUMN "wtd_creator" TO "WTD_Creator";

-------------------------------------------------------------------------------------------------
	
COMMIT;