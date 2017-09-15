package com.nk.streetsnaps.api;

import com.nk.streetsnaps.entity.Album;
import com.nk.streetsnaps.entity.ApiResponse;
import jdk.nashorn.internal.scripts.JD;
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
@RequestMapping("/api/favorite")
public class FavoriteApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 谁收藏/取消收藏了那本书
     */

    @RequestMapping("/switch/{userId}/{albumId}")
    @ResponseBody
    public ApiResponse switchFavorite(@PathVariable("userId")String userId,@PathVariable("albumId")String albumId) {
        ApiResponse apiResponse = new ApiResponse();

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(albumId)) {
            apiResponse.setFailureMsg("3","userId/albumId未传递");
            return apiResponse;
        }

        String querySql = "SELECT count(*) FROM user_album_favorite WHERE user_id = ? AND album_id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);
        params.add(albumId);

        Integer count = jdbcTemplate.queryForObject(querySql,params.toArray(), Integer.class);

        if(count > 0){
            String delSql = "delete FROM user_album_favorite WHERE user_id = ? AND album_id = ?";
            jdbcTemplate.update(delSql, params.toArray());
            apiResponse.setSuccessMsg("取消收藏成功");
        }
        else {
            String insertSql = "INSERT INTO user_album_favorite(user_id,album_id) VALUES(?,?)";
            jdbcTemplate.update(insertSql,params.toArray());
            apiResponse.setSuccessMsg("收藏成功");
        }
        return apiResponse;
    }

    /**
     * 这个是我加的，获取某用户收藏的接口
     * @param userId
     * @return
     */

    @RequestMapping("/{userId}")
    @ResponseBody
    public ApiResponse getFavorite(@PathVariable("userId")String userId) {
        ApiResponse apiResponse = new ApiResponse();

        if (StringUtils.isBlank(userId) ) {
            apiResponse.setFailureMsg("3", "userId/albumId未传递");
            return apiResponse;
        }

        String querySql = "SELECT * FROM album where id in"+"(SELECT album_id FROM user_album_favorite WHERE user_id =?)";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);

        List<Album> album = jdbcTemplate.query(querySql,params.toArray(),new BeanPropertyRowMapper<Album>(Album.class));

        apiResponse.setSuccessData(album);
        return apiResponse;
    }

}
