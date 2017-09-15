package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Vip;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("api/vip/")
public class VipApi {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @RequestMapping("isvip/{userId}")
    @ResponseBody
    public ApiResponse isVip(@PathVariable("userId") String userId){
        ApiResponse apiResponse = new ApiResponse();
        String querySql = "SELECT * FROM user_vip WHERE user_id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);
        String sql = "SELECT DISTINCT IF(EXISTS(SELECT * FROM user_vip WHERE user_id = ?),1,0)";
        Integer isExists = jdbcTemplate.queryForObject(sql,params.toArray(),Integer.class);
        if(isExists == 1) {
            Vip vip = jdbcTemplate.queryForObject(querySql, params.toArray(), new BeanPropertyRowMapper<Vip>(Vip.class));
            apiResponse.setSuccessData(vip);
        }
        else {
            String data = "该用户不是vip,或者vip已过期";
            apiResponse.setSuccessData(data);
        }

        return apiResponse;

    }

    @RequestMapping("paid/{userId}/{isSuperVip}/{duration}")
    @ResponseBody
    public ApiResponse isPaid(@PathVariable("userId") String userId,
                              @PathVariable("isSuperVip") String isSuperVip,
                              @PathVariable("duration") String duration){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(userId)||StringUtils.isBlank(isSuperVip)||StringUtils.isBlank(duration)){
            apiResponse.setFailureMsg("3","userId/isSuperVip/duration是必填属性");
        }
        String querySql = "INSERT INTO user_vip(user_id,is_super_vip,duration,create_time) VALUES(?,?,?,?)";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);
        params.add(isSuperVip);
        params.add(duration);
        params.add(new Date());
        jdbcTemplate.update(querySql,params.toArray());
        apiResponse.setSuccess();
        apiResponse.setSuccessData(0);
        return apiResponse;
    }

}
