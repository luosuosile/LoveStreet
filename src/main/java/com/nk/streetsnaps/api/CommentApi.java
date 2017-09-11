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
     * 现在的问题是一个用户可以给一个评论多次点赞
     * 没评论的时候怎么办没写
     * 输入套图ID，获取套图ID下面所有用户的评论
     * @param albumId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/{albumId}/list")
    @ResponseBody
    public ApiResponse getList(@PathVariable("albumId") String albumId,
                            @RequestParam(defaultValue = "0") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize){

        ApiResponse apiResponse = new ApiResponse();

        if(StringUtils.isBlank(albumId)){
            apiResponse.setFailureMsg("3","albumId未传递");
            return apiResponse;
        }
        //
        String sql = "SELECT count(*) FROM user_album_comment where  album_id = ?";//评论表中有没有某相册的评论数量
        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        Integer count = jdbcTemplate.queryForObject(sql,params.toArray(),Integer.class);
        if(count > 0){
            String querySql ="SELECT * ," +
                    "(SELECT count(*) FROM user_album_comment_praise WHERE comment_id = comment.id) AS praiseNum" +
                    " FROM user_album_comment AS comment where album_id = ? limit ?,?";
            params.add(pageNum*pageSize);
            params.add(pageSize);
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
     * 获取某相册下某用户的所有评论
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
                               @RequestParam(defaultValue = "0") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize){

        ApiResponse apiResponse = new ApiResponse();

        if(StringUtils.isBlank(albumId)){
            apiResponse.setFailureMsg("3","albumId/userId未传递");
            return apiResponse;
        }
        //
        String sql = "SELECT count(*) FROM user_album_comment where  album_id = ? AND user_id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        params.add(userId);
        Integer count = jdbcTemplate.queryForObject(sql,params.toArray(),Integer.class);
        if(count > 0){
            String querySql ="SELECT *,"+
                    "(SELECT count(*) FROM user_album_comment_praise WHERE comment_id = comment.id) AS praiseNum" +
                    " FROM user_album_comment AS comment where album_id = ? AND user_id = ?" +
                    "LIMIT ?,?";
            params.add(pageNum*pageSize);
            params.add(pageSize);
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
     * 对某相册里，某用户的评论的点赞开关
     * 如何防止一个用户点赞多次，在可以多次评论的情况下，是否需要分表(未完成)
     * 是否要增加个注册查看所有评论功能（非注册限制查看数量）
     * @return
     */

    @RequestMapping("/{albumId}/{userId}/{userPraiseId}/{commentId}/list")
    @ResponseBody
    public ApiResponse parise(@PathVariable("albumId") String albumId, @PathVariable("userId") String userId,
                              @PathVariable("userPraiseId") String userPraiseId, @PathVariable("commentId") String commentId) {
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(albumId)||StringUtils.isBlank(userId)||StringUtils.isBlank(userPraiseId)||StringUtils.isBlank(commentId)){
            apiResponse.setFailureMsg("3","xx为必传入数值");
            return apiResponse;
        }

        String sql = "SELECT count(*) FROM user_album_comment_praise WHERE album_id = ? AND user_id = ? AND user_praise_id = ? AND comment_id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        params.add(userId);
        params.add(userPraiseId);
        params.add(commentId);
        Integer count = jdbcTemplate.queryForObject(sql,params.toArray(),Integer.class);//是否大于0


        if(count > 0) {

            String delSql = "DELETE FROM user_album_comment_praise WHERE album_id = ? AND user_id = ? AND user_praise_id = ? AND comment_id = ? ";//删除点赞
            jdbcTemplate.update(delSql,params.toArray());
            apiResponse.setSuccessMsg("取消点赞请求成功" + String.valueOf(count));
        }
        else{
            String insertSql = "INSERT INTO user_album_comment_praise (album_id,user_id,user_praise_id,comment_id) VALUES (?,?,?,?)";
            jdbcTemplate.update(insertSql,params.toArray());
            apiResponse.setSuccessMsg("点赞请求成功" + String.valueOf(count));
        }

        return apiResponse;
    }



}
