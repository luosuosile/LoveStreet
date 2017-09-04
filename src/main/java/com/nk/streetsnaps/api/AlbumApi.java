package com.nk.streetsnaps.api;


import com.nk.streetsnaps.entity.Album;
import com.nk.streetsnaps.entity.ApiResponse;
import com.nk.streetsnaps.entity.Picture;
import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;
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
@RequestMapping("/api/album")
public class AlbumApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;


//    @RequestMapping("list")
//    @ResponseBody
//    public ApiResponse ceshi(@PathVariable("list")String list){
//
//        ApiResponse apiResponse = new ApiResponse();
//        if(StringUtils.isBlank(list)){
//            apiResponse.setFailureMsg("3","list是必传参数");
//            return apiResponse;
//        }
//        List<Album> album = new ArrayList<Album>();
//        apiResponse.setSuccessData(album);
//        return apiResponse;
//    }

    @RequestMapping("list")
    @ResponseBody
    public ApiResponse getEbooks(@PathVariable("list")String list){//这是拿来确认是否连接上数据库的
        ApiResponse apiResponse = new ApiResponse();
        List<Picture> ebooks = new ArrayList<Picture>();
        apiResponse.setSuccessData(ebooks);
        return apiResponse;
    }

    /**
     *返回某相册里面所有的url
     * @param albumId
     * @return
     */
    @RequestMapping("/{albumid}")
    @ResponseBody
    public ApiResponse getAlbum(@PathVariable("albumid") String albumId ){
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
