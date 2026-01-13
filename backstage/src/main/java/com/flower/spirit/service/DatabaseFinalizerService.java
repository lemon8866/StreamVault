package com.flower.spirit.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class DatabaseFinalizerService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFinalizerService.class);

    @Autowired
    private DataSource dataSource;

    @PreDestroy
    public void onApplicationShutdown() {
        log.info("应用正在停止,正在准备触发数据库回写..");
        try {
            ensureDataPersisted();
            log.info("数据库回写成功,应用正常退出");
        } catch (Exception e) {
            log.warn("数据回写失败,应用退出可能存在异常", e);
        }
    }


    /**
     * @throws SQLException
     */
    public void ensureDataPersisted() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA wal_checkpoint(FULL)");
        }
    }
}