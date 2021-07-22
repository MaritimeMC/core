package org.maritimemc.core.chatlog;

import lombok.SneakyThrows;
import org.apache.commons.lang.RandomStringUtils;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.db.SqlDatastore;

import java.sql.*;
import java.util.Set;
import java.util.UUID;

public class ChatLogDataManager {

    private static final String CREATE_CHATLOG_TABLE = "CREATE TABLE IF NOT EXISTS chatlog_base (id INT NOT NULL AUTO_INCREMENT, token CHAR(8), creator BINARY(16), PRIMARY KEY (id));";
    private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS chatlog_message (id INT NOT NULL AUTO_INCREMENT, sender BINARY(16), serverName TEXT, channelName TEXT, `time` BIGINT, type TEXT, content TEXT, PRIMARY KEY (id));";
    private static final String CREATE_RECIPIENTS_TABLE = "CREATE TABLE IF NOT EXISTS chatlog_recipients (messageId INT NOT NULL, recipient BINARY(16), FOREIGN KEY (messageId) REFERENCES chatlog_message(id));";
    private static final String CREATE_MESSAGE_MAP_TABLE = "CREATE TABLE IF NOT EXISTS chatlog_message_mapping (logId INT NOT NULL, messageId INT NOT NULL, FOREIGN KEY (logId) REFERENCES chatlog_base(id), FOREIGN KEY (messageId) REFERENCES chatlog_message(id));";

    private static final String INSERT_NEW_CHATLOG = "INSERT INTO chatlog_base (token, creator) VALUES (?, ?);";
    private static final String INSERT_MESSAGE = "INSERT INTO chatlog_message (sender, serverName, channelName, `time`, type, content) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String INSERT_MESSAGE_RECIPIENT = "INSERT INTO chatlog_recipients (messageId, recipient) VALUES (?, ?);";
    private static final String INSERT_MESSAGE_MAP = "INSERT INTO chatlog_message_mapping (logId, messageId) VALUES (?, ?);";

    private final SqlDatastore sqlDatastore = Locator.locate(SqlModule.class);

    public ChatLogDataManager() {
        createTables();
    }

    @SneakyThrows
    public void createTables() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            Statement statement = conn.createStatement();
            statement.addBatch(CREATE_CHATLOG_TABLE);
            statement.addBatch(CREATE_MESSAGES_TABLE);
            statement.addBatch(CREATE_RECIPIENTS_TABLE);
            statement.addBatch(CREATE_MESSAGE_MAP_TABLE);

            statement.executeBatch();
        }
    }

    @SneakyThrows
    public void addChatLog(ChatLog chatLog, Set<LogMessage> messages) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            chatLog.setToken(generateToken());

            PreparedStatement clInsert = conn.prepareStatement(INSERT_NEW_CHATLOG, Statement.RETURN_GENERATED_KEYS);
            clInsert.setString(1, chatLog.getToken());
            clInsert.setBytes(2, UtilUuid.toBytes(chatLog.getCreator()));

            clInsert.executeUpdate();

            ResultSet generatedKeys = clInsert.getGeneratedKeys();
            generatedKeys.next();

            chatLog.setId(generatedKeys.getInt(1));

            for (LogMessage message : messages) {
                PreparedStatement messageInsert = conn.prepareStatement(INSERT_MESSAGE, Statement.RETURN_GENERATED_KEYS);

                messageInsert.setBytes(1, UtilUuid.toBytes(message.getSender()));
                messageInsert.setString(2, message.getServerName());

                if (message.getChannelName() == null) {
                    messageInsert.setNull(3, Types.VARCHAR);
                } else {
                    messageInsert.setString(3, message.getChannelName());
                }

                messageInsert.setLong(4, message.getTime());
                messageInsert.setString(5, message.getType().name());
                messageInsert.setString(6, message.getContent());

                messageInsert.executeUpdate();

                ResultSet messageKeys = messageInsert.getGeneratedKeys();
                messageKeys.next();

                message.setId(messageKeys.getInt(1));

                PreparedStatement recipientInsert = conn.prepareStatement(INSERT_MESSAGE_RECIPIENT);
                for (UUID recipient : message.getRecipients()) {
                    recipientInsert.setInt(1, message.getId());
                    recipientInsert.setBytes(2, UtilUuid.toBytes(recipient));

                    recipientInsert.addBatch();
                }

                recipientInsert.executeBatch();

                PreparedStatement mapInsert = conn.prepareStatement(INSERT_MESSAGE_MAP);
                mapInsert.setInt(1, chatLog.getId());
                mapInsert.setInt(2, message.getId());

                mapInsert.executeUpdate();
            }

        }
    }



    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
