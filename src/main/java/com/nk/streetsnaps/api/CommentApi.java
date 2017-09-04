package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Comment;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/comment")
public class CommentApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 输入套图ID，获取套图ID下面所有用户的评论
     * @param albumId
     * @param pageNum
     * @param pageSizee
     * @return
     */
    @RequestMapping("/{albumId}/list")
    @ResponseBody
    public ApiResponse getList(@PathVariable("albumId") String albumId,
                            @RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSizee){

        ApiResponse apiResponse = new ApiResponse();

        if(StringUtils.isBlank(albumId)){
            apiResponse.setFailureMsg("3","albumId未传递");
            return apiResponse;
        }
        //
        String sql = "SELECT count(*) FROM user_album_comment where  album_id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        Integer count = jdbcTemplate.queryForObject(sql,params.toArray(),Integer.class);
        if(count > 0){
            String querySql ="SELECT * FROM user_album_comment where album_id = ?";
            List<Comment> comments = jdbcTemplate.query(querySql,params.toArray(),new BeanPropertyRowMapper<Comment>(Comment.class));
            apiResponse.setSuccessData(comments);
            return apiResponse;
        }
        else{

        }

        List<Comment> comments = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Comment>(Comment.class));

        apiResponse.setSuccessData(comments);

        return apiResponse;

    }

    /**
     * 获取某相册某用户的所有评论
     * @param albumId
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/{albumId}/{userId}/list")
    @ResponseBody
    public ApiResponse getUserCommentList(@PathVariable("albumId") String albumId,
                               @PathVariable("userId") String userId,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize){

        ApiResponse apiResponse = new ApiResponse();

        if(StringUtils.isBlank(albumId)){
            apiResponse.setFailureMsg("3","albumId/userId未传递");
            return apiResponse;
        }
        //
        String sql = "SELECT count(*) FROM user_album_comment where  album_id = ? AND userId = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        params.add(userId);
        Integer count = jdbcTemplate.queryForObject(sql,params.toArray(),Integer.class);
        if(count > 0){
            String querySql ="SELECT * FROM user_album_comment where album_id = ? AND userId = ?";
            List<Comment> comments = jdbcTemplate.query(querySql,params.toArray(),new BeanPropertyRowMapper<Comment>(Comment.class));
            apiResponse.setSuccessData(comments);
            return apiResponse;
        }
        else{

        }

        List<Comment> comments = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Comment>(Comment.class));

        apiResponse.setSuccessData(comments);

        return apiResponse;

    }

}
