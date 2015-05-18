/**
 * 
 */
package model;

import java.util.Date;

/**
 * @author dahengwang
 *
 */
public class Entry {
	
	private int id;
	private String title;
	private String description;
	
	private String originlan;
	private String translated;
	
	private String publisher;
	private Date created;
	private String origin;
	private String url;
	
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getOriginLanguage(){
		return this.originlan;
	}
	
	public void setOriginLanguage(String originlan){
		this.originlan = originlan;
	}
	
	public String getTranslated(){
		return this.translated;
	}
	
	public void setTranslated(String translated){
		this.translated = translated;
	}
	
	public String getPublisher(){
		return this.publisher;
	}
	
	public void setPublisher(String publisher){
		this.publisher = publisher;
	}
	
	public Date getCreated(){
		return this.created;
	}
	
	public void setCreated(Date created){
		this.created = created;
	}
	
	public String getOrigin(){
		return this.origin;
	}
	
	public void setOrigin(String origin){
		this.origin = origin;
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
}
