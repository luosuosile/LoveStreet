package com.nk.streetsnaps.api;

import com.nk.streetsnaps.entity.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/recommend")
public class ReconmendApi {
    ApiResponse apiResponse;
    String sql = "SELECT * FROM album WHEREid IN (SELECT album_idFROM album_tag WHERE tag_id = ? )";

}
