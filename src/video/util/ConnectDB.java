package video.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

import fileop.FileUploadServlet;

public class ConnectDB {
	AmazonS3Client s3;
	AWSCredentials credentials;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private String hostname = "aj2568db.cdgekvhipva5.us-east-1.rds.amazonaws.com";
	private String port = "3306";
	private String dbName = "VIDEODB";
	private String userName = "aj2568";
	private String password = "columbia123";
	private String videoTable = "VIDEODB.Video";
	private String userTable = "VIDEODB.User";
	private String videoRatingTable = "VIDEODB.ratings";
	private String bucketName = "aj2568videos";

	public ConnectDB() throws ClassNotFoundException, SQLException {
		String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName
				+ "?user=" + userName + "&password=" + password;
		// This will load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");
		// Setup the connection with the DB
		connect = DriverManager.getConnection(jdbcUrl);
		try {
			credentials = new PropertiesCredentials(
					FileUploadServlet.class
							.getResourceAsStream("../AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s3 = new AmazonS3Client(credentials);
	}

	public Video getVideo(int videoid) throws SQLException {
		Video v = new Video();
		String query = "SELECT * FROM " + videoTable + " WHERE VIDEOID = ?";
		// PreparedStatements can use variables and are more efficient
		preparedStatement = connect.prepareStatement(query);
		// "myuser, webpage, datum, summary, COMMENTS from FEEDBACK.COMMENTS");
		// Parameters start with 1
		preparedStatement.setInt(1, videoid);
		ResultSet r = preparedStatement.executeQuery();
		while (r.next()) {
			v.setVideoid(r.getInt("videoid"));
			v.setVname(r.getString("filename"));
			v.setDescription(r.getString("description"));
			v.setRating(getVideoRating(videoid));
			v.setCreatedAt(r.getDate("createdAt"));
			v.setCreateTime(r.getTime("createTime"));
			v.setExtension(r.getString("extension"));
			v.setTitle(r.getString("title"));
		}
		return v;
	}

	public float getVideoRating(int videoid) throws SQLException {
		int numberOfRatings = 0, totalRatings = 0;
		String query = "SELECT rating FROM " + videoRatingTable
				+ " INNER JOIN " + videoTable
				+ " ON Video.videoid=ratings.videoid" + " AND Video.videoid="
				+ videoid;
		preparedStatement = connect.prepareStatement(query);
		ResultSet r = preparedStatement.executeQuery();
		while (r.next()) {
			numberOfRatings += 1;
			totalRatings += r.getInt("rating");
		}
		if (numberOfRatings == 0)
			return (float) 0.0;
		else {
			return ((float)totalRatings / numberOfRatings);
		}
	}

	public ArrayList<Video> getVideoList(int userid) throws SQLException {
		ArrayList<Video> vl = new ArrayList<Video>();
		String query = null;
		if (userid == -1) {
			// Getting all videos in the system
			query = "SELECT * FROM " + videoTable;
		} else {
			// Getting videos of the user
			query = "SELECT * FROM " + videoTable + " WHERE userid = " + userid;
		}
		preparedStatement = connect.prepareStatement(query);
		ResultSet r = preparedStatement.executeQuery();
		while (r.next()) {
			Video v = new Video();
			v.setVideoid(r.getInt("videoid"));
			v.setVname(r.getString("filename"));
			v.setDescription(r.getString("description"));
			v.setRating(getVideoRating(v.getVideoid()));
			v.setCreatedBy(r.getInt("userid"));
			v.setCreatedAt(r.getDate("createdAt"));
			v.setCreateTime(r.getTime("createTime"));
			v.setTitle(r.getString("title"));
			vl.add(v);
		}
		return vl;
	}

	public boolean createVideo(Video v) throws SQLException {
		String query = "INSERT INTO " + videoTable
				+ " VALUE (default, ?, ?, ?, ?, ?, ?, ?)";
		preparedStatement = connect.prepareStatement(query);
		preparedStatement.setString(1, v.getVname());
		preparedStatement.setString(2, v.getDescription());
		preparedStatement.setDate(3, v.getCreatedAt());
		preparedStatement.setString(4, v.getExtension());
		preparedStatement.setInt(5, v.getCreatedBy());
		preparedStatement.setTime(6, v.getCreateTime());
		preparedStatement.setString(7, v.getTitle());
		if (preparedStatement.executeUpdate() == 1)
			return true;
		else
			return false;
	}

	public boolean insertRating(Video v, int userid, int rating)
			throws SQLException {
		boolean entryPresent = false;
		String query1 = "SELECT * FROM " + videoRatingTable
				+ " WHERE videoid = " + v.getVideoid() + " AND userid = "
				+ userid;
		preparedStatement = connect.prepareStatement(query1);
		ResultSet r = preparedStatement.executeQuery();
		while (r.next()) {
			entryPresent = true;
			break;
		}
		if (entryPresent) {
			String newQuery = "UPDATE " + videoRatingTable + " SET RATING = "
					+ rating + " WHERE videoid = " + v.getVideoid()
					+ " AND userid = " + userid;
			// String delQuery = "DELETE FROM " + videoRatingTable
			// + " WHERE videoid = " + v.getVideoid() + " AND userid="
			// + userid;
			preparedStatement = connect.prepareStatement(newQuery);
			if (preparedStatement.executeUpdate() == 1) {
				System.out.println("Rating deleted for video id "
						+ v.getVideoid() + " of user " + userid);
				return true;
			} else {
				System.out.println("Rating didn't get deleted for video id "
						+ v.getVideoid() + " of user " + userid);
				return false;
			}
		} else {
			String query = "INSERT INTO " + videoRatingTable
					+ " VALUES (?, ?, ?)";
			preparedStatement = connect.prepareStatement(query);
			preparedStatement.setInt(1, v.getVideoid());
			preparedStatement.setInt(2, userid);
			preparedStatement.setInt(3, rating);
			if (preparedStatement.executeUpdate() == 1)
				return true;
			else
				return false;
		}
	}

	public boolean deleteVideo(Video v) throws SQLException {
		if (v.getRating() != 0.0) {
			String ratingsQuery = "DELETE FROM " + videoRatingTable
					+ " WHERE videoid = " + v.getVideoid();
			preparedStatement = connect.prepareStatement(ratingsQuery);
			if (preparedStatement.executeUpdate() == 1)
				System.out.println("DELETED Ratings of the video = "
						+ v.getVideoid());
			else {
				System.out.println("Can't DELETE Ratings of the video = "
						+ v.getVideoid());
				System.out.println("Please try again later");
				return false;
			}
		}
		String vidQuery = "DELETE FROM " + videoTable + " WHERE videoid ="
				+ v.getVideoid();
		preparedStatement = connect.prepareStatement(vidQuery);
		if (preparedStatement.executeUpdate() == 1) {
			s3.deleteObject(bucketName, v.getVname());
			System.out.println("VIDEO " + v.getVideoid()
					+ " DELETED successfully");
			return true;
		} else {
			System.out
					.println("Error while deleting video = " + v.getVideoid());
			System.out.println("Please try again later");
			return false;
		}
	}

	public int loginUser(User u) {
		ResultSet r = null;
		String loginQuery = "SELECT * FROM " + userTable
				+ " WHERE username = ? and password = ?";
		try {
			preparedStatement = connect.prepareStatement(loginQuery);
			preparedStatement.setString(1, u.getUsername());
			preparedStatement.setString(2, u.getPassword());
			r = preparedStatement.executeQuery();
			while (r.next()) {
				System.out.println(r.getInt("userid"));
				return r.getInt("userid");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
