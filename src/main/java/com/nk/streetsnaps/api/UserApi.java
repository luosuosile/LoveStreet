package com.nk.streetsnaps.api;

import com.nk.streetsnaps.entity.*;
import com.nk.streetsnaps.util.IDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/devnum")
    public ApiResponse postUserId(@RequestParam(defaultValue = "null") String deviceNumber){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(deviceNumber)){
            apiResponse.setFailureMsg("3","deviceNumber是必传数值");
        }
        //取随机值
        int random =(int)(1+Math.random()*(1000-1+1));

        String sql = "SELECT DISTINCT IF(EXISTS(SELECT * FROM user WHERE device_number = ?),1,0)";
        List<Object> params = new ArrayList<Object>();
        params.add(deviceNumber);
        Integer count = jdbcTemplate.queryForObject(sql,params.toArray(),Integer.class);

        //如果用户存在，返回用户信息，如果不存在，创建一个账号然后返回
        if(count>0){
            String querySql = "SELECT * FROM user WHERE device_number = ?";
            User user = jdbcTemplate.queryForObject(querySql,params.toArray(),new BeanPropertyRowMapper<User>(User.class));
            apiResponse.setSuccess();
            apiResponse.setSuccessData(user);
        }
        else{
            String inserSql = "INSERT INTO user (id,device_number,nick_name,create_time,head_picture) VALUES(?,?,?,?,?)";
            List<Object> params2 = new ArrayList<Object>();
            params2.add(IDUtil.getUUID());
            params2.add(deviceNumber);
            params2.add("Sách bạn "+ random);
            params2.add(new Date());
            params2.add("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=1885593064,484595924&fm=55&s=D4CF9256061223D6DAA2D7B403000009&w=320&h=160&img.JPG");
            jdbcTemplate.update(inserSql,params2.toArray());
            String querySql = "SELECT * FROM user WHERE device_number = ?";
            User user = jdbcTemplate.queryForObject(querySql,params.toArray(),new BeanPropertyRowMapper<User>(User.class));
            apiResponse.setSuccess();
            apiResponse.setSuccessMsg("机器码中没有这个账户，现已新建一个账户");
            apiResponse.setData(user);
        }
        return apiResponse;
    }

    @RequestMapping("/behavior/{userId}")
    @ResponseBody
    public ApiResponse getUserBehavior(@PathVariable("userId") String userId){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(userId)){
            apiResponse.setFailureMsg("3","userId是必传数值");
        }
        //用户看过相册按看过次数排序
        String sql = "SELECT album_id,count(*)AS count FROM user_album_read WHERE user_id = ? GROUP BY album_id ORDER BY count(*) limit 10";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);
        List<AlbumCount> albumCounts = jdbcTemplate.query(sql,params.toArray(),new BeanPropertyRowMapper<AlbumCount>(AlbumCount.class));
        AlbumCount album = albumCounts.get(0);

        String tagsql = "SELECT * FROM tag WHERE id IN (SELECT tag_id FROM album_tag WHERE album_id = ?)";
        List<Object> params2 = new ArrayList<Object>();
        params2.add(album.getAlbumId());
        List<Tag> tags = jdbcTemplate.query(tagsql,params2.toArray(),new BeanPropertyRowMapper<Tag>(Tag.class));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("album",albumCounts);
        map.put("tags",tags);
        apiResponse.setSuccessData(map);
        return apiResponse;
    }
}