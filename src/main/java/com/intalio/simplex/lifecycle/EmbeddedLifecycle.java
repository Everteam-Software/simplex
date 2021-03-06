/*
 * Simplex, lightweight SimPEL server
 * Copyright (C) 2008-2009  Intalio, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.intalio.simplex.lifecycle;

import com.intalio.simplex.EmbeddedServer;
import com.intalio.simplex.Options;
import com.intalio.simplex.http.EngineWebResource;
import com.intalio.simplex.http.AdminWebResource;
import com.intalio.simplex.messaging.BindingContextImpl;
import com.intalio.simplex.messaging.MessageExchangeContextImpl;
import org.apache.log4j.Logger;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactory;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.engine.CountLRUDehydrationPolicy;
import org.apache.ode.bpel.evtproc.DebugBpelEventListener;
import org.apache.ode.bpel.iapi.*;
import org.apache.ode.il.dbutil.Database;
import org.apache.ode.scheduler.simple.JdbcDelegate;
import org.apache.ode.scheduler.simple.SimpleScheduler;
import org.apache.ode.utils.GUID;
import org.apache.ode.daohib.bpel.BpelDAOConnectionFactoryImpl;
import org.hibernate.cfg.Environment;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;

public class EmbeddedLifecycle {
    private static final Logger __log = Logger.getLogger(EmbeddedServer.class);

    protected Options _options;
    protected TransactionManager _txMgr;
    protected BpelServerImpl _server;
    protected Database _db;
    protected DataSource _ds;
    protected BpelDAOConnectionFactory _daoCF;
    protected ExecutorService _executorService;
    protected Scheduler _scheduler;
    protected EmbeddedStore _store;
    protected EngineWebResource _webEngine;

    public EmbeddedLifecycle(Options options) {
        _options = options;
        if (_options.getThreadPoolMaxSize() <= 0) _executorService = Executors.newCachedThreadPool();
        else _executorService = Executors.newFixedThreadPool(_options.getThreadPoolMaxSize());
    }

    public void start() {
        if (System.getProperty("btm.root") == null && getClass().getClassLoader().getResource("marker") != null) {
            File rootDir = new File(new File(getClass().getClassLoader().getResource("marker").getFile()).getParent());
            System.setProperty("btm.root", rootDir.getAbsolutePath());
        }

        __log.debug("Initializing transaction manager");
        initTxMgr();
        __log.debug("Creating data source.");
        initDataSource();
        __log.debug("Starting DAO.");
        initDAO();
        __log.debug("Initializing BPEL process store.");
        initProcessStore();

        if (_options.isRestful()) initRestfulServer();

        __log.debug("Initializing BPEL server.");
        initBpelServer();

        // Register BPEL event listeners configured in axis2.properties file.
        registerEventListeners();

        _server.start();
        _store.start();
        __log.info("Up and ready to serve.");
    }

    public void clean() {
        if (_store != null) _store.stop();
        if (_db != null) _db.shutdown();
        _db = null;
        _server = null;
        _txMgr = null;
        _executorService = null;
        _store = null;
        _ds = null;
        _scheduler = null;
        _scheduler = null;
        _webEngine = null;
    }

    private void initBpelServer() {
        _server = new BpelServerImpl();
        _scheduler = createScheduler();
        _scheduler.setJobProcessor(_server);

        _server.setDaoConnectionFactory(_daoCF);
//        _server.setEndpointReferenceContext(new EndpointReferenceContextImpl(this));
        _server.setMessageExchangeContext(new MessageExchangeContextImpl(_options.getMessageSender()));
        
        BindingContextImpl bc = new BindingContextImpl(_options);
        _server.setBindingContext(bc);

        _server.setScheduler(_scheduler);
        _server.setTransactionManager(_txMgr);
        if (_options.isDehydrationEnabled()) {
            CountLRUDehydrationPolicy dehy = new CountLRUDehydrationPolicy();
            _server.setDehydrationPolicy(dehy);
        }
        _server.setConfigProperties(_options.getOdeProperties());
        _server.init();
    }

//    protected void initDAO() {
//        BPELDAOConnectionFactoryImpl daoCF = new BPELDAOConnectionFactoryImpl();
//        daoCF.setDataSource(_ds);
//        daoCF.setTransactionManager(_txMgr);
//
//        boolean createSchema = createSchedulerTables();
//        Properties props = new Properties();
////        props.put("openjpa.Sequence", "org.apache.openjpa.jdbc.kernel.NativeJDBCSeq");
//        if (createSchema)
//            props.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
//        daoCF.init(props);
//        // Forcing EM creation to initialize the DB
//        if (createSchema)
//            daoCF.getEMF().createEntityManager().close();
//        _daoCF = daoCF;
//    }

    protected void initDAO() {
        BpelDAOConnectionFactoryImpl daoCF = new BpelDAOConnectionFactoryImpl();
        daoCF.setDataSource(_ds);
        daoCF.setTransactionManager(_txMgr);

        boolean createSchema = createSchedulerTables();
        Properties props = new Properties();
        props.put(Environment.AUTO_CLOSE_SESSION, "true");
        props.put(Environment.FLUSH_BEFORE_COMPLETION, "true");
        if (createSchema)
            props.put(Environment.HBM2DDL_AUTO, "create-drop");
        daoCF.init(props);
        _daoCF = daoCF;
    }

    protected void initDataSource() {
        try {
            InitialContext ctx = new InitialContext();
            _ds = (DataSource) ctx.lookup(_options.getDatasource());
        } catch (NamingException e) {
            throw new RuntimeException("Could not find datasource " + _options.getDatasource(), e);
        }
    }

    protected void initTxMgr() {
        try {
            InitialContext ctx = new InitialContext();
            _txMgr = (TransactionManager) ctx.lookup(_options.getTransactionManager());
        } catch (NamingException e) {
            throw new RuntimeException("Could not find transaction manager under java:comp/UserTransaction.", e);
        }
    }

    protected Scheduler createScheduler() {
        SimpleScheduler scheduler = new SimpleScheduler(new GUID().toString(),new JdbcDelegate(_ds), new Properties());
        scheduler.setTransactionManager(_txMgr);
        _scheduler = scheduler;
        return scheduler;
    }

    protected boolean createSchedulerTables() {
        boolean createSchema = false;
        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        try {
            conn = _ds.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            if (metaData != null) {
                result = metaData.getTables("APP", null, "ODE_JOB", null);

                if (!result.next()) {
                    createSchema = true;
                    String dbProductName = metaData.getDatabaseProductName();
                    if (dbProductName.indexOf("Derby") >= 0) {
                        stmt = conn.createStatement();
                        stmt.execute(DERBY_SCHEDULER_DDL1);
                        stmt.close();
                        stmt = conn.createStatement();
                        stmt.execute(GENERIC_SCHEDULER_DDL2);
                        stmt.close();
                        stmt = conn.createStatement();
                        stmt.execute(GENERIC_SCHEDULER_DDL3);
                        stmt.close();
                    } else if (dbProductName.indexOf("HSQL") >= 0 || dbProductName.indexOf("H2") >= 0) {
                        stmt = conn.createStatement();
                        stmt.execute(HSQL_SCHEDULER_DDL);
                    } else if (dbProductName.indexOf("MySQL") >= 0) {
                        stmt = conn.createStatement();
                        stmt.execute(MYSQL_SCHEDULER_DDL);
                        stmt.close();
                        stmt = conn.createStatement();
                        stmt.execute(GENERIC_SCHEDULER_DDL2);
                        stmt.close();
                        stmt = conn.createStatement();
                        stmt.execute(GENERIC_SCHEDULER_DDL3);
                    }
//                    stmt = conn.createStatement();
//                    stmt.execute(JPA_SEQ);
                }
            }

        } catch (SQLException e) {
            createSchema = false;
            // Swallowing it, either it already exists in which case we don't care or
            // creation failed and we'll find out soon enough
        } finally {
            try {
                if (result != null) result.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                __log.info(se);
            }
        }
        return createSchema;
    }

    protected void initProcessStore() {
        _store = new EmbeddedStore();
        _store.registerListener(new ProcessStoreListenerImpl());
    }

    protected void initRestfulServer() {
        EngineWebResource.setupRestfulServer(this);
        AdminWebResource.init(_store);
    }
    
    public void setEngineWebResource(EngineWebResource webEngine) {
        _webEngine = webEngine;
    }

    /**
     * Register event listeners configured in the configuration.
     *
     */
    private void registerEventListeners() {
        // let's always register the debugging listener....
        _server.registerBpelEventListener(new DebugBpelEventListener());

        // then, whatever else they want.
        List<BpelEventListener> listeners = _options.getBpelEventListeners();
        if (listeners != null) {
            for (BpelEventListener listener : listeners) {
               _server.registerBpelEventListener(listener);
            }
        }
    }

    public class ProcessStoreListenerImpl implements ProcessStoreListener {
        public void onProcessStoreEvent(ProcessStoreEvent event) {
            handleEvent(event);
        }
    }

    private void handleEvent(ProcessStoreEvent pse) {
        __log.debug("Process store event: " + pse);
        switch (pse.type) {
            case ACTIVATED:
            case RETIRED:
                // bounce the process
                _server.unregister(pse.pid);
                ProcessConf pconf = _store.getProcessConfiguration(pse.pid);
                if (pconf != null) _server.register(pconf);
                else __log.debug("slighly odd: received event " + pse + " for process not in store!");
                break;
            case DISABLED:
            case UNDEPLOYED:
                _server.unregister(pse.pid);
                break;
            default:
                __log.debug("Ignoring store event: " + pse);
        }
    }

    public EmbeddedStore getStore() {
        return _store;
    }

    public BpelServerImpl getServer() {
        return _server;
    }

    private static final String HSQL_SCHEDULER_DDL =
            "CREATE TABLE ODE_JOB (" +
                    " jobid CHAR(64) DEFAULT '' NOT NULL," +
                    " ts BIGINT DEFAULT 0 NOT NULL ," +
                    " nodeid char(64)  NULL," +
                    " scheduled int DEFAULT 0 NOT NULL," +
                    " transacted int DEFAULT 0 NOT NULL," +
                    " details LONGVARBINARY NULL," +
                    " PRIMARY KEY(jobid));\n" +
                    "CREATE INDEX IDX_ODE_JOB_TS ON ODE_JOB (ts);\n" +
                    "CREATE INDEX IDX_ODE_JOB_NODEID ON ODE_JOB (nodeid);\n";
//                    "CREATE SEQUENCE IF NOT EXISTS OPENJPA_SEQUENCE;\n";

    private static final String MYSQL_SCHEDULER_DDL =
            "CREATE TABLE ODE_JOB (" +
                    " jobid CHAR(64) DEFAULT '' NOT NULL," +
                    " ts BIGINT DEFAULT 0 NOT NULL ," +
                    " nodeid char(64)  NULL," +
                    " scheduled int DEFAULT 0 NOT NULL," +
                    " transacted int DEFAULT 0 NOT NULL," +
                    " details BLOB(4096) NULL," +
                    " PRIMARY KEY(jobid));\n";

    private static final String DERBY_SCHEDULER_DDL1 =
            "CREATE TABLE ODE_JOB (" +
                    " jobid CHAR(64) DEFAULT '' NOT NULL," +
                    " ts BIGINT DEFAULT 0 NOT NULL ," +
                    " nodeid char(64)," +
                    " scheduled int DEFAULT 0 NOT NULL," +
                    " transacted int DEFAULT 0 NOT NULL," +
                    " details BLOB(50K)," +
                    " PRIMARY KEY (jobid))";
    private static final String GENERIC_SCHEDULER_DDL2 = "CREATE INDEX IDX_ODE_JOB_TS ON ODE_JOB (ts)";
    private static final String GENERIC_SCHEDULER_DDL3 = "CREATE INDEX IDX_ODE_JOB_NODEID ON ODE_JOB (nodeid)";

    private static final String JPA_SEQ = "CREATE TABLE OPENJPA_SEQUENCE_TABLE (ID SMALLINT NOT NULL, SEQUENCE_VALUE BIGINT, PRIMARY KEY (ID));";

}
