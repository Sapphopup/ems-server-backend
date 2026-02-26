package com.ecarozzini.springbootchatapp;


import com.ecarozzini.springbootchatapp.datatypes.Chatroom;
import com.ecarozzini.springbootchatapp.datatypes.Message;
import com.ecarozzini.springbootchatapp.datatypes.MsgType;
import com.ecarozzini.springbootchatapp.datatypes.User;
import com.ecarozzini.springbootchatapp.datatypes.*;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class DatabaseManager {
    String databaseUrl;
    String databaseUser;
    String password;
    Connection conn;
    Statement stmt;
    @SneakyThrows
    public DatabaseManager(){
        this.databaseUrl = "jdbc:mariadb://localhost:3306/chat";     //jdbc:mariadb://localhost:3306/chat
        this.databaseUser = "test";                                 //test
        this.password = "test123";                                  //test123
    }


    public ResultSet connectToDbAndQueryForData(String query) {
        ResultSet result = null;
        try {

            conn = DriverManager.getConnection(databaseUrl, databaseUser, password);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Class.forName("org.mariadb.jdbc.Driver");

            log.info("(String request) Connecting to Database...");
            log.info("(String request) Connected to Chat Database as 'test' User");

            log.info("(String request) querying chat database");
            result = stmt.executeQuery(query); //KEYPOINT get String from database

        } catch (SQLException se) {
            se.printStackTrace(); //JDBC errors
        } catch (Exception e) {
            //Class.forName errors
            e.printStackTrace();
        } finally {
            //close resources
            try {
                if (stmt != null) conn.close();
            } catch (SQLException se) {//do nothing (? no clue why it's there lol)

                try {
                    if (conn != null) conn.close();
                } catch (SQLException se2) {
                    se2.printStackTrace();
                }
            }
        }
        return result;
    }
    public boolean connectToDbAndExecuteQuery(String query){
        Connection conn = null;
        boolean result = false;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(databaseUrl, databaseUser, password);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            Class.forName("org.mariadb.jdbc.Driver");

            log.info("(Execute request) Connecting to Database...");
            log.info("(Execute request) Connected to Chat Database as 'test' User");

            log.info("(Execute request) querying chat database");
            result = stmt.execute(query); //KEYPOINT execute query

        } catch (SQLException se) {
            se.printStackTrace(); //JDBC errors
        } catch (Exception e) {
            //Class.forName errors
            e.printStackTrace();
        } finally {
            //close resources
            try {
                if (stmt != null) conn.close();
            } catch (SQLException se) {//do nothing (? no clue why it's there lol)}
                try {
                    if (conn != null) conn.close();
                } catch (SQLException se2) {
                    se2.printStackTrace();
                }
            }
        }
        return result;
    }
    @SneakyThrows
    public boolean checkIfUnique(String valueToCheck, String whereToCheck, String whatToCheck){
        String query = "SELECT "+whatToCheck+" FROM "+whereToCheck+" WHERE "+whatToCheck+" = '"+valueToCheck+"';";
        return !(connectToDbAndQueryForData(query).getFetchSize() >0);
    }



    //region REGION load Message
    public Message loadMessage(String mID) {
        return new Message(mID, loadTypeOfMessage(mID), loadContentOfMessage(mID), loadSenderOfMessage(mID),
                                loadTimeStampOfMessage(mID));
    }

    @SneakyThrows
    MsgType loadTypeOfMessage(String mID) {
        String typeQ = "SELECT messageType FROM Messages WHERE messageID = '" + mID + "';";
        MsgType type;
        switch (connectToDbAndQueryForData(typeQ).getInt(1)) {
            case 0:
                type = MsgType.SYSTEM_INFO;
                break;
            case 1:
                type = MsgType.TEXT;
                break;
            case 2:
                type = MsgType.MEDIA;
                break;
            default:
                type = null;
                break;
        }
        return type;
    }

    @SneakyThrows
    String loadContentOfMessage(String mID) {
        String messageContentQ = "SELECT content FROM Messages WHERE messageID = '" + mID + "';";
        return connectToDbAndQueryForData(messageContentQ).getString(1);

    }

    @SneakyThrows
    User loadSenderOfMessage(String mID) {
        String uIDQ = "SELECT userID FROM Messages WHERE messageID = '" + mID + "';";
        return loadUser(connectToDbAndQueryForData(uIDQ).getString(1));
    }

    @SneakyThrows
    Timestamp loadTimeStampOfMessage(String mID) {
        String getTimeStampQuery = "SELECT timeStamp FROM Messages WHERE messageID = '" + mID + "';";
        return connectToDbAndQueryForData(getTimeStampQuery).getTimestamp(1);
    }
    //endregion

    //region REGION load user
    public User loadUser(String uID) {

        return new User(uID, loadUsername(uID), loadChatrooms(uID));
    }

    @SneakyThrows
    public String loadUsername(String uID) {
        String usernameQuery = "SELECT username FROM Users WHERE userID = '" + uID + "';";
        String uName="";
        ResultSet resultSet = connectToDbAndQueryForData(usernameQuery);
        if(resultSet.next()) uName = resultSet.getString("username");
        return uName;
    }

    @SneakyThrows
    public List<Chatroom> loadChatrooms(String uID) {
        String chatroomListQuery = "SELECT chatroomID FROM UsersInChatrooms WHERE userID = '" + uID + "';";
        ResultSet cList = connectToDbAndQueryForData(chatroomListQuery);
        List<Chatroom> usersChats = new ArrayList<Chatroom>();
        for (int i = 0; i < cList.getFetchSize(); i++) {
            if(cList.next()){
                usersChats.add(loadChatroom(cList.getString("chatroomID")));
                //KEYPOINT loads chatroom by ID and adds it to the List/l
            }
        }
        return usersChats;
    }
    //endregion

    //region REGION load Chatroom
    @SneakyThrows
    public Chatroom loadChatroom(String cID) {
        String chatNameQuery = "SELECT chatname FROM Chatrooms WHERE chatroomID = '" + cID + "';";
        java.sql.ResultSet rs  = connectToDbAndQueryForData(chatNameQuery);
        String chatName = "If you see this there was an error loading the chatname";
        if(rs.next()) chatName = rs.getString("chatname");
        String participantIdQuery = "SELECT userID FROM UsersInChatrooms WHERE chatroomID = '" + cID + "';"; //todo query database
        ResultSet pIdQResults = connectToDbAndQueryForData(participantIdQuery);
        List<String> participantIDs = new ArrayList<>();
        if(pIdQResults.next()){
            for (int i = 0; i < pIdQResults.getFetchSize(); i++) {
                String pId = pIdQResults.getString(i);

                participantIDs.add(""); //KEYPOINT creating list of chatroom participants
                participantIDs.set(participantIDs.size() - 1, pId);

            }
        }

        String chatHistoryQuery = "SELECT messageID FROM Messages WHERE chatroomID = '" + cID + "' ORDER BY timeStamp;";
        ResultSet cHistory = connectToDbAndQueryForData(chatHistoryQuery);
        List<Message> history = new ArrayList<>();
        if(cHistory.next()){
            for (int i = 0; i < rs.getFetchSize(); i++) {
                String mID = cHistory.getString(i);
                history.add(loadMessage(mID));//KEYPOINT loading message and adding it to the chat history
            }
        }

        //todo sort chathistory chronologically
        Chatroom chat = new Chatroom(cID, participantIDs, history, chatName);
        return chat;
    }
    //endregion

    //region REGION manage user
        @Getter
        private String latestGeneratedUserID;
        @SneakyThrows
        public boolean createNewUserInDB(String username){
            //generate userID
             String getUIDQ = "SELECT COUNT(*) AS count FROM Users;";
            System.out.println(getUIDQ);
            ResultSet resultSet = connectToDbAndQueryForData(getUIDQ);

            int count = 0;

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }

            System.out.println(count);

             String uID = "U"+ (count += 1) +"";
             latestGeneratedUserID = uID;
             //new Users entry
             String insertUQ = "INSERT INTO Users (userID,username) VALUES('"+uID+"','"+username+"');";
             return connectToDbAndExecuteQuery(insertUQ);
        }
        @SneakyThrows
        public boolean createNewLoginInDB(String loginName, String password, String uID){
             //new login entry
             String insertLQ = "INSERT INTO Logins (loginName,userID,password) VALUES ('"+loginName+"','"+uID+"','"+password+"');";
            System.out.println(insertLQ);
             return connectToDbAndExecuteQuery(insertLQ);
        }
        @SneakyThrows
        public boolean changeUsername(String newUsername, String uID){
        String changeNQ = "UPDATE Users SET username = '"+newUsername+"' WHERE userID = '"+uID+"';";
        return connectToDbAndExecuteQuery(changeNQ);

        }
        @SneakyThrows
        public boolean changePassword(String newPassword, String uID){
            String changePQ = "UPDATE Logins SET password = '"+newPassword+"' WHERE userID = '"+uID+"';";
            return connectToDbAndExecuteQuery(changePQ);
        }



    //endregion

    //region REGION manage message

        @SneakyThrows
        public boolean sendMessageToChatroom(@NotNull Message m, String chatId){
            int type;
            switch(m.getType()){
                case SYSTEM_INFO -> type = 0;
                case TEXT -> type = 1;
                case MEDIA -> type = 2;
                default -> type = -1;
            }
            String content;
            content = m.getContent();
            String sendMQuery = "INSERT INTO Messages (messageID,chatroomID,userID,messageType,content,timeStamp) " +
                    "VALUES ('"+m.getMessageId()+"','"+chatId+"','"
                                                         +m.getSender().getUserId()+"','"
                                                         +type+"','"+content+"','"
                                                         +m.getTimeStamp()+"');";
            return connectToDbAndExecuteQuery(sendMQuery);
        }
        @SneakyThrows
        public boolean editMessage(String newContent, String mID){
            String editMQ = "UPDATE Messages SET content = '"+newContent+"' WHERE messageID = '"+mID+"';";
            return connectToDbAndExecuteQuery(editMQ);
        }
    //endregion

    //region REGION manage chatroom
        @Getter
        private String lastGeneratedChatID = "";

        @SneakyThrows
        public boolean createNewChat(String chatName){
            //generate chat ID
            String getCIDQ = "SELECT COUNT(chatroomID) FROM Chatrooms;";
            String cID = "C"+connectToDbAndQueryForData(getCIDQ).getInt(1)+1+"";
            lastGeneratedChatID = cID;
            String createNCQ = "INSERT INTO Chatrooms (chatroomID,chatname)VALUES('"+cID+"','"+chatName+"');";
            return connectToDbAndExecuteQuery(createNCQ);
        }
        @SneakyThrows
        public boolean addUsersToChat(List<String> newMembers, String cID){
            String conIDQ = "SELECT COUNT(connectionID) FROM UsersInChatrooms;";
            String conID = "C"+connectToDbAndQueryForData(conIDQ).getInt(1)+1+"";
            for(String nM : newMembers){
                String addMQ = "INSERT INTO (connectionID, userID, chatroomID)VALUES UsersInChatrooms ('"+conID+"','"+nM+"','"+cID+"');";
                if (!connectToDbAndExecuteQuery(addMQ)) return false;
            }
            return true;
        }
        @SneakyThrows
        public boolean removeUsersFromChat(List<String> formerMembers, String cID){
            for(String fM : formerMembers){
                String removeMQ = "UPDATE UsersInChatrooms SET userID = 'REMOVED USER' " +
                                  "WHERE userID = '"+fM+"' AND chatroomID = '"+cID+"';";
                if(!connectToDbAndExecuteQuery(removeMQ)) return false;
            }
            return true;
        }
    //endregion

    //region REGION load word filter
    @SneakyThrows
    public List<String> loadFilteredWordList(){
        String filteredWQ = "SELECT * FROM BadWords;";
        return convertResultToStringList(connectToDbAndQueryForData(filteredWQ));
    }
    //endregion
    @SneakyThrows
    public List<String> convertResultToStringList(ResultSet rs){
            List<String> res = new ArrayList<>();
            for(int i = 0; i < rs.getFetchSize(); i++){
                res.add(rs.getString(i+1));
            }
            return res;
    }
}


