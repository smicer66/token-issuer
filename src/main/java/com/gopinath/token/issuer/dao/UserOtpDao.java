package com.gopinath.token.issuer.dao;

import com.gopinath.token.issuer.model.UserOtp;
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
public class UserOtpDao {

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getUserOtpByOtpAndKey;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);

        getUserOtpByOtpAndKey = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetUserOtpByOtpAndKey")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(UserOtp.class));
    }


    public List<UserOtp> getUserOtpByOtpAndKey(String username, String key, String otp) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("username", username)
                .addValue("otpKey", key)
                .addValue("otp", otp);
        Map<String, Object> m = getUserOtpByOtpAndKey.execute(in);
        logger.info("{}", m);
        List<UserOtp> result = (List<UserOtp>) m.get("#result-set-1");

        return result;
    }
}
