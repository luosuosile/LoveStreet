package com.nk.streetsnaps.api;

import com.nk.streetsnaps.entity.Album;
import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.HotWord;
import com.nk.streetsnaps.util.IDUtil;
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
@RequestMapping("/api/serach")
public class SerachApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/{keyword}")
    @ResponseBody
    public ApiResponse serach(@PathVariable("keyword") String keyword,
                              @RequestParam(defaultValue = "0") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(keyword)){
            apiResponse.setFailureMsg("3","keyword是必传数值");
        }
        //下面这部分有关于把用户搜索的关键字插入hot_word表中
        String checkSql = "SELECT DISTINCT IF(EXISTS(SELECT * FROM hot_word WHERE hot_word= ?),1,0)";
        List<Object> params3 = new ArrayList<Object>();
        params3.add(keyword);
        Integer check = jdbcTemplate.queryForObject(checkSql,params3.toArray(),Integer.class);

        //下面update count +1
        if(check == 1) {
            String updateSql = "update hot_word SET count = count + 1 WHERE hot_word = ? ";
            jdbcTemplate.update(updateSql,params3.toArray());
        }//下面这部分有关于把用户搜索的关键字插入hot_word表中
        else {
            String insertSql = "INSERT INTO hot_word (id,hot_word,count) VALUES(?,?,?)";
            List<Object> params2 = new ArrayList<Object>();
            params2.add(IDUtil.getUUID());
            params2.add(keyword);
            params2.add(1);
            jdbcTemplate.update(insertSql,params2.toArray());
        }
        //下面这部分是有关于搜索关键字
        String sql = "SELECT *" +
                " FROM album WHERE name like ? or content like ? LIMIT ?,?";
        List<Object> params = new ArrayList<Object>();
        params.add("%" +keyword + "%");
        params.add("%" +keyword + "%");
        params.add(pageNum*pageSize);
        params.add(pageSize);
        List<Album> albums = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Album>(Album.class));
     /*   for(Album album : albums){
            String querySql = "SELECT url FROM picture WHERE id IN " +
                    "(SELECT picture_id FROM album_picture WHERE album_id = ?) limit 1";
            List<Object> params4 = new ArrayList<Object>();
            params4.add(album.getId());
            String pictureUrl = jdbcTemplate.queryForObject(querySql,params4.toArray(),String.class);
            album.setFirstPicture(pictureUrl);
        }*/
        apiResponse.setSuccess();
        apiResponse.setData(albums);
        return apiResponse;
    }

    @RequestMapping("/hotkeyword")
    @ResponseBody
    public ApiResponse getHotKeyWord(@RequestParam(defaultValue = "0") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize){
        ApiResponse apiResponse = new ApiResponse();
        String sql = "SELECT * FROM hot_word ORDER BY count DESC LIMIT ?,?";
        List<Object> params = new ArrayList<Object>();
        params.add(pageNum*pageSize);
        params.add(pageSize);
        List<HotWord> hotWords = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<HotWord>(HotWord.class));
        apiResponse.setSuccess();
        apiResponse.setData(hotWords);
        return apiResponse;
    }
}