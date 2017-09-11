package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Version;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/version")
public class VersionApi {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 传入版本ID，用int类型比较最新版本ID，版本不同就更新，否则返回成功消息（已是最新）
     * @param versionId
     * @return
     */

    @RequestMapping("check/{versionId}")
    @ResponseBody
    public ApiResponse checkVersion(@PathVariable("versionId") String versionId){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(versionId)){
            apiResponse.setFailureMsg("3","versionId是必传数值");
            return apiResponse;
        }

        String querySql = "SELECT id FROM version ORDER BY id DESC limit 1";

        Integer latestVersionId = jdbcTemplate.queryForObject(querySql,Integer.class);

        if(Integer.parseInt(versionId) < latestVersionId){
            String sql = "SELECT * FROM version ORDER BY id DESC limit 1";
            Version version = jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<Version>(Version.class));
            apiResponse.setSuccess();
            apiResponse.setSuccessData(version);
        }
        else{
            apiResponse.setSuccessMsg("当前已经是最新版本");
        }
        return apiResponse;
    }

    /**
     * 传入版本id，返回此版本ID所有信息（包括id，number，description，url，createTime）
     * @param versionId
     * @return
     */

    @RequestMapping("get/{versionId}")
    @ResponseBody
    public ApiResponse getVersion(@PathVariable("versionId") String versionId){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(versionId)){
            apiResponse.setFailureMsg("3","versionId是必传数值");
            return apiResponse;
        }

        String querySql = "SELECT * FROM version WHERE id = ?";

        List<Object> params = new ArrayList<Object>();
        params.add(versionId);

        Version version = jdbcTemplate.queryForObject(querySql,params.toArray(), new BeanPropertyRowMapper<Version>(Version.class));
        apiResponse.setSuccess();
        apiResponse.setSuccessData(version);
        return apiResponse;
    }

}
