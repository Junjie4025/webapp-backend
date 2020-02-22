package edu.northeastern.ccwebapp.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Image {

	@Id
    @Column(name = "image_id")
    private String image_id;
	
	@Column(name = "url")
	private String url;

	public String getId() {
		return image_id;
	}

	public void setId(String id) {
		this.image_id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
