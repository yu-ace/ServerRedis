package com.example.serverredis.server;

import com.example.serverredis.model.Command;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class CommandServer {
    private static final CommandServer commandServer = new CommandServer();
    private CommandServer(){
        initializeCommands();
    }
    public static CommandServer getInstance(){
        return commandServer;
    }

    private final Map<String, Function<Command, Object>> commandMap = new HashMap<>();
    private final Memory memory = Memory.getInstance();
    public void initializeCommands() {
        commandMap.put("set", this::setKey);
        commandMap.put("get", this::getValue);
        commandMap.put("setNX", this::setNX);
        commandMap.put("exists",this::exists);
        commandMap.put("stat", command -> stat());
        commandMap.put("delete", this::deleteKey);
        commandMap.put("list", command -> list());
        commandMap.put("decr",this::decr);
        commandMap.put("incr",this::incr);
        commandMap.put("shutDown",command -> shutDown());
    }

    public Object executeCommand(Command command) {
        Object result;
        Function<Command, Object> action = commandMap.get(command.getName());
        if (action != null) {
            result = action.apply(command);
        } else {
            result = "不存在的指令: " + command.getName();
        }
        return result;
    }

    public void init() throws Exception{
        memory.initMap();
        memory.init();
    }

    public String shutDown() {
        try{
            memory.shutDown();
            memory.saveMap();
        }catch (Exception e){
            e.getStackTrace();
        }
        return "save ok";
    }

    public String exists(Command command){
        Object s = memory.get(command.getKey());
        if(s != " " && s != "null"){
            return "key 存在";
        }
        return "key 不存在";
    }
    public String setNX(Command command){
        if(command.getKey() != null){
            Object s = memory.get(command.getKey());
            if(!(Objects.equals(s, " ")) && !(Objects.equals(s, "null"))){
                return "key 存在";
            }
            memory.set(memory.buffer,memory.map,command.getKey(), 1);
            return "key 添加成功";
        }else {
            return "error";
        }
    }

    public Object incr(Command command){
        return memory.incr(command.getKey());
    }

    public Object decr(Command command){
        return memory.decr(command.getKey());
    }

    public String setKey(Command command){
        if(command.getKey() != null && command.getValue() != null){
            memory.set(memory.buffer,memory.map,command.getKey(), command.getValue());
            return "set ok";
        }else {
            return "set error";
        }
    }

    public Object getValue(Command command){
        return memory.get(command.getKey());
    }

    public Object stat(){
        Integer usedMemory = memory.usedMemory();
        int freeMemory = memory.FreeMemory();
        return "usedMemory:"+ usedMemory+"\nfreeMemory:" + freeMemory;
    }

    public String deleteKey(Command command){
        return memory.delete(command.getKey());
    }

    public String list() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> keys = memory.keyList();
        if(keys.size() != 0){
            for(String key:keys){
                stringBuilder.append(key).append(" ");
            }
            return stringBuilder.toString();
        }else {
            return "null";
        }

    }

    public void clean() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        Set<String> strings = memory.keyList();
        for(String s:strings){
            memory.set(byteBuffer,memory.map,s,memory.get(s));
        }
        memory.buffer = byteBuffer;
    }
}
