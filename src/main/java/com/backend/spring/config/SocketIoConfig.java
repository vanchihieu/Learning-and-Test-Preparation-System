//package com.backend.spring.config;
//
//import com.corundumstudio.socketio.*;
//import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
//import com.corundumstudio.socketio.Configuration;
//
//@Configuration
//public class SocketIoConfig {
//
//    @Bean
//    public SocketIOServer socketIoServer() {
//        Configuration config = new Configuration();
//        config.setHostname("localhost");
//        config.setPort(9004);
//
//        SocketConfig socketConfig = new SocketConfig();
//        socketConfig.setReuseAddress(true);
//
//        config.setSocketConfig(socketConfig);
//
//        return new SocketIOServer(config);
//    }
//
//}
