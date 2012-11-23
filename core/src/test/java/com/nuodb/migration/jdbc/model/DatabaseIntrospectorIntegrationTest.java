package com.nuodb.migration.jdbc.model;


import com.nuodb.migration.TestConstants;
import com.nuodb.migration.jdbc.connection.JdbcConnectionProvider;
import com.nuodb.migration.spec.JdbcConnectionSpec;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class DatabaseIntrospectorIntegrationTest {


    private Connection connection;
    private DatabaseInspector inspector;
    private Connection mySqlConnection;


    @Before
    public void setUp() throws Exception {
        JdbcConnectionSpec mysql = new JdbcConnectionSpec();
        mysql.setDriverClassName("com.mysql.jdbc.Driver");
        mysql.setUrl("jdbc:mysql://localhost:3306/test");
        mysql.setUsername("root");

        JdbcConnectionSpec nuodb = TestConstants.createTestNuoDBConnectionSpec();

        final JdbcConnectionProvider connectionProvider =
                new JdbcConnectionProvider(nuodb);
        final JdbcConnectionProvider mySqlConnectionProvider
                = new JdbcConnectionProvider(mysql);


        connection = connectionProvider.getConnection();
        // mySqlConnection = mySqlConnectionProvider.getConnection();


        Assert.assertNotNull(connection);
        Assert.assertNotNull(connection.getMetaData());
        Assert.assertFalse(connection.isClosed());

        inspector = new DatabaseInspector();
        inspector.withConnection(connection);
    }

    @Test
    public void testReadInfo() throws Exception {

        final Database database = new Database();
        inspector.readInfo(connection.getMetaData(), database);

        final DriverInfo driverInfo = database.getDriverInfo();
        final DatabaseInfo databaseInfo = database.getDatabaseInfo();
        Assert.assertNotNull(driverInfo);
        Assert.assertNotNull(databaseInfo);
        Assert.assertNotNull(driverInfo.getName());
        Assert.assertNotNull(driverInfo.getMajorVersion());
        Assert.assertNotNull(driverInfo.getMinorVersion());
        Assert.assertNotNull(driverInfo.getVersion());
        Assert.assertNotNull(databaseInfo.getProductName());
        Assert.assertNotNull(databaseInfo.getMajorVersion());
        Assert.assertNotNull(databaseInfo.getMinorVersion());
    }

    @Test
    public void testReadObjects() throws Exception {
        final Database database = new Database();
        inspector.readObjects(connection.getMetaData(), database);

        final List<Catalog> catalogs = database.listCatalogs();
        Assert.assertFalse(catalogs.isEmpty());

        final List<Schema> schemas = database.listSchemas();
        Assert.assertFalse(schemas.isEmpty());

        final List<Table> tables = database.listTables();
        Assert.assertFalse(tables.isEmpty());
    }


    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
