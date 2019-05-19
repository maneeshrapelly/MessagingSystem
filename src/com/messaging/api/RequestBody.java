package com.messaging.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RequestBody {
	@XmlElement
	public String from;
	@XmlElement
	public String to;
	@XmlElement
	public String text;
}
