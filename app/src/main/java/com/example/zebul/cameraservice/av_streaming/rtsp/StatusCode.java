package com.example.zebul.cameraservice.av_streaming.rtsp;

/*
|     "100"      ; Continue
|     "200"      ; OK
|     "201"      ; Created
|     "250"      ; Low on Storage Space
|     "300"      ; Multiple Choices
|     "301"      ; Moved Permanently
|     "302"      ; Moved Temporarily
|     "303"      ; See Other
|     "304"      ; Not Modified
|     "305"      ; Use Proxy
|     "400"      ; Bad Request
|     "401"      ; Unauthorized
|     "402"      ; Payment Required
|     "403"      ; Forbidden
|     "404"      ; Not Found
|     "405"      ; Method Not Allowed
|     "406"      ; Not Acceptable
|     "407"      ; Proxy Authentication Required
|     "408"      ; Request Time-out
|     "410"      ; Gone
|     "411"      ; Length Required
|     "412"      ; Precondition Failed
|     "413"      ; Request Entity Too Large
|     "414"      ; Request-URI Too Large
|     "415"      ; Unsupported Media Type
|     "451"      ; Parameter Not Understood
|     "452"      ; Conference Not Found
|     "453"      ; Not Enough Bandwidth
|     "454"      ; Session Not Found
|     "455"      ; Method Not Valid in This State
|     "456"      ; Header Field Not Valid for Resource
|     "457"      ; Invalid Range
|     "458"      ; Parameter Is Read-Only
|     "459"      ; Aggregate operation not allowed
|     "460"      ; Only aggregate operation allowed
|     "461"      ; Unsupported transport
|     "462"      ; Destination unreachable
|     "500"      ; Internal Server Error
|     "501"      ; Not Implemented
|     "502"      ; Bad Gateway
|     "503"      ; Service Unavailable
|     "504"      ; Gateway Time-out
|     "505"      ; RTSP Version not supported
|     "551"      ; Option not supported
*/

/*
grep "|"  StatusCode.java | sed 's/.*"\(.*\)".*; \(.*\)/\2@=@new@StatusCode\(\1,"\2"\)/g' | sed 's/-/ /g' | sed 's/\(.*\)=\(.*\)/\U\1\E=\2/g' | sed 's/ \(.*=\)/_\1/' | sed 's/ \(.*=\)/_\1/' | sed 's/ \(.*=\)/_\1/' | sed 's/ \(.*=\)/_\1/' | sed 's/ \(.*=\)/_\1/' | sed 's/\(.*\)/public static final StatusCode \1;/' | sed 's/@/ /g'
*/

public class StatusCode {
	
	private int code;
	private String details;
	
	private StatusCode(int code, String details){
	
		this.code = code;
		this.details = details;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}

	public static final StatusCode CONTINUE = new StatusCode(100,"Continue");
	public static final StatusCode OK = new StatusCode(200,"OK");
	public static final StatusCode CREATED = new StatusCode(201,"Created");
	public static final StatusCode LOW_ON_STORAGE_SPACE = new StatusCode(250,"Low on Storage Space");
	public static final StatusCode MULTIPLE_CHOICES = new StatusCode(300,"Multiple Choices");
	public static final StatusCode MOVED_PERMANENTLY = new StatusCode(301,"Moved Permanently");
	public static final StatusCode MOVED_TEMPORARILY = new StatusCode(302,"Moved Temporarily");
	public static final StatusCode SEE_OTHER = new StatusCode(303,"See Other");
	public static final StatusCode NOT_MODIFIED = new StatusCode(304,"Not Modified");
	public static final StatusCode USE_PROXY = new StatusCode(305,"Use Proxy");
	public static final StatusCode BAD_REQUEST = new StatusCode(400,"Bad Request");
	public static final StatusCode UNAUTHORIZED = new StatusCode(401,"Unauthorized");
	public static final StatusCode PAYMENT_REQUIRED = new StatusCode(402,"Payment Required");
	public static final StatusCode FORBIDDEN = new StatusCode(403,"Forbidden");
	public static final StatusCode NOT_FOUND = new StatusCode(404,"Not Found");
	public static final StatusCode METHOD_NOT_ALLOWED = new StatusCode(405,"Method Not Allowed");
	public static final StatusCode NOT_ACCEPTABLE = new StatusCode(406,"Not Acceptable");
	public static final StatusCode PROXY_AUTHENTICATION_REQUIRED = new StatusCode(407,"Proxy Authentication Required");
	public static final StatusCode REQUEST_TIME_OUT = new StatusCode(408,"Request Time out");
	public static final StatusCode GONE = new StatusCode(410,"Gone");
	public static final StatusCode LENGTH_REQUIRED = new StatusCode(411,"Length Required");
	public static final StatusCode PRECONDITION_FAILED = new StatusCode(412,"Precondition Failed");
	public static final StatusCode REQUEST_ENTITY_TOO_LARGE = new StatusCode(413,"Request Entity Too Large");
	public static final StatusCode REQUEST_URI_TOO_LARGE = new StatusCode(414,"Request URI Too Large");
	public static final StatusCode UNSUPPORTED_MEDIA_TYPE = new StatusCode(415,"Unsupported Media Type");
	public static final StatusCode PARAMETER_NOT_UNDERSTOOD = new StatusCode(451,"Parameter Not Understood");
	public static final StatusCode CONFERENCE_NOT_FOUND = new StatusCode(452,"Conference Not Found");
	public static final StatusCode NOT_ENOUGH_BANDWIDTH = new StatusCode(453,"Not Enough Bandwidth");
	public static final StatusCode SESSION_NOT_FOUND = new StatusCode(454,"Session Not Found");
	public static final StatusCode METHOD_NOT_VALID_IN_THIS_STATE = new StatusCode(455,"Method Not Valid in This State");
	public static final StatusCode HEADER_FIELD_NOT_VALID_FOR_RESOURCE = new StatusCode(456,"Header Field Not Valid for Resource");
	public static final StatusCode INVALID_RANGE = new StatusCode(457,"Invalid Range");
	public static final StatusCode PARAMETER_IS_READ_ONLY = new StatusCode(458,"Parameter Is Read Only");
	public static final StatusCode AGGREGATE_OPERATION_NOT_ALLOWED = new StatusCode(459,"Aggregate operation not allowed");
	public static final StatusCode ONLY_AGGREGATE_OPERATION_ALLOWED = new StatusCode(460,"Only aggregate operation allowed");
	public static final StatusCode UNSUPPORTED_TRANSPORT = new StatusCode(461,"Unsupported transport");
	public static final StatusCode DESTINATION_UNREACHABLE = new StatusCode(462,"Destination unreachable");
	public static final StatusCode INTERNAL_SERVER_ERROR = new StatusCode(500,"Internal Server Error");
	public static final StatusCode NOT_IMPLEMENTED = new StatusCode(501,"Not Implemented");
	public static final StatusCode BAD_GATEWAY = new StatusCode(502,"Bad Gateway");
	public static final StatusCode SERVICE_UNAVAILABLE = new StatusCode(503,"Service Unavailable");
	public static final StatusCode GATEWAY_TIME_OUT = new StatusCode(504,"Gateway Time out");
	public static final StatusCode RTSP_VERSION_NOT_SUPPORTED = new StatusCode(505,"RTSP Version not supported");
	public static final StatusCode OPTION_NOT_SUPPORTED = new StatusCode(551,"Option not supported");
}
