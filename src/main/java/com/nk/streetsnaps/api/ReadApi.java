package com.nk.streetsnaps.api;

import com.nk.streetsnaps.entity.Album;
import com.nk.streetsnaps.entity.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/read")
public class ReadApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 谁阅读了哪本书
     * @return
     */

    @RequestMapping("/insertread")//插入谁读了哪本书
    @ResponseBody
    public ApiResponse read(@RequestParam(defaultValue = "null") String userId, @RequestParam(defaultValue = "null") String albumId) {
        ApiResponse apiResponse = new ApiResponse();
        if (StringUtils.isBlank(userId) || StringUtils.isBlank((albumId))) {
            apiResponse.setFailureMsg("3", "userId/albumId未传递");
            return apiResponse;
        }

        String querySql = "SELECT count(*) FROM user_album_read WHERE user_id = ? AND album_id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);
        params.add(albumId);

        Integer count = jdbcTemplate.queryForObject(querySql, params.toArray(), Integer.class);
        if (count > 0) {
            String updateSql = "UPDATE user_album_read SET last_read_time = ? WHERE user_id = ? AND album_id = ?";
            jdbcTemplate.update(updateSql,new Object[] {new Date(),albumId, userId});
            apiResponse.setSuccess();
            apiResponse.setData(0);
        } else {
            String insertSql = "INSERT INTO user_album_read(user_id,album_id, last_read_time) VALUES(?,?,?)";
            params.add(new Date());
            jdbcTemplate.update(insertSql, params.toArray());
            apiResponse.setSuccess();
            apiResponse.setData(0);

        }
        return apiResponse;

    }

    /**
     * 这是我加的获取某用户已读的接口
     * @param userId
     * @return
     */

    @RequestMapping("/{userId}")
    @ResponseBody
    public ApiResponse getReaded(@PathVariable("userId") String userId){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(userId)){
            apiResponse.setFailureMsg("3","userId未传递");
            return apiResponse;
        }

        String querySql = "SELECT *" +
                "FROM album where id in "+
                "(SELECT album_id from user_album_read WHERE user_id = ?)";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);

        List<Album>  album = jdbcTemplate.query(querySql,params.toArray(),new BeanPropertyRowMapper<Album>(Album.class));
        apiResponse.setSuccessData(album);
        return apiResponse;
    }
}
