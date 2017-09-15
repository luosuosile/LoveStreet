package com.nk.streetsnaps.api;

import com.nk.streetsnaps.entity.Album;
import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Tag;
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
@RequestMapping("/api/tag")
public class TagApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("list")
    @ResponseBody
    public ApiResponse tagList() {
        ApiResponse apiResponse = new ApiResponse();
        String sql = "SELECT * FROM tag";

        List<Tag> tag = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Tag>(Tag.class));
        apiResponse.setSuccess();
        apiResponse.setSuccessData(tag);

        return apiResponse;
    }


    @RequestMapping("/getalbum/{name}")
    @ResponseBody
    public ApiResponse getTagBook(@PathVariable("name") String name,
                                  @RequestParam(defaultValue = "0") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        ApiResponse apiResponse = new ApiResponse();
        if (StringUtils.isBlank(name)) {
            apiResponse.setFailureMsg("3", "name是必传数值");
            return apiResponse;
        }

        String querySql = "SELECT * FROM album WHERE id IN " +
                "(SELECT album_id FROM album_tag WHERE tag_id IN" +
                "(SELECT id FROM tag WHERE picture_tag =?))" +
                "LIMIT ?,?";
        List<Object> params = new ArrayList<Object>();
        params.add(name);
        params.add((pageNum * pageSize));
        params.add(pageSize);
        List<Album> albums = jdbcTemplate.query(querySql, params.toArray(), new BeanPropertyRowMapper<Album>(Album.class));
        for (Album album : albums) {
            String sql = "SELECT picture_tag FROM tag WHERE id IN (SELECT tag_id FROM album_tag WHERE album_id = ?)";
            List<Object> params2 = new ArrayList<Object>();
            params2.add(album.getId());
            List<String> tags = jdbcTemplate.queryForList(sql, params2.toArray(), String.class);
            album.setTag(tags);
        }
        apiResponse.setSuccess();
        apiResponse.setSuccessData(albums);
        return apiResponse;
    }
}