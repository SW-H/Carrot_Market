package com.example.demo.src.post;


import com.example.demo.src.post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<GetPostRes> getPost(int userIdx) {
        String getPostQuery =
                "select distinct Post.postIdx, title, attention, concat(FORMAT(price,0),'원') as price, UserLocation.townName,Post.status, Post.isRenewed,\n" +
                "      (case \n" +
                "           when isRenewed = 0 and timestampdiff(hour, Post.createdAt, now()) < 1 then concat(timestampdiff(minute, Post.createdAt, now()), '분 전')\n" +
                "           when isRenewed = 0 and timestampdiff(hour, Post.createdAt, now()) < 24 then concat(timestampdiff(hour, Post.createdAt, now()), '시간 전')\n" +
                "           when isRenewed = 0 and timestampdiff(day, Post.createdAt, now()) < 31 then concat(timestampdiff(day, Post.createdAt, now()), '일 전')\n" +
                "           when isRenewed = 0 and timestampdiff(week, Post.createdAt, now()) < 4 then concat(timestampdiff(week, Post.createdAt, now()), '주 전')\n" +
                "           when isRenewed = 0 and timestampdiff(month, Post.createdAt, now()) < 12 then concat(timestampdiff(month, Post.createdAt, now()), '개월 전')\n" +
                "           when isRenewed = 0 then concat(timestampdiff(year, Post.createdAt, now()), '년 전')\n" +
                "           when isRenewed = 1 and timestampdiff(hour, Post.createdAt, now()) < 1 then concat('끌올 ',concat(timestampdiff(minute, now(), Post.createdAt), '분 전'))\n" +
                "           when isRenewed = 1 and timestampdiff(hour, Post.createdAt, now()) < 24 then concat('끌올 ',concat(timestampdiff(hour, Post.createdAt, now()), '시간 전'))\n" +
                "           when isRenewed = 1 and timestampdiff(day, Post.createdAt, now()) < 31 then concat('끌올 ',concat(timestampdiff(day, Post.createdAt, now()), '일 전'))\n" +
                "           when isRenewed = 1 and timestampdiff(week, Post.createdAt, now()) < 4 then concat('끌올 ',concat(timestampdiff(week, Post.createdAt, now()), '주 전'))\n" +
                "           when isRenewed = 1 and timestampdiff(month, Post.createdAt, now()) < 12 then concat('끌올 ',concat(timestampdiff(month, Post.createdAt, now()), '개월 전'))\n" +
                "           else concat(timestampdiff(year, Post.createdAt, now()), '년 전')\n" +
                "       end) as displayTime,\n" +
                "\n" +
                "        COUNT(chatIdx) OVER (PARTITION BY Post.postIdx) as chatCnt,\n" +
                "        PostImage.imageIdx as imageIdx, PostImage.`i\bmagePath` as imagePath\n" +
                "    from (Post left join Chating on Post.postIdx = Chating.PostIdx) join PostImage\n" +
                "        join UserLocation on Post.sellerLocationIdx = UserLocation.userLocationIdx\n" +
                "    where Post.writerIdx = ? and\n" +
                "        (0<Post.status and Post.status<3);";
            int getPostParams = userIdx;
            return this.jdbcTemplate.query(getPostQuery,
                    (rs, rowNum) -> new GetPostRes(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getInt("attention"),
                        rs.getString("price"),
                        rs.getString("townName"),
                        rs.getInt("status"),
                        rs.getBoolean("isRenewed"),
                        rs.getString("displayTime"),
                        rs.getInt("chatCnt"),
                        rs.getString("imagePath")),
                    getPostParams);
    }
}