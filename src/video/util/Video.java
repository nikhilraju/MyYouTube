package video.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Video {
	public int getVideoid() {
		return videoid;
	}
	public void setVideoid(int videoid) {
		this.videoid = videoid;
	}
	public String getVname() {
		return vname;
	}
	public void setVname(String vname) {
		this.vname = vname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public Time getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Time createTime) {
		this.createTime = createTime;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}	
	int videoid;
	String vname;
	Date createdAt;
	Time createTime;
	String title;
	String description;
	String extension;
	float rating;
	int createdBy;
	public Video() {
		// TODO Auto-generated constructor stub
	}
}
