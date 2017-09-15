package com.nk.streetsnaps.api;

import com.nk.streetsnaps.entity.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import sun.management.snmp.AdaptorBootstrap;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/copyright/")
public class CopyrightApi  {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 版权反馈接口
     * @param complaint
     * @return
     */
    @RequestMapping("/copyright")
    @ResponseBody
    public ApiResponse postComplaint(@RequestParam(defaultValue = "null") String albumId,
                                     @RequestParam(defaultValue = "null") String complaint,
                                     @RequestParam(defaultValue = "null") String userId,
                                     @RequestParam(defaultValue = "null") String connectWay){
        ApiResponse apiResponse = new ApiResponse();
        String sql = "Insert INTO user_album_complaint (album_id,complaint,user_id,connect_way) VALUES (?,?,?,?)";
        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        params.add(complaint);
        params.add(userId);
        params.add(connectWay);
        jdbcTemplate.update(sql,params.toArray());
        apiResponse.setSuccess();
        apiResponse.setData(0);
        return apiResponse;
    }
}