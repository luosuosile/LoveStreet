package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.Album;
import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Picture;
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
     * @return
     */

    @RequestMapping("list")
    @ResponseBody
    public ApiResponse getAlbumList( @RequestParam(defaultValue = "0") Integer pageNum ,@RequestParam(defaultValue = "10" ) Integer pageNum2){
        ApiResponse apiResponse = new ApiResponse();
        String sql = "SELECT " +
                " album.*,  " +
//                "(SELECT picture_tag FROM tag WHERE id IN (SELECT tag_id FROM album_tag WHERE album_id = album.id)) AS tag," +//之后再说
                " (SELECT count(*) FROM user_album_comment WHERE album_id=album.id ) AS commentAmount, " + //这句是某书的评论总数
                " (SELECT count(*) FROM user_album_favorite WHERE album_id=album.id ) AS favoriteAmount, " +//这句是某书的收藏总数
                " (SELECT count(*) FROM user_album_praise WHERE album_id=album.id ) AS praiseAmount, " +//这句是某数的点赞总数
                " (SELECT count(*) FROM user_album_read WHERE album_id=album.id ) AS readAmount, " +//这句是某书用户阅读总数
                " (SELECT count(*) FROM album_picture WHERE album_id=album.id ) AS pictureAmount " +//这是某书的章节总数
                "FROM " +
                " album AS album " +
                "limit ?,?";
        List<Object> params = new ArrayList<Object>();
        params.add(pageNum);
        params.add(pageNum2);
        List<Album> albums = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Album>(Album.class));

        for( Album album : albums ){
            String querySql =  "SELECT picture_tag FROM tag WHERE id IN (SELECT tag_id FROM album_tag WHERE album_id = ? )";
            List<Object> params2 = new ArrayList<Object>();
            params2.add(album.getId());
            List<String> tags = jdbcTemplate.queryForList(querySql,params2.toArray(),String.class);
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
    public ApiResponse getAlbum (@PathVariable("albumId") String albumId) {
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(albumId)){
            apiResponse.setFailureMsg("3","albumId是必传数值");
            return apiResponse;
        }
        String sql = "SELECT * FROM "+
                "(SELECT "+
                " album.*,  " +
                "(SELECT picture_tag FROM tag WHERE id IN ANY (SELECT tag_id FROM album_tag WHERE album_id = album.id)) AS tag," +
                " (SELECT count(*) FROM user_album_comment WHERE album_id=album.id ) AS commentAmount, " + //这句是某书的评论总数
                " (SELECT count(*) FROM user_album_favorite WHERE album_id=album.id ) AS favoriteAmount, " +//这句是某书的收藏总数
                " (SELECT count(*) FROM user_album_praise WHERE album_id=album.id ) AS praiseAmount, " +//这句是某数的点赞总数
                " (SELECT count(*) FROM user_album_read WHERE album_id=album.id ) AS readAmount, " +//这句是某书用户阅读总数
                " (SELECT count(*) FROM album_picture WHERE album_id=album.id ) AS pictureAmount " +//这是某书的章节总数
                "FROM " +
                " album AS album ) AS A WHERE id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        List<Album> Album = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Album>(Album.class));
        apiResponse.setSuccessData(Album);
        return apiResponse;
    }

    /**
     *返回某相册里面所有的图片url
     * @param albumId
     * @return
     */
    @RequestMapping("/{albumid}/picture")
    @ResponseBody
    public ApiResponse getAlbumPicture(@PathVariable("albumid") String albumId ,
                                       @RequestParam(defaultValue = "0") Integer pageNum1,
                                       @RequestParam(defaultValue = "4") Integer pageNUm2){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(albumId)){
            apiResponse.setFailureMsg("3","albumId是必输的数值");
            return apiResponse;
        }

        String sql ="SELECT * from picture where id in" +
                "(SELECT picture_id from album_picture where album_id = ?)" +
                "limit ?,?";

        List<Object> params = new ArrayList<Object>();
        params.add(albumId);
        params.add(pageNum1);
        params.add(pageNUm2);

        List<Picture> PictureAlbum = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<Picture>(Picture.class));
        apiResponse.setSuccessData(PictureAlbum);
        return apiResponse;
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
}
