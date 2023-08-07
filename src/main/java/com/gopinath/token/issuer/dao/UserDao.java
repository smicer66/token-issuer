package com.gopinath.token.issuer.dao;

import com.gopinath.token.issuer.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getUserByEmailAddress;
    private SimpleJdbcCall createOneTimePassword;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);

        getUserByEmailAddress = new SimpleJdbcCall(jdbcTemplate)
//                .withFunctionName("GetUserByEmailAddress")
                .withProcedureName("GetUserByEmailAddress")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));

        createOneTimePassword = new SimpleJdbcCall(jdbcTemplate)
//                .withFunctionName("GetUserByEmailAddress")
                .withProcedureName("CreateOneTimePassword")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(String.class));
    }


    public List<User> getUserByEmailAddress(String emailAddress) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("emailAddress", emailAddress);
        Map<String, Object> m = getUserByEmailAddress.execute(in);
        logger.info("{}", m);
        List<User> result = (List<User>) m.get("#result-set-1");

        return result;
    }

    public void createOneTimePassword(String username, String otp, LocalDateTime expiryDate, String otpKey) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("username", username)
                .addValue("otp", otp)
                .addValue("otpKey", otpKey)
                .addValue("expiryDate", expiryDate);
        createOneTimePassword.execute(in);
    }
}
