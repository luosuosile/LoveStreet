package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Comment;
import com.nk.streetsnaps.entity.SubComment;
import com.nk.streetsnaps.util.IDUtil;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/comment")
public class CommentApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 返回某书下面所有的评论
     * 父评论列表（包含子评论）
     * @param albumId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/{albumId}/list")
    public ApiResponse list(@PathVariable("albumId") String albumId, @RequestParam(value = "hot", defaultValue = "false")boolean hot,
                            @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam(defaultValue = "1") Integer subPageNum, @RequestParam(defaultValue = "3") Integer subPageSize,
                            @RequestParam String userId){
        ApiResponse apiResponse = new ApiResponse();
        //
        String sql = "";
        if (hot){
            sql = "SELECT " +
                    " uComment.*, u.nick_name,u.head_picture, " +
                    " (SELECT count(*) FROM user_album_comment_praise WHERE uComment.id = comment_id) AS praiseAmount, " +
                    " (SELECT COUNT(*) FROM user_album_comment_praise WHERE user_id=? and comment_id=uComment.id) AS isPraise " +
                    " FROM " +
                    " user_album_comment uComment " +
                    " LEFT JOIN user u ON uComment.user_id = u.id " +
                    " WHERE album_id = ?" +
                    " AND uComment.id IN (" +
                    " SELECT comment_id FROM( " +
                    " SELECT comment_id from user_album_comment_praise GROUP BY comment_id ORDER BY COUNT(comment_id) DESC LIMIT ?,?)AS a ) ";
        } else {
            sql = "SELECT " +
                    " uComment.*, u.nick_name,u.head_picture, " +
                    " (SELECT count(*) FROM user_album_comment_praise WHERE uComment.id = comment_id) AS praiseAmount, " +
                    " (SELECT COUNT(*) FROM user_album_comment_praise WHERE user_id=? and comment_id=uComment.id) AS isPraise " +
                    " FROM " +
                    " user_album_comment uComment " +
                    " LEFT JOIN user u ON uComment.user_id = u.id " +
                    " WHERE album_id = ?" +
                    "ORDER BY create_time DESC"+//加了个根据时间倒序
                    " LIMIT ?,?";
        }
        List<Object> params = new ArrayList<Object>();
        params.add(userId);
        params.add(albumId);
        params.add((pageNum - 1) * pageSize);
        params.add(pageSize);
        List<Comment> comments = jdbcTemplate.query(sql, params.toArray(), new BeanPropertyRowMapper<Comment>(Comment.class));

        //是不是缺个时间倒序？
        String sql2 = "SELECT " +
                " uCommentSub.*, u.nick_name, " +
//                "(SELECT count(*) FROM user_ebook_comment_praise WHERE uCommentSub.id = user_ebook_comment_praise.comment_id) AS praiseNum," +//加了个获取子评论点赞数量
                " (SELECT nick_name from user WHERE id = uCommentSub.user_id) AS atNickname " +
                "FROM " +
                " user_album_comment_sub uCommentSub " +
                "LEFT JOIN user u ON uCommentSub.user_id = u.id " +
                "WHERE " +
                " uCommentSub.parent_comment_id = ?" +
                " ORDER BY create_time DESC" +
                " LIMIT ?,?"; //只显示2条，还需要分页显示最新评论，还需要热评5条应该是父评论的
        for (Comment comment : comments) {
            Map<String , Object> map = new HashMap<String , Object>();
            List<SubComment> subComments = jdbcTemplate.query(sql2,
                    new Object[]{comment.getId(),(subPageNum - 1) * subPageSize,  subPageSize}, new BeanPropertyRowMapper<SubComment>(SubComment.class));
            Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_album_comment_sub WHERE parent_comment_id=?",new Object[]{comment.getId()}, Integer.class);
            map.put("list", subComments);
            map.put("total", total);
            comment.setSubComments(map);
        }

        apiResponse.setSuccessData(comments);
        return apiResponse;
    }

    /**
     * 评论点赞开关（只对父评论起作用）
     * @param userId
     * @param commentId
     * @return
     */
    @RequestMapping("/praise/switch/{userId}/{commentId}")
    public ApiResponse praise(@PathVariable("userId")String userId, @PathVariable("commentId")String commentId){
        ApiResponse apiResponse = new ApiResponse();

        //
        String querySql = "SELECT count(*) FROM user_album_comment_praise WHERE user_id = ? AND comment_id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);
        params.add(commentId);

        //
        Integer count = jdbcTemplate.queryForObject(querySql, params.toArray(), Integer.class);
        if(count > 0){
            String delSql = "delete FROM user_album_comment_praise WHERE user_id = ? AND comment_id = ?";
            jdbcTemplate.update(delSql, params.toArray());
            apiResponse.setSuccessMsg("取消点赞成功");
        } else {
            String insertSql = "INSERT INTO user_album_comment_praise(user_id,comment_id) VALUES (?,?)";
            jdbcTemplate.update(insertSql, params.toArray());
            apiResponse.setSuccessMsg("点赞成功");
        }
        return apiResponse;
    }

    /**
     * 对评论进行再评论的情况
     * @param userId
     * @param commentId
     * @param content
     * @param subCommentId
     * @return
     */
    @RequestMapping("/sub/{userId}/{commentId}")
    public ApiResponse subComment(@PathVariable("userId")String userId, @PathVariable("commentId")String commentId,
                                  @RequestParam String content, String subCommentId){
        ApiResponse apiResponse = new ApiResponse();
        String sql = "INSERT INTO user_album_comment_sub(id, user_id, parent_comment_id,at_comment_id, content, create_time) VALUES (?, ?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<Object>();
        params.add(IDUtil.getUUID());
        params.add(userId);
        params.add(commentId);
        params.add(subCommentId);
        params.add(content);
        params.add(new Date());

        //
        jdbcTemplate.update(sql, params.toArray());
        apiResponse.setSuccess();
        return apiResponse;
    }

    /**
     * 谁评论了哪本书
     * @return
     */
    @RequestMapping("/{userId}/{albumId}")
    public ApiResponse comment(@PathVariable("userId")String userId, @PathVariable("albumId")String albumId,
                               @RequestParam(value = "content")String content, @RequestParam(defaultValue = "0")Double rate){
        ApiResponse apiResponse = new ApiResponse();

        //
        String sql = "INSERT INTO user_album_comment(id, user_id, album_id, content, rate, create_time) VALUES (?, ? ,?, ?, ?, ?)";
        List<Object> params = new ArrayList<Object>();
        params.add(IDUtil.getUUID());
        params.add(userId);
        params.add(albumId);
        params.add(content);
        params.add(rate);
        params.add(new Date());

        //
        try {
            int count = jdbcTemplate.update(sql, params.toArray());
            if(count > 0){
                apiResponse.setSuccess();
            } else {
                apiResponse.setFailureMsg("4","插入数据库失败");
            }
        } catch (Exception e){
            apiResponse.setFailureMsg("4","插入数据库失败");
        }
        System.out.println();
        return apiResponse;
    }


}
