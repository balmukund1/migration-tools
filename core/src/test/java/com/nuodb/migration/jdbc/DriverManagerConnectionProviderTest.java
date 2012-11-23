package com.nuodb.migration.jdbc;


import com.nuodb.migration.jdbc.connection.JdbcConnectionProvider;
import com.nuodb.migration.spec.JdbcConnectionSpec;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

import static org.mockito.Mockito.*;

public class DriverManagerConnectionProviderTest {

    public static final String URL = "jdbc:test";
    private JdbcConnectionSpec connectionSpec;
    private Connection connection;
    private Driver driver;
    private static final int transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;

    @Before
    public void setUp() throws Exception {
        connection = mock(Connection.class);

        driver = (Driver) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{Driver.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                if ("connect".equals(name)) {
                    return connection;
                } else if ("acceptsURL".equals(name)) {
                    return URL;
                }
                return null;
            }
        });
        DriverManager.registerDriver(driver);

        connectionSpec = mock(JdbcConnectionSpec.class);
        when(connectionSpec.getDriver()).thenReturn(driver);
        when(connectionSpec.getUrl()).thenReturn(URL);
        when(connectionSpec.getUsername()).thenReturn("user");
        when(connectionSpec.getPassword()).thenReturn("pass");
    }

    @Test
    public void testGetConnection() throws Exception {
        JdbcConnectionProvider connectionProvider =
                new JdbcConnectionProvider(connectionSpec);
        connectionProvider.setTransactionIsolation(transactionIsolation);
        connectionProvider.setAutoCommit(false);
        final Connection connection = connectionProvider.getConnection();
        Assert.assertNotNull(connection);
        verify(connectionSpec, times(1)).getDriver();
        verify(connectionSpec, times(1)).getUsername();
        verify(connectionSpec, times(1)).getUrl();
        verify(connectionSpec, times(1)).getPassword();
        verify(this.connection, times(1)).setTransactionIsolation(anyInt());
        verify(this.connection, times(1)).setAutoCommit(false);
    }

    @After
    public void tearDown() throws Exception {
        DriverManager.deregisterDriver(driver);
    }
}
