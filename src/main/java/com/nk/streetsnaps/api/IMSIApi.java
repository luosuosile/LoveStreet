package com.nk.streetsnaps.api;

import com.nk.streetsnaps.entity.ApiResponse;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractPipeImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/imsi/")
public class IMSIApi {
    private String IMSI;
    private String a1="";
    private String a2="";
    private String a3="";

    @RequestMapping("{imsi}")
    @ResponseBody
    public ApiResponse getIMSI(@PathVariable("imsi") String imsi){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(IMSI)){
            apiResponse.setFailureMsg("3","imsi是必传的");
        }

        IMSI = imsi;

        for(int i=0; i<5;i++){
            char a=IMSI.charAt(i);
            a1 = a1 + a;
        }
        for(int i=5; i<13;i++){
            char a=IMSI.charAt(i);
            a2 = a2 + a;
        }
        for(int i = 13; i<15;i++){
            char a = IMSI.charAt(i);
            a3 = a3 +a;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("operators",a2);
        map.put("Conuntry",a3);
        apiResponse.setSuccessData(map);
        return apiResponse;
    }

}
