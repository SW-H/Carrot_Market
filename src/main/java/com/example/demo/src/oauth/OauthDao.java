package com.example.demo.src.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class OauthDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public int getUserIdx(long id){
        String getUserIdxQuery = "select userIdx from User where kakaoId=?";
        return this.jdbcTemplate.queryForObject(getUserIdxQuery,
                int.class,
                id);
    }
}
