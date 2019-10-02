package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HelloController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/save")
    public String save(){
        for(int i=0;i<100;i++){
            redisTemplate.opsForValue().set("瓜田李下"+i,"海贼王"+i);
        }

        return "success";
    }

    @RequestMapping("/get")
    public void get(){
        redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            try{
                AtomicInteger count= new AtomicInteger();
                Cursor<byte[]> cursor=redisConnection.scan(ScanOptions.scanOptions().count(100).build());
                while(cursor.hasNext()){
                    count.getAndIncrement();
                    byte[] bytes=cursor.next();
                    String s=new String(bytes);

                    System.out.print(s+"  ");
                    if (count.get()%10==0){
                        System.out.println();
                    }
                }

                System.out.println(count.get());
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        });
    }

    @RequestMapping("/get2")
    public List<String> get2(){
        List<String> list=new ArrayList<>();

        redisTemplate.execute((RedisCallback<List<String>>) redisConnection -> {
            Cursor<byte[]> cursors=redisConnection.scan(ScanOptions.scanOptions().build());
            while(cursors.hasNext()){
                String key=new String(cursors.next());
                list.add(key);
            }

            return list;
        });

        return list;
    }
}