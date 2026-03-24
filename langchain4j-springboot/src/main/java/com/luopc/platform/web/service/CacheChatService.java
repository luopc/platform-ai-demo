package com.luopc.platform.web.service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author dev小筑
 * @copyright: 2020-2026 dev小筑
 * @className CacheChatService
 * @dateTime 2026-02-13 18:00:00
 * @description 带内存缓存的聊天服务，支持会话历史管理和缓存查询
 */
@Service
public class CacheChatService {

    // 内存缓存存储 - 使用ConcurrentHashMap保证线程安全
    private final Map<String, ChatSession> chatSessions = new ConcurrentHashMap<>();

    // 缓存最大容量
    private static final int MAX_CACHE_SIZE = 1000;

    // 会话过期时间（分钟）
    private static final int SESSION_EXPIRE_MINUTES = 30;

    /**
     * 聊天消息实体类
     */
    public static class ChatMessage {
        private String role; // "user" 或 "assistant"
        private String content;
        private LocalDateTime timestamp;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
            this.timestamp = LocalDateTime.now();
        }

        // getters and setters
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("[%s][%s]: %s",
                    timestamp.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                    role, content);
        }
    }

    /**
     * 聊天会话实体类
     */
    public static class ChatSession {
        private String sessionId;
        private List<ChatMessage> messages;
        private LocalDateTime lastAccessTime;
        private String sessionName;

        public ChatSession(String sessionId) {
            this.sessionId = sessionId;
            this.messages = new ArrayList<>();
            this.lastAccessTime = LocalDateTime.now();
            this.sessionName = "会话-" + sessionId.substring(0, 8);
        }

        // getters and setters
        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public List<ChatMessage> getMessages() {
            return messages;
        }

        public void setMessages(List<ChatMessage> messages) {
            this.messages = messages;
        }

        public LocalDateTime getLastAccessTime() {
            return lastAccessTime;
        }

        public void setLastAccessTime(LocalDateTime lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }

        public String getSessionName() {
            return sessionName;
        }

        public void setSessionName(String sessionName) {
            this.sessionName = sessionName;
        }

        /**
         * 添加消息到会话
         */
        public void addMessage(ChatMessage message) {
            this.messages.add(message);
            this.lastAccessTime = LocalDateTime.now();
        }

        /**
         * 获取最近的几条消息作为上下文
         */
        public List<ChatMessage> getRecentMessages(int count) {
            int size = messages.size();
            int fromIndex = Math.max(0, size - count);
            return new ArrayList<>(messages.subList(fromIndex, size));
        }

        /**
         * 判断会话是否过期
         */
        public boolean isExpired() {
            return LocalDateTime.now().minusMinutes(SESSION_EXPIRE_MINUTES).isAfter(lastAccessTime);
        }
    }

    /**
     * 创建新的聊天会话
     */
    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(sessionId);
        chatSessions.put(sessionId, session);

        // 清理过期会话和超出容量的会话
        cleanupExpiredSessions();
        trimCacheIfNeeded();

        return sessionId;
    }

    /**
     * 向指定会话添加用户消息并获取AI回复
     */
    public String addMessageAndGetReply(String sessionId, String userMessage, AiHelperService assistant) {
        ChatSession session = chatSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }

        // 添加用户消息
        session.addMessage(new ChatMessage("user", userMessage));

        // 构建上下文
        StringBuilder context = new StringBuilder();
        context.append("你是一个AI助手，以下是对话历史：\n");

        List<ChatMessage> recentMessages = session.getRecentMessages(10); // 获取最近10条消息
        for (ChatMessage msg : recentMessages) {
            if (msg.getRole().equals("user")) {
                context.append("用户: ").append(msg.getContent()).append("\n");
            } else {
                context.append("助手: ").append(msg.getContent()).append("\n");
            }
        }
        context.append("用户最新问题: ").append(userMessage).append("\n");
        context.append("请根据以上对话历史回答用户的问题：");

        // 获取AI回复
        String aiReply = assistant.chatWithContext("你是一个AI助手，能够根据对话历史进行连贯的回答", context.toString());

        // 添加AI回复到会话
        session.addMessage(new ChatMessage("assistant", aiReply));

        return aiReply;
    }

    /**
     * 获取会话的所有消息
     */
    public List<ChatMessage> getSessionMessages(String sessionId) {
        ChatSession session = chatSessions.get(sessionId);
        if (session == null) {
            return new ArrayList<>();
        }
        session.setLastAccessTime(LocalDateTime.now()); // 更新访问时间
        return new ArrayList<>(session.getMessages());
    }

    /**
     * 获取所有活跃会话
     */
    public List<Map<String, Object>> getAllSessions() {
        return chatSessions.entrySet().stream()
                .filter(entry -> !entry.getValue().isExpired())
                .map(entry -> {
                    Map<String, Object> sessionInfo = new HashMap<>();
                    ChatSession session = entry.getValue();
                    sessionInfo.put("sessionId", entry.getKey());
                    sessionInfo.put("sessionName", session.getSessionName());
                    sessionInfo.put("messageCount", session.getMessages().size());
                    sessionInfo.put("lastAccessTime", session.getLastAccessTime().toString());
                    return sessionInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 删除指定会话
     */
    public boolean deleteSession(String sessionId) {
        return chatSessions.remove(sessionId) != null;
    }

    /**
     * 重命名会话
     */
    public boolean renameSession(String sessionId, String newName) {
        ChatSession session = chatSessions.get(sessionId);
        if (session != null) {
            session.setSessionName(newName);
            session.setLastAccessTime(LocalDateTime.now());
            return true;
        }
        return false;
    }

    /**
     * 清理过期会话
     */
    private void cleanupExpiredSessions() {
        chatSessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * 当缓存超过最大容量时，删除最久未访问的会话
     */
    private void trimCacheIfNeeded() {
        if (chatSessions.size() > MAX_CACHE_SIZE) {
            // 找到最久未访问的会话并删除
            Optional<Map.Entry<String, ChatSession>> oldestSession = chatSessions.entrySet()
                    .stream()
                    .min(Comparator.comparing(entry -> entry.getValue().getLastAccessTime()));

            oldestSession.ifPresent(entry -> chatSessions.remove(entry.getKey()));
        }
    }

    /**
     * 只添加用户消息到会话（不获取AI回复）
     */
    public void addUserMessageOnly(String sessionId, String userMessage) {
        ChatSession session = chatSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }
        session.addMessage(new ChatMessage("user", userMessage));
    }

    /**
     * 只添加AI助手消息到会话
     */
    public void addAssistantMessageOnly(String sessionId, String assistantMessage) {
        ChatSession session = chatSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }
        session.addMessage(new ChatMessage("assistant", assistantMessage));
    }

    /**
     * 为指定会话构建上下文用于流式响应
     */
    public String buildContextForSession(String sessionId, String userMessage) {
        ChatSession session = chatSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }

        // 构建上下文
        StringBuilder context = new StringBuilder();
        context.append("你是一个AI助手，以下是对话历史：\n");

        List<ChatMessage> recentMessages = session.getRecentMessages(10); // 获取最近10条消息
        for (ChatMessage msg : recentMessages) {
            if (msg.getRole().equals("user")) {
                context.append("用户: ").append(msg.getContent()).append("\n");
            } else {
                context.append("助手: ").append(msg.getContent()).append("\n");
            }
        }
        context.append("用户最新问题: ").append(userMessage).append("\n");
        context.append("请根据以上对话历史回答用户的问题：");

        return context.toString();
    }

    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", chatSessions.size());
        stats.put("maxCacheSize", MAX_CACHE_SIZE);
        stats.put("sessionExpireMinutes", SESSION_EXPIRE_MINUTES);

        long activeSessions = chatSessions.values().stream()
                .filter(session -> !session.isExpired())
                .count();
        stats.put("activeSessions", activeSessions);

        long expiredSessions = chatSessions.size() - activeSessions;
        stats.put("expiredSessions", expiredSessions);

        return stats;
    }
}
