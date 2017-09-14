package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.Album;
import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Picture;
import com.nk.streetsnaps.entity.Tag;
import com.nk.streetsnaps.util.IDUtil;
import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;

import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.java2d.pipe.AATextRenderer;
import sun.java2d.pipe.LCDTextRenderer;
import sun.management.snmp.AdaptorBootstrap;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/album")
public class AlbumApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 这里其实不需要做任何限制不是吗？
     * 返回所有相册
     *
     * @return
     */

    @RequestMapping("list")
    @ResponseBody
    public ApiResponse getAlbumList(@RequestParam(defaultValue = "0") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        ApiResponse apiResponse = new ApiResponse();
        String sql = "SELECT " +
                " album.*,  " +
                " (SELECT count(*) FROM user_album_comment WHERE album_id=album.id ) AS commentAmount, " +
                " (SELECT count(*) FROM user_album_favorite WHERE album_id=album.id ) AS favoriteAmount, " +
                " (SELECT count(*) FROM user_album_praise WHERE album_id=album.id ) AS praiseAmount, " +
                " (SELECT count(*) FROM user_album_read WHERE album_id=album.id ) AS readAmount " +
                "FROM " +
                " album AS album " +
                "limit ?,?";
        List<Object> params = new ArrayList<Object>();
        params.add(pageNum*pageSize);
        params.add(pageSize);
        List<Album> albums = jdbcTemplate.query(sql, params.toArray(), new BeanPropertyRowMapper<Album>(Album.class));

        for (Album album : albums) {
            String querySql = "SELECT picture_tag FROM tag WHERE id IN (SELECT tag_id FROM album_tag WHERE album_id = ? )";
            List<Object> params2 = new ArrayList<Object>();
            params2.add(album.getId());
            List<String> tags = jdbcTemplate.queryForList(querySql, params2.toArray(), String.class);
            album.setTag(tags);
        }
        apiResponse.setSuccessData(albums);
        return apiResponse;
    }

    /**
     * 获取某个相册，未写
     */
    @RequestMapping("list/{albumId}")
    @ResponseBody
    public ApiResponse getAlbum(@PathVariable("albumId") String albumId) {
        ApiResponse apiResponse = new ApiResponse();
        if (StringUtils.isBlank(albumId)) {
            apiResponse.setFailureMsg("3", "albumId是必传数值");
            return apiResponse;
        }
        String sql = "SELECT * FROM " +
                "(SELECT " +
                " album.*,  " +
                " (SELECT count(*) FROM user_album_comment WHERE album_id=album.id ) AS commentAmount, " +
                " (SELECT count(*) FROM user_album_favorite WHERE album_id=album.id ) AS favoriteAmount, " +
                " (SELECT count(*) FROM user_album_praise WHERE album_id=album.id ) AS praiseAmount, " +
                " (SELECT count(*) FROM user_album_read WHERE album_id=album.id ) AS readAmount, " +
                "FROM " +
                " album AS album ) AS A WHERE id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        Album album = jdbcTemplate.queryForObject(sql, params.toArray(), new BeanPropertyRowMapper<Album>(Album.class));
        String querySql = "SELECT picture_tag FROM tag WHERE id IN (SELECT tag_id FROM album_tag WHERE album_id = ? )";
        List<String> tags = jdbcTemplate.queryForList(querySql, params.toArray(), String.class);
        album.setTag(tags);
        apiResponse.setSuccessData(album);
        return apiResponse;
    }

    /**
     *返回某相册里面所有的图片url,并且会给album_count里面对应行加一，用来统计某相册的点击人数
     * @param albumId
     * @return
     */
    @RequestMapping("/{albumid}/picture")
    @ResponseBody
    public ApiResponse getAlbumPicture(@PathVariable("albumid") String albumId){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(albumId)){
            apiResponse.setFailureMsg("3","albumId是必输的数值");
            return apiResponse;
        }

        String sql ="SELECT * from picture where id in" +
                "(SELECT picture_id from album_picture where album_id = ?)";


        List<Object> params = new ArrayList<Object>();
        params.add(albumId);

        List<Picture> PictureAlbum = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Picture>(Picture.class));

        //上面是返回图片地址，下面是给这个相册点击数加一
        //下面这部分有关于把用户搜索的关键字插入hot_word表中
        String checkSql = "SELECT DISTINCT IF(EXISTS(SELECT * FROM album_count WHERE album_id= ?),1,0)";
        List<Object> params3 = new ArrayList<Object>();
        params3.add(albumId);
        Integer check = jdbcTemplate.queryForObject(checkSql,params3.toArray(),Integer.class);
        //下面update count +1
        if(check == 1) {
            String updateSql = "update album_count SET count = count + 1 WHERE album_id = ? ";
            jdbcTemplate.update(updateSql,params3.toArray());
        }//下面这部分有关于把用户搜索的关键字插入hot_word表中
        else {
            String insertSql = "INSERT INTO album_count (album_id,count) VALUES(?,?)";
            List<Object> params2 = new ArrayList<Object>();
            params2.add(albumId);
            params2.add(1);
            jdbcTemplate.update(insertSql,params2.toArray());
        }

        /*******************************************************************************/
        //这里给对应的类型加一？
        String querySql ="SELECT * FROM tag WHERE id IN (SELECT tag_id FROM album_tag WHERE album_id = ? )";
        List<Object> params4 = new ArrayList<Object>();
        params4.add(albumId);
        List<Tag> tags = jdbcTemplate.query(querySql,params4.toArray(),new BeanPropertyRowMapper<Tag>(Tag.class));
        for(Tag tag : tags) {
            String checkTagSql = "SELECT DISTINCT IF(EXISTS(SELECT * FROM tag_count WHERE tag_id = ?),1,0)";
            List<Object> params5 = new ArrayList<Object>();
            params5.add(tag.getId());
            Integer checkTag = jdbcTemplate.queryForObject(checkTagSql, params5.toArray(), Integer.class);
            //下面update count +1
            if (checkTag == 1) {
                String updateSql = "update tag_count SET count = count + 1 WHERE tag_id = ? ";
                jdbcTemplate.update(updateSql, params5.toArray());
            }//下面这部分有关于把用户搜索的关键字插入tag_count表中
            else {
                String insertSql = "INSERT INTO tag_count (tag_id,count) VALUES(?,?)";
                List<Object> params2 = new ArrayList<Object>();
                params2.add(tag.getId());
                params2.add(1);
                jdbcTemplate.update(insertSql, params2.toArray());
            }
        }

        apiResponse.setSuccessData(PictureAlbum);
        return apiResponse;
    }


    @RequestMapping("/insert")
    @ResponseBody
    public ApiResponse insert(){
        ApiResponse apiResponse = new ApiResponse();
        String querySql = "SELECT * FROM album ";
        List<Album> albums = jdbcTemplate.query(querySql,new BeanPropertyRowMapper<Album>(Album.class));
        for(Album album :albums){
            String sql = "SELECT count(*) FROM album_picture WHERE album_id = ?";
            List<Object> params = new ArrayList<Object>();
            params.add(album.getId());
            Integer picNum = jdbcTemplate.queryForObject(sql,params.toArray(),Integer.class);
            String updateSql = "UPDATE album SET pics_num = ? WHERE id = ?";
            List<Object> params2 = new ArrayList<Object>();
            params2.add(picNum);
            params2.add(album.getId());
            jdbcTemplate.update(updateSql,params2.toArray());
        }
        apiResponse.setSuccess();
        return apiResponse;
    }

//    /**
//     * 通过套图url返回套图所有url
//     *
//     * @param albumUrl
//     * @param pageNum1
//     * @param pageNum2
//     * @return
//     */
//    @RequestMapping("/{albumurl}/picture")
//    @ResponseBody
//    public ApiResponse getAlbumPicture(@PathVariable("albumurl") String albumUrl,
//                                       @RequestParam(defaultValue = "0") Integer pageNum1,
//                                       @RequestParam(defaultValue = "4") Integer pageNum2) {
//        ApiResponse apiResponse = new ApiResponse();
//        if (StringUtils.isBlank(albumUrl)) {
//            apiResponse.setFailureMsg("3", "albumUrl是必传数值");
//            return apiResponse;
//        }
//
//        String sql = "SELECT * FROM picture WHERE id IN " +//通过pictureId获取picture信息
//                "(SELECT picture_id WHERE album_id IN " +//通过id获取 pictureId
//                "(SELECT id from album where album_url = ?)))" +    //通过album_url从album获取id
//                "limit ?,?";
//
//        List<Object> params = new ArrayList<Object>();
//        params.add(pageNum1);
//        params.add(pageNum2);
//        params.add(albumUrl);
//        List<Picture> pictures = jdbcTemplate.queryForList(sql, params.toArray(), Picture.class);
//
//        apiResponse.setSuccess();
//        apiResponse.setSuccessData(pictures);
//        return apiResponse;
//    }
}

//    @RequestMapping("pictureId")
//    @ResponseBody
//    public ApiResponse getPicture(@RequestParam("pictureId")String pictureId){
//        ApiResponse apiResponse = new ApiResponse();
//        if(StringUtils.isBlank(pictureId)){
//            apiResponse.setFailureMsg("3","pictureId为闭窗参数");
//            return apiRespone;
//        }
//
//        String sql = "SELECT" +
//                "ebook.*"  +
//                "(SELECT count(*) FROM )";
//
//    }

