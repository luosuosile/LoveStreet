package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.Album;
import com.nk.streetsnaps.entity.AlbumCount;
import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Tag;
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
@RequestMapping("/api/hot")
public class HotApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/list")
    @ResponseBody
    public ApiResponse getHotAlbum(@RequestParam(defaultValue = "0") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize){
        ApiResponse apiResponse = new ApiResponse();

        String sql = "SELECT * FROM album WHERE id IN (SELECT album_id FROM album_count Order BY count DESC) LIMIT ?,?";

        List<Object> params = new ArrayList<Object>();

        params.add(pageNum);

        params.add(pageSize);

        List<Album> albums = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Album>(Album.class));

        apiResponse.setSuccessData(albums);

        return apiResponse;
    }

    @RequestMapping("/taglist")
    @ResponseBody
    public ApiResponse getHotTag(@RequestParam(defaultValue = "0") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize){
        ApiResponse apiResponse = new ApiResponse();

        String sql = "SELECT * FROM tag WHERE id IN (SELECT tag_id FROM tag_count Order BY count DESC) LIMIT ?,?";

        List<Object> params = new ArrayList<Object>();

        params.add(pageNum);

        params.add(pageSize);

        List<Tag> tags = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Tag>(Tag.class));

        apiResponse.setSuccessData(tags);

        return apiResponse;
    }

    @RequestMapping("/{tag}/list")
    @ResponseBody
    public ApiResponse getTagHot(@PathVariable("tag") String tag,
                                 @RequestParam(defaultValue = "0") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize){
        ApiResponse apiResponse = new ApiResponse();
        String sql = "SELECT * FROM album_count WHERE album_id IN " +
                "(SELECT album_id FROM album_tag WHERE tag_id IN (SELECT id FROM tag WHERE picture_tag = ?)) ORDER BY count DESC";
        List<Object> params = new ArrayList<Object>();
        params.add(tag);
        List<AlbumCount> albumCounts = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<AlbumCount>(AlbumCount.class));

        apiResponse.setData(albumCounts);

        return apiResponse;
    }

}
