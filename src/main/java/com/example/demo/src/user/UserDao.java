package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetUserRes> getUsersByNickname(String nickName) {
        String getUsersByNickNameQuery = "select U.userIdx,U.nickName, UL.townName, U.profileImage\n" +
                "        from User as U join UserLocation as UL on U.userIdx = UL.userIdx\n" +
                "        where U.nickName like ? and U.status=1 and UL.createdAt = (select max(createdAt) from UserLocation where userIdx=U.userIdx)\n;";

        String getUsersByNickNameParams = "%" + nickName + "%";
        return this.jdbcTemplate.query(getUsersByNickNameQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("townName"),
                        rs.getString("profileImage")),
                getUsersByNickNameParams);
    }

    public List<GetUserRes> getUsersByCode(int userIdx) {
        String getUsersByCodeQuery = "select U.userIdx,U.nickName, UL.townName, U.profileImage\n" +
                "        from User as U join UserLocation as UL on U.userIdx = UL.userIdx\n" +
                "        where U.userIdx = ? and U.status=1 and UL.createdAt = (select max(createdAt) from UserLocation where userIdx=U.userIdx)\n;";
        int getUsersByCodeParams = userIdx;
        return this.jdbcTemplate.query(getUsersByCodeQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("townName"),
                        rs.getString("profileImage")),
                getUsersByCodeParams);
    }

    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select * from User join UserLocation on User.userIdx = UserLocation.userIdx where User.status=1";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("townName"),
                        rs.getString("profileImage"))
        );
    }

    public GetProfileRes getProfiles(int userIdx) {
        String getProfilesQuery = "select User.userIdx, User.nickName, \n" +
                "       concat(userTemperature,'???')                                                      as 'userTemperature',\n" +
                "       concat(desiredRate, '%')                                                         as 'desiredRate',\n" +
                "       concat(responseRate, '%')                                                        as 'responseRate',\n" +
                "       (IF(isnull(lastlyLoginAt), concat('?????? ', concat(timestampdiff(day, User.createdAt, now()), '??? ?????? ??????')),\n" +
                "           concat('?????? ', concat(timestampdiff(day, lastlyLoginAt, now()), '??? ?????? ??????')))) as 'LoginedAt',\n" +
                "        date_format(User.createdAt, '%Y??? %c??? %d??? ??????')                                    as 'createdAt',\n" +
                "        ifnull(badgeCnt,0) as badgeCnt,\n" +
                "        ifnull(postCnt,0) as salesCnt\n" +
                "\n" +
                "from User left join (select userIdx, max(loginAt) as lastlyLoginAt from LoginInfo group by userIdx) LoginInfo on User.userIdx = LoginInfo.userIdx\n" +
                "    left join (select userIdx, count(userBadgeIdx) as badgeCnt from userBadge group by userIdx) as BadgePerUser on User.userIdx = BadgePerUser.userIdx\n" +
                "    left join (select writerIdx, count(postIdx) as postCnt from Post group by writerIdx) as PostPerUser on User.userIdx = PostPerUser.writerIdx\n" +
                "\n" +
                "where User.status = 1 and User.userIdx = ?;";

        int getProfilesParams = userIdx;
        return this.jdbcTemplate.queryForObject(getProfilesQuery,
                (rs, rowNum) -> new GetProfileRes(
                        rs.getString("nickName"),
                        rs.getInt("userIdx"),
                        rs.getString("userTemperature"),
                        rs.getString("desiredRate"),
                        rs.getString("responseRate"),
                        rs.getString("loginedAt"),
                        rs.getString("createdAt"),
                        rs.getInt("badgeCnt"),
                        rs.getInt("salesCnt")),
                getProfilesParams);
    }

    public GetDurationRes getNickNameDuration(int userIdx) {
        String getDurationQuery = "select userIdx, IF(timestampdiff(day, nickNameChangedAt, now()) > 30 , 0 , timestampdiff(day, nickNameChangedAt, now()) ) as duration\n" +
                "from User\n" +
                "where userIdx = ? and status = 1;";

        int getDurationParams = userIdx;
        return this.jdbcTemplate.queryForObject(getDurationQuery,
                (rs, rowNum) -> new GetDurationRes(
                        rs.getInt("userIdx"),
                        rs.getInt("duration")),
                getDurationParams);
    }

    public List<GetCertsRes> getCertifications(int userIdx) {
        String getCertsQuery = "select townName,cnt from (select cnt, townName,\n" +
                "                      IF(timestampdiff(day, createdAt, now()) > 30, 0,\n" +
                "                         timestampdiff(day, createdAt, now())) as certificatedAt\n" +
                "               from (select all townName, count(townName) cnt,max(createdAt) createdAt\n" +
                "                     from UserLocation \n" +
                "                     where userIdx = ? \n" +
                "                       and status = 1 \n" +
                "                     group by townName) L) UL  order by  certificatedAt asc limit 2 offset 0;\n";

        int getCertsParams = userIdx;
        return this.jdbcTemplate.query(getCertsQuery,
                (rs, rowNum) -> new GetCertsRes(
                        rs.getString("townName"),
                        rs.getInt("cnt")),
                getCertsParams);
    }

    public List<GetBadgeRes> getBadge(int userIdx) {
        String getBadgeQuery =
                "select userBadgeIdx, achivedAt, isRepresented, iconPath, badgeName, badgeDescription\n" +
                        "from userBadge inner join Badge B on userBadge.badgeIdx = B.badgeIdx inner join User U\n" +
                        "    on userBadge.userIdx = U.userIdx where U.userIdx = ?;";
        int getBadgeParams = userIdx;
        return this.jdbcTemplate.query(getBadgeQuery,
                (rs, rowNum) -> new GetBadgeRes(
                        rs.getInt("userBadgeIdx"),
                        rs.getTimestamp("achivedAt"),
                        rs.getBoolean("isRepresented"),
                        rs.getString("iconPath"),
                        rs.getString("badgeName"),
                        rs.getString("badgeDescription")),
                getBadgeParams);
    }

    public int checkPhoneNum(String phoneNumber) {
        String checkPhoneNumQuery = "select IFNULL((select status+1 as st\n" +
                "       from User\n" +
                "       where phoneNumber = ? and\n" +
                "             (status=1 or (status=0 and timestampdiff(day, updatedAt, now()) <= 7))),0) as s";
        String checkPhoneNumParams = phoneNumber;

        return this.jdbcTemplate.queryForObject(checkPhoneNumQuery,
                int.class,
                checkPhoneNumParams);
    }

    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User(userIdx, phoneNumber, nickName, profileImage) VALUES (default,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getPhoneNumber(), postUserReq.getNickName(), postUserReq.getProfileImage()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public void createUserTown(int userIdx, PostUserReq postUserReq) {
        String createUserQuery = "insert into UserLocation(userIdx, townName) values(?,?)";
        Object[] createUserParams = new Object[]{userIdx, postUserReq.getTownName()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
    }

    public User getPhoneNumber(String encryptPhoneNum) {
        String getPhoneNumQuery = "select userIdx,User.status from User where phoneNumber = ?";

        return this.jdbcTemplate.queryForObject(getPhoneNumQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getInt("status")
                ),
                encryptPhoneNum
        );
    }

}
//    public int modifyNickName(PatchUserReq patchUserReq){
//        String modifyNickNameQuery = "update User set nickName = ? and nickNameChangedAt =? where userIdx = ? ";
//        Object[] modifyNickNameParams = new Object[]{patchUserReq.getNewNickName(),patchUserReq.getUpdatedAt(), patchUserReq.getUserIdx()};
//
//        return this.jdbcTemplate.update(modifyNickNameQuery,modifyNickNameParams);
//    }
//}
//    public int checkEmail(String email){
//        String checkEmailQuery = "select exists(select email from UserInfo where email = ?)";
//        String checkEmailParams = email;
//        return this.jdbcTemplate.queryForObject(checkEmailQuery,
//                int.class,
//                checkEmailParams);???
//    }
//    public List<GetUserRes> getUsersByEmail(String email){
//        String getUsersByEmailQuery = "select * from UserInfo where email =?";
//        String getUsersByEmailParams = email;
//        return this.jdbcTemplate.query(getUsersByEmailQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("userName"),
//                        rs.getString("ID"),
//                        rs.getString("Email"),
//                        rs.getString("password")),
//                getUsersByEmailParams);
//    }
//
//    public GetUserRes getUser(int userIdx){
//        String getUserQuery = "select * from UserInfo where userIdx = ?";
//        int getUserParams = userIdx;
//        return this.jdbcTemplate.queryForObject(getUserQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("userName"),
//                        rs.getString("ID"),
//                        rs.getString("Email"),
//                        rs.getString("password")),
//                getUserParams);
//    }
//
//

//

//

//
//    public User getPwd(PostLoginReq postLoginReq){
//        String getPwdQuery = "select userIdx, password,email,userName,ID from UserInfo where ID = ?";
//        String getPwdParams = postLoginReq.getId();
//
//        return this.jdbcTemplate.queryForObject(getPwdQuery,
//                (rs,rowNum)-> new User(
//                        rs.getInt("userIdx"),
//                        rs.getString("ID"),
//                        rs.getString("userName"),
//                        rs.getString("password"),
//                        rs.getString("email")
//                ),
//                getPwdParams
//                );
//
//    }
//
//
//}
