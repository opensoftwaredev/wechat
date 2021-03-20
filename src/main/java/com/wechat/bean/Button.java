package com.wechat.bean;
import java.util.List;

/**
 * 项目名称：deminwx
 * 类描述：微信菜单按钮
 * 创建者：zenglianping
 * 创建时间：2014-6-19上午11:11:01
 * 版本号 
 * 
 */
public class Button {

	private String type;
	
	private String name;
	
	private String key;
	
	private String url;
	
	private List<Button> sub_button;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Button> getSub_button() {
		return sub_button;
	}

	public void setSub_button(List<Button> sub_button) {
		this.sub_button = sub_button;
	}
}
