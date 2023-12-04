package com.sky.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.exception.UserNotLoginException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String wxURL="http://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Override
    public User wechatLogin(UserLoginDTO userLoginDTO) {
       String openID=getCode(userLoginDTO.getCode());

        if(openID==null){
            throw new LoginFailedException("登录失败!");
        }
        User user=userMapper.selectOrNot(openID);

        if(user==null){
            user=User.builder()
                    .openid(openID)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insertUser(user);
        }

        return user;
    }

    private String getCode(String code){
        Map<String, String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json=HttpClientUtil.doGet(wxURL,map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openID=jsonObject.getString("openid");
        return openID;
    }
}
