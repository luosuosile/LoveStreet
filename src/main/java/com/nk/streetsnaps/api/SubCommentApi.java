package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.SubComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subComment")
public class SubCommentApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/{parentCommentId}/list")
    public ApiResponse list(@PathVariable("parentCommentId")String parentCommentId,
                            @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize){
        ApiResponse apiResponse = new ApiResponse();

        Map<String , Object> map = new HashMap<String , Object>();
        List<SubComment> subComments = jdbcTemplate.query("SELECT * FROM user_album_comment_sub WHERE parent_comment_id=? LIMIT ?,?",
                new Object[]{parentCommentId, (pageNum - 1) * pageSize, pageSize}, new BeanPropertyRowMapper<SubComment>(SubComment.class));
        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_album_comment_sub WHERE parent_comment_id=?",new Object[]{parentCommentId}, Integer.class);
        map.put("list", subComments);
        map.put("total", total);
        apiResponse.setSuccess();
        apiResponse.setSuccessData(map);
        return apiResponse;
    }

}