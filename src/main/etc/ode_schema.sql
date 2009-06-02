DROP TABLE IF EXISTS ODE_JOB;
CREATE TABLE ODE_JOB (
  jobid CHAR(64)  NOT NULL DEFAULT '',
  ts BIGINT  NOT NULL DEFAULT 0,
  nodeid char(64)  NULL,
  scheduled int  NOT NULL DEFAULT 0,
  transacted int  NOT NULL DEFAULT 0,
  details blob(4096)  NULL,
  PRIMARY KEY(jobid),
  INDEX IDX_ODE_JOB_TS(ts),
  INDEX IDX_ODE_JOB_NODEID(nodeid)
) TYPE=InnoDB;

create table BPEL_ACTIVITY_RECOVERY (ID bigint not null auto_increment, PIID bigint, AID bigint, CHANNEL varchar(255), REASON varchar(255), DATE_TIME datetime, LDATA_ID bigint, ACTIONS varchar(255), RETRIES integer, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_CORRELATION_PROP (ID bigint not null auto_increment, NAME varchar(255), NAMESPACE varchar(255), VALUE varchar(255), CORR_SET_ID bigint, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_CORRELATION_SET (ID bigint not null auto_increment, VALUE varchar(255), CORR_SET_NAME varchar(255), SCOPE_ID bigint, PIID bigint, PROCESS_ID bigint, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_CORRELATOR (ID bigint not null auto_increment, CID varchar(255), PROCESS_ID bigint, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_CORRELATOR_MESSAGE_CKEY (ID bigint not null auto_increment, CKEY varchar(255), CORRELATOR_MESSAGE_ID bigint, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_EVENT (ID bigint not null auto_increment, IID bigint, PID bigint, TSTAMP datetime, TYPE varchar(255), DETAIL text, LDATA_ID bigint, SID bigint, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_FAULT (ID bigint not null auto_increment, FAULTNAME varchar(255), LDATA_ID bigint, EXPLANATION varchar(4000), LINE_NUM integer, AID integer, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_INSTANCE (ID bigint not null auto_increment, INSTANTIATING_CORRELATOR bigint, FAULT bigint, JACOB_STATE bigint, PREVIOUS_STATE smallint, PROCESS_ID bigint, STATE smallint, LAST_ACTIVE_DT datetime, SEQUENCE bigint, FAILURE_COUNT integer, FAILURE_DT datetime, EXEC_STATE_COUNT integer, INSTANTIATE_URL varchar(255), INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_MESSAGE (ID bigint not null auto_increment, TYPE varchar(255), DATA bigint, HEADER bigint, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_MESSAGE_EXCHANGE (ID bigint not null auto_increment, MEXID varchar(255) not null unique, PORT_TYPE varchar(255), CHANNEL_NAME varchar(255), CLIENTKEY varchar(255), LDATA_EPR_ID bigint, LDATA_CEPR_ID bigint, REQUEST bigint, RESPONSE bigint, INSERT_DT datetime, OPERATION varchar(255), STATE varchar(255), PROCESS bigint, PIID bigint, DIR char(1), PLINK_MODELID integer, PATTERN varchar(255), CORR_STATUS varchar(255), FAULT_TYPE varchar(255), FAULT_EXPL varchar(255), CALLEE varchar(255), PARTNERLINK bigint, TIMEOUT bigint, ISTYLE varchar(255), P2P_PEER varchar(255), FAILURE_TYPE varchar(255), ACK_TYPE varchar(255), PIPED_PID varchar(255), RESOURCE varchar(255), INST_RES bit, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_MEX_PROPS (MEX bigint not null, VALUE varchar(8000), NAME varchar(255) not null, primary key (MEX, NAME)) TYPE=InnoDB;
create table BPEL_PLINK_VAL (ID bigint not null auto_increment, PARTNER_LINK varchar(100) not null, PARTNERROLE varchar(100), MYROLE_EPR bigint, PARTNERROLE_EPR bigint, PROCESS bigint, SCOPE bigint, SVCNAME varchar(255), MYROLE varchar(100), MODELID integer, MYSESSIONID varchar(255), PARTNERSESSIONID varchar(255), INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_PROCESS (ID bigint not null auto_increment, PROCID varchar(255) not null unique, deployer varchar(255), deploydate datetime, type_name varchar(255), type_ns varchar(255), version bigint, ACTIVE_ bit, guid varchar(255), INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_RES_ROUTE (ID bigint not null auto_increment, URL varchar(255) not null, METHOD varchar(255) not null, CHANNEL varchar(255) not null, CHAN_INDEX integer not null, PIID bigint, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_SCOPE (ID bigint not null auto_increment, PIID bigint, PARENT_SCOPE_ID bigint, STATE varchar(255) not null, NAME varchar(255), MODELID integer, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_SELECTORS (ID bigint not null auto_increment, PIID bigint not null, SELGRPID varchar(255) not null, IDX integer not null, CORRELATION_KEY varchar(255) not null, PROC_TYPE varchar(255) not null, CORRELATOR bigint not null, INSERT_TIME datetime, MLOCK integer not null, primary key (ID), unique (CORRELATION_KEY, CORRELATOR)) TYPE=InnoDB;
create table BPEL_UNMATCHED (ID bigint not null auto_increment, MEX bigint, CORRELATION_KEY varchar(255), CORRELATOR bigint not null, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table BPEL_XML_DATA (ID bigint not null auto_increment, LDATA_ID bigint, NAME varchar(255) not null, SCOPE_ID bigint, PIID bigint, IS_SIMPLE_TYPE bit, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table LARGE_DATA (ID bigint not null auto_increment, BIN_DATA blob, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create table VAR_PROPERTY (ID bigint not null auto_increment, XML_DATA_ID bigint, PROP_VALUE varchar(255), PROP_NAME varchar(255) not null, INSERT_TIME datetime, MLOCK integer not null, primary key (ID)) TYPE=InnoDB;
create index IDX_CORRELATOR_CID on BPEL_CORRELATOR (CID);
create index IDX_BPEL_CORRELATOR_MESSAGE_CKEY on BPEL_CORRELATOR_MESSAGE_CKEY (CKEY);
create index IDX_SELECTOR_CORRELATOR on BPEL_SELECTORS (CORRELATOR);
create index IDX_SELECTOR_CKEY on BPEL_SELECTORS (CORRELATION_KEY);
create index IDX_SELECTOR_SELGRPID on BPEL_SELECTORS (SELGRPID);
create index IDX_UNMATCHED_CKEY on BPEL_UNMATCHED (CORRELATION_KEY);
create index IDX_UNMATCHED_CORRELATOR on BPEL_UNMATCHED (CORRELATOR);
create index IDX_RES_ROUTE_PIID on BPEL_RES_ROUTE (PIID);
create index IDX_RES_ROUTE_RES on BPEL_RES_ROUTE (URL);
